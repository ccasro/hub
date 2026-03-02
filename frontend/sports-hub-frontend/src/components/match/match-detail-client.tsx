"use client"

import {useRouter} from "next/navigation"
import {Badge} from "@/components/ui/badge"
import {Button} from "@/components/ui/button"
import {Card, CardContent} from "@/components/ui/card"
import {ArrowLeft, Calendar, CheckCircle2, Clock, Copy, Share2, Swords, Users} from "lucide-react"
import type {MatchRequestResponse, UserProfile} from "@/types"
import {useCallback, useEffect, useState} from "react"

interface Props {
    user: UserProfile
    matchRequest: MatchRequestResponse
}

const FORMAT_LABELS: Record<string, string> = {
    ONE_VS_ONE: "1 vs 1",
    TWO_VS_TWO: "2 vs 2",
}

const SKILL_LABELS: Record<string, string> = {
    BEGINNER: "Principiante",
    INTERMEDIATE: "Intermedio",
    ADVANCED: "Avanzado",
    ANY: "Cualquier nivel",
}

const STATUS_CONFIG: Record<string, { label: string; color: string }> = {
    OPEN:      { label: "Buscando jugadores", color: "bg-emerald-500/10 text-emerald-400 border-emerald-500/20" },
    FULL:      { label: "Partido completo",   color: "bg-blue-500/10 text-blue-400 border-blue-500/20" },
    EXPIRED:   { label: "Expirado",           color: "bg-amber-500/10 text-amber-400 border-amber-500/20" },
    CANCELLED: { label: "Cancelado",          color: "bg-red-500/10 text-red-400 border-red-500/20" },
}

// Estados en los que tiene sentido hacer polling
const POLLING_STATES = new Set(["OPEN", "FULL"])

export function MatchDetailClient({ user, matchRequest: initialMatch }: Props) {
    const router = useRouter()
    const [match, setMatch] = useState(initialMatch)
    const [copied, setCopied] = useState(false)

    // ── Polling ───────────────────────────────────────────────────
    const refresh = useCallback(async () => {
        try {
            const res = await fetch(`/api/proxy/api/match/requests/${initialMatch.id}`)
            if (res.ok) {
                const data: MatchRequestResponse = await res.json()
                setMatch(data)
            }
        } catch {
            // silenciar errores de red
        }
    }, [initialMatch.id])

    useEffect(() => {
        if (!POLLING_STATES.has(match.status)) return
        const interval = setInterval(refresh, 5000)
        return () => clearInterval(interval)
    }, [match.status, refresh])

    // ── Derived state ─────────────────────────────────────────────
    const statusConf = STATUS_CONFIG[match.status] ?? STATUS_CONFIG.OPEN
    const team1      = match.players?.filter(p => p.team === "TEAM_1") ?? []
    const team2      = match.players?.filter(p => p.team === "TEAM_2") ?? []
    const maxPerTeam = match.format === "ONE_VS_ONE" ? 1 : 2
    const isOrganizer = match.players?.some(
        p => p.playerId === user.id && p.role === "ORGANIZER"
    ) ?? false

    const handleCopy = () => {
        const joinUrl = `${window.location.origin}/match/join/${match.invitationToken}`
        navigator.clipboard.writeText(joinUrl)
        setCopied(true)
        setTimeout(() => setCopied(false), 2000)
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
                <div className="flex-1">
                    <div className="flex items-center gap-2">
                        <h1 className="font-[var(--font-space-grotesk)] text-xl font-bold tracking-tight text-foreground">
                            Partido
                        </h1>
                        <Badge className={`border text-xs font-medium ${statusConf.color}`}>
                            {statusConf.label}
                        </Badge>
                    </div>
                    <p className="text-xs text-muted-foreground">
                        {match.bookingDate} · {match.startTime.slice(0, 5)} — {match.endTime.slice(0, 5)}
                    </p>
                </div>
            </div>

            {/* Info card */}
            <Card className="border-border/50 bg-card">
                <CardContent className="flex flex-col gap-4 p-5">
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">Fecha</p>
                            <div className="mt-1 flex items-center gap-1.5 text-sm font-medium text-foreground">
                                <Calendar className="h-3.5 w-3.5 text-primary" />
                                {match.bookingDate}
                            </div>
                        </div>
                        <div>
                            <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">Horario</p>
                            <div className="mt-1 flex items-center gap-1.5 text-sm font-medium text-foreground">
                                <Clock className="h-3.5 w-3.5 text-primary" />
                                {match.startTime.slice(0, 5)} — {match.endTime.slice(0, 5)}
                            </div>
                        </div>
                        <div>
                            <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">Formato</p>
                            <div className="mt-1 flex items-center gap-1.5 text-sm font-medium text-foreground">
                                <Swords className="h-3.5 w-3.5 text-primary" />
                                {FORMAT_LABELS[match.format]}
                            </div>
                        </div>
                        <div>
                            <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">Nivel</p>
                            <div className="mt-1 flex items-center gap-1.5 text-sm font-medium text-foreground">
                                <Users className="h-3.5 w-3.5 text-primary" />
                                {SKILL_LABELS[match.skillLevel]}
                            </div>
                        </div>
                    </div>
                </CardContent>
            </Card>

            {/* Score Board */}
            <div className="relative overflow-hidden rounded-2xl border border-border/50 bg-gradient-to-br from-secondary/40 to-secondary/20 p-6">
                <div className="flex items-center justify-between">
                    <div className="flex flex-col items-center gap-1">
                        <span className="text-[10px] font-semibold uppercase tracking-widest text-muted-foreground/60">Equipo 1</span>
                        <span className="text-6xl font-black tabular-nums text-blue-400">0</span>
                    </div>
                    <div className="flex flex-col items-center gap-1">
                        <span className="text-2xl font-black text-muted-foreground/30">:</span>
                        <Badge className={`border text-[10px] font-semibold ${statusConf.color}`}>
                            {match.status === "FULL" ? "EN JUEGO" : "PENDIENTE"}
                        </Badge>
                    </div>
                    <div className="flex flex-col items-center gap-1">
                        <span className="text-[10px] font-semibold uppercase tracking-widest text-muted-foreground/60">Equipo 2</span>
                        <span className="text-6xl font-black tabular-nums text-red-400">0</span>
                    </div>
                </div>

                {match.status === "FULL" && (
                    <div className="mt-4 rounded-xl border border-emerald-500/20 bg-emerald-500/5 px-4 py-2 text-center">
                        <p className="text-xs font-medium text-emerald-400">
                            ¡Partido completo! La pista está confirmada. ¡Buena suerte a todos!
                        </p>
                    </div>
                )}

                {match.status === "CANCELLED" && (
                    <div className="mt-4 rounded-xl border border-red-500/20 bg-red-500/5 px-4 py-2 text-center">
                        <p className="text-xs font-medium text-red-400">
                            El partido ha sido cancelado.
                        </p>
                    </div>
                )}
            </div>

            {/* Teams */}
            <div className="grid grid-cols-2 gap-3">
                {([
                    { key: "TEAM_1" as const, players: team1, label: "Equipo 1" },
                    { key: "TEAM_2" as const, players: team2, label: "Equipo 2" },
                ]).map(({ key, players, label }) => (
                    <Card key={key} className="border-border/50 bg-card">
                        <CardContent className="flex flex-col gap-3 p-4">
                            <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">
                                {label}
                            </p>
                            {Array.from({ length: maxPerTeam }).map((_, i) => (
                                <div key={i} className="flex items-center gap-2">
                                    <div className={`flex h-7 w-7 items-center justify-center rounded-full border text-xs font-bold ${
                                        players[i]
                                            ? "bg-primary/20 border-primary/30 text-primary"
                                            : "bg-secondary/30 border-border/30 text-muted-foreground"
                                    }`}>
                                        {players[i] ? "✓" : "?"}
                                    </div>
                                    <div>
                                        <p className="text-xs font-medium text-foreground">
                                            {players[i]
                                                ? players[i].role === "ORGANIZER"
                                                    ? "Organizador"
                                                    : "Jugador"
                                                : "Plaza libre"
                                            }
                                        </p>
                                        {players[i] && (
                                            <p className="text-[10px] text-muted-foreground">
                                                {players[i].role === "ORGANIZER" ? "⚡ Creó el partido" : "✓ Confirmado"}
                                            </p>
                                        )}
                                    </div>
                                    {players[i]?.playerId === user.id && (
                                        <Badge className="ml-auto border-0 bg-primary/10 px-1.5 text-[10px] text-primary">
                                            Tú
                                        </Badge>
                                    )}
                                </div>
                            ))}
                            <p className="text-xs text-muted-foreground">
                                {players.length}/{maxPerTeam} jugadores
                            </p>
                        </CardContent>
                    </Card>
                ))}
            </div>

            {/* Compartir link — solo organizador y partido abierto */}
            {isOrganizer && match.status === "OPEN" && (
                <Card className="border-border/50 bg-card">
                    <CardContent className="flex flex-col gap-3 p-5">
                        <div className="flex items-center gap-2">
                            <Share2 className="h-4 w-4 text-primary" />
                            <p className="text-sm font-medium text-foreground">Compartir invitación</p>
                        </div>
                        <p className="text-xs text-muted-foreground">
                            Comparte este link con jugadores que quieras invitar directamente.
                        </p>
                        <div className="flex gap-2">
                            <div className="flex-1 truncate rounded-lg border border-border/50 bg-secondary/30 px-3 py-2 text-xs text-muted-foreground">
                                {typeof window !== "undefined"
                                    ? `${window.location.origin}/match/join/${match.invitationToken}`
                                    : `/match/join/${match.invitationToken}`}
                            </div>
                            <Button
                                variant="outline"
                                size="sm"
                                onClick={handleCopy}
                                className="shrink-0 border-border/50 text-foreground"
                            >
                                {copied
                                    ? <><CheckCircle2 className="h-3.5 w-3.5 text-emerald-400" /> Copiado</>
                                    : <><Copy className="h-3.5 w-3.5" /> Copiar</>
                                }
                            </Button>
                        </div>
                    </CardContent>
                </Card>
            )}

            {/* Unirse — solo si hay plazas y no es organizador */}
            {match.status === "OPEN" && match.availableSlots > 0 && !isOrganizer && (
                <Button
                    onClick={() => router.push(`/match/join/${match.invitationToken}`)}
                    className="h-11 gap-2 bg-primary font-medium"
                >
                    <Swords className="h-4 w-4" />
                    Unirse al partido
                </Button>
            )}

            {/* Indicador de actualización automática */}
            {POLLING_STATES.has(match.status) && (
                <p className="text-center text-[10px] text-muted-foreground/40">
                    Actualizando automáticamente cada 5s
                </p>
            )}
        </div>
    )
}