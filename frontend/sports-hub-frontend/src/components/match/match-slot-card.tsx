"use client"

import {useState} from "react"
import {useRouter} from "next/navigation"
import {Badge} from "@/components/ui/badge"
import {Button} from "@/components/ui/button"
import {Card, CardContent} from "@/components/ui/card"
import {Building2, ChevronRight, Clock, MapPin, Swords, Users} from "lucide-react"
import type {MatchFormat, MatchSkillLevel, MatchSlotResult} from "@/types"

interface Props {
    slot: MatchSlotResult
    format: MatchFormat
    skillLevel: MatchSkillLevel
    date: string
    duration: number
}

const SPORT_COLORS: Record<string, string> = {
    PADEL:     "bg-emerald-500/10 text-emerald-400",
    TENNIS:    "bg-blue-500/10 text-blue-400",
    SQUASH:    "bg-amber-500/10 text-amber-400",
    BADMINTON: "bg-purple-500/10 text-purple-400",
}


export function MatchSlotCard({ slot, format, skillLevel, date, duration }: Props) {
    const router = useRouter()

    const [tooSoon] = useState(
        () => (new Date(`${date}T${slot.startTime.slice(0, 5)}`).getTime() - Date.now()) / 3_600_000 < 48,
    )

    const handleCreate = () => {
        const params = new URLSearchParams({
            resourceId:   slot.resourceId,
            date,
            startTime:    slot.startTime.slice(0, 5),
            duration:     duration.toString(),
            format,
            skillLevel,
            venueName:    slot.venueName,
            resourceName: slot.resourceName,
        })
        router.push(`/match/create?${params}`)
    }

    return (
        <Card className="border-border/50 bg-card transition-colors hover:border-border">
            <CardContent className="flex items-center gap-4 p-4">
                <div className={`flex h-12 w-12 shrink-0 items-center justify-center rounded-xl text-xs font-bold ${SPORT_COLORS[slot.resourceType] ?? "bg-secondary text-secondary-foreground"}`}>
                    {slot.resourceType.slice(0, 3)}
                </div>

                <div className="min-w-0 flex-1">
                    <div className="flex items-start justify-between gap-2">
                        <div>
                            <p className="text-sm font-semibold text-foreground">{slot.resourceName}</p>
                            <div className="mt-0.5 flex items-center gap-1 text-xs text-muted-foreground">
                                <Building2 className="h-3 w-3" />
                                {slot.venueName}
                            </div>
                        </div>
                        <Badge className="shrink-0 border-0 bg-secondary px-2 text-[10px] text-muted-foreground">
                            <MapPin className="mr-1 h-2.5 w-2.5" />
                            {slot.distanceKm} km
                        </Badge>
                    </div>

                    <div className="mt-2 flex flex-wrap items-center gap-3 text-xs text-muted-foreground">
                        <span className="flex items-center gap-1">
                            <Clock className="h-3 w-3" />
                            {slot.startTime.slice(0, 5)} — {slot.endTime.slice(0, 5)}
                        </span>
                        <span className="flex items-center gap-1">
                            {slot.price > 0 ? `${slot.price} ${slot.currency}` : "Precio a convenir"}
                        </span>
                        <span className="flex items-center gap-1">
                            <Swords className="h-3 w-3" />
                            {format === "ONE_VS_ONE" ? "1 vs 1" : "2 vs 2"}
                        </span>
                        {slot.eligiblePlayersNearby > 0 && (
                            <span className="flex items-center gap-1 text-emerald-400">
                                <Users className="h-3 w-3" />
                                {slot.eligiblePlayersNearby} jugadores disponibles
                            </span>
                        )}
                    </div>
                </div>

                {tooSoon ? (
                    <span className="shrink-0 rounded-lg border border-amber-500/30 bg-amber-500/5 px-3 py-1.5 text-[10px] font-medium text-amber-400">
                        &lt;48h
                    </span>
                ) : (
                    <Button
                        size="sm"
                        onClick={handleCreate}
                        className="h-9 shrink-0 gap-1.5 bg-primary text-xs font-medium"
                    >
                        Crear partido
                        <ChevronRight className="h-3.5 w-3.5" />
                    </Button>
                )}
            </CardContent>
        </Card>
    )
}