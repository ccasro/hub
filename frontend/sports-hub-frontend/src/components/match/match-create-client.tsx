"use client"

import {useEffect, useState} from "react"
import {useRouter, useSearchParams} from "next/navigation"
import {Badge} from "@/components/ui/badge"
import {Button} from "@/components/ui/button"
import {Card, CardContent} from "@/components/ui/card"
import {Label} from "@/components/ui/label"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"
import {Textarea} from "@/components/ui/textarea"
import {ArrowLeft, Building2, Calendar, Clock, CreditCard, Loader2, MapPin, Send, Swords, Users,} from "lucide-react"
import type {MatchFormat, MatchSkillLevel, UserProfile} from "@/types"

interface CityDto {
    id: number
    name: string
    countryCode: string
    latitude: number
    longitude: number
}

interface Props { user: UserProfile }

const FORMAT_LABELS: Record<MatchFormat, string> = {
    ONE_VS_ONE: "1 vs 1",
    TWO_VS_TWO: "2 vs 2",
}

const SKILL_LABELS: Record<MatchSkillLevel, string> = {
    BEGINNER:     "Principiante",
    INTERMEDIATE: "Intermedio",
    ADVANCED:     "Avanzado",
    ANY:          "Cualquier nivel",
}

const SKILL_OPTIONS: { value: MatchSkillLevel; label: string }[] = [
    { value: "ANY",          label: "Cualquier nivel" },
    { value: "BEGINNER",     label: "Principiante" },
    { value: "INTERMEDIATE", label: "Intermedio" },
    { value: "ADVANCED",     label: "Avanzado" },
]

export function MatchCreateClient({ user }: Props) {
    const router      = useRouter()
    const params      = useSearchParams()

    const resourceId   = params.get("resourceId")   ?? ""
    const resourceName = params.get("resourceName") ?? "Pista"
    const venueName    = params.get("venueName")    ?? "Club"
    const date         = params.get("date")         ?? ""
    const startTime    = params.get("startTime")    ?? ""
    const duration     = parseInt(params.get("duration") ?? "90")

    const [format, setFormat]         = useState<MatchFormat>(
        (params.get("format") as MatchFormat) ?? "TWO_VS_TWO"
    )
    const [skillLevel, setSkillLevel] = useState<MatchSkillLevel>(
        (params.get("skillLevel") as MatchSkillLevel) ?? "ANY"
    )
    const [customMessage, setCustomMessage] = useState("")
    const [searchRadiusKm, setSearchRadiusKm] = useState(10)

    const [cities, setCities]             = useState<CityDto[]>([])
    const [selectedCity, setSelectedCity] = useState<CityDto | null>(null)

    const [loading, setLoading]         = useState(false)
    const [error, setError]             = useState<string | null>(null)
    const [pendingMatch, setPendingMatch] = useState<{id: string; pricePerPlayer: number} | null>(null)
    const [paying, setPaying]           = useState(false)

    // El backend requiere mínimo 48h de antelación para crear un partido
    const tooSoon = date && startTime
        ? (new Date(`${date}T${startTime}`).getTime() - Date.now()) / 3_600_000 < 48
        : false

    const endTime = (() => {
        if (!startTime) return ""
        const [h, m] = startTime.split(":").map(Number)
        const total = h * 60 + m + duration
        return `${String(Math.floor(total / 60)).padStart(2, "0")}:${String(total % 60).padStart(2, "0")}`
    })()
    
    useEffect(() => {
        fetch("/api/proxy/api/cities?countryCode=ES")
            .then((r) => r.json())
            .then((data: CityDto[]) => {
                setCities(data)
                if (user.city) {
                    const match = data.find(
                        (c) => c.name.toLowerCase() === user.city!.toLowerCase()
                    )
                    if (match) setSelectedCity(match)
                }
            })
            .catch(() => {})
    }, [user.city])

    const handleCreate = async () => {
        if (!selectedCity) { setError("Selecciona una ciudad de búsqueda"); return }
        if (!resourceId)   { setError("Datos del slot incompletos"); return }

        setLoading(true)
        setError(null)

        try {
            const res = await fetch("/api/proxy/api/match/requests", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    resourceId,
                    bookingDate:          date,
                    startTime:            startTime + ":00",
                    slotDurationMinutes:  duration,
                    format,
                    skillLevel,
                    customMessage:        customMessage.trim() || null,
                    searchLat:            selectedCity.latitude,
                    searchLng:            selectedCity.longitude,
                    searchRadiusKm,
                }),
            })

            if (!res.ok) {
                const body = await res.json().catch(() => null)
                throw new Error(body?.detail || `Error ${res.status}`)
            }

            const data = await res.json()
            if (data.status === "AWAITING_ORGANIZER_PAYMENT") {
                setPendingMatch({ id: data.id, pricePerPlayer: data.pricePerPlayer })
            } else {
                router.push(`/match/${data.id}`)
            }
        } catch (e) {
            setError(e instanceof Error ? e.message : "Error creando el partido")
        } finally {
            setLoading(false)
        }
    }

    const handleConfirmPayment = async () => {
        if (!pendingMatch) return
        setPaying(true)
        setError(null)
        try {
            const res = await fetch(`/api/proxy/api/match/requests/${pendingMatch.id}/confirm-payment`, {
                method: "POST",
            })
            if (!res.ok) {
                const body = await res.json().catch(() => null)
                throw new Error(body?.detail || `Error ${res.status}`)
            }
            router.push(`/match/${pendingMatch.id}`)
        } catch (e) {
            setError(e instanceof Error ? e.message : "Error confirmando el pago")
        } finally {
            setPaying(false)
        }
    }

    if (pendingMatch) {
        return (
            <div className="mx-auto flex max-w-2xl flex-col gap-6 p-4 lg:p-8">
                <div className="flex items-center gap-3">
                    <div>
                        <h1 className="font-[var(--font-space-grotesk)] text-2xl font-bold tracking-tight text-foreground">
                            Confirmar pago
                        </h1>
                        <p className="text-sm text-muted-foreground">
                            Paga tu parte para abrir el partido e invitar jugadores.
                        </p>
                    </div>
                </div>

                <Card className="border-border/50 bg-card">
                    <CardContent className="flex flex-col gap-4 p-5">
                        <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">
                            Resumen del pago
                        </p>
                        <div className="flex items-center justify-between rounded-lg border border-border/50 bg-secondary/30 px-4 py-3">
                            <span className="text-sm text-muted-foreground">Tu parte (organizador)</span>
                            <span className="text-lg font-bold text-foreground">
                                €{pendingMatch.pricePerPlayer?.toFixed(2) ?? "—"}
                            </span>
                        </div>
                        <p className="text-xs text-muted-foreground">
                            Tienes 30 minutos para completar el pago. Si no pagas, el slot se liberará automáticamente.
                        </p>
                    </CardContent>
                </Card>

                {error && (
                    <p className="rounded-lg border border-destructive/20 bg-destructive/5 px-3 py-2 text-xs text-destructive">
                        {error}
                    </p>
                )}

                <Button
                    onClick={handleConfirmPayment}
                    disabled={paying}
                    className="h-12 gap-2 bg-primary text-base font-semibold"
                >
                    {paying
                        ? <><Loader2 className="h-4 w-4 animate-spin" />Procesando pago...</>
                        : <><CreditCard className="h-4 w-4" />Pagar y abrir el partido</>
                    }
                </Button>

                <button
                    onClick={() => router.push(`/match/${pendingMatch.id}`)}
                    className="text-center text-xs text-muted-foreground underline-offset-2 hover:underline"
                >
                    Pagar más tarde (el partido se cancelará en 30 min si no pagas)
                </button>
            </div>
        )
    }

    return (
        <div className="mx-auto flex max-w-2xl flex-col gap-6 p-4 lg:p-8">

            {/* Header */}
            <div className="flex items-center gap-3">
                <Button
                    variant="ghost"
                    size="icon"
                    onClick={() => router.back()}
                    className="h-9 w-9 text-muted-foreground hover:text-foreground"
                >
                    <ArrowLeft className="h-4 w-4" />
                </Button>
                <div>
                    <h1 className="font-[var(--font-space-grotesk)] text-2xl font-bold tracking-tight text-foreground">
                        Crear partido
                    </h1>
                    <p className="text-sm text-muted-foreground">
                        Bloquea la pista e invita jugadores de tu zona.
                    </p>
                </div>
            </div>

            {/* Slot summary */}
            <Card className="border-border/50 bg-card">
                <CardContent className="p-5">
                    <p className="mb-3 text-xs font-semibold uppercase tracking-wide text-muted-foreground">
                        Slot seleccionado
                    </p>
                    <div className="flex flex-col gap-2">
                        <div className="flex items-center gap-2 text-sm text-foreground">
                            <Swords className="h-4 w-4 text-primary" />
                            <span className="font-medium">{resourceName}</span>
                        </div>
                        <div className="flex items-center gap-2 text-sm text-muted-foreground">
                            <Building2 className="h-4 w-4" />
                            {venueName}
                        </div>
                        <div className="flex items-center gap-4 text-sm text-muted-foreground">
                            <span className="flex items-center gap-1.5">
                                <Calendar className="h-4 w-4" />
                                {date}
                            </span>
                            <span className="flex items-center gap-1.5">
                                <Clock className="h-4 w-4" />
                                {startTime} — {endTime}
                            </span>
                        </div>
                    </div>
                </CardContent>
            </Card>

            {/* Match config */}
            <Card className="border-border/50 bg-card">
                <CardContent className="flex flex-col gap-5 p-5">
                    <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">
                        Configuración del partido
                    </p>

                    {/* Formato */}
                    <div className="flex flex-col gap-1.5">
                        <Label className="text-sm font-medium text-foreground">Formato</Label>
                        <div className="flex gap-2">
                            {(["ONE_VS_ONE", "TWO_VS_TWO"] as MatchFormat[]).map((f) => (
                                <button
                                    key={f}
                                    onClick={() => setFormat(f)}
                                    className={`flex h-10 flex-1 items-center justify-center rounded-lg border text-sm font-medium transition-colors ${
                                        format === f
                                            ? "border-primary/50 bg-primary/10 text-primary"
                                            : "border-border/50 bg-secondary/30 text-muted-foreground hover:bg-secondary/50"
                                    }`}
                                >
                                    {FORMAT_LABELS[f]}
                                </button>
                            ))}
                        </div>
                    </div>

                    {/* Nivel */}
                    <div className="flex flex-col gap-1.5">
                        <Label className="text-sm font-medium text-foreground">Nivel requerido</Label>
                        <Select
                            value={skillLevel}
                            onValueChange={(v) => setSkillLevel(v as MatchSkillLevel)}
                        >
                            <SelectTrigger className="h-10 border-border/50 bg-secondary/30 text-foreground">
                                <SelectValue />
                            </SelectTrigger>
                            <SelectContent className="border-border bg-card text-card-foreground">
                                {SKILL_OPTIONS.map((s) => (
                                    <SelectItem key={s.value} value={s.value}>{s.label}</SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>

                    {/* Ciudad de búsqueda */}
                    <div className="flex flex-col gap-1.5">
                        <Label className="text-sm font-medium text-foreground">
                            Buscar jugadores en
                        </Label>
                        <div className="flex items-center gap-2 rounded-lg border border-border/50 bg-secondary/30 px-3">
                            <MapPin className="h-4 w-4 shrink-0 text-primary" />
                            <Select
                                value={selectedCity?.id.toString() ?? ""}
                                onValueChange={(v) => {
                                    const city = cities.find((c) => c.id.toString() === v) ?? null
                                    setSelectedCity(city)
                                }}
                            >
                                <SelectTrigger className="h-10 border-0 bg-transparent px-0 text-sm text-foreground shadow-none focus:ring-0">
                                    <SelectValue placeholder="Selecciona ciudad..." />
                                </SelectTrigger>
                                <SelectContent className="border-border bg-card text-card-foreground">
                                    {cities.map((c) => (
                                        <SelectItem key={c.id} value={c.id.toString()}>
                                            {c.name}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        </div>
                        <div className="flex items-center gap-3 px-1">
                            <span className="text-xs text-muted-foreground">Radio:</span>
                            <input
                                type="range"
                                min={1} max={50} step={1}
                                value={searchRadiusKm}
                                onChange={(e) => setSearchRadiusKm(Number(e.target.value))}
                                className="flex-1 accent-primary"
                            />
                            <span className="w-14 text-right text-xs font-medium text-foreground">
                                {searchRadiusKm} km
                            </span>
                        </div>
                        <p className="text-xs text-muted-foreground">
                            Los jugadores con notificaciones activas en esta zona recibirán la invitación por email.
                        </p>
                    </div>

                    {/* Mensaje personalizado */}
                    <div className="flex flex-col gap-1.5">
                        <Label className="text-sm font-medium text-foreground">
                            Mensaje personalizado
                            <span className="ml-1 text-xs font-normal text-muted-foreground">(opcional)</span>
                        </Label>
                        <Textarea
                            value={customMessage}
                            onChange={(e) => setCustomMessage(e.target.value)}
                            placeholder="Ej: ¡Buen ambiente garantizado! Somos dos amigos buscando completar equipo..."
                            maxLength={300}
                            rows={3}
                            className="resize-none border-border/50 bg-secondary/30 text-sm text-foreground placeholder:text-muted-foreground"
                        />
                        <p className="text-right text-xs text-muted-foreground">
                            {customMessage.length}/300
                        </p>
                    </div>

                </CardContent>
            </Card>

            {/* Summary badges */}
            <div className="flex flex-wrap gap-2">
                <Badge className="border-0 bg-secondary text-xs text-muted-foreground">
                    <Swords className="mr-1 h-3 w-3" />
                    {FORMAT_LABELS[format]}
                </Badge>
                <Badge className="border-0 bg-secondary text-xs text-muted-foreground">
                    <Users className="mr-1 h-3 w-3" />
                    {SKILL_LABELS[skillLevel]}
                </Badge>
                {selectedCity && (
                    <Badge className="border-0 bg-secondary text-xs text-muted-foreground">
                        <MapPin className="mr-1 h-3 w-3" />
                        {selectedCity.name} · {searchRadiusKm} km
                    </Badge>
                )}
            </div>

            {tooSoon && (
                <div className="rounded-lg border border-amber-500/20 bg-amber-500/5 px-3 py-2 text-xs text-amber-400">
                    Este slot está a menos de 48 horas. Solo puedes crear partidos con al menos 48h de antelación para que los jugadores tengan tiempo de organizarse.
                </div>
            )}

            {error && (
                <p className="rounded-lg border border-destructive/20 bg-destructive/5 px-3 py-2 text-xs text-destructive">
                    {error}
                </p>
            )}

            {/* CTA */}
            <Button
                onClick={handleCreate}
                disabled={loading || !selectedCity || tooSoon}
                className="h-12 gap-2 bg-primary text-base font-semibold"
            >
                {loading
                    ? <><Loader2 className="h-4 w-4 animate-spin" />Creando partido...</>
                    : <><Send className="h-4 w-4" />Continuar al pago</>
                }
            </Button>

            <p className="text-center text-xs text-muted-foreground">
                Deberás pagar tu parte para confirmar la reserva. Si no pagas en 30 minutos, el slot se liberará automáticamente.
            </p>

        </div>
    )
}