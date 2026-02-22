"use client";

import {useState} from "react";
import Link from "next/link";
import {DashboardNavbar} from "@/components/dashboard/dashboard-navbar";
import {VenueGrid} from "@/components/dashboard/venue-grid";
import {UpcomingBookings} from "@/components/dashboard/upcoming-bookings";
import {Button} from "@/components/ui/button";
import {Badge} from "@/components/ui/badge";
import {mockBookings, mockUser, mockVenues} from "@/lib/mock-data";
import {ArrowRight, CalendarCheck, MapPin, Sparkles, Trophy, Users, Zap,} from "lucide-react";

export default function DashboardPage() {


    const [selectedCity, setSelectedCity] = useState(mockUser.city ?? "Todas");
    const [searchQuery, setSearchQuery] = useState("");

    const activeVenues = mockVenues.filter((v) => v.status === "ACTIVE");
    const totalCourts = activeVenues.reduce(
        (sum, v) => sum + v.resourceCount,
        0
    );
    const cities = new Set(activeVenues.map((v) => v.city).filter(Boolean));

    const today = new Date().toISOString().split("T")[0];
    const upcomingBookings = mockBookings.filter(
        (b) => b.status === "CONFIRMED" && b.bookingDate >= today
    );

    const skillLabel =
        mockUser.skillLevel === "BEGINNER"
            ? "Principiante"
            : mockUser.skillLevel === "INTERMEDIATE"
                ? "Intermedio"
                : mockUser.skillLevel === "ADVANCED"
                    ? "Avanzado"
                    : "--";

    return (
        <div className="flex min-h-screen flex-col bg-background">
            <DashboardNavbar
                user={mockUser}
                selectedCity={selectedCity}
                onCityChange={setSelectedCity}
                searchQuery={searchQuery}
                onSearchChange={setSearchQuery}
                upcomingBookingsCount={upcomingBookings.length}
            />

            <main className="mx-auto w-full max-w-7xl flex-1 px-4 py-6 lg:px-6 lg:py-8">
                {/* Hero welcome section */}
                <section className="mb-8">
                    <div className="relative overflow-hidden rounded-2xl border border-border/50 bg-card p-6 lg:p-8">
                        {/* Decorative background glow */}
                        <div
                            className="pointer-events-none absolute -right-20 -top-20 h-64 w-64 rounded-full bg-primary/5 blur-3xl"/>
                        <div
                            className="pointer-events-none absolute -bottom-10 -left-10 h-40 w-40 rounded-full bg-primary/5 blur-3xl"/>

                        <div className="relative flex flex-col gap-6 lg:flex-row lg:items-center lg:justify-between">
                            <div>
                                <div className="flex items-center gap-2">
                                    <Badge
                                        variant="secondary"
                                        className="border-0 bg-primary/10 text-xs font-medium text-primary"
                                    >
                                        <Sparkles className="mr-1 h-3 w-3"/>
                                        {mockUser.preferredSport ?? "Deportista"}
                                    </Badge>
                                    <Badge
                                        variant="secondary"
                                        className="border-0 bg-secondary/50 text-xs font-medium text-muted-foreground"
                                    >
                                        {skillLabel}
                                    </Badge>
                                </div>
                                <h1 className="mt-3 font-[var(--font-space-grotesk)] text-3xl font-bold tracking-tight text-foreground lg:text-4xl">
                                    Hola, {mockUser.displayName?.split(" ")[0] ?? "Jugador"}
                                </h1>
                                <p className="mt-1.5 max-w-md text-sm leading-relaxed text-muted-foreground">
                                    Encuentra tu proxima pista, reserva al instante y sal a jugar.
                                </p>
                            </div>

                            {/* Quick actions */}
                            <div className="flex shrink-0 items-center gap-3">
                                <Link href="/dashboard/bookings">
                                    <Button
                                        variant="outline"
                                        className="gap-2 border-border/60 bg-secondary/30 text-foreground hover:border-primary/40 hover:bg-secondary/50"
                                    >
                                        <CalendarCheck className="h-4 w-4 text-primary"/>
                                        Mis Reservas
                                        {upcomingBookings.length > 0 && (
                                            <Badge
                                                className="ml-1 h-5 min-w-5 justify-center rounded-full border-0 bg-primary px-1.5 text-[10px] font-bold text-primary-foreground">
                                                {upcomingBookings.length}
                                            </Badge>
                                        )}
                                    </Button>
                                </Link>
                                <Button
                                    className="gap-2 bg-primary font-medium text-primary-foreground hover:bg-primary/90">
                                    <Zap className="h-4 w-4"/>
                                    Reserva rapida
                                </Button>
                            </div>
                        </div>

                        {/* Stats row */}
                        <div className="relative mt-6 grid grid-cols-2 gap-3 sm:grid-cols-4">
                            <div
                                className="flex items-center gap-3 rounded-xl border border-border/30 bg-background/50 p-3.5">
                                <div
                                    className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-primary/10">
                                    <MapPin className="h-4 w-4 text-primary"/>
                                </div>
                                <div>
                                    <p className="text-lg font-bold leading-none text-foreground">
                                        {activeVenues.length}
                                    </p>
                                    <p className="mt-1 text-[11px] text-muted-foreground">
                                        Venues activos
                                    </p>
                                </div>
                            </div>
                            <div
                                className="flex items-center gap-3 rounded-xl border border-border/30 bg-background/50 p-3.5">
                                <div
                                    className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-primary/10">
                                    <CalendarCheck className="h-4 w-4 text-primary"/>
                                </div>
                                <div>
                                    <p className="text-lg font-bold leading-none text-foreground">
                                        {totalCourts}
                                    </p>
                                    <p className="mt-1 text-[11px] text-muted-foreground">
                                        Pistas disponibles
                                    </p>
                                </div>
                            </div>
                            <div
                                className="flex items-center gap-3 rounded-xl border border-border/30 bg-background/50 p-3.5">
                                <div
                                    className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-primary/10">
                                    <Users className="h-4 w-4 text-primary"/>
                                </div>
                                <div>
                                    <p className="text-lg font-bold leading-none text-foreground">
                                        {cities.size}
                                    </p>
                                    <p className="mt-1 text-[11px] text-muted-foreground">
                                        Ciudades
                                    </p>
                                </div>
                            </div>
                            <div
                                className="flex items-center gap-3 rounded-xl border border-border/30 bg-background/50 p-3.5">
                                <div
                                    className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-primary/10">
                                    <Trophy className="h-4 w-4 text-primary"/>
                                </div>
                                <div>
                                    <p className="text-lg font-bold leading-none text-foreground">
                                        {upcomingBookings.length}
                                    </p>
                                    <p className="mt-1 text-[11px] text-muted-foreground">
                                        Reservas activas
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                </section>

                {/* Two-column: Upcoming Bookings + Venues */}
                <div className="flex flex-col gap-8 lg:flex-row">
                    {/* Sidebar: Upcoming Bookings */}
                    <aside className="shrink-0 lg:w-80 xl:w-96">
                        <div className="lg:sticky lg:top-[calc(4rem+1.5rem)]">
                            <UpcomingBookings bookings={mockBookings}/>

                            {/* Matching teaser */}
                            <div className="mt-4 rounded-xl border border-dashed border-primary/20 bg-primary/5 p-5">
                                <div className="flex items-center gap-2">
                                    <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary/10">
                                        <Users className="h-4 w-4 text-primary"/>
                                    </div>
                                    <h3 className="font-[var(--font-space-grotesk)] text-sm font-bold text-foreground">
                                        Buscar jugadores
                                    </h3>
                                </div>
                                <p className="mt-2 text-xs leading-relaxed text-muted-foreground">
                                    Encuentra companeros de juego de tu nivel en tu zona. Matching
                                    inteligente proximamente.
                                </p>
                                <Button
                                    variant="ghost"
                                    size="sm"
                                    className="mt-3 gap-1.5 px-0 text-xs text-primary hover:bg-transparent hover:text-primary/80"
                                    disabled
                                >
                                    Proximamente
                                    <ArrowRight className="h-3 w-3"/>
                                </Button>
                            </div>
                        </div>
                    </aside>

                    {/* Main: Venue Grid */}
                    <div className="flex-1 min-w-0">
                        <VenueGrid
                            venues={mockVenues}
                            selectedCity={selectedCity}
                            searchQuery={searchQuery}
                        />
                    </div>
                </div>
            </main>

            {/* Footer */}
            <footer className="mt-8 border-t border-border/50 bg-background/80 py-6 backdrop-blur-xl">
                <div className="mx-auto max-w-7xl px-4 lg:px-6">
                    <div className="flex flex-col items-center justify-between gap-3 sm:flex-row">
                        <p className="text-xs text-muted-foreground">
                            SportsHub {new Date().getFullYear()} - Reserva, Juega, Compite
                        </p>
                        <div className="flex items-center gap-4">
                            <a
                                href="/terms"
                                className="text-xs text-muted-foreground transition-colors hover:text-foreground"
                            >
                                Terminos
                            </a>
                            <a
                                href="/privacy"
                                className="text-xs text-muted-foreground transition-colors hover:text-foreground"
                            >
                                Privacidad
                            </a>
                            <a
                                href="/help"
                                className="text-xs text-muted-foreground transition-colors hover:text-foreground"
                            >
                                Ayuda
                            </a>
                        </div>
                    </div>
                </div>
            </footer>
        </div>
    );
}
