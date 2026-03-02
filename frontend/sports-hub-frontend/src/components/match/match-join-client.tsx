"use client"

import {useState} from "react"
import {useRouter} from "next/navigation"
import {Badge} from "@/components/ui/badge"
import {Button} from "@/components/ui/button"
import {Card, CardContent} from "@/components/ui/card"
import {Calendar, CheckCircle2, Clock, Loader2, Swords, Users, XCircle,} from "lucide-react"
import type {MatchRequestResponse, UserProfile} from "@/types"

interface Props {
    user: UserProfile
    matchRequest: MatchRequestResponse
    token: string
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
    OPEN:      { label: "Abierto",   color: "bg-emerald-500/10 text-emerald-400 border-emerald-500/20" },
    FULL:      { label: "Completo",  color: "bg-blue-500/10 text-blue-400 border-blue-500/20" },
    EXPIRED:   { label: "Expirado",  color: "bg-amber-500/10 text-amber-400 border-amber-500/20" },
    CANCELLED: { label: "Cancelado", color: "bg-red-500/10 text-red-400 border-red-500/20" },
}

export function MatchJoinClient({ user, matchRequest, token }: Props) {
    const router  = useRouter()
    const [selectedTeam, setSelectedTeam] = useState<"TEAM_1" | "TEAM_2" | null>(null)
    const [loading, setLoading]           = useState(false)
    const [error, setError]               = useState<string | null>(null)
    const [joined, setJoined]             = useState(false)

    const isOpen     = matchRequest.status === "OPEN"
    const statusConf = STATUS_CONFIG[matchRequest.status] ?? STATUS_CONFIG.OPEN

    const team1Players = matchRequest.players?.filter(p => p.team === "TEAM_1") ?? []
    const team2Players = matchRequest.players?.filter(p => p.team === "TEAM_2") ?? []
    const maxPerTeam   = matchRequest.format === "ONE_VS_ONE" ? 1 : 2

    const alreadyJoined = matchRequest.players?.some(
        p => p.playerId === user.id
    ) ?? false

    const handleJoin = async () => {
        if (!selectedTeam) { setError("Selecciona un equipo"); return }
        setLoading(true)
        setError(null)
        try {
            const res = await fetch(`/api/proxy/api/match/join/${token}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ team: selectedTeam }),
            })
            if (!res.ok) {
                const body = await res.json().catch(() => null)
                throw new Error(body?.detail || `Error ${res.status}`)
            }
            const data = await res.json()
            setJoined(true)
            // Si el partido se llenó navegar al detalle
            setTimeout(() => router.push(`/match/${data.id}`), 1500)
        } catch (e) {
            setError(e instanceof Error ? e.message : "Error uniéndose al partido")
        } finally {
            setLoading(false)
        }
    }

    if (joined) {
        return (
            <div className="flex min-h-screen items-center justify-center bg-background p-4">
                <div className="flex flex-col items-center gap-4 text-center">
                    <div className="flex h-16 w-16 items-center justify-center rounded-full bg-emerald-500/15 border border-emerald-500/30">
                        <CheckCircle2 className="h-8 w-8 text-emerald-400" />
                    </div>
                    <h1 className="text-2xl font-bold text-foreground">¡Te has unido!</h1>
                    <p className="text-sm text-muted-foreground">Redirigiendo al partido...</p>
                </div>
            </div>
        )
    }

    return (
        <div className="mx-auto flex max-w-lg flex-col gap-6 p-4 py-8 lg:p-8">

            {/* Header */}
            <div className="flex flex-col items-center gap-3 text-center">
                <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-primary/10 border border-primary/20">
                    <Swords className="h-7 w-7 text-primary" />
                </div>
                <div>
                    <h1 className="font-[var(--font-space-grotesk)] text-2xl font-bold tracking-tight text-foreground">
                        Invitación a partido
                    </h1>
                    <p className="mt-1 text-sm text-muted-foreground">
                        Te han invitado a unirte a este partido
                    </p>
                </div>
                <Badge className={`border text-xs font-medium ${statusConf.color}`}>
                    {statusConf.label}
                </Badge>
            </div>

            {/* Match info */}
            <Card className="border-border/50 bg-card">
                <CardContent className="flex flex-col gap-4 p-5">
                    <div className="flex items-center justify-between">
                        <div className="flex items-center gap-2 text-sm font-medium text-foreground">
                            <Calendar className="h-4 w-4 text-primary" />
                            {matchRequest.bookingDate}
                        </div>
                        <div className="flex items-center gap-2 text-sm text-muted-foreground">
                            <Clock className="h-4 w-4" />
                            {matchRequest.startTime.slice(0, 5)} — {matchRequest.endTime.slice(0, 5)}
                        </div>
                    </div>
                    <div className="flex gap-2">
                        <Badge className="border-0 bg-secondary text-xs text-muted-foreground">
                            <Swords className="mr-1 h-3 w-3" />
                            {FORMAT_LABELS[matchRequest.format]}
                        </Badge>
                        <Badge className="border-0 bg-secondary text-xs text-muted-foreground">
                            <Users className="mr-1 h-3 w-3" />
                            {SKILL_LABELS[matchRequest.skillLevel]}
                        </Badge>
                        {isOpen && (
                            <Badge className="border-0 bg-emerald-500/10 text-xs text-emerald-400">
                                {matchRequest.availableSlots} plazas libres
                            </Badge>
                        )}
                    </div>
                </CardContent>
            </Card>

            {/* Teams */}
            <div className="grid grid-cols-2 gap-3">
                {(["TEAM_1", "TEAM_2"] as const).map((team) => {
                    const players  = team === "TEAM_1" ? team1Players : team2Players
                    const isFull   = players.length >= maxPerTeam
                    const isSelected = selectedTeam === team

                    return (
                        <button
                            key={team}
                            onClick={() => isOpen && !isFull && !alreadyJoined
                                ? setSelectedTeam(team) : undefined}
                            disabled={!isOpen || isFull || alreadyJoined}
                            className={`flex flex-col gap-3 rounded-xl border p-4 text-left transition-colors ${
                                isSelected
                                    ? "border-primary/50 bg-primary/10"
                                    : isFull
                                        ? "border-border/30 bg-secondary/10 opacity-50 cursor-not-allowed"
                                        : "border-border/50 bg-card hover:border-border cursor-pointer"
                            }`}
                        >
                            <div className="flex items-center justify-between">
                                <span className="text-xs font-semibold text-muted-foreground uppercase tracking-wide">
                                    {team === "TEAM_1" ? "Equipo 1" : "Equipo 2"}
                                </span>
                                {isFull && (
                                    <XCircle className="h-3.5 w-3.5 text-muted-foreground" />
                                )}
                                {isSelected && (
                                    <CheckCircle2 className="h-3.5 w-3.5 text-primary" />
                                )}
                            </div>
                            <div className="flex flex-col gap-1">
                                {Array.from({ length: maxPerTeam }).map((_, i) => (
                                    <div key={i} className="flex items-center gap-2">
                                        <div className={`h-6 w-6 rounded-full border flex items-center justify-center text-[10px] font-bold ${
                                            players[i]
                                                ? "bg-primary/20 border-primary/30 text-primary"
                                                : "bg-secondary/30 border-border/30 text-muted-foreground"
                                        }`}>
                                            {players[i] ? "✓" : "?"}
                                        </div>
                                        <span className="text-xs text-muted-foreground">
                                            {players[i]
                                                ? players[i].role === "ORGANIZER" ? "Organizador" : "Jugador"
                                                : "Plaza libre"
                                            }
                                        </span>
                                    </div>
                                ))}
                            </div>
                            <p className="text-xs font-medium text-foreground">
                                {players.length}/{maxPerTeam} jugadores
                            </p>
                        </button>
                    )
                })}
            </div>

            {/* CTA */}
            {alreadyJoined ? (
                <div className="rounded-xl border border-emerald-500/20 bg-emerald-500/10 p-4 text-center">
                    <p className="text-sm font-medium text-emerald-400">
                        ✓ Ya estás en este partido
                    </p>
                    <Button
                        variant="ghost"
                        size="sm"
                        className="mt-2 text-xs text-muted-foreground"
                        onClick={() => router.push(`/match/${matchRequest.id}`)}
                    >
                        Ver detalles
                    </Button>
                </div>
            ) : !isOpen ? (
                <div className="rounded-xl border border-border/50 bg-secondary/20 p-4 text-center">
                    <p className="text-sm text-muted-foreground">
                        Este partido ya no está disponible.
                    </p>
                </div>
            ) : (
                <>
                    {error && (
                        <p className="rounded-lg border border-destructive/20 bg-destructive/5 px-3 py-2 text-xs text-destructive">
                            {error}
                        </p>
                    )}
                    <Button
                        onClick={handleJoin}
                        disabled={loading || !selectedTeam}
                        className="h-12 gap-2 bg-primary text-base font-semibold"
                    >
                        {loading
                            ? <><Loader2 className="h-4 w-4 animate-spin" />Uniéndose...</>
                            : <><Swords className="h-4 w-4" />Unirse al partido</>
                        }
                    </Button>
                    <p className="text-center text-xs text-muted-foreground">
                        Al unirte aceptas jugar en la fecha y horario indicados.
                    </p>
                </>
            )}
        </div>
    )
}