"use client"

import {useMemo, useState} from "react"
import Link from "next/link"
import {Badge} from "@/components/ui/badge"
import {Button} from "@/components/ui/button"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import {Textarea} from "@/components/ui/textarea"
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs"
import {DashboardNavbar} from "@/components/dashboard/dashboard-navbar"
import {Booking, UserProfile} from "@/types"
import {
    AlertTriangle,
    ArrowLeft,
    CalendarDays,
    CheckCircle2,
    Clock,
    Euro,
    MapPin,
    Swords,
    Ticket,
    XCircle,
} from "lucide-react"

interface Props {
    user: UserProfile
    bookings: Booking[]
}

function formatDate(dateStr: string): string {
    return new Date(dateStr + "T00:00:00").toLocaleDateString("es-ES", {
        weekday: "long", day: "numeric", month: "long", year: "numeric",
    })
}

function formatTime(t: string) {
    return t.slice(0, 5)
}

function StatusBadge({ status }: { status: Booking["status"] }) {
    const config = {
        CONFIRMED:       { className: "bg-green-500/10 text-green-400",      icon: CheckCircle2,   label: "Confirmada" },
        CANCELLED:       { className: "bg-destructive/10 text-destructive",   icon: XCircle,        label: "Cancelada" },
        PENDING_PAYMENT: { className: "bg-amber-500/10 text-amber-400",       icon: AlertTriangle,  label: "Pendiente pago" },
        PENDING_MATCH:   { className: "bg-blue-500/10 text-blue-400",         icon: Swords,         label: "Partido en curso" },
    }
    const c = config[status] ?? { className: "bg-gray-500/10 text-gray-400", label: status, icon: Clock };
    const Icon = c.icon
    return (
        <Badge className={`border-0 gap-1 ${c.className}`}>
            <Icon className="h-3 w-3" /> {c.label}
        </Badge>
    )
}

function PaymentBadge({ status }: { status: Booking["paymentStatus"] }) {
    const config = {
        PAID:     { className: "bg-green-500/10 text-green-400",        label: "Pagado" },
        REFUNDED: { className: "bg-blue-500/10 text-blue-400",          label: "Reembolsado" },
        PENDING:  { className: "bg-amber-500/10 text-amber-400",        label: "Pendiente" },
        FAILED:   { className: "bg-destructive/10 text-destructive",    label: "Fallido" },
    }
    const c = config[status]
    return (
        <Badge className={`border-0 gap-1 ${c.className}`}>
            <Euro className="h-3 w-3" /> {c.label}
        </Badge>
    )
}

export function BookingsClient({ user, bookings: initialBookings }: Props) {
    const [bookings, setBookings] = useState<Booking[]>(initialBookings)
    const [cancelTarget, setCancelTarget] = useState<Booking | null>(null)
    const [cancelReason, setCancelReason] = useState("")
    const [cancelling, setCancelling] = useState(false)
    const [cancelError, setCancelError] = useState<string | null>(null)  // error dentro del dialog

    const today = useMemo(() => {
        const now = new Date()
        return now.toISOString().slice(0, 10)
    }, [])

    const nowTime = useMemo(() => {
        const now = new Date()
        return `${String(now.getUTCHours()).padStart(2, "0")}:${String(now.getUTCMinutes()).padStart(2, "0")}:${String(now.getUTCSeconds()).padStart(2, "0")}`
    }, [])

    const isUpcoming = (b: Booking) =>
        b.bookingDate > today || (b.bookingDate === today && b.startTime > nowTime)

    const upcoming = useMemo(() =>
            bookings
                .filter((b) => (b.status === "CONFIRMED" || b.status === "PENDING_MATCH") && !b.leftMatch && isUpcoming(b))
                .sort((a, b) => a.bookingDate.localeCompare(b.bookingDate) || a.startTime.localeCompare(b.startTime)),
        [bookings, today, nowTime]
    )

    const past = useMemo(() =>
            bookings
                .filter((b) => (b.status === "CONFIRMED" && !isUpcoming(b)) || b.status === "CANCELLED")
                .sort((a, b) => b.bookingDate.localeCompare(a.bookingDate)),
        [bookings, today, nowTime]
    )

    const handleCancel = async () => {
        if (!cancelTarget) return
        setCancelling(true)
        setCancelError(null)

        try {
            const res = await fetch(`/api/proxy/api/bookings/${cancelTarget.id}/cancel`, {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ reason: cancelReason || undefined }),
            })

            if (!res.ok) {
                const body = await res.json().catch(() => null)
                const msg = body?.message || `Error ${res.status}`
                throw new Error(msg)
            }

            const updated: Booking = await res.json()
            setBookings((prev) => prev.map((b) => b.id === cancelTarget.id ? updated : b))
            setCancelTarget(null)
            setCancelReason("")
            setCancelError(null)
        } catch (e) {
            setCancelError(e instanceof Error ? e.message : "Error cancelando la reserva")
        } finally {
            setCancelling(false)
        }
    }

    const handleCloseCancelDialog = () => {
        setCancelTarget(null)
        setCancelReason("")
        setCancelError(null)
    }

    const BookingCard = ({ booking, showCancel }: { booking: Booking; showCancel?: boolean }) => {
        const isMatchBooking = booking.matchRequestId != null

        return (
        <div className="rounded-xl border border-border/50 bg-card p-4 transition-colors hover:border-border/80">
            <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
                <div className="flex items-start gap-4">
                    <div className={`flex h-14 w-16 shrink-0 flex-col items-center justify-center rounded-lg ${isMatchBooking ? "bg-blue-500/10" : "bg-primary/10"}`}>
                        <span className={`text-[10px] font-bold uppercase leading-none ${isMatchBooking ? "text-blue-400" : "text-primary"}`}>
                            {new Date(booking.bookingDate + "T00:00:00").toLocaleDateString("es-ES", { weekday: "short" })}
                        </span>
                        <span className="mt-0.5 text-xl font-bold leading-none text-foreground">
                            {new Date(booking.bookingDate + "T00:00:00").getDate()}
                        </span>
                        <span className="mt-0.5 text-[10px] font-medium uppercase leading-none text-muted-foreground">
                            {new Date(booking.bookingDate + "T00:00:00").toLocaleDateString("es-ES", { month: "short" })}
                        </span>
                    </div>

                    <div className="min-w-0 flex-1">
                        <div className="flex items-center gap-2">
                            <p className="text-sm font-semibold text-foreground">
                                {booking.venueName ?? "—"}
                            </p>
                            {isMatchBooking && (
                                <Badge className="border-0 bg-blue-500/10 px-1.5 text-[10px] font-medium text-blue-400">
                                    <Swords className="mr-1 h-2.5 w-2.5" />
                                    Partido
                                </Badge>
                            )}
                        </div>
                        <div className="mt-1.5 flex flex-wrap items-center gap-x-3 gap-y-1">
                            <span className="flex items-center gap-1 text-xs text-muted-foreground">
                                <Clock className="h-3 w-3 text-primary/70" />
                                {formatTime(booking.startTime)} - {formatTime(booking.endTime)}
                            </span>
                            {booking.venueCity && (
                                <span className="flex items-center gap-1 text-xs text-muted-foreground">
                                    <MapPin className="h-3 w-3 text-primary/70" />
                                    {booking.venueCity}
                                </span>
                            )}
                        </div>
                        {booking.resourceName && (
                            <Badge variant="secondary" className="mt-2 h-5 border-0 bg-secondary/50 text-[10px] font-medium text-muted-foreground">
                                {booking.resourceName}
                            </Badge>
                        )}
                        {booking.cancelReason && (
                            <p className="mt-2 text-xs text-destructive/80 italic">
                                Motivo: {booking.cancelReason}
                            </p>
                        )}
                    </div>
                </div>

                <div className="flex shrink-0 flex-col items-end gap-2">
                    <p className="text-lg font-bold text-foreground">
                        {booking.pricePaid}{booking.currency === "EUR" ? "€" : booking.currency}
                    </p>
                    <div className="flex flex-wrap items-center gap-1.5">
                        <StatusBadge status={booking.status} />
                        <PaymentBadge status={booking.paymentStatus} />
                    </div>
                    {isMatchBooking ? (
                        <Link href={`/match/${booking.matchRequestId}`}>
                            <Button
                                variant="ghost"
                                size="sm"
                                className="mt-1 gap-1.5 text-xs text-blue-400 hover:bg-blue-500/10 hover:text-blue-300"
                            >
                                <Swords className="h-3.5 w-3.5" />
                                Ver partido
                            </Button>
                        </Link>
                    ) : showCancel && booking.status === "CONFIRMED" ? (
                        <Button
                            variant="ghost"
                            size="sm"
                            className="mt-1 gap-1.5 text-xs text-destructive hover:bg-destructive/10 hover:text-destructive"
                            onClick={() => setCancelTarget(booking)}
                        >
                            <XCircle className="h-3.5 w-3.5" />
                            Cancelar
                        </Button>
                    ) : null}
                </div>
            </div>
        </div>
        )
    }

    const EmptyState = ({ message }: { message: string }) => (
        <div className="flex flex-col items-center py-16">
            <div className="flex h-14 w-14 items-center justify-center rounded-full bg-secondary/50">
                <Ticket className="h-7 w-7 text-muted-foreground/50" />
            </div>
            <p className="mt-4 text-sm font-medium text-foreground">{message}</p>
            <p className="mt-1 text-xs text-muted-foreground">Explora los venues y reserva tu proxima pista</p>
            <Link href="/dashboard" className="mt-4">
                <Button size="sm" className="gap-2 bg-primary text-primary-foreground hover:bg-primary/90">
                    <CalendarDays className="h-4 w-4" />
                    Explorar venues
                </Button>
            </Link>
        </div>
    )

    return (
        <div className="flex min-h-screen flex-col bg-background">
            <DashboardNavbar
                user={user}
                selectedCity="Todas"
                onCityChange={() => {}}
                searchQuery=""
                onSearchChange={() => {}}
                upcomingBookingsCount={upcoming.length}
            />

            <main className="mx-auto w-full max-w-4xl flex-1 px-4 py-6 lg:px-6 lg:py-8">
                <div className="mb-6 flex items-center gap-4">
                    <Link href="/dashboard">
                        <Button variant="ghost" size="icon" className="h-9 w-9 text-muted-foreground hover:text-foreground">
                            <ArrowLeft className="h-4 w-4" />
                        </Button>
                    </Link>
                    <div>
                        <h1 className="font-[var(--font-space-grotesk)] text-2xl font-bold tracking-tight text-foreground">
                            Mis Reservas
                        </h1>
                        <p className="mt-0.5 text-sm text-muted-foreground">
                            Gestiona tus reservas actuales y consulta el historial
                        </p>
                    </div>
                </div>

                {/* Stats */}
                <div className="mb-6 grid grid-cols-3 gap-3">
                    {[
                        { icon: CalendarDays, value: upcoming.length,                                    label: "Proximas",   className: "bg-primary/10 text-primary" },
                        { icon: CheckCircle2, value: bookings.filter((b) => b.status === "CONFIRMED").length, label: "Confirmadas", className: "bg-green-500/10 text-green-400" },
                        { icon: XCircle,      value: bookings.filter((b) => b.status === "CANCELLED").length, label: "Canceladas",  className: "bg-destructive/10 text-destructive" },
                    ].map(({ icon: Icon, value, label, className }) => (
                        <div key={label} className="flex items-center gap-3 rounded-xl border border-border/30 bg-card p-4">
                            <div className={`flex h-10 w-10 shrink-0 items-center justify-center rounded-lg ${className}`}>
                                <Icon className="h-5 w-5" />
                            </div>
                            <div>
                                <p className="text-xl font-bold text-foreground">{value}</p>
                                <p className="text-[11px] text-muted-foreground">{label}</p>
                            </div>
                        </div>
                    ))}
                </div>

                <Tabs defaultValue="upcoming" className="w-full">
                    <TabsList className="mb-4 w-full border border-border/50 bg-secondary/30">
                        <TabsTrigger value="upcoming" className="flex-1 text-sm">Proximas ({upcoming.length})</TabsTrigger>
                        <TabsTrigger value="history" className="flex-1 text-sm">Historial ({past.length})</TabsTrigger>
                        <TabsTrigger value="all" className="flex-1 text-sm">Todas ({bookings.length})</TabsTrigger>
                    </TabsList>

                    <TabsContent value="upcoming" className="flex flex-col gap-3">
                        {upcoming.length === 0
                            ? <EmptyState message="No tienes reservas proximas" />
                            : upcoming.map((b) => <BookingCard key={b.id} booking={b} showCancel />)
                        }
                    </TabsContent>

                    <TabsContent value="history" className="flex flex-col gap-3">
                        {past.length === 0
                            ? <EmptyState message="Sin historial de reservas" />
                            : past.map((b) => <BookingCard key={b.id} booking={b} />)
                        }
                    </TabsContent>

                    <TabsContent value="all" className="flex flex-col gap-3">
                        {bookings.length === 0
                            ? <EmptyState message="Sin reservas aun" />
                            : [...bookings]
                                .sort((a, b) => b.bookingDate.localeCompare(a.bookingDate))
                                .map((b) => (
                                    <BookingCard
                                        key={b.id}
                                        booking={b}
                                        showCancel={b.status === "CONFIRMED" && isUpcoming(b)}
                                    />
                                ))
                        }
                    </TabsContent>
                </Tabs>
            </main>

            {/* Cancel Dialog */}
            <Dialog
                open={!!cancelTarget}
                onOpenChange={(open) => { if (!open) handleCloseCancelDialog() }}
            >
                <DialogContent className="border-border bg-card text-card-foreground sm:max-w-md">
                    <DialogHeader>
                        <DialogTitle className="text-foreground">Cancelar reserva</DialogTitle>
                        <DialogDescription className="text-muted-foreground">
                            {cancelTarget && (
                                <>{cancelTarget.venueName ?? "—"} · {formatDate(cancelTarget.bookingDate)} · {formatTime(cancelTarget.startTime)} - {formatTime(cancelTarget.endTime)}</>
                            )}
                        </DialogDescription>
                    </DialogHeader>

                    <div className="flex flex-col gap-3 py-2">
                        <div className="rounded-lg border border-amber-500/20 bg-amber-500/5 p-3">
                            <p className="flex items-start gap-2 text-xs text-amber-400">
                                <AlertTriangle className="mt-0.5 h-3.5 w-3.5 shrink-0" />
                                Al cancelar se iniciara el proceso de reembolso. Esta accion no se puede deshacer.
                            </p>
                        </div>

                        <Textarea
                            value={cancelReason}
                            onChange={(e) => setCancelReason(e.target.value)}
                            placeholder="Motivo de cancelacion (opcional)"
                            rows={3}
                            className="border-border/60 bg-secondary/30 text-foreground placeholder:text-muted-foreground resize-none"
                        />

                        {/* Error del backend — visible dentro del dialog */}
                        {cancelError && (
                            <div className="flex items-start gap-2 rounded-lg border border-destructive/20 bg-destructive/5 px-3 py-2.5">
                                <AlertTriangle className="mt-0.5 h-3.5 w-3.5 shrink-0 text-destructive" />
                                <p className="text-xs text-destructive">{cancelError}</p>
                            </div>
                        )}
                    </div>

                    <DialogFooter className="gap-2 sm:gap-0">
                        <Button
                            variant="outline"
                            onClick={handleCloseCancelDialog}
                            disabled={cancelling}
                            className="border-border/60 bg-secondary/30 text-foreground hover:bg-secondary/50"
                        >
                            Volver
                        </Button>
                        <Button
                            onClick={handleCancel}
                            disabled={cancelling}
                            className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
                        >
                            {cancelling ? "Cancelando..." : "Confirmar cancelacion"}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    )
}
