"use client"

import {useState} from "react"
import {useRouter} from "next/navigation"
import {Badge} from "@/components/ui/badge"
import {Button} from "@/components/ui/button"
import {Card, CardContent} from "@/components/ui/card"
import {ArrowLeft, Calendar, CheckCircle2, Clock, CreditCard, MapPin, Swords, Users, XCircle} from "lucide-react"
import type {MatchFormat, MatchInvitation, MatchSkillLevel} from "@/types"
import {toast} from "sonner"

interface Props {
    invitations: MatchInvitation[]
}

const FORMAT_LABELS: Record<MatchFormat, string> = {
    ONE_VS_ONE: "1 vs 1",
    TWO_VS_TWO: "2 vs 2",
}

const SKILL_LABELS: Record<MatchSkillLevel, string> = {
    BEGINNER: "Principiante",
    INTERMEDIATE: "Intermedio",
    ADVANCED: "Avanzado",
    ANY: "Cualquier nivel",
}

function InvitationCard({ invitation, onResponded }: {
    invitation: MatchInvitation
    onResponded: () => void
}) {
    const [selectingTeam, setSelectingTeam] = useState(false)
    const [selectedTeam, setSelectedTeam] = useState<"TEAM_1" | "TEAM_2" | null>(null)
    const [loading, setLoading] = useState(false)

    const maxPerTeam = invitation.format === "ONE_VS_ONE" ? 1 : 2
    const isFull = invitation.availableSlots === 0

    const handleAccept = async () => {
        if (!selectedTeam) return
        setLoading(true)
        try {
            const res = await fetch(`/api/proxy/api/match/invitations/${invitation.id}/accept`, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({team: selectedTeam}),
            })
            if (res.ok) {
                const data = await res.json()
                toast.success("¡Te has unido al partido!")
                onResponded()
                // pequeño delay para que el toast sea visible
                setTimeout(() => {
                    window.location.href = `/match/${data.id}`
                }, 800)
            } else {
                const err = await res.json().catch(() => ({}))
                toast.error(err.detail ?? "No se pudo aceptar la invitación")
                setLoading(false)
            }
        } catch {
            toast.error("Error de red. Inténtalo de nuevo.")
            setLoading(false)
        }
    }

    const handleDecline = async () => {
        setLoading(true)
        try {
            const res = await fetch(`/api/proxy/api/match/invitations/${invitation.id}/decline`, {
                method: "POST",
            })
            if (res.ok) {
                toast.success("Invitación rechazada.")
                onResponded()
            } else {
                const err = await res.json().catch(() => ({}))
                toast.error(err.detail ?? "No se pudo rechazar la invitación")
            }
        } catch {
            toast.error("Error de red. Inténtalo de nuevo.")
        } finally {
            setLoading(false)
        }
    }

    return (
        <Card className="border-border/50 bg-card">
            <CardContent className="flex flex-col gap-4 p-5">
                {/* Match info */}
                <div className="flex items-start justify-between gap-2">
                    <div className="flex flex-col gap-1">
                        <div className="flex items-center gap-1.5 text-sm font-medium text-foreground">
                            <Calendar className="h-3.5 w-3.5 text-primary"/>
                            {invitation.bookingDate}
                        </div>
                        <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                            <Clock className="h-3 w-3"/>
                            {invitation.startTime?.slice(0, 5)} — {invitation.endTime?.slice(0, 5)}
                        </div>
                        {(invitation.venueName || invitation.resourceName) && (
                            <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                                <MapPin className="h-3 w-3 shrink-0"/>
                                <span className="truncate">
                                    {[invitation.venueName, invitation.resourceName].filter(Boolean).join(" · ")}
                                    {invitation.venueCity ? `, ${invitation.venueCity}` : ""}
                                </span>
                            </div>
                        )}
                    </div>
                    <div className="flex flex-col items-end gap-1">
                        {invitation.format && (
                            <Badge className="border-0 bg-secondary text-[10px] text-muted-foreground">
                                <Swords className="mr-1 h-2.5 w-2.5"/>
                                {FORMAT_LABELS[invitation.format]}
                            </Badge>
                        )}
                        {invitation.skillLevel && (
                            <Badge className="border-0 bg-secondary text-[10px] text-muted-foreground">
                                <Users className="mr-1 h-2.5 w-2.5"/>
                                {SKILL_LABELS[invitation.skillLevel]}
                            </Badge>
                        )}
                        {invitation.pricePerPlayer != null && (
                            <Badge className="border-0 bg-primary/10 text-[10px] text-primary">
                                <CreditCard className="mr-1 h-2.5 w-2.5"/>
                                €{invitation.pricePerPlayer.toFixed(2)} / jugador
                            </Badge>
                        )}
                    </div>
                </div>

                {isFull ? (
                    <p className="rounded-lg border border-amber-500/20 bg-amber-500/5 px-3 py-2 text-xs text-amber-400">
                        El partido ya no tiene plazas disponibles.
                    </p>
                ) : selectingTeam ? (
                    /* Team selection */
                    <div className="flex flex-col gap-3">
                        <p className="text-xs font-medium text-muted-foreground">Elige tu equipo:</p>
                        <div className="grid grid-cols-2 gap-2">
                            {(["TEAM_1", "TEAM_2"] as const).map((team) => (
                                <button
                                    key={team}
                                    onClick={() => setSelectedTeam(team)}
                                    className={`rounded-xl border px-4 py-3 text-sm font-medium transition-colors ${
                                        selectedTeam === team
                                            ? "border-primary/50 bg-primary/10 text-primary"
                                            : "border-border/50 bg-secondary/20 text-foreground hover:border-border"
                                    }`}
                                >
                                    {team === "TEAM_1" ? "Equipo 1" : "Equipo 2"}
                                    {selectedTeam === team && (
                                        <CheckCircle2 className="ml-2 inline h-3.5 w-3.5"/>
                                    )}
                                </button>
                            ))}
                        </div>
                        {invitation.pricePerPlayer != null && selectedTeam && (
                            <p className="rounded-lg border border-primary/20 bg-primary/5 px-3 py-2 text-xs text-primary">
                                <CreditCard className="mr-1 inline h-3 w-3"/>
                                Se cargará <strong>€{invitation.pricePerPlayer.toFixed(2)}</strong> al confirmar.
                            </p>
                        )}
                        <div className="flex gap-2">
                            <Button
                                variant="outline"
                                size="sm"
                                onClick={() => {setSelectingTeam(false); setSelectedTeam(null)}}
                                disabled={loading}
                                className="flex-1 border-border/50"
                            >
                                Atrás
                            </Button>
                            <Button
                                size="sm"
                                onClick={handleAccept}
                                disabled={!selectedTeam || loading}
                                className="flex-1 bg-primary font-medium"
                            >
                                {loading
                                    ? "Procesando…"
                                    : invitation.pricePerPlayer != null
                                        ? `Pagar €${invitation.pricePerPlayer.toFixed(2)} y unirse`
                                        : "Confirmar"
                                }
                            </Button>
                        </div>
                    </div>
                ) : (
                    /* Accept / Decline buttons */
                    <div className="flex gap-2">
                        <Button
                            size="sm"
                            onClick={handleDecline}
                            disabled={loading}
                            variant="outline"
                            className="flex-1 gap-1.5 border-red-500/30 text-red-400 hover:bg-red-500/10 hover:text-red-300"
                        >
                            <XCircle className="h-3.5 w-3.5"/>
                            Rechazar
                        </Button>
                        <Button
                            size="sm"
                            onClick={() => setSelectingTeam(true)}
                            disabled={loading}
                            className="flex-1 gap-1.5 bg-primary font-medium"
                        >
                            <CheckCircle2 className="h-3.5 w-3.5"/>
                            Aceptar
                        </Button>
                    </div>
                )}
            </CardContent>
        </Card>
    )
}

export function MatchInvitationsClient({invitations: initial}: Props) {
    const router = useRouter()
    const [invitations, setInvitations] = useState(initial)

    const pending = invitations.filter(i => i.status === "PENDING" && i.matchStatus === "OPEN")

    const removeInvitation = (id: string) => {
        setInvitations(prev => prev.filter(i => i.id !== id))
    }

    return (
        <div className="mx-auto flex max-w-2xl flex-col gap-6 p-4 lg:p-8">
            <div className="flex items-center gap-3">
                <Button
                    variant="ghost"
                    size="icon"
                    onClick={() => router.back()}
                    className="h-9 w-9 text-muted-foreground hover:text-foreground"
                >
                    <ArrowLeft className="h-4 w-4"/>
                </Button>
                <div>
                    <h1 className="font-[var(--font-space-grotesk)] text-xl font-bold tracking-tight text-foreground">
                        Invitaciones
                    </h1>
                    <p className="text-xs text-muted-foreground">
                        Partidos en los que te han invitado a jugar
                    </p>
                </div>
                {pending.length > 0 && (
                    <Badge className="ml-auto border-0 bg-primary px-2 text-xs font-bold text-primary-foreground">
                        {pending.length}
                    </Badge>
                )}
            </div>

            {pending.length === 0 ? (
                <div className="flex flex-col items-center gap-3 rounded-2xl border border-dashed border-border/50 bg-secondary/10 py-16 text-center">
                    <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-secondary/30 border border-border/30">
                        <Swords className="h-6 w-6 text-muted-foreground/50"/>
                    </div>
                    <p className="text-sm font-medium text-foreground">Sin invitaciones pendientes</p>
                    <p className="max-w-xs text-xs text-muted-foreground">
                        Cuando el sistema encuentre un partido para ti o alguien te invite, aparecerá aquí.
                    </p>
                    <Button
                        variant="outline"
                        size="sm"
                        onClick={() => router.push("/match/search")}
                        className="mt-2 gap-1.5 border-primary/30 text-primary hover:bg-primary/10"
                    >
                        <Swords className="h-3.5 w-3.5"/>
                        Buscar partido
                    </Button>
                </div>
            ) : (
                <div className="flex flex-col gap-3">
                    {pending.map(inv => (
                        <InvitationCard
                            key={inv.id}
                            invitation={inv}
                            onResponded={() => removeInvitation(inv.id)}
                        />
                    ))}
                </div>
            )}
        </div>
    )
}