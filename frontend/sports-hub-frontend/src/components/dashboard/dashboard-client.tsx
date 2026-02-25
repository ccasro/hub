"use client"

import {useMemo, useState} from "react"
import Link from "next/link"
import {DashboardNavbar} from "@/components/dashboard/dashboard-navbar"
import {VenueGrid} from "@/components/dashboard/venue-grid"
import {UpcomingBookings} from "@/components/dashboard/upcoming-bookings"
import {Button} from "@/components/ui/button"
import {Badge} from "@/components/ui/badge"
import {Booking, UserProfile, Venue} from "@/types"
import {ArrowRight, CalendarCheck, MapPin, Sparkles, Trophy, Users, Zap} from "lucide-react"

interface Props {
    user: UserProfile
    venues: Venue[]
    bookings: Booking[]
}

const SKILL_LABELS: Record<string, string> = {
    BEGINNER: "Principiante",
    INTERMEDIATE: "Intermedio",
    ADVANCED: "Avanzado",
}

const SPORT_LABELS: Record<string, string> = {
    PADEL: "Padel",
    TENNIS: "Tenis",
    SQUASH: "Squash",
    BADMINTON: "Badminton",
}

export function DashboardClient({ user, venues, bookings }: Props) {
    const [selectedCity, setSelectedCity] = useState(user.city ?? "Todas")
    const [searchQuery, setSearchQuery] = useState("")

    const today = useMemo(() => new Date().toISOString().split("T")[0], [])

    const activeVenues = useMemo(
        () => venues.filter((v) => v.status === "ACTIVE"),
        [venues]
    )

    const cities = useMemo(
        () => new Set(activeVenues.map((v) => v.city).filter((c): c is string => c !== null)),
        [activeVenues]
    )

    const totalCourts = useMemo(
        () => activeVenues.reduce((sum, v) => sum + (v.resourceCount ?? 0), 0),
        [activeVenues]
    )
    
    const upcomingBookings = useMemo(
        () => bookings.filter((b) => b.status === "CONFIRMED" && b.bookingDate >= today),
        [bookings, today]
    )

    const skillLabel = SKILL_LABELS[user.skillLevel ?? ""] ?? "--"
    const sportLabel = SPORT_LABELS[user.preferredSport ?? ""] ?? "Deportista"

    return (
        <div className="flex min-h-screen flex-col bg-background">
            <DashboardNavbar
                user={user}
                selectedCity={selectedCity}
                onCityChange={setSelectedCity}
                searchQuery={searchQuery}
                onSearchChange={setSearchQuery}
                upcomingBookingsCount={upcomingBookings.length}
                cities={["Todas", ...Array.from(cities)]}
            />

            <main className="mx-auto w-full max-w-7xl flex-1 px-4 py-6 lg:px-6 lg:py-8">
                {/* Hero welcome section */}
                <section className="mb-8">
                    <div className="relative overflow-hidden rounded-2xl border border-border/50 bg-card p-6 lg:p-8">
                        <div className="pointer-events-none absolute -right-20 -top-20 h-64 w-64 rounded-full bg-primary/5 blur-3xl" />
                        <div className="pointer-events-none absolute -bottom-10 -left-10 h-40 w-40 rounded-full bg-primary/5 blur-3xl" />

                        <div className="relative flex flex-col gap-6 lg:flex-row lg:items-center lg:justify-between">
                            <div>
                                <div className="flex items-center gap-2">
                                    <Badge variant="secondary" className="border-0 bg-primary/10 text-xs font-medium text-primary">
                                        <Sparkles className="mr-1 h-3 w-3" />
                                        {sportLabel}
                                    </Badge>
                                    <Badge variant="secondary" className="border-0 bg-secondary/50 text-xs font-medium text-muted-foreground">
                                        {skillLabel}
                                    </Badge>
                                </div>
                                <h1 className="mt-3 font-[var(--font-space-grotesk)] text-3xl font-bold tracking-tight text-foreground lg:text-4xl">
                                    Hola, {user.displayName?.split(" ")[0] ?? "Jugador"}
                                </h1>
                                <p className="mt-1.5 max-w-md text-sm leading-relaxed text-muted-foreground">
                                    Encuentra tu proxima pista, reserva al instante y sal a jugar.
                                </p>
                            </div>

                            <div className="flex shrink-0 items-center gap-3">
                                <Link href="/dashboard/bookings">
                                    <Button variant="outline" className="gap-2 border-border/60 bg-secondary/30 text-foreground hover:border-primary/40 hover:bg-secondary/50">
                                        <CalendarCheck className="h-4 w-4 text-primary" />
                                        Mis Reservas
                                        {upcomingBookings.length > 0 && (
                                            <Badge className="ml-1 h-5 min-w-5 justify-center rounded-full border-0 bg-primary px-1.5 text-[10px] font-bold text-primary-foreground">
                                                {upcomingBookings.length}
                                            </Badge>
                                        )}
                                    </Button>
                                </Link>
                                <Button className="gap-2 bg-primary font-medium text-primary-foreground hover:bg-primary/90">
                                    <Zap className="h-4 w-4" />
                                    Reserva rapida
                                </Button>
                            </div>
                        </div>

                        {/* Stats */}
                        <div className="relative mt-6 grid grid-cols-2 gap-3 sm:grid-cols-4">
                            {[
                                { icon: MapPin,        value: activeVenues.length,     label: "Venues activos" },
                                { icon: CalendarCheck, value: totalCourts,             label: "Pistas disponibles" },
                                { icon: Users,         value: cities.size,             label: "Ciudades" },
                                { icon: Trophy,        value: upcomingBookings.length, label: "Reservas activas" },
                            ].map(({ icon: Icon, value, label }) => (
                                <div key={label} className="flex items-center gap-3 rounded-xl border border-border/30 bg-background/50 p-3.5">
                                    <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-primary/10">
                                        <Icon className="h-4 w-4 text-primary" />
                                    </div>
                                    <div>
                                        <p className="text-lg font-bold leading-none text-foreground">{value}</p>
                                        <p className="mt-1 text-[11px] text-muted-foreground">{label}</p>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </section>

                <div className="flex flex-col gap-8 lg:flex-row">
                    <aside className="shrink-0 lg:w-80 xl:w-96">
                        <div className="lg:sticky lg:top-[calc(4rem+1.5rem)]">
                            <UpcomingBookings bookings={bookings} />
                            <div className="mt-4 rounded-xl border border-dashed border-primary/20 bg-primary/5 p-5">
                                <div className="flex items-center gap-2">
                                    <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary/10">
                                        <Users className="h-4 w-4 text-primary" />
                                    </div>
                                    <h3 className="font-[var(--font-space-grotesk)] text-sm font-bold text-foreground">
                                        Buscar jugadores
                                    </h3>
                                </div>
                                <p className="mt-2 text-xs leading-relaxed text-muted-foreground">
                                    Encuentra companeros de juego de tu nivel en tu zona. Matching inteligente proximamente.
                                </p>
                                <Button variant="ghost" size="sm" className="mt-3 gap-1.5 px-0 text-xs text-primary hover:bg-transparent" disabled>
                                    Proximamente
                                    <ArrowRight className="h-3 w-3" />
                                </Button>
                            </div>
                        </div>
                    </aside>

                    <div className="flex-1 min-w-0">
                        <VenueGrid
                            venues={venues}
                            selectedCity={selectedCity}
                            searchQuery={searchQuery}
                        />
                    </div>
                </div>
            </main>

            <footer className="mt-8 border-t border-border/50 bg-background/80 py-6 backdrop-blur-xl">
                <div className="mx-auto max-w-7xl px-4 lg:px-6">
                    <div className="flex flex-col items-center justify-between gap-3 sm:flex-row">
                        <p className="text-xs text-muted-foreground">
                            SportsHub {new Date().getFullYear()} - Reserva, Juega, Compite
                        </p>
                        <div className="flex items-center gap-4">
                            {["Terminos", "Privacidad", "Ayuda"].map((item) => (
                                <a key={item} href={`/${item.toLowerCase()}`} className="text-xs text-muted-foreground transition-colors hover:text-foreground">
                                    {item}
                                </a>
                            ))}
                        </div>
                    </div>
                </div>
            </footer>
        </div>
    )
}