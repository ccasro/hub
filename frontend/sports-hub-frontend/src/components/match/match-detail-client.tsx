"use client"

import {useRouter} from "next/navigation"
import {Badge} from "@/components/ui/badge"
import {Button} from "@/components/ui/button"
import {Card, CardContent} from "@/components/ui/card"
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
    AlertDialogTrigger,
} from "@/components/ui/alert-dialog"
import {ArrowLeft, Calendar, CheckCircle2, Clock, Copy, CreditCard, LogOut, MapPin, Share2, Swords, Users} from "lucide-react"
import type {MatchRequestResponse, UserProfile} from "@/types"
import {useCallback, useEffect, useRef, useState} from "react"
import {toast} from "sonner"

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
    AWAITING_ORGANIZER_PAYMENT: { label: "Pendiente de pago", color: "bg-amber-500/10 text-amber-400 border-amber-500/20" },
    OPEN:      { label: "Buscando jugadores", color: "bg-emerald-500/10 text-emerald-400 border-emerald-500/20" },
    FULL:      { label: "Partido completo",   color: "bg-blue-500/10 text-blue-400 border-blue-500/20" },
    EXPIRED:   { label: "Expirado",           color: "bg-amber-500/10 text-amber-400 border-amber-500/20" },
    CANCELLED: { label: "Cancelado",          color: "bg-red-500/10 text-red-400 border-red-500/20" },
}

// Estados en los que tiene sentido hacer polling
const POLLING_STATES = new Set(["AWAITING_ORGANIZER_PAYMENT", "OPEN", "FULL"])

export function MatchDetailClient({ user, matchRequest: initialMatch }: Props) {
    const router = useRouter()
    const [match, setMatch] = useState(initialMatch)
    const [copied, setCopied] = useState(false)
    const [checkingIn, setCheckingIn] = useState(false)
    const checkingInRef = useRef(false)
    const [reportingAbsence, setReportingAbsence] = useState(false)
    const [cancelling, setCancelling]   = useState(false)
    const [leaving, setLeaving]         = useState(false)
    const [paying, setPaying]           = useState(false)

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
    const currentPlayer = match.players?.find(p => p.playerId === user.id)
    const isOrganizer   = currentPlayer?.role === "ORGANIZER"
    const isPlayer      = currentPlayer !== undefined

    // >48h antes del inicio → puede abandonar limpiamente; ≤48h → solo notificar ausencia
    const hoursUntilMatch = (new Date(`${match.bookingDate}T${match.startTime}`).getTime() - Date.now()) / 3_600_000
    const canLeave = hoursUntilMatch > 48

    const handleCopy = () => {
        const joinUrl = `${window.location.origin}/match/join/${match.invitationToken}`
        navigator.clipboard.writeText(joinUrl)
        setCopied(true)
        setTimeout(() => setCopied(false), 2000)
    }

    // ── Check-in (GPS) ────────────────────────────────────────────
    const handleCheckIn = () => {
        if (!navigator.geolocation) {
            toast.error("Tu dispositivo no soporta geolocalización")
            return
        }

        setCheckingIn(true)

        navigator.geolocation.getCurrentPosition(
            async (position) => {
                const { latitude, longitude, accuracy } = position.coords
                try {
                    const res = await fetch(
                        `/api/proxy/api/match/requests/${match.id}/checkin?lat=${latitude}&lng=${longitude}&accuracy=${accuracy}`,
                        { method: "POST" }
                    )
                    if (res.ok) {
                        toast.success("¡Check-in realizado! Tu asistencia ha quedado registrada.")
                        await refresh()
                    } else {
                        const err = await res.json().catch(() => ({}))
                        toast.error(err.message ?? "No se pudo completar el check-in")
                    }
                } catch {
                    toast.error("Error de red. Inténtalo de nuevo.")
                } finally {
                    setCheckingIn(false)
                }
            },
            (err) => {
                setCheckingIn(false)
                if (err.code === err.PERMISSION_DENIED) {
                    toast.error("Permite el acceso a la ubicación para hacer check-in")
                } else {
                    toast.error("No se pudo obtener tu ubicación. Sal al exterior e inténtalo de nuevo.")
                }
            },
            { enableHighAccuracy: true, timeout: 15000, maximumAge: 0 }
        )
    }

    // ── Report absence ────────────────────────────────────────────
    const handleReportAbsence = async () => {
        setReportingAbsence(true)
        try {
            const res = await fetch(
                `/api/proxy/api/match/requests/${match.id}/absence`,
                { method: "POST" }
            )
            if (res.ok) {
                toast.success("Ausencia notificada. Los demás jugadores han sido avisados.")
                await refresh()
            } else {
                const err = await res.json().catch(() => ({}))
                toast.error(err.message ?? "No se pudo notificar la ausencia")
            }
        } catch {
            toast.error("Error de red. Inténtalo de nuevo.")
        } finally {
            setReportingAbsence(false)
        }
    }

    // ── Confirm organizer payment ─────────────────────────────────
    const handleConfirmPayment = async () => {
        setPaying(true)
        try {
            const res = await fetch(`/api/proxy/api/match/requests/${match.id}/confirm-payment`, { method: "POST" })
            if (res.ok) {
                toast.success("¡Pago confirmado! El partido está abierto y los jugadores han sido invitados.")
                await refresh()
            } else {
                const err = await res.json().catch(() => ({}))
                toast.error(err.message ?? "No se pudo confirmar el pago")
            }
        } catch {
            toast.error("Error de red. Inténtalo de nuevo.")
        } finally {
            setPaying(false)
        }
    }

    // ── Cancel match (organizer) ──────────────────────────────────
    const handleCancel = async () => {
        setCancelling(true)
        try {
            const res = await fetch(`/api/proxy/api/match/requests/${match.id}`, {method: "DELETE"})
            if (res.ok) {
                toast.success("Partido cancelado. Los jugadores han sido notificados.")
                await refresh()
            } else {
                const err = await res.json().catch(() => ({}))
                toast.error(err.message ?? "No se pudo cancelar el partido")
            }
        } catch {
            toast.error("Error de red. Inténtalo de nuevo.")
        } finally {
            setCancelling(false)
        }
    }

    // ── Leave match (non-organizer, >48h) ─────────────────────────
    const handleLeave = async () => {
        setLeaving(true)
        try {
            const res = await fetch(`/api/proxy/api/match/requests/${match.id}/leave`, {method: "DELETE"})
            if (res.ok) {
                toast.success("Has abandonado el partido.")
                await refresh()
            } else {
                const err = await res.json().catch(() => ({}))
                toast.error(err.message ?? "No se pudo abandonar el partido")
            }
        } catch {
            toast.error("Error de red. Inténtalo de nuevo.")
        } finally {
            setLeaving(false)
        }
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
                        {match.pricePerPlayer != null && (
                            <div>
                                <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">Precio por jugador</p>
                                <div className="mt-1 text-sm font-medium text-foreground">
                                    {match.pricePerPlayer.toFixed(2)}
                                </div>
                            </div>
                        )}
                    </div>
                    {(match.venueName || match.resourceName || match.venueCity) && (
                        <div className="border-t border-border/40 pt-4">
                            <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">Ubicación</p>
                            <div className="mt-1 flex items-start gap-1.5 text-sm font-medium text-foreground">
                                <MapPin className="mt-0.5 h-3.5 w-3.5 shrink-0 text-primary" />
                                <span>
                                    {[match.venueName, match.resourceName, match.venueCity].filter(Boolean).join(" · ")}
                                </span>
                            </div>
                        </div>
                    )}
                </CardContent>
            </Card>

            {/* Organizer payment pending */}
            {isOrganizer && match.status === "AWAITING_ORGANIZER_PAYMENT" && (
                <Card className="border-amber-500/30 bg-amber-500/5">
                    <CardContent className="flex flex-col gap-3 p-5">
                        <div className="flex items-center gap-2">
                            <CreditCard className="h-4 w-4 text-amber-400" />
                            <p className="text-sm font-medium text-foreground">Pago pendiente</p>
                        </div>
                        <p className="text-xs text-muted-foreground">
                            Para confirmar la reserva y abrir el partido a otros jugadores, debes pagar tu parte (€{match.pricePerPlayer?.toFixed(2)}).
                            Tienes 30 minutos desde la creación del partido.
                        </p>
                        <Button
                            onClick={handleConfirmPayment}
                            disabled={paying}
                            className="h-10 gap-2 bg-amber-600 text-white hover:bg-amber-700"
                        >
                            <CreditCard className="h-4 w-4" />
                            {paying ? "Procesando…" : `Pagar €${match.pricePerPlayer?.toFixed(2)} y abrir el partido`}
                        </Button>
                    </CardContent>
                </Card>
            )}

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
                                            ? players[i].checkedIn
                                                ? "bg-emerald-500/20 border-emerald-500/40 text-emerald-400"
                                                : "bg-primary/20 border-primary/30 text-primary"
                                            : "bg-secondary/30 border-border/30 text-muted-foreground"
                                    }`}>
                                        {players[i] ? (players[i].checkedIn ? "✓" : "●") : "?"}
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
                                                {players[i].checkedIn
                                                    ? "📍 Check-in realizado"
                                                    : players[i].role === "ORGANIZER"
                                                        ? "⚡ Creó el partido"
                                                        : "✓ Confirmado"
                                                }
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

            {/* Check-in — solo para jugadores del partido FULL que no han hecho check-in */}
            {isPlayer && match.status === "FULL" && !currentPlayer?.checkedIn && (
                <Card className="border-emerald-500/20 bg-emerald-500/5">
                    <CardContent className="flex flex-col gap-3 p-5">
                        <div className="flex items-center gap-2">
                            <MapPin className="h-4 w-4 text-emerald-400" />
                            <p className="text-sm font-medium text-foreground">Check-in al partido</p>
                        </div>
                        <p className="text-xs text-muted-foreground">
                            Registra tu asistencia cuando estés en la pista. Disponible 30 minutos antes y después de la hora de inicio.
                        </p>
                        <Button
                            onClick={handleCheckIn}
                            disabled={checkingIn}
                            className="h-10 gap-2 bg-emerald-600 text-white hover:bg-emerald-700"
                        >
                            <MapPin className="h-4 w-4" />
                            {checkingIn ? "Obteniendo ubicación…" : "Hacer check-in"}
                        </Button>
                    </CardContent>
                </Card>
            )}

            {/* Check-in completado */}
            {isPlayer && match.status === "FULL" && currentPlayer?.checkedIn && (
                <div className="flex items-center justify-center gap-2 rounded-xl border border-emerald-500/20 bg-emerald-500/5 px-4 py-3">
                    <CheckCircle2 className="h-4 w-4 text-emerald-400" />
                    <p className="text-sm font-medium text-emerald-400">Check-in realizado</p>
                </div>
            )}

            {/* Cancelar partido — solo organizador cuando está OPEN o AWAITING_ORGANIZER_PAYMENT */}
            {isOrganizer && (match.status === "OPEN" || match.status === "AWAITING_ORGANIZER_PAYMENT") && (
                <AlertDialog>
                    <AlertDialogTrigger asChild>
                        <Button
                            variant="outline"
                            className="h-10 gap-2 border-red-500/30 text-red-400 hover:bg-red-500/10 hover:text-red-300"
                            disabled={cancelling}
                        >
                            <LogOut className="h-4 w-4"/>
                            {cancelling ? "Cancelando…" : "Cancelar partido"}
                        </Button>
                    </AlertDialogTrigger>
                    <AlertDialogContent>
                        <AlertDialogHeader>
                            <AlertDialogTitle>¿Cancelar el partido?</AlertDialogTitle>
                            <AlertDialogDescription>
                                Se cancelará la reserva de la pista y se notificará a todos los jugadores apuntados.
                                Esta acción no se puede deshacer.
                            </AlertDialogDescription>
                        </AlertDialogHeader>
                        <AlertDialogFooter>
                            <AlertDialogCancel>Volver</AlertDialogCancel>
                            <AlertDialogAction
                                onClick={handleCancel}
                                className="bg-red-600 text-white hover:bg-red-700"
                            >
                                Cancelar partido
                            </AlertDialogAction>
                        </AlertDialogFooter>
                    </AlertDialogContent>
                </AlertDialog>
            )}

            {/* Abandonar partido — non-organizer en OPEN/FULL, o organizer en FULL, >48h antes del inicio */}
            {isPlayer && canLeave && (match.status === "OPEN" || match.status === "FULL") && (!isOrganizer || match.status === "FULL") && (
                <AlertDialog>
                    <AlertDialogTrigger asChild>
                        <Button
                            variant="outline"
                            className="h-10 gap-2 border-red-500/30 text-red-400 hover:bg-red-500/10 hover:text-red-300"
                            disabled={leaving}
                        >
                            <LogOut className="h-4 w-4"/>
                            {leaving ? "Abandonando…" : "Abandonar partido"}
                        </Button>
                    </AlertDialogTrigger>
                    <AlertDialogContent>
                        <AlertDialogHeader>
                            <AlertDialogTitle>¿Abandonar el partido?</AlertDialogTitle>
                            <AlertDialogDescription asChild>
                                <div className="flex flex-col gap-2 text-sm text-muted-foreground">
                                    <p>Tu plaza quedará libre y se te devolverá el pago.</p>
                                    <ul className="list-disc pl-4 space-y-1 text-xs">
                                        <li>Solo permitido con más de 48 horas de antelación.</li>
                                        <li className="font-medium text-red-400">No podrás volver a unirte a este partido.</li>
                                    </ul>
                                </div>
                            </AlertDialogDescription>
                        </AlertDialogHeader>
                        <AlertDialogFooter>
                            <AlertDialogCancel>Volver</AlertDialogCancel>
                            <AlertDialogAction
                                onClick={handleLeave}
                                className="bg-red-600 text-white hover:bg-red-700"
                            >
                                Abandonar partido
                            </AlertDialogAction>
                        </AlertDialogFooter>
                    </AlertDialogContent>
                </AlertDialog>
            )}

            {/* Notificar ausencia — non-organizer en OPEN/FULL, o organizer en FULL, ≤48h antes */}
            {isPlayer && !canLeave && (match.status === "OPEN" || match.status === "FULL") && (!isOrganizer || match.status === "FULL") && (
                <AlertDialog>
                    <AlertDialogTrigger asChild>
                        <Button
                            variant="outline"
                            className="h-10 gap-2 border-red-500/30 text-red-400 hover:bg-red-500/10 hover:text-red-300"
                            disabled={reportingAbsence}
                        >
                            <LogOut className="h-4 w-4"/>
                            {reportingAbsence ? "Notificando…" : "No podré asistir"}
                        </Button>
                    </AlertDialogTrigger>
                    <AlertDialogContent>
                        <AlertDialogHeader>
                            <AlertDialogTitle>¿Confirmas que no podrás asistir?</AlertDialogTitle>
                            <AlertDialogDescription asChild>
                                <div className="flex flex-col gap-2 text-sm text-muted-foreground">
                                    <p>Se avisará a los demás jugadores y se buscará un sustituto gratuito.</p>
                                    <ul className="list-disc pl-4 space-y-1 text-xs">
                                        <li className="font-medium text-red-400">Tu pago no será reembolsado.</li>
                                        <li className="font-medium text-red-400">No podrás volver a unirte a este partido.</li>
                                    </ul>
                                </div>
                            </AlertDialogDescription>
                        </AlertDialogHeader>
                        <AlertDialogFooter>
                            <AlertDialogCancel>Cancelar</AlertDialogCancel>
                            <AlertDialogAction
                                onClick={handleReportAbsence}
                                className="bg-red-600 text-white hover:bg-red-700"
                            >
                                Notificar ausencia
                            </AlertDialogAction>
                        </AlertDialogFooter>
                    </AlertDialogContent>
                </AlertDialog>
            )}

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

            {/* Unirse — solo si hay plazas y no es jugador */}
            {match.status === "OPEN" && match.availableSlots > 0 && !isPlayer && (
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
