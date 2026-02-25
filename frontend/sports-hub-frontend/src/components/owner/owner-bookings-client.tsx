"use client"

import {useMemo, useState} from "react"
import {OwnerSidebar} from "@/components/owner/owner-sidebar"
import {Badge} from "@/components/ui/badge"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs"
import {AlertCircle, CalendarDays, CheckCircle2, Clock, Filter, LayoutGrid, User, XCircle,} from "lucide-react"
import type {Booking, Resource, UserProfile, Venue} from "@/types"

// ── Props ────────────────────────────────────────────────────────

interface Props {
    user: UserProfile
    venues: Venue[]
    resources: Resource[]
    bookings: Booking[]
}

// ── Config ───────────────────────────────────────────────────────

const bookingStatusConfig: Record<string, {
    label: string
    color: string
    icon: React.ComponentType<{ className?: string }>
}> = {
    CONFIRMED: {
        label: "Confirmada",
        color: "bg-green-500/10 text-green-400 border-green-500/20",
        icon: CheckCircle2,
    },
    CANCELLED: {
        label: "Cancelada",
        color: "bg-red-500/10 text-red-400 border-red-500/20",
        icon: XCircle,
    },
    PENDING_PAYMENT: {
        label: "Pago pendiente",
        color: "bg-yellow-500/10 text-yellow-400 border-yellow-500/20",
        icon: AlertCircle,
    },
}

const paymentStatusConfig: Record<string, { label: string; color: string }> = {
    PAID:     { label: "Pagado",      color: "bg-green-500/10 text-green-400" },
    PENDING:  { label: "Pendiente",   color: "bg-yellow-500/10 text-yellow-400" },
    FAILED:   { label: "Fallido",     color: "bg-red-500/10 text-red-400" },
    REFUNDED: { label: "Reembolsado", color: "bg-blue-500/10 text-blue-400" },
}

// ── Helpers ──────────────────────────────────────────────────────

function formatTime(t: string): string {
    return t ? t.slice(0, 5) : ""
}

function formatDate(d: string): string {
    return new Date(d + "T00:00:00").toLocaleDateString("es-ES", {
        weekday: "short", day: "numeric", month: "short",
    })
}

// ── Component ────────────────────────────────────────────────────

export function OwnerBookingsClient({ user, venues, resources, bookings }: Props) {
    const [filterResource, setFilterResource] = useState("all")
    const [tab, setTab] = useState("all")

    const today = useMemo(() => new Date().toISOString().split("T")[0], [])

    const venueByResourceId = useMemo(() => {
        const map = new Map<string, Venue>()
        resources.forEach((r) => {
            const venue = venues.find((v) => v.id === r.venueId)
            if (venue) map.set(r.id, venue)
        })
        return map
    }, [resources, venues])

    const filtered = useMemo(() => {
        return bookings
            .filter((b) => filterResource === "all" || b.resourceId === filterResource)
            .filter((b) => {
                if (tab === "upcoming") return b.status !== "CANCELLED" && b.bookingDate >= today
                if (tab === "past")     return b.bookingDate < today
                if (tab === "cancelled") return b.status === "CANCELLED"
                return true
            })
            .sort((a, b) =>
                a.bookingDate.localeCompare(b.bookingDate) ||
                a.startTime.localeCompare(b.startTime)
            )
    }, [bookings, filterResource, tab, today])

    const totalRevenue = useMemo(() =>
            bookings
                .filter((b) => b.paymentStatus === "PAID")
                .reduce((sum, b) => sum + (b.pricePaid ?? 0), 0),
        [bookings]
    )

    const confirmedCount = useMemo(() =>
            bookings.filter((b) => b.status === "CONFIRMED").length,
        [bookings]
    )

    const upcomingCount = useMemo(() =>
            bookings.filter((b) => b.status !== "CANCELLED" && b.bookingDate >= today).length,
        [bookings, today]
    )

    return (
        <div className="flex min-h-screen bg-background">
            <OwnerSidebar user={user} />

            <main className="flex-1 min-w-0">
                <div className="mx-auto max-w-6xl px-4 py-6 lg:px-8 lg:py-8">

                    {/* Header */}
                    <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                        <div>
                            <h1 className="font-[var(--font-space-grotesk)] text-3xl font-bold tracking-tight text-foreground">
                                Reservas
                            </h1>
                            <p className="mt-1 text-sm text-muted-foreground">
                                Reservas realizadas en tus pistas
                            </p>
                        </div>

                        <Select value={filterResource} onValueChange={setFilterResource}>
                            <SelectTrigger className="h-9 w-[220px] border-border/50 bg-secondary/30 text-sm text-foreground">
                                <Filter className="mr-2 h-3.5 w-3.5 text-primary" />
                                <SelectValue placeholder="Filtrar por pista" />
                            </SelectTrigger>
                            <SelectContent className="border-border bg-card text-card-foreground">
                                <SelectItem value="all">Todas las pistas</SelectItem>
                                {resources.map((r) => (
                                    <SelectItem key={r.id} value={r.id}>
                                        {r.name} ({venues.find((v) => v.id === r.venueId)?.name ?? "—"})
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>

                    {/* Stats */}
                    <div className="mt-6 grid grid-cols-2 gap-3 sm:grid-cols-4">
                        {[
                            { label: "Total reservas", value: bookings.length,  color: "text-foreground" },
                            { label: "Confirmadas",    value: confirmedCount,   color: "text-green-400" },
                            { label: "Proximas",       value: upcomingCount,    color: "text-primary" },
                            { label: "Ingresos",       value: `${totalRevenue}€`, color: "text-foreground" },
                        ].map(({ label, value, color }) => (
                            <div key={label} className="rounded-xl border border-border/30 bg-card p-4">
                                <p className="text-xs text-muted-foreground">{label}</p>
                                <p className={`mt-1 text-2xl font-bold ${color}`}>{value}</p>
                            </div>
                        ))}
                    </div>

                    {/* Tabs */}
                    <Tabs value={tab} onValueChange={setTab} className="mt-8">
                        <TabsList className="bg-secondary/30">
                            <TabsTrigger value="all"       className="text-xs">Todas</TabsTrigger>
                            <TabsTrigger value="upcoming"  className="text-xs">Proximas</TabsTrigger>
                            <TabsTrigger value="past"      className="text-xs">Pasadas</TabsTrigger>
                            <TabsTrigger value="cancelled" className="text-xs">Canceladas</TabsTrigger>
                        </TabsList>

                        <TabsContent value={tab} className="mt-4">
                            {filtered.length === 0 ? (
                                <div className="flex flex-col items-center justify-center rounded-xl border border-dashed border-border/50 bg-card/50 py-16">
                                    <CalendarDays className="h-12 w-12 text-muted-foreground/30" />
                                    <h3 className="mt-4 font-[var(--font-space-grotesk)] text-lg font-semibold text-foreground">
                                        Sin reservas
                                    </h3>
                                    <p className="mt-1 text-sm text-muted-foreground">
                                        No hay reservas que coincidan con los filtros seleccionados.
                                    </p>
                                </div>
                            ) : (
                                <div className="flex flex-col gap-3">
                                    {filtered.map((booking) => {
                                        const bStatus = bookingStatusConfig[booking.status] ?? bookingStatusConfig.CONFIRMED
                                        const pStatus = paymentStatusConfig[booking.paymentStatus] ?? paymentStatusConfig.PENDING
                                        const BStatusIcon = bStatus.icon
                                        const resource = resources.find((r) => r.id === booking.resourceId)
                                        const venue = venueByResourceId.get(booking.resourceId)

                                        return (
                                            <div
                                                key={booking.id}
                                                className="flex flex-col gap-3 rounded-xl border border-border/50 bg-card p-4 sm:flex-row sm:items-center sm:gap-5"
                                            >
                                                {/* Date block */}
                                                <div className="flex h-14 w-16 shrink-0 flex-col items-center justify-center rounded-lg bg-primary/10">
                          <span className="text-[10px] font-semibold uppercase text-primary">
                            {formatDate(booking.bookingDate).split(" ")[0]}
                          </span>
                                                    <span className="text-xl font-bold text-foreground">
                            {new Date(booking.bookingDate + "T00:00:00").getDate()}
                          </span>
                                                    <span className="text-[9px] uppercase text-muted-foreground">
                            {formatDate(booking.bookingDate).split(" ").slice(2).join(" ")}
                          </span>
                                                </div>

                                                {/* Info */}
                                                <div className="flex-1 min-w-0">
                                                    <div className="flex flex-wrap items-center gap-2">
                                                        <h3 className="text-sm font-semibold text-foreground">
                                                            {venue?.name ?? booking.venueName ?? "—"}
                                                        </h3>
                                                        <Badge variant="outline" className={`${bStatus.color} gap-1 text-[10px]`}>
                                                            <BStatusIcon className="h-2.5 w-2.5" />
                                                            {bStatus.label}
                                                        </Badge>
                                                    </div>

                                                    <div className="mt-1.5 flex flex-wrap items-center gap-x-4 gap-y-1">
                            <span className="flex items-center gap-1 text-xs text-muted-foreground">
                              <Clock className="h-3 w-3 text-primary/70" />
                                {formatTime(booking.startTime)} - {formatTime(booking.endTime)}
                            </span>
                                                        {resource && (
                                                            <span className="flex items-center gap-1 text-xs text-muted-foreground">
                                <LayoutGrid className="h-3 w-3 text-primary/70" />
                                                                {resource.name}
                              </span>
                                                        )}
                                                        <span className="flex items-center gap-1 text-xs text-muted-foreground">
                              <User className="h-3 w-3 text-primary/70" />
                              Jugador #{booking.playerId?.slice(-4) ?? "—"}
                            </span>
                                                    </div>

                                                    {booking.cancelReason && (
                                                        <p className="mt-1.5 text-xs text-red-400/80">
                                                            Motivo: {booking.cancelReason}
                                                        </p>
                                                    )}
                                                </div>

                                                {/* Price */}
                                                <div className="flex items-center gap-4 sm:flex-col sm:items-end sm:gap-1">
                          <span className="text-lg font-bold text-foreground">
                            {booking.pricePaid ?? 0}€
                          </span>
                                                    <Badge className={`${pStatus.color} border-0 text-[10px]`}>
                                                        {pStatus.label}
                                                    </Badge>
                                                </div>
                                            </div>
                                        )
                                    })}
                                </div>
                            )}
                        </TabsContent>
                    </Tabs>
                </div>
            </main>
        </div>
    )
}
