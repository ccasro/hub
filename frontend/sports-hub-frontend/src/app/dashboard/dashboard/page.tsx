"use client";

import React, {useEffect, useMemo, useState} from "react";
import Link from "next/link";
import {DashboardNavbar} from "@/components/dashboard/dashboard-navbar";
import {VenueGrid} from "@/components/dashboard/venue-grid";
import {UpcomingBookings} from "@/components/dashboard/upcoming-bookings";
import {Button} from "@/components/ui/button";
import {Badge} from "@/components/ui/badge";
import {ArrowRight, CalendarCheck, MapPin, Sparkles, Trophy, Users, Zap,} from "lucide-react";
import type {UserProfile} from "@/types";

type UUID = string;

type VenueImageResponse = {
  id: UUID;
  url: string;
  displayOrder: number;
};

type VenueResponse = {
  id: UUID;
  ownerId: UUID;
  name: string;
  description?: string | null;
  street: string;
  city: string;
  country: string;
  postalCode?: string | null;
  latitude: number;
  longitude: number;
  status: string;
  rejectReason?: string | null;
  images?: VenueImageResponse[];
  createdAt: string;
  updatedAt: string;
};

type BookingResponse = {
  id: UUID;
  resourceId: UUID;
  playerId: UUID;
  bookingDate: string; // YYYY-MM-DD
  startTime: string; // "HH:mm:ss" or "HH:mm"
  endTime: string;
  pricePaid: number;
  currency: string;
  status: string; // CONFIRMED, CANCELLED, ...
  paymentStatus: string; // PAID, PENDING, ...
  cancelReason?: string | null;
};

type ResourceResponse = {
  id: UUID;
  venueId: UUID;
  name: string;
  status: string;
};

async function apiRequest<T>(path: string, init?: RequestInit): Promise<T> {
  const proxiedPath = `/api/proxy${path}`;

  const res = await fetch(proxiedPath, {
    headers: {
      Accept: "application/json",
      ...(init?.body ? { "Content-Type": "application/json" } : {}),
      ...(init?.headers ?? {}),
    },
    ...init,
  });

  if (!res.ok) {
    const txt = await res.text().catch(() => "");
    throw new Error(
        `${init?.method ?? "GET"} ${path} -> ${res.status} ${res.statusText} ${txt}`
    );
  }

  const contentType = res.headers.get("content-type") || "";
  if (!contentType.includes("application/json")) return undefined as T;
  return (await res.json()) as T;
}

function prettySport(sport: UserProfile["preferredSport"]): string {
  if (!sport) return "Deportista";
  return sport === "PADEL"
      ? "Padel"
      : sport === "TENNIS"
          ? "Tenis"
          : sport === "SQUASH"
              ? "Squash"
              : sport === "BADMINTON"
                  ? "Badminton"
              : String(sport);
}

const SKILL_LABEL: Record<NonNullable<UserProfile["skillLevel"]>, string> = {
  BEGINNER: "Principiante",
  INTERMEDIATE: "Intermedio",
  ADVANCED: "Avanzado",
};

export default function DashboardPage() {
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  const [user, setUser] = useState<UserProfile | null>(null);
  const [venues, setVenues] = useState<VenueResponse[]>([]);
  const [bookings, setBookings] = useState<BookingResponse[]>([]);

  const [selectedCity, setSelectedCity] = useState("Todas");
  const [searchQuery, setSearchQuery] = useState("");

  // Cargar datos reales
  useEffect(() => {
    let cancelled = false;

    async function load() {
      setLoading(true);
      setErrorMsg(null);

      try {
        const [me, activeVenues, myBookings] = await Promise.all([
          apiRequest<UserProfile>("/api/me"),
          apiRequest<VenueResponse[]>("/api/venues"),
          apiRequest<BookingResponse[]>("/api/bookings/my"),
        ]);

        if (cancelled) return;

        setUser(me);
        setVenues(activeVenues);
        setBookings(myBookings);

        // Inicializa ciudad desde perfil si la tienes
        const initialCity = (me.city ?? "").trim();
        setSelectedCity(initialCity || "Todas");
      } catch (e) {
        console.error(e);
        if (!cancelled) setErrorMsg(e instanceof Error ? e.message : "Error cargando dashboard");
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    load();
    return () => {
      cancelled = true;
    };
  }, []);

  const today = useMemo(() => new Date().toISOString().split("T")[0], []);

  const upcomingBookings = useMemo(() => {
    return bookings.filter(
        (b) => b.status === "CONFIRMED" && b.bookingDate >= today
    );
  }, [bookings, today]);

  const cities = useMemo(() => {
    const set = new Set<string>();
    for (const v of venues) {
      const c = (v.city ?? "").trim();
      if (c) set.add(c);
    }
    return set;
  }, [venues]);

  // ⚠️ Total courts: no viene en VenueResponse.
  // Opción A (rápida): mostrar "—" o 0
  // Opción B (real): contar recursos por venue (requiere N requests).
  // Te dejo la B implementada, pero puedes desactivarla si no te interesa.
  const [resourceCounts, setResourceCounts] = useState<Record<string, number>>({});

  useEffect(() => {
    let cancelled = false;

    async function loadCounts() {
      if (venues.length === 0) return;

      try {
        const entries = await Promise.all(
            venues.map(async (v) => {
              const resources = await apiRequest<ResourceResponse[]>(
                  `/api/venues/${v.id}/resources`
              );
              const active = resources.filter((r) => r.status === "ACTIVE").length;
              return [v.id, active] as const;
            })
        );

        if (cancelled) return;

        const map: Record<string, number> = {};
        for (const [id, cnt] of entries) map[id] = cnt;
        setResourceCounts(map);
      } catch (e) {
        // No bloqueamos dashboard si esto falla
        console.warn("No se pudo cargar resource counts", e);
      }
    }

    loadCounts();
    return () => {
      cancelled = true;
    };
  }, [venues]);

  const totalCourts = useMemo(() => {
    return venues.reduce((sum, v) => sum + (resourceCounts[v.id] ?? 0), 0);
  }, [venues, resourceCounts]);

  const skillLabel = user?.skillLevel ? SKILL_LABEL[user.skillLevel] : "--";

  // “Venues” para el grid: si tu VenueGrid esperaba resourceCount,
  // se lo añadimos sin romper el tipo (como any NO, usamos intersección local).
  type VenueForGrid = VenueResponse & { resourceCount?: number };
  const venuesForGrid: VenueForGrid[] = useMemo(() => {
    return venues.map((v) => ({
      ...v,
      resourceCount: resourceCounts[v.id] ?? 0,
    }));
  }, [venues, resourceCounts]);

  // “Bookings” para UpcomingBookings: si esperaba venueName/resourceName (mock),
  // añadimos placeholders para que no pete.
  const bookingsForUpcoming = useMemo(() => {
    return bookings.map((b) => ({
      ...b,
      venueName: "—",
      resourceName: "—",
    }));
  }, [bookings]);

  // Navbar user fallback
  const navbarUser: UserProfile = user ?? {
    id: "00000000-0000-0000-0000-000000000000",
    email: "—",
    displayName: "Cargando…",
    description: null,
    phoneNumber: null,
    avatarUrl: null,
    role: "PLAYER",
    ownerRequestStatus: null,
    preferredSport: null,
    skillLevel: null,
    city: "",
    countryCode: null,
    onboardingCompleted: false,
    lastLoginAt: new Date().toISOString(),
  };

  return (
      <div className="flex min-h-screen flex-col bg-background">
        <DashboardNavbar
            user={navbarUser}
            selectedCity={selectedCity}
            onCityChange={setSelectedCity}
            searchQuery={searchQuery}
            onSearchChange={setSearchQuery}
            upcomingBookingsCount={upcomingBookings.length}
            cities={["Todas", ...Array.from(cities)]}
        />

        <main className="mx-auto w-full max-w-7xl flex-1 px-4 py-6 lg:px-6 lg:py-8">
          {errorMsg && (
              <div className="mb-6 rounded-xl border border-destructive/30 bg-destructive/5 p-4 text-sm text-destructive">
                {errorMsg}
              </div>
          )}

          {/* Hero welcome section */}
          <section className="mb-8">
            <div className="relative overflow-hidden rounded-2xl border border-border/50 bg-card p-6 lg:p-8">
              <div className="pointer-events-none absolute -right-20 -top-20 h-64 w-64 rounded-full bg-primary/5 blur-3xl" />
              <div className="pointer-events-none absolute -bottom-10 -left-10 h-40 w-40 rounded-full bg-primary/5 blur-3xl" />

              <div className="relative flex flex-col gap-6 lg:flex-row lg:items-center lg:justify-between">
                <div>
                  <div className="flex items-center gap-2">
                    <Badge
                        variant="secondary"
                        className="border-0 bg-primary/10 text-xs font-medium text-primary"
                    >
                      <Sparkles className="mr-1 h-3 w-3" />
                      {prettySport(user?.preferredSport ?? null)}
                    </Badge>
                    <Badge
                        variant="secondary"
                        className="border-0 bg-secondary/50 text-xs font-medium text-muted-foreground"
                    >
                      {skillLabel}
                    </Badge>
                  </div>

                  <h1 className="mt-3 font-[var(--font-space-grotesk)] text-3xl font-bold tracking-tight text-foreground lg:text-4xl">
                    Hola, {(user?.displayName ?? "Jugador").split(" ")[0]}
                  </h1>
                  <p className="mt-1.5 max-w-md text-sm leading-relaxed text-muted-foreground">
                    Encuentra tu proxima pista, reserva al instante y sal a jugar.
                  </p>
                </div>

                <div className="flex shrink-0 items-center gap-3">
                  <Link href="/dashboard/bookings">
                    <Button
                        variant="outline"
                        className="gap-2 border-border/60 bg-secondary/30 text-foreground hover:border-primary/40 hover:bg-secondary/50"
                        disabled={loading}
                    >
                      <CalendarCheck className="h-4 w-4 text-primary" />
                      Mis Reservas
                      {upcomingBookings.length > 0 && (
                          <Badge className="ml-1 h-5 min-w-5 justify-center rounded-full border-0 bg-primary px-1.5 text-[10px] font-bold text-primary-foreground">
                            {upcomingBookings.length}
                          </Badge>
                      )}
                    </Button>
                  </Link>

                  <Button
                      className="gap-2 bg-primary font-medium text-primary-foreground hover:bg-primary/90"
                      disabled={loading}
                  >
                    <Zap className="h-4 w-4" />
                    Reserva rapida
                  </Button>
                </div>
              </div>

              <div className="relative mt-6 grid grid-cols-2 gap-3 sm:grid-cols-4">
                <div className="flex items-center gap-3 rounded-xl border border-border/30 bg-background/50 p-3.5">
                  <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-primary/10">
                    <MapPin className="h-4 w-4 text-primary" />
                  </div>
                  <div>
                    <p className="text-lg font-bold leading-none text-foreground">
                      {venues.length}
                    </p>
                    <p className="mt-1 text-[11px] text-muted-foreground">
                      Venues activos
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-3 rounded-xl border border-border/30 bg-background/50 p-3.5">
                  <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-primary/10">
                    <CalendarCheck className="h-4 w-4 text-primary" />
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

                <div className="flex items-center gap-3 rounded-xl border border-border/30 bg-background/50 p-3.5">
                  <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-primary/10">
                    <Users className="h-4 w-4 text-primary" />
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

                <div className="flex items-center gap-3 rounded-xl border border-border/30 bg-background/50 p-3.5">
                  <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-primary/10">
                    <Trophy className="h-4 w-4 text-primary" />
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

          <div className="flex flex-col gap-8 lg:flex-row">
            <aside className="shrink-0 lg:w-80 xl:w-96">
              <div className="lg:sticky lg:top-[calc(4rem+1.5rem)]">
                {/* OJO: UpcomingBookings recibe bookings; le pasamos reales con placeholders */}
                <UpcomingBookings bookings={bookingsForUpcoming as unknown as never} />

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
                    <ArrowRight className="h-3 w-3" />
                  </Button>
                </div>
              </div>
            </aside>

            <div className="flex-1 min-w-0">
              <VenueGrid
                  venues={venuesForGrid as unknown as never}
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