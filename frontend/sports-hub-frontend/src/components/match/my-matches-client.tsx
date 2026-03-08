"use client"

import {useRouter} from "next/navigation"
import {Badge} from "@/components/ui/badge"
import {Button} from "@/components/ui/button"
import {Card, CardContent} from "@/components/ui/card"
import {ArrowLeft, Calendar, Clock, Swords} from "lucide-react"
import type {MatchRequestResponse, UserProfile} from "@/types"

interface Props {
    user: UserProfile
    matches: MatchRequestResponse[]
}

const STATUS_CONFIG: Record<string, { label: string; color: string }> = {
    AWAITING_ORGANIZER_PAYMENT: { label: "Pago pendiente", color: "bg-amber-500/10 text-amber-400 border-amber-500/20" },
    OPEN:      { label: "Buscando jugadores", color: "bg-emerald-500/10 text-emerald-400 border-emerald-500/20" },
    FULL:      { label: "Completo",           color: "bg-blue-500/10 text-blue-400 border-blue-500/20" },
    EXPIRED:   { label: "Expirado",           color: "bg-amber-500/10 text-amber-400 border-amber-500/20" },
    CANCELLED: { label: "Cancelado",          color: "bg-red-500/10 text-red-400 border-red-500/20" },
}

const FORMAT_LABELS: Record<string, string> = {
    ONE_VS_ONE: "1 vs 1",
    TWO_VS_TWO: "2 vs 2",
}

export function MyMatchesClient({user, matches}: Props) {
    const router = useRouter()

    const active   = matches.filter(m => m.status === "AWAITING_ORGANIZER_PAYMENT" || m.status === "OPEN" || m.status === "FULL")
    const finished = matches.filter(m => m.status === "EXPIRED" || m.status === "CANCELLED")

    const MatchCard = ({match}: { match: MatchRequestResponse }) => {
        const statusConf = STATUS_CONFIG[match.status] ?? STATUS_CONFIG.OPEN
        const isOrganizer = match.players?.find(p => p.playerId === user.id)?.role === "ORGANIZER"

        return (
            <Card
                className="cursor-pointer border-border/50 bg-card transition-colors hover:border-border"
                onClick={() => router.push(`/match/${match.id}`)}
            >
                <CardContent className="flex items-center gap-4 p-4">
                    <div className="min-w-0 flex-1">
                        <div className="flex items-center gap-2">
                            <Badge className={`border text-[10px] font-medium ${statusConf.color}`}>
                                {statusConf.label}
                            </Badge>
                            {isOrganizer && (
                                <Badge className="border-0 bg-primary/10 text-[10px] text-primary">
                                    Organizador
                                </Badge>
                            )}
                        </div>
                        <div className="mt-2 flex flex-wrap items-center gap-3 text-xs text-muted-foreground">
                            <span className="flex items-center gap-1">
                                <Calendar className="h-3 w-3"/>
                                {match.bookingDate}
                            </span>
                            <span className="flex items-center gap-1">
                                <Clock className="h-3 w-3"/>
                                {match.startTime.slice(0, 5)} — {match.endTime.slice(0, 5)}
                            </span>
                            <span className="flex items-center gap-1">
                                <Swords className="h-3 w-3"/>
                                {FORMAT_LABELS[match.format]}
                            </span>
                        </div>
                    </div>
                    <div className="shrink-0 text-right">
                        <p className="text-xs text-muted-foreground">
                            {match.players?.length ?? 0}/{match.format === "ONE_VS_ONE" ? 2 : 4} jugadores
                        </p>
                        {match.pricePerPlayer != null && (
                            <p className="mt-1 text-xs font-medium text-foreground">
                                {match.pricePerPlayer.toFixed(2)} / jugador
                            </p>
                        )}
                    </div>
                </CardContent>
            </Card>
        )
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
                        Mis partidos
                    </h1>
                    <p className="text-xs text-muted-foreground">
                        Partidos en los que participas o has participado
                    </p>
                </div>
            </div>

            {matches.length === 0 ? (
                <div className="flex flex-col items-center gap-3 rounded-2xl border border-dashed border-border/50 bg-secondary/10 py-16 text-center">
                    <div className="flex h-14 w-14 items-center justify-center rounded-2xl border border-border/30 bg-secondary/30">
                        <Swords className="h-6 w-6 text-muted-foreground/50"/>
                    </div>
                    <p className="text-sm font-medium text-foreground">Sin partidos todavía</p>
                    <p className="max-w-xs text-xs text-muted-foreground">
                        Crea o únete a un partido para verlo aquí.
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
                <div className="flex flex-col gap-6">
                    {active.length > 0 && (
                        <div className="flex flex-col gap-3">
                            <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">
                                Activos
                            </p>
                            {active.map(m => <MatchCard key={m.id} match={m}/>)}
                        </div>
                    )}
                    {finished.length > 0 && (
                        <div className="flex flex-col gap-3">
                            <p className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">
                                Historial
                            </p>
                            {finished.map(m => <MatchCard key={m.id} match={m}/>)}
                        </div>
                    )}
                </div>
            )}
        </div>
    )
}
