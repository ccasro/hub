"use client"

import Link from "next/link"
import {useMemo} from "react"
import {OwnerSidebar} from "@/components/owner/owner-sidebar"
import {Badge} from "@/components/ui/badge"
import {Button} from "@/components/ui/button"
import {
    ArrowRight,
    Building2,
    CalendarDays,
    CheckCircle2,
    Clock,
    Euro,
    LayoutGrid,
    Pause,
    Plus,
    TrendingUp,
    Users,
    XCircle,
} from "lucide-react"
import type {Booking, Resource, UserProfile, Venue} from "@/types"

// ── Props ────────────────────────────────────────────────────────

interface Props {
    user: UserProfile
    venues: Venue[]
    resources: Resource[]
    bookings: Booking[]
}

// ── Component ────────────────────────────────────────────────────

export function OwnerDashboardClient({ user, venues, resources, bookings }: Props) {
    const today = useMemo(() => new Date().toISOString().split("T")[0], [])

    const activeVenues    = useMemo(() => venues.filter((v) => v.status === "ACTIVE").length, [venues])
    const pendingVenues   = useMemo(() => venues.filter((v) => v.status === "PENDING_REVIEW").length, [venues])
    const suspendedVenues = useMemo(() => venues.filter((v) => v.status === "SUSPENDED").length, [venues])
    const rejectedVenues  = useMemo(() => venues.filter((v) => v.status === "REJECTED").length, [venues])

    const upcomingBookings = useMemo(() =>
            bookings.filter((b) => b.status !== "CANCELLED" && b.bookingDate >= today),
        [bookings, today]
    )

    const totalRevenue = useMemo(() =>
            bookings
                .filter((b) => b.paymentStatus === "PAID")
                .reduce((sum, b) => sum + (b.pricePaid ?? 0), 0),
        [bookings]
    )

    const recentBookings = useMemo(() =>
            [...bookings]
                .sort((a, b) => (b.createdAt ?? "").localeCompare(a.createdAt ?? ""))
                .slice(0, 5),
        [bookings]
    )

    const venueBreakdown = [
        { status: "ACTIVE",         label: "Activos",     count: activeVenues,    color: "text-green-400",  icon: CheckCircle2 },
        { status: "PENDING_REVIEW", label: "Pendientes",  count: pendingVenues,   color: "text-yellow-400", icon: Clock },
        { status: "SUSPENDED",      label: "Suspendidos", count: suspendedVenues, color: "text-orange-400", icon: Pause },
        { status: "REJECTED",       label: "Rechazados",  count: rejectedVenues,  color: "text-red-400",    icon: XCircle },
    ]

    return (
        <div className="flex min-h-screen bg-background">
            <OwnerSidebar user={user} />

            <main className="flex-1 min-w-0">
                <div className="mx-auto max-w-6xl px-4 py-6 lg:px-8 lg:py-8">

                    {/* Hero */}
                    <section className="relative overflow-hidden rounded-2xl border border-border/50 bg-card p-6 lg:p-8">
                        <div className="pointer-events-none absolute -right-20 -top-20 h-64 w-64 rounded-full bg-primary/5 blur-3xl" />
                        <div className="pointer-events-none absolute -bottom-10 -left-10 h-40 w-40 rounded-full bg-primary/5 blur-3xl" />

                        <div className="relative flex flex-col gap-5 lg:flex-row lg:items-center lg:justify-between">
                            <div>
                                <Badge variant="secondary" className="border-0 bg-primary/10 text-xs font-medium text-primary">
                                    <Building2 className="mr-1 h-3 w-3" />
                                    Panel Propietario
                                </Badge>
                                <h1 className="mt-3 font-[var(--font-space-grotesk)] text-3xl font-bold tracking-tight text-foreground lg:text-4xl">
                                    Hola, {user.displayName?.split(" ")[0] ?? "Propietario"}
                                </h1>
                                <p className="mt-1.5 max-w-md text-sm leading-relaxed text-muted-foreground">
                                    Gestiona tus venues, pistas, horarios y controla las reservas.
                                </p>
                            </div>
                            <div className="flex items-center gap-3">
                                <Link href="/owner/venues">
                                    <Button variant="outline" className="gap-2 border-border/60 bg-secondary/30 text-foreground hover:border-primary/40 hover:bg-secondary/50">
                                        <Plus className="h-4 w-4 text-primary" />
                                        Nuevo Venue
                                    </Button>
                                </Link>
                                <Link href="/owner/bookings">
                                    <Button className="gap-2 bg-primary font-medium text-primary-foreground hover:bg-primary/90">
                                        <CalendarDays className="h-4 w-4" />
                                        Ver Reservas
                                    </Button>
                                </Link>
                            </div>
                        </div>

                        {/* KPIs */}
                        <div className="relative mt-6 grid grid-cols-2 gap-3 sm:grid-cols-4">
                            {[
                                { icon: Building2,   value: venues.length,           label: "Venues",   color: "bg-primary/10 text-primary" },
                                { icon: LayoutGrid,  value: resources.length,        label: "Pistas",   color: "bg-primary/10 text-primary" },
                                { icon: CalendarDays,value: upcomingBookings.length,  label: "Proximas", color: "bg-primary/10 text-primary" },
                                { icon: Euro,        value: `${totalRevenue}€`,      label: "Ingresos", color: "bg-green-500/10 text-green-400" },
                            ].map(({ icon: Icon, value, label, color }) => (
                                <div key={label} className="flex items-center gap-3 rounded-xl border border-border/30 bg-background/50 p-3.5">
                                    <div className={`flex h-9 w-9 shrink-0 items-center justify-center rounded-lg ${color.split(" ")[0]}`}>
                                        <Icon className={`h-4 w-4 ${color.split(" ")[1]}`} />
                                    </div>
                                    <div>
                                        <p className={`text-lg font-bold leading-none ${label === "Ingresos" ? "text-green-400" : "text-foreground"}`}>
                                            {value}
                                        </p>
                                        <p className="mt-1 text-[11px] text-muted-foreground">{label}</p>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </section>

                    {/* Two-column layout */}
                    <div className="mt-8 flex flex-col gap-6 lg:flex-row">

                        {/* Left */}
                        <div className="flex flex-col gap-6 lg:w-80 xl:w-96">

                            {/* Venue breakdown */}
                            <div className="rounded-xl border border-border/50 bg-card p-5">
                                <div className="flex items-center justify-between">
                                    <h2 className="font-[var(--font-space-grotesk)] text-sm font-bold text-foreground">
                                        Estado de Venues
                                    </h2>
                                    <Link href="/owner/venues">
                                        <Button variant="ghost" size="sm" className="gap-1 text-xs text-muted-foreground hover:text-foreground">
                                            Ver todos <ArrowRight className="h-3 w-3" />
                                        </Button>
                                    </Link>
                                </div>
                                <div className="mt-4 flex flex-col gap-2">
                                    {venueBreakdown.map(({ status, label, count, color, icon: Icon }) => (
                                        <div key={status} className="flex items-center justify-between rounded-lg border border-border/30 bg-secondary/10 px-3 py-2.5">
                                            <div className="flex items-center gap-2">
                                                <Icon className={`h-4 w-4 ${color}`} />
                                                <span className="text-sm text-foreground">{label}</span>
                                            </div>
                                            <span className={`text-sm font-bold ${color}`}>{count}</span>
                                        </div>
                                    ))}
                                </div>
                            </div>

                            {/* Quick actions */}
                            <div className="rounded-xl border border-border/50 bg-card p-5">
                                <h2 className="font-[var(--font-space-grotesk)] text-sm font-bold text-foreground">
                                    Acciones rapidas
                                </h2>
                                <div className="mt-4 flex flex-col gap-2">
                                    {[
                                        { href: "/owner/venues",    icon: Plus,       title: "Crear Venue",       desc: "Anade un nuevo club o centro" },
                                        { href: "/owner/resources", icon: LayoutGrid, title: "Gestionar Pistas",  desc: "Horarios, precios y estado" },
                                        { href: "/owner/bookings",  icon: TrendingUp, title: "Ver ingresos",       desc: "Reservas y facturacion" },
                                    ].map(({ href, icon: Icon, title, desc }) => (
                                        <Link key={href} href={href}>
                                            <Button variant="ghost" className="h-auto w-full justify-start gap-3 px-3 py-3 text-foreground hover:bg-secondary/50">
                                                <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary/10">
                                                    <Icon className="h-4 w-4 text-primary" />
                                                </div>
                                                <div className="text-left">
                                                    <p className="text-sm font-medium">{title}</p>
                                                    <p className="text-[11px] text-muted-foreground">{desc}</p>
                                                </div>
                                            </Button>
                                        </Link>
                                    ))}
                                </div>
                            </div>
                        </div>

                        {/* Right */}
                        <div className="flex-1 min-w-0">

                            {/* Recent bookings */}
                            <div className="rounded-xl border border-border/50 bg-card">
                                <div className="flex items-center justify-between border-b border-border/50 px-5 py-4">
                                    <h2 className="font-[var(--font-space-grotesk)] text-sm font-bold text-foreground">
                                        Ultimas reservas
                                    </h2>
                                    <Link href="/owner/bookings">
                                        <Button variant="ghost" size="sm" className="gap-1 text-xs text-muted-foreground hover:text-foreground">
                                            Ver todas <ArrowRight className="h-3 w-3" />
                                        </Button>
                                    </Link>
                                </div>

                                {recentBookings.length === 0 ? (
                                    <div className="flex flex-col items-center py-10">
                                        <CalendarDays className="h-10 w-10 text-muted-foreground/30" />
                                        <p className="mt-3 text-sm text-muted-foreground">Sin reservas todavia</p>
                                    </div>
                                ) : (
                                    <div className="flex flex-col divide-y divide-border/30">
                                        {recentBookings.map((booking) => {
                                            const resource = resources.find((r) => r.id === booking.resourceId)
                                            const statusConfig = {
                                                CONFIRMED:       { label: "Confirmada",    className: "bg-green-500/10 text-green-400" },
                                                CANCELLED:       { label: "Cancelada",     className: "bg-red-500/10 text-red-400" },
                                                PENDING_PAYMENT: { label: "Pago pendiente", className: "bg-yellow-500/10 text-yellow-400" },
                                            }
                                            const s = statusConfig[booking.status as keyof typeof statusConfig]

                                            return (
                                                <div key={booking.id} className="flex items-center gap-4 px-5 py-4">
                                                    <div className="flex h-12 w-14 shrink-0 flex-col items-center justify-center rounded-lg bg-primary/10">
                            <span className="text-[9px] font-semibold uppercase text-primary">
                              {new Date(booking.bookingDate + "T00:00:00").toLocaleDateString("es-ES", { weekday: "short" })}
                            </span>
                                                        <span className="text-lg font-bold leading-none text-foreground">
                              {new Date(booking.bookingDate + "T00:00:00").getDate()}
                            </span>
                                                    </div>
                                                    <div className="flex-1 min-w-0">
                                                        <div className="flex items-center gap-2">
                                                            <p className="truncate text-sm font-semibold text-foreground">
                                                                {resource?.name ?? booking.resourceName ?? "—"}
                                                            </p>
                                                            {s && (
                                                                <Badge className={`h-5 border-0 text-[10px] ${s.className}`}>
                                                                    {s.label}
                                                                </Badge>
                                                            )}
                                                        </div>
                                                        <div className="mt-1 flex items-center gap-3 text-xs text-muted-foreground">
                              <span className="flex items-center gap-1">
                                <Clock className="h-3 w-3 text-primary/70" />
                                  {booking.startTime?.slice(0, 5)} - {booking.endTime?.slice(0, 5)}
                              </span>
                                                            <span className="flex items-center gap-1">
                                <Users className="h-3 w-3 text-primary/70" />
                                #{booking.playerId?.slice(-4) ?? "—"}
                              </span>
                                                        </div>
                                                    </div>
                                                    <span className="shrink-0 text-sm font-bold text-foreground">
                            {booking.pricePaid}€
                          </span>
                                                </div>
                                            )
                                        })}
                                    </div>
                                )}
                            </div>

                            {/* Resources overview */}
                            <div className="mt-6 rounded-xl border border-border/50 bg-card p-5">
                                <div className="flex items-center justify-between">
                                    <h2 className="font-[var(--font-space-grotesk)] text-sm font-bold text-foreground">
                                        Resumen de Pistas
                                    </h2>
                                    <Link href="/owner/resources">
                                        <Button variant="ghost" size="sm" className="gap-1 text-xs text-muted-foreground hover:text-foreground">
                                            Gestionar <ArrowRight className="h-3 w-3" />
                                        </Button>
                                    </Link>
                                </div>
                                <div className="mt-4 grid gap-3 sm:grid-cols-2">
                                    {resources.map((resource) => {
                                        const confirmedCount = bookings.filter(
                                            (b) => b.resourceId === resource.id && b.status === "CONFIRMED"
                                        ).length

                                        const resourceStatusConfig = {
                                            ACTIVE:         { label: "Activa",    className: "border-green-500/20 bg-green-500/10 text-green-400" },
                                            PENDING_REVIEW: { label: "Pendiente", className: "border-yellow-500/20 bg-yellow-500/10 text-yellow-400" },
                                            SUSPENDED:      { label: "Suspendida",className: "border-orange-500/20 bg-orange-500/10 text-orange-400" },
                                        }
                                        const rs = resourceStatusConfig[resource.status as keyof typeof resourceStatusConfig]
                                            ?? resourceStatusConfig.SUSPENDED

                                        return (
                                            <div key={resource.id} className="flex items-center gap-3 rounded-lg border border-border/30 bg-secondary/10 p-3">
                                                <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-primary/10">
                                                    <LayoutGrid className="h-4 w-4 text-primary" />
                                                </div>
                                                <div className="flex-1 min-w-0">
                                                    <p className="truncate text-sm font-medium text-foreground">{resource.name}</p>
                                                    <p className="text-[11px] text-muted-foreground">
                                                        {resource.slotDurationMinutes}min · {confirmedCount} reservas
                                                    </p>
                                                </div>
                                                <Badge variant="outline" className={`text-[10px] ${rs.className}`}>
                                                    {rs.label}
                                                </Badge>
                                            </div>
                                        )
                                    })}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    )
}
