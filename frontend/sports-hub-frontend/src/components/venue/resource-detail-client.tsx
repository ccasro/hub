"use client"

import {useMemo, useState} from "react"
import Image from "next/image"
import Link from "next/link"
import {Badge} from "@/components/ui/badge"
import {Button} from "@/components/ui/button"
import {Separator} from "@/components/ui/separator"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import {
    ArrowLeft,
    CalendarDays,
    CheckCircle2,
    Clock,
    CreditCard,
    Euro,
    LayoutGrid,
    Loader2,
    MapPin,
    ShieldCheck,
} from "lucide-react"
import type {Resource, SlotAvailability, Venue} from "@/types"


interface Props {
    venue: Venue
    resource: Resource
    initialSlots: SlotAvailability[]
    initialDate: string
}


const sportLabels: Record<string, string> = {
    PADEL: "Padel", TENNIS: "Tenis", SQUASH: "Squash", BADMINTON: "Badminton",
}

const dayLabelsFull: Record<string, string> = {
    MON: "Lunes", TUE: "Martes", WED: "Miercoles", THU: "Jueves",
    FRI: "Viernes", SAT: "Sabado", SUN: "Domingo",
}


function getNext7Days(fromDate: string): Date[] {
    const [year, month, day] = fromDate.split("-").map(Number)
    return Array.from({ length: 7 }, (_, i) => {
        return new Date(year, month - 1, day + i)
    })
}

function formatDate(date: Date): string {
    const y = date.getFullYear()
    const m = String(date.getMonth() + 1).padStart(2, "0")
    const d = String(date.getDate()).padStart(2, "0")
    return `${y}-${m}-${d}`
}

function formatDisplayDate(date: Date): string {
    return date.toLocaleDateString("es-ES", {
        weekday: "long", day: "numeric", month: "long",
    })
}


export function ResourceDetailClient({ venue, resource, initialSlots, initialDate }: Props) {
    const next7Days = useMemo(() => getNext7Days(initialDate), [initialDate])

    const [selectedDate, setSelectedDate] = useState<Date>(next7Days[0])
    const [slots, setSlots] = useState<SlotAvailability[]>(initialSlots)
    const [loadingSlots, setLoadingSlots] = useState(false)

    const [selectedSlot, setSelectedSlot] = useState<SlotAvailability | null>(null)

    const [showSummary, setShowSummary] = useState(false)

    const [pendingBooking, setPendingBooking] = useState<{
        id: string
        price: number
        currency: string
    } | null>(null)

    const [isCreatingBooking, setIsCreatingBooking] = useState(false)
    const [isConfirmingPayment, setIsConfirmingPayment] = useState(false)
    const [bookingConfirmed, setBookingConfirmed] = useState(false)
    const [bookingError, setBookingError] = useState<string | null>(null)

    const availableSlots   = slots.filter((s) => s.available)
    const mainImage        = resource.images?.[0]
    const priceRange       = resource.priceRules.length > 0
        ? { min: Math.min(...resource.priceRules.map((r) => r.price)), max: Math.max(...resource.priceRules.map((r) => r.price)) }
        : { min: 0, max: 0 }


    const handleDateChange = async (day: Date) => {
        setSelectedDate(day)
        setSelectedSlot(null)
        setLoadingSlots(true)
        try {
            const res = await fetch(
                `/api/proxy/api/resources/${resource.id}/slots?date=${formatDate(day)}`
            )
            if (!res.ok) throw new Error(`Error ${res.status}`)
            const data: SlotAvailability[] = await res.json()
            setSlots(data)
        } catch (e) {
            console.error(e)
            setSlots([])
        } finally {
            setLoadingSlots(false)
        }
    }

    const handleCreateBooking = async () => {
        if (!selectedSlot) return
        setIsCreatingBooking(true)
        setBookingError(null)
        try {
            const res = await fetch("/api/proxy/api/bookings", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    resourceId: resource.id,
                    bookingDate: formatDate(selectedDate),
                    startTime: selectedSlot.startTime,
                }),
            })
            if (!res.ok) {
                const txt = await res.text().catch(() => "")
                throw new Error(txt || `Error ${res.status}`)
            }
            const data = await res.json()
            // booking creado → pasamos a fase 2
            setPendingBooking({
                id: data.booking.id,
                price: data.booking.pricePaid,
                currency: data.booking.currency,
            })
            setShowSummary(false)
        } catch (e) {
            setBookingError(e instanceof Error ? e.message : "Error al crear la reserva")
        } finally {
            setIsCreatingBooking(false)
        }
    }


    const handleConfirmPayment = async () => {
        if (!pendingBooking) return
        setIsConfirmingPayment(true)
        setBookingError(null)
        try {
            const res = await fetch(
                `/api/proxy/api/dev/payments/${pendingBooking.id}/confirm`,
                {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        amount: pendingBooking.price,
                        currency: pendingBooking.currency,
                    }),
                }
            )
            if (!res.ok) {
                const txt = await res.text().catch(() => "")
                throw new Error(txt || `Error ${res.status}`)
            }
            setPendingBooking(null)
            setBookingConfirmed(true)
        } catch (e) {
            setBookingError(e instanceof Error ? e.message : "Error al confirmar el pago")
        } finally {
            setIsConfirmingPayment(false)
        }
    }

    const handleCancelPending = () => {
        setPendingBooking(null)
        setBookingError(null)
        setSelectedSlot(null)
        void handleDateChange(selectedDate)
    }

    // ── Render ───────────────────────────────────────────────────

    return (
        <main className="min-h-screen bg-background">
            {/* Top bar */}
            <div className="sticky top-0 z-30 border-b border-border/30 bg-background/80 backdrop-blur-xl">
                <div className="mx-auto flex h-14 max-w-6xl items-center gap-2 px-4">
                    <Link
                        href={`/venue/${venue.id}`}
                        className="flex items-center gap-2 text-sm font-medium text-muted-foreground transition-colors hover:text-foreground"
                    >
                        <ArrowLeft className="h-4 w-4" />
                        {venue.name}
                    </Link>
                    <Separator orientation="vertical" className="h-5 bg-border/50" />
                    <span className="truncate text-sm font-medium text-foreground">{resource.name}</span>
                </div>
            </div>

            <div className="mx-auto max-w-6xl px-4 py-6">
                <div className="flex flex-col gap-8 lg:flex-row">

                    {/* Left — Resource info */}
                    <div className="lg:w-[380px] lg:shrink-0">
                        <div className="relative aspect-[4/3] overflow-hidden rounded-xl border border-border/30 bg-secondary/20">
                            {mainImage ? (
                                <Image src={mainImage.url} alt={resource.name} fill
                                       className="object-cover" sizes="(max-width: 1024px) 100vw, 380px" priority />
                            ) : (
                                <div className="flex h-full items-center justify-center bg-gradient-to-br from-secondary/30 to-secondary/10">
                                    <LayoutGrid className="h-12 w-12 text-muted-foreground/20" />
                                </div>
                            )}
                            <div className="absolute left-3 top-3">
                                <Badge className="border-0 bg-primary/90 text-primary-foreground backdrop-blur-sm">
                                    {sportLabels[resource.type] || resource.type}
                                </Badge>
                            </div>
                        </div>

                        <div className="mt-5">
                            <h1 className="font-[var(--font-space-grotesk)] text-2xl font-bold text-foreground">
                                {resource.name}
                            </h1>
                            <div className="mt-1 flex items-center gap-1.5 text-sm text-muted-foreground">
                                <MapPin className="h-3.5 w-3.5 text-primary/70" />
                                <span>{venue.name} - {venue.city}</span>
                            </div>
                            {resource.description && (
                                <p className="mt-3 text-sm leading-relaxed text-muted-foreground">{resource.description}</p>
                            )}

                            <div className="mt-5 flex flex-col gap-2">
                                <div className="flex items-center gap-3 rounded-lg border border-border/30 bg-card px-3 py-2.5">
                                    <Clock className="h-4 w-4 text-primary" />
                                    <div>
                                        <p className="text-sm font-medium text-foreground">{resource.slotDurationMinutes} min por sesion</p>
                                        <p className="text-xs text-muted-foreground">Duracion fija de cada reserva</p>
                                    </div>
                                </div>
                                <div className="flex items-center gap-3 rounded-lg border border-border/30 bg-card px-3 py-2.5">
                                    <Euro className="h-4 w-4 text-primary" />
                                    <div>
                                        <p className="text-sm font-medium text-foreground">
                                            {priceRange.min === priceRange.max
                                                ? `${priceRange.min} EUR`
                                                : `${priceRange.min} - ${priceRange.max} EUR`}
                                        </p>
                                        <p className="text-xs text-muted-foreground">Precio segun horario y dia</p>
                                    </div>
                                </div>
                            </div>

                            {/* Schedules */}
                            <div className="mt-5">
                                <h3 className="text-sm font-semibold text-foreground">Horarios</h3>
                                <div className="mt-2 flex flex-col gap-1.5">
                                    {resource.schedules.map((s) => (
                                        <div key={s.dayOfWeek} className="flex items-center justify-between rounded-md bg-secondary/20 px-3 py-1.5 text-sm">
                                            <span className="font-medium text-foreground">{dayLabelsFull[s.dayOfWeek]}</span>
                                            <span className="text-muted-foreground">{s.openingTime.slice(0, 5)} - {s.closingTime.slice(0, 5)}</span>
                                        </div>
                                    ))}
                                </div>
                            </div>

                            {/* Price rules */}
                            <div className="mt-5">
                                <h3 className="text-sm font-semibold text-foreground">Tarifas</h3>
                                <div className="mt-2 flex flex-col gap-1.5">
                                    {resource.priceRules.map((rule) => (
                                        <div key={rule.id} className="flex items-center justify-between rounded-md border border-border/30 bg-card px-3 py-2 text-sm">
                                            <div>
                        <span className="font-medium text-foreground">
                          {rule.dayType === "WEEKDAY" ? "L-V" : rule.dayType === "WEEKEND" ? "S-D" : rule.dayType}
                        </span>
                                                <span className="ml-2 text-muted-foreground">
                          {rule.startTime.slice(0, 5)} - {rule.endTime.slice(0, 5)}
                        </span>
                                            </div>
                                            <span className="font-bold text-primary">{rule.price} {rule.currency}</span>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Right — Date + Slots */}
                    <div className="flex-1">
                        {/* Date selector */}
                        <div className="rounded-xl border border-border/40 bg-card p-5">
                            <h2 className="font-[var(--font-space-grotesk)] text-lg font-bold text-foreground">Selecciona fecha</h2>
                            <p className="mt-1 text-sm text-muted-foreground">Proximos 7 dias disponibles</p>
                            <div className="mt-4 flex gap-2 overflow-x-auto pb-1">
                                {next7Days.map((day) => {
                                    const isSelected = formatDate(day) === formatDate(selectedDate)
                                    return (
                                        <button
                                            key={formatDate(day)}
                                            onClick={() => handleDateChange(day)}
                                            className={`flex shrink-0 flex-col items-center rounded-xl border px-4 py-3 transition-all ${
                                                isSelected
                                                    ? "border-primary bg-primary/10 text-primary"
                                                    : "border-border/40 bg-secondary/10 text-muted-foreground hover:border-primary/30 hover:bg-secondary/20"
                                            }`}
                                        >
                      <span className="text-xs font-medium uppercase">
                        {day.toLocaleDateString("es-ES", { weekday: "short" })}
                      </span>
                                            <span className="mt-0.5 text-xl font-bold">{day.getDate()}</span>
                                            <span className="text-xs uppercase">
                        {day.toLocaleDateString("es-ES", { month: "short" })}
                      </span>
                                        </button>
                                    )
                                })}
                            </div>
                        </div>

                        {/* Slots grid */}
                        <div className="mt-6 rounded-xl border border-border/40 bg-card p-5">
                            <div className="flex items-center justify-between">
                                <div>
                                    <h2 className="font-[var(--font-space-grotesk)] text-lg font-bold capitalize text-foreground">
                                        {formatDisplayDate(selectedDate)}
                                    </h2>
                                    <p className="mt-0.5 text-sm text-muted-foreground">
                                        {availableSlots.length} de {slots.length} tramos disponibles
                                    </p>
                                </div>
                                <div className="flex items-center gap-4 text-xs text-muted-foreground">
                                    <div className="flex items-center gap-1.5">
                                        <div className="h-3 w-3 rounded border border-primary/30 bg-primary/10" />
                                        <span>Disponible</span>
                                    </div>
                                    <div className="flex items-center gap-1.5">
                                        <div className="h-3 w-3 rounded bg-secondary/40" />
                                        <span>Ocupado</span>
                                    </div>
                                </div>
                            </div>

                            {loadingSlots ? (
                                <div className="mt-6 flex items-center justify-center py-12">
                                    <Loader2 className="h-6 w-6 animate-spin text-primary" />
                                </div>
                            ) : slots.length === 0 ? (
                                <div className="mt-6 flex flex-col items-center justify-center rounded-lg border border-dashed border-border/40 py-12 text-center">
                                    <CalendarDays className="mb-2 h-8 w-8 text-muted-foreground/30" />
                                    <p className="font-medium text-foreground">No hay horario este dia</p>
                                    <p className="mt-1 text-sm text-muted-foreground">Prueba otro dia de la semana</p>
                                </div>
                            ) : (
                                <div className="mt-5 grid grid-cols-2 gap-2 sm:grid-cols-3 md:grid-cols-4">
                                    {slots.map((slot) => {
                                        const isSelected =
                                            selectedSlot?.startTime === slot.startTime &&
                                            selectedSlot?.endTime === slot.endTime
                                        return (
                                            <button
                                                key={slot.startTime}
                                                disabled={!slot.available}
                                                onClick={() => setSelectedSlot(isSelected ? null : slot)}
                                                className={`relative flex flex-col items-center rounded-lg border px-3 py-3 transition-all ${
                                                    !slot.available
                                                        ? "cursor-not-allowed border-border/20 bg-secondary/10 opacity-40"
                                                        : isSelected
                                                            ? "border-primary bg-primary/15 ring-2 ring-primary/30"
                                                            : "border-border/40 bg-secondary/10 hover:border-primary/40 hover:bg-primary/5"
                                                }`}
                                            >
                        <span className={`text-sm font-bold ${isSelected ? "text-primary" : slot.available ? "text-foreground" : "text-muted-foreground"}`}>
                          {slot.startTime.slice(0, 5)}
                        </span>
                                                <span className="text-xs text-muted-foreground">{slot.endTime.slice(0, 5)}</span>
                                                {slot.available && (
                                                    <span className={`mt-1 text-xs font-semibold ${isSelected ? "text-primary" : "text-primary/80"}`}>
                            {slot.price} EUR
                          </span>
                                                )}
                                                {!slot.available && (
                                                    <span className="mt-1 text-xs text-muted-foreground/60">Ocupado</span>
                                                )}
                                                {isSelected && (
                                                    <div className="absolute -right-1 -top-1 rounded-full bg-primary p-0.5">
                                                        <CheckCircle2 className="h-3.5 w-3.5 text-primary-foreground" />
                                                    </div>
                                                )}
                                            </button>
                                        )
                                    })}
                                </div>
                            )}
                        </div>

                        {/* Booking summary */}
                        {selectedSlot && (
                            <div className="sticky bottom-0 z-20 mt-6 rounded-xl border border-primary/30 bg-card p-5 shadow-lg shadow-primary/5">
                                <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                                    <div className="flex items-center gap-4">
                                        <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-primary/10">
                                            <CalendarDays className="h-6 w-6 text-primary" />
                                        </div>
                                        <div>
                                            <p className="font-[var(--font-space-grotesk)] text-base font-bold capitalize text-foreground">
                                                {formatDisplayDate(selectedDate)}
                                            </p>
                                            <p className="text-sm text-muted-foreground">
                                                {selectedSlot.startTime.slice(0, 5)} - {selectedSlot.endTime.slice(0, 5)}
                                                <span className="mx-2 text-border">|</span>
                                                {resource.name} - {venue.name}
                                            </p>
                                        </div>
                                    </div>
                                    <div className="flex items-center gap-4">
                                        <div className="text-right">
                                            <p className="text-2xl font-bold text-primary">{selectedSlot.price} EUR</p>
                                            <p className="text-xs text-muted-foreground">{resource.slotDurationMinutes} min</p>
                                        </div>
                                        <Button
                                            onClick={() => { setShowSummary(true); setBookingError(null) }}
                                            className="h-12 bg-primary px-6 font-semibold text-primary-foreground hover:bg-primary/90"
                                        >
                                            <CreditCard className="mr-2 h-4 w-4" />
                                            Reservar
                                        </Button>
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </div>

            {/* ── Dialog Fase 1: Resumen ── */}
            <Dialog open={showSummary} onOpenChange={setShowSummary}>
                <DialogContent className="border-border/50 bg-card text-foreground sm:max-w-md">
                    <DialogHeader>
                        <DialogTitle className="font-[var(--font-space-grotesk)] text-xl">Resumen de reserva</DialogTitle>
                        <DialogDescription className="text-muted-foreground">
                            Revisa los detalles. Al continuar se bloqueara el slot durante 5 minutos.
                        </DialogDescription>
                    </DialogHeader>

                    {selectedSlot && (
                        <div className="flex flex-col gap-4 py-2">
                            <div className="rounded-lg border border-border/30 bg-secondary/10 p-4">
                                <div className="flex flex-col gap-2.5 text-sm">
                                    <div className="flex justify-between">
                                        <span className="text-muted-foreground">Venue</span>
                                        <span className="font-medium text-foreground">{venue.name}</span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="text-muted-foreground">Pista</span>
                                        <span className="font-medium text-foreground">{resource.name}</span>
                                    </div>
                                    <Separator className="bg-border/30" />
                                    <div className="flex justify-between">
                                        <span className="text-muted-foreground">Fecha</span>
                                        <span className="font-medium capitalize text-foreground">{formatDisplayDate(selectedDate)}</span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="text-muted-foreground">Horario</span>
                                        <span className="font-medium text-foreground">
                      {selectedSlot.startTime.slice(0, 5)} - {selectedSlot.endTime.slice(0, 5)}
                    </span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="text-muted-foreground">Duracion</span>
                                        <span className="font-medium text-foreground">{resource.slotDurationMinutes} minutos</span>
                                    </div>
                                    <Separator className="bg-border/30" />
                                    <div className="flex justify-between">
                                        <span className="font-semibold text-foreground">Total</span>
                                        <span className="text-xl font-bold text-primary">{selectedSlot.price} EUR</span>
                                    </div>
                                </div>
                            </div>

                            {bookingError && (
                                <p className="rounded-lg border border-destructive/30 bg-destructive/5 px-3 py-2 text-sm text-destructive">
                                    {bookingError}
                                </p>
                            )}

                            <div className="flex items-center gap-2 text-xs text-muted-foreground">
                                <ShieldCheck className="h-3.5 w-3.5 text-primary/70" />
                                <span>El slot se bloqueara 5 min para completar el pago.</span>
                            </div>
                        </div>
                    )}

                    <DialogFooter className="gap-2 sm:gap-0">
                        <Button
                            variant="outline"
                            onClick={() => setShowSummary(false)}
                            className="border-border/60 text-foreground hover:bg-secondary/30"
                            disabled={isCreatingBooking}
                        >
                            Cancelar
                        </Button>
                        <Button
                            onClick={handleCreateBooking}
                            className="bg-primary font-semibold text-primary-foreground hover:bg-primary/90"
                            disabled={isCreatingBooking}
                        >
                            {isCreatingBooking ? (
                                <><Loader2 className="mr-2 h-4 w-4 animate-spin" />Bloqueando slot...</>
                            ) : (
                                <>Continuar al pago</>
                            )}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>

            {/* ── Dialog Fase 2: Pago ── */}
            <Dialog open={!!pendingBooking && !bookingConfirmed} onOpenChange={(open) => { if (!open) handleCancelPending() }}>
                <DialogContent className="border-border/50 bg-card text-foreground sm:max-w-md">
                    <DialogHeader>
                        <DialogTitle className="font-[var(--font-space-grotesk)] text-xl">Completar pago</DialogTitle>
                        <DialogDescription className="text-muted-foreground">
                            Slot bloqueado. Tienes 5 minutos para confirmar el pago.
                        </DialogDescription>
                    </DialogHeader>

                    <div className="flex flex-col gap-4 py-2">
                        {/* Simulated payment method */}
                        <div className="rounded-lg border border-border/30 bg-secondary/10 p-4">
                            <div className="flex items-center gap-3">
                                <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10">
                                    <CreditCard className="h-5 w-5 text-primary" />
                                </div>
                                <div className="flex-1">
                                    <p className="text-sm font-medium text-foreground">Tarjeta de credito</p>
                                    <p className="text-xs text-muted-foreground">**** **** **** 4242</p>
                                </div>
                                <Badge variant="secondary" className="border-0 bg-primary/10 text-primary">Visa</Badge>
                            </div>
                        </div>

                        <div className="flex items-center justify-between rounded-lg border border-primary/20 bg-primary/5 px-4 py-3">
                            <span className="text-sm font-medium text-foreground">Total a pagar</span>
                            <span className="text-xl font-bold text-primary">
                {pendingBooking?.price} {pendingBooking?.currency}
              </span>
                        </div>

                        {bookingError && (
                            <p className="rounded-lg border border-destructive/30 bg-destructive/5 px-3 py-2 text-sm text-destructive">
                                {bookingError}
                            </p>
                        )}

                        <div className="flex items-center gap-2 text-xs text-muted-foreground">
                            <ShieldCheck className="h-3.5 w-3.5 text-primary/70" />
                            <span>Pago seguro. Puedes cancelar gratis hasta 24h antes.</span>
                        </div>
                    </div>

                    <DialogFooter className="gap-2 sm:gap-0">
                        <Button
                            variant="outline"
                            onClick={handleCancelPending}
                            className="border-border/60 text-foreground hover:bg-secondary/30"
                            disabled={isConfirmingPayment}
                        >
                            Desistir
                        </Button>
                        <Button
                            onClick={handleConfirmPayment}
                            className="bg-primary font-semibold text-primary-foreground hover:bg-primary/90"
                            disabled={isConfirmingPayment}
                        >
                            {isConfirmingPayment ? (
                                <><Loader2 className="mr-2 h-4 w-4 animate-spin" />Procesando...</>
                            ) : (
                                <><CreditCard className="mr-2 h-4 w-4" />Pagar {pendingBooking?.price} EUR</>
                            )}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>

            {/* ── Dialog confirmacion ── */}
            <Dialog
                open={bookingConfirmed}
                onOpenChange={(open) => {
                    if (!open) { setBookingConfirmed(false); setSelectedSlot(null) }
                }}
            >
                <DialogContent className="border-border/50 bg-card text-foreground sm:max-w-md">
                    <DialogTitle className="sr-only">Reserva confirmada</DialogTitle>
                    <div className="flex flex-col items-center gap-4 py-6 text-center">
                        <div className="flex h-16 w-16 items-center justify-center rounded-full bg-primary/10">
                            <CheckCircle2 className="h-8 w-8 text-primary" />
                        </div>
                        <div>
                            <h2 className="font-[var(--font-space-grotesk)] text-2xl font-bold text-foreground">
                                Reserva confirmada
                            </h2>
                            <p className="mt-2 text-sm text-muted-foreground">
                                Tu reserva en <span className="font-medium text-foreground">{resource.name}</span> ha sido confirmada.
                                Recibiras un email con los detalles.
                            </p>
                        </div>
                        {selectedSlot && (
                            <div className="w-full rounded-lg border border-primary/20 bg-primary/5 p-4 text-sm">
                                <div className="flex flex-col gap-1.5">
                                    <div className="flex justify-between">
                                        <span className="text-muted-foreground">Fecha</span>
                                        <span className="font-medium capitalize text-foreground">{formatDisplayDate(selectedDate)}</span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="text-muted-foreground">Horario</span>
                                        <span className="font-medium text-foreground">
                      {selectedSlot.startTime.slice(0, 5)} - {selectedSlot.endTime.slice(0, 5)}
                    </span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="text-muted-foreground">Total pagado</span>
                                        <span className="font-bold text-primary">{selectedSlot.price} EUR</span>
                                    </div>
                                </div>
                            </div>
                        )}
                        <div className="flex w-full gap-3">
                            <Link href="/dashboard/bookings" className="flex-1">
                                <Button variant="outline" className="w-full border-border/60 text-foreground hover:bg-secondary/30">
                                    Mis reservas
                                </Button>
                            </Link>
                            <Link href="/dashboard" className="flex-1">
                                <Button className="w-full bg-primary font-medium text-primary-foreground hover:bg-primary/90">
                                    Dashboard
                                </Button>
                            </Link>
                        </div>
                    </div>
                </DialogContent>
            </Dialog>
        </main>
    )
}
