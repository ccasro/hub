"use client"

import {useEffect, useState} from "react"
import {MatchMap} from "@/components/match/match-map"
import {Badge} from "@/components/ui/badge"
import {Button} from "@/components/ui/button"
import {Input} from "@/components/ui/input"
import {Label} from "@/components/ui/label"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"
import {Slider} from "@/components/ui/slider"
import {ArrowLeft, ChevronDown, ChevronUp, Loader2, MapPin, Search, Users} from "lucide-react"
import type {MatchFormat, MatchSkillLevel, MatchSlotResult, UserProfile} from "@/types"
import {MatchSlotCard} from "@/components/match/match-slot-card"
import {useRouter} from "next/navigation";

interface CityDto {
    id: number
    name: string
    countryCode: string
    latitude: number
    longitude: number
}

interface Props { user: UserProfile }

const FORMAT_OPTIONS: { value: MatchFormat; label: string }[] = [
    { value: "ONE_VS_ONE", label: "1 vs 1" },
    { value: "TWO_VS_TWO", label: "2 vs 2" },
]

const SKILL_OPTIONS: { value: MatchSkillLevel; label: string }[] = [
    { value: "ANY",          label: "Cualquier nivel" },
    { value: "BEGINNER",     label: "Principiante" },
    { value: "INTERMEDIATE", label: "Intermedio" },
    { value: "ADVANCED",     label: "Avanzado" },
]

const DURATION_OPTIONS = [
    { value: 60,  label: "60 min" },
    { value: 90,  label: "90 min" },
    { value: 120, label: "120 min" },
    { value: 0,   label: "Cualquiera" },
]

function getTodayString() {
    return new Date().toISOString().split("T")[0]
}

function groupByVenue(results: MatchSlotResult[]) {
    const map = new Map<string, {
        venue: Pick<MatchSlotResult, "venueId" | "venueName" | "venueCity" | "venueLatitude" | "venueLongitude" | "distanceKm">
        slots: MatchSlotResult[]
    }>()
    for (const r of results) {
        if (!map.has(r.venueId)) {
            map.set(r.venueId, {
                venue: {
                    venueId: r.venueId,
                    venueName: r.venueName,
                    venueCity: r.venueCity,
                    venueLatitude: r.venueLatitude,
                    venueLongitude: r.venueLongitude,
                    distanceKm: r.distanceKm,
                },
                slots: [],
            })
        }
        map.get(r.venueId)!.slots.push(r)
    }
    return [...map.values()].sort((a, b) => a.venue.distanceKm - b.venue.distanceKm)
}

export function MatchSearchClient({ user }: Props) {
    const [cities, setCities]               = useState<CityDto[]>([])
    const [selectedCity, setSelectedCity]   = useState<CityDto | null>(null)
    const [radiusKm, setRadiusKm]           = useState(10)
    const [date, setDate]                   = useState(getTodayString())
    const [startTimeFrom, setStartTimeFrom] = useState("17:00")
    const [startTimeTo, setStartTimeTo]     = useState("22:00")
    const [duration, setDuration]           = useState(90)
    const [format, setFormat]               = useState<MatchFormat>("TWO_VS_TWO")
    const [skillLevel, setSkillLevel]       = useState<MatchSkillLevel>("ANY")
    const [results, setResults]             = useState<MatchSlotResult[] | null>(null)
    const [loading, setLoading]             = useState(false)
    const [error, setError]                 = useState<string | null>(null)
    const [selectedVenueId, setSelectedVenueId] = useState<string | null>(null)
    const [filtersOpen, setFiltersOpen]     = useState(true)
    const router = useRouter();

    useEffect(() => {
        fetch("/api/proxy/api/cities?countryCode=ES")
            .then(r => r.json())
            .then((data: CityDto[]) => {
                setCities(data)
                if (user.city) {
                    const match = data.find(c => c.name.toLowerCase() === user.city!.toLowerCase())
                    if (match) setSelectedCity(match)
                }
            })
            .catch(() => {})
    }, [user.city])

    const mapCenter: [number, number] = selectedCity
        ? [selectedCity.latitude, selectedCity.longitude]
        : [40.4168, -3.7038]

    const handleCityChange = (cityId: string) => {
        const city = cities.find(c => c.id.toString() === cityId) ?? null
        setSelectedCity(city)
        setResults(null)
        setSelectedVenueId(null)
    }

    const handleSearch = async () => {
        if (!selectedCity) { setError("Selecciona una ciudad"); return }
        setLoading(true)
        setError(null)
        setSelectedVenueId(null)
        try {
            const controller = new AbortController()
            const timeout = setTimeout(() => controller.abort(), 15000)

            const params = new URLSearchParams({
                lat:                 selectedCity.latitude.toString(),
                lng:                 selectedCity.longitude.toString(),
                radiusKm:            radiusKm.toString(),
                date,
                startTimeFrom:       startTimeFrom + ":00",
                startTimeTo:         startTimeTo + ":00",
                slotDurationMinutes: duration.toString(),
                format,
                skillLevel,
            })

            const res = await fetch(`/api/proxy/api/match/search?${params}`, { signal: controller.signal })
            clearTimeout(timeout)

            const data: MatchSlotResult[] = res.ok ? await res.json() : []

            const seen = new Set<string>()
            const combined = data.filter(s => {
                const key = `${s.resourceId}-${s.startTime}-${s.endTime}`
                if (seen.has(key)) return false
                seen.add(key)
                return true
            })

            setResults(combined.sort((a, b) =>
                a.distanceKm !== b.distanceKm
                    ? a.distanceKm - b.distanceKm
                    : a.startTime.localeCompare(b.startTime)
            ))
        } catch (e) {
            if (e instanceof Error && e.name === "AbortError") {
                setError("La búsqueda tardó demasiado. Intenta reducir el radio o cambiar los filtros.")
            } else {
                setError(e instanceof Error ? e.message : "Error buscando partidos")
            }
        } finally {
            setLoading(false)
        }
    }

    const venueGroups = results ? groupByVenue(results) : []
    const eligibleCount = results?.[0]?.eligiblePlayersNearby ?? 0

    const venueMarkers = venueGroups.map(({ venue, slots }) => ({
        venueId:       venue.venueId,
        venueName:     venue.venueName,
        venueCity:     venue.venueCity,
        venueLatitude: venue.venueLatitude,
        venueLongitude: venue.venueLongitude,
        distanceKm:    venue.distanceKm,
        slotCount:     slots.length,
    }))

    return (
        <div className="flex h-[calc(100vh-4rem)] flex-col lg:flex-row">

            {/* ── Left panel ── */}
            <div className="flex w-full flex-col overflow-y-auto lg:w-[380px] lg:shrink-0 lg:border-r lg:border-border/50">

                {/* Header */}
                <div className="border-b border-border/50 p-4">
                    <div className="flex items-center gap-2 mb-1">
                        <Button
                            variant="ghost"
                            size="icon"
                            onClick={() => router.back()}
                            className="h-8 w-8 text-muted-foreground hover:text-foreground"
                        >
                            <ArrowLeft className="h-4 w-4" />
                        </Button>
                        <h1 className="font-[var(--font-space-grotesk)] text-xl font-bold tracking-tight text-foreground">
                            Buscar Partido
                        </h1>
                    </div>
                    <p className="text-xs text-muted-foreground pl-10">
                        Encuentra pistas y oponentes cerca de ti
                    </p>
                </div>

                {/* Ciudad + Radio — always visible */}
                <div className="border-b border-border/50 p-4 flex flex-col gap-3">
                    <div className="flex items-center gap-2 rounded-lg border border-border/50 bg-secondary/30 px-3">
                        <MapPin className="h-4 w-4 shrink-0 text-primary" />
                        <Select
                            value={selectedCity?.id.toString() ?? ""}
                            onValueChange={handleCityChange}
                        >
                            <SelectTrigger className="h-10 border-0 bg-transparent px-0 text-sm text-foreground shadow-none focus:ring-0">
                                <SelectValue placeholder="Selecciona una ciudad..." />
                            </SelectTrigger>
                            <SelectContent className="border-border bg-card text-card-foreground">
                                {cities.map(c => (
                                    <SelectItem key={c.id} value={c.id.toString()}>{c.name}</SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>

                    <div className="flex items-center gap-3">
                        <span className="text-xs text-muted-foreground shrink-0">Radio</span>
                        <Slider
                            min={1} max={50} step={1}
                            value={[radiusKm]}
                            onValueChange={([v]) => setRadiusKm(v)}
                            className="flex-1"
                        />
                        <span className="w-12 text-right text-xs font-semibold text-foreground">{radiusKm} km</span>
                    </div>
                </div>

                {/* Filtros colapsables */}
                <div className="border-b border-border/50">
                    <button
                        onClick={() => setFiltersOpen(o => !o)}
                        className="flex w-full items-center justify-between px-4 py-3 text-xs font-semibold uppercase tracking-wide text-muted-foreground hover:text-foreground transition-colors"
                    >
                        Filtros avanzados
                        {filtersOpen
                            ? <ChevronUp className="h-3.5 w-3.5" />
                            : <ChevronDown className="h-3.5 w-3.5" />
                        }
                    </button>

                    {filtersOpen && (
                        <div className="flex flex-col gap-3 px-4 pb-4">
                            <div className="grid grid-cols-3 gap-2">
                                <div className="flex flex-col gap-1">
                                    <Label className="text-[10px] text-muted-foreground">Fecha</Label>
                                    <Input
                                        type="date" value={date} min={getTodayString()}
                                        onChange={e => setDate(e.target.value)}
                                        className="h-9 border-border/50 bg-secondary/30 text-xs text-foreground"
                                    />
                                </div>
                                <div className="flex flex-col gap-1">
                                    <Label className="text-[10px] text-muted-foreground">Desde</Label>
                                    <Input
                                        type="time" value={startTimeFrom}
                                        onChange={e => setStartTimeFrom(e.target.value)}
                                        className="h-9 border-border/50 bg-secondary/30 text-xs text-foreground"
                                    />
                                </div>
                                <div className="flex flex-col gap-1">
                                    <Label className="text-[10px] text-muted-foreground">Hasta</Label>
                                    <Input
                                        type="time" value={startTimeTo}
                                        onChange={e => setStartTimeTo(e.target.value)}
                                        className="h-9 border-border/50 bg-secondary/30 text-xs text-foreground"
                                    />
                                </div>
                            </div>

                            <div className="grid grid-cols-3 gap-2">
                                <div className="flex flex-col gap-1">
                                    <Label className="text-[10px] text-muted-foreground">Duración</Label>
                                    <Select value={duration.toString()} onValueChange={v => setDuration(Number(v))}>
                                        <SelectTrigger className="h-9 border-border/50 bg-secondary/30 text-xs text-foreground">
                                            <SelectValue />
                                        </SelectTrigger>
                                        <SelectContent className="border-border bg-card text-card-foreground">
                                            {DURATION_OPTIONS.map(d => (
                                                <SelectItem key={d.value} value={d.value.toString()}>{d.label}</SelectItem>
                                            ))}
                                        </SelectContent>
                                    </Select>
                                </div>
                                <div className="flex flex-col gap-1">
                                    <Label className="text-[10px] text-muted-foreground">Formato</Label>
                                    <div className="flex gap-1">
                                        {FORMAT_OPTIONS.map(f => (
                                            <button
                                                key={f.value}
                                                onClick={() => setFormat(f.value)}
                                                className={`flex h-9 flex-1 items-center justify-center rounded-lg border text-xs font-medium transition-colors ${
                                                    format === f.value
                                                        ? "border-primary/50 bg-primary/10 text-primary"
                                                        : "border-border/50 bg-secondary/30 text-muted-foreground hover:bg-secondary/50"
                                                }`}
                                            >
                                                {f.label}
                                            </button>
                                        ))}
                                    </div>
                                </div>
                                <div className="flex flex-col gap-1">
                                    <Label className="text-[10px] text-muted-foreground">Nivel</Label>
                                    <Select value={skillLevel} onValueChange={v => setSkillLevel(v as MatchSkillLevel)}>
                                        <SelectTrigger className="h-9 border-border/50 bg-secondary/30 text-xs text-foreground">
                                            <SelectValue />
                                        </SelectTrigger>
                                        <SelectContent className="border-border bg-card text-card-foreground">
                                            {SKILL_OPTIONS.map(s => (
                                                <SelectItem key={s.value} value={s.value}>{s.label}</SelectItem>
                                            ))}
                                        </SelectContent>
                                    </Select>
                                </div>
                            </div>
                        </div>
                    )}
                </div>

                {error && (
                    <div className="px-4 pt-3">
                        <p className="rounded-lg border border-destructive/20 bg-destructive/5 px-3 py-2 text-xs text-destructive">
                            {error}
                        </p>
                    </div>
                )}

                <div className="p-4">
                    <Button
                        onClick={handleSearch}
                        disabled={loading || !selectedCity}
                        className="h-10 w-full gap-2 bg-primary font-medium"
                    >
                        {loading
                            ? <><Loader2 className="h-4 w-4 animate-spin" />Buscando...</>
                            : <><Search className="h-4 w-4" />Buscar partidos</>
                        }
                    </Button>
                </div>

                {/* Results */}
                {results !== null && (
                    <div className="flex flex-col gap-2 border-t border-border/50 p-4">
                        <div className="flex items-center justify-between">
                            <p className="text-xs font-semibold text-foreground">
                                {venueGroups.length === 0
                                    ? "Sin resultados"
                                    : `${venueGroups.length} venue${venueGroups.length !== 1 ? "s" : ""} encontrado${venueGroups.length !== 1 ? "s" : ""}`
                                }
                            </p>
                            {eligibleCount > 0 && (
                                <Badge className="border-0 bg-emerald-500/10 text-[10px] text-emerald-400">
                                    <Users className="mr-1 h-2.5 w-2.5" />
                                    {eligibleCount} jugadores
                                </Badge>
                            )}
                        </div>

                        {venueGroups.map(({ venue, slots }) => (
                            <div key={venue.venueId}>
                                <button
                                    onClick={() => setSelectedVenueId(id => id === venue.venueId ? null : venue.venueId)}
                                    className={`w-full rounded-xl border p-3 text-left transition-colors ${
                                        selectedVenueId === venue.venueId
                                            ? "border-primary/40 bg-primary/5"
                                            : "border-border/50 bg-card hover:border-border/80"
                                    }`}
                                >
                                    <div className="flex items-center justify-between">
                                        <div>
                                            <p className="text-sm font-semibold text-foreground">{venue.venueName}</p>
                                            <p className="text-xs text-muted-foreground">
                                                {venue.venueCity} · {venue.distanceKm} km
                                            </p>
                                        </div>
                                        <div className="flex items-center gap-2">
                                            <Badge className="border-0 bg-secondary px-2 text-[10px] text-secondary-foreground">
                                                {slots.length} slot{slots.length !== 1 ? "s" : ""}
                                            </Badge>
                                            {selectedVenueId === venue.venueId
                                                ? <ChevronUp className="h-3.5 w-3.5 text-muted-foreground" />
                                                : <ChevronDown className="h-3.5 w-3.5 text-muted-foreground" />
                                            }
                                        </div>
                                    </div>
                                </button>

                                {selectedVenueId === venue.venueId && (
                                    <div className="mt-1 flex flex-col gap-2">
                                        {slots.map((slot, i) => (
                                            <MatchSlotCard
                                                key={`${slot.resourceId}-${slot.startTime}-${i}`}
                                                slot={slot}
                                                format={format}
                                                skillLevel={skillLevel}
                                                date={date}
                                                duration={duration || 90}
                                            />
                                        ))}
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {/* ── Right panel: map ── */}
            <div className="relative h-72 w-full flex-1 lg:h-auto">
                <MatchMap
                    center={mapCenter}
                    radiusKm={radiusKm}
                    venues={venueMarkers}
                    selectedVenueId={selectedVenueId}
                    onVenueClick={id => setSelectedVenueId(prev => prev === id ? null : id)}
                />

                {results !== null && venueGroups.length === 0 && (
                    <div className="pointer-events-none absolute inset-0 flex items-center justify-center">
                        <div className="rounded-xl border border-border/50 bg-card/90 px-6 py-4 text-center backdrop-blur-sm">
                            <p className="text-sm font-medium text-foreground">Sin pistas disponibles</p>
                            <p className="text-xs text-muted-foreground">
                                Prueba a aumentar el radio o cambiar la fecha
                            </p>
                        </div>
                    </div>
                )}
            </div>
        </div>
    )
}
