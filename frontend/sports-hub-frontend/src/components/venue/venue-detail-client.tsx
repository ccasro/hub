"use client"

import {useState} from "react"
import Image from "next/image"
import Link from "next/link"
import {Badge} from "@/components/ui/badge"
import {Button} from "@/components/ui/button"
import {Separator} from "@/components/ui/separator"
import {Textarea} from "@/components/ui/textarea"
import {
    ArrowLeft,
    ArrowRight,
    CalendarDays,
    ChevronDown,
    ChevronLeft,
    ChevronRight,
    ChevronUp,
    Clock,
    Euro,
    LayoutGrid,
    MapPin,
    MessageSquare,
    Send,
    Star,
    User,
} from "lucide-react"
import type {VenueReview} from "@/lib/venue-reviews-mock"
import {getAverageRating, venueReviews} from "@/lib/venue-reviews-mock"
import type {Resource, Venue} from "@/types"
import {VenueMap} from "@/components/venue/venue-map"

// ── Props ────────────────────────────────────────────────────────

interface Props {
    venue: Venue
    resources: Resource[]
}

// ── Constants ────────────────────────────────────────────────────

const sportLabels: Record<string, string> = {
    PADEL: "Padel", TENNIS: "Tenis", SQUASH: "Squash", BADMINTON: "Badminton",
}

const dayLabelsShort: Record<string, string> = {
    MON: "Lun", TUE: "Mar", WED: "Mie", THU: "Jue",
    FRI: "Vie", SAT: "Sab", SUN: "Dom",
}

const dayOrder = ["MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"]

// ── Helpers ──────────────────────────────────────────────────────

function getPriceRange(resource: Resource) {
    if (resource.priceRules.length === 0) return { min: 0, max: 0 }
    const prices = resource.priceRules.map((r) => r.price)
    return { min: Math.min(...prices), max: Math.max(...prices) }
}

function getTimeAgo(date: Date): string {
    const now = new Date()
    const diffDays = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60 * 24))
    if (diffDays === 0) return "Hoy"
    if (diffDays === 1) return "Ayer"
    if (diffDays < 7) return `Hace ${diffDays} dias`
    if (diffDays < 30) return `Hace ${Math.floor(diffDays / 7)} sem`
    if (diffDays < 365) return `Hace ${Math.floor(diffDays / 30)} meses`
    return `Hace ${Math.floor(diffDays / 365)} ano(s)`
}

// ── Sub-components ───────────────────────────────────────────────

function ImageGallery({ images, venueName }: { images: Venue["images"]; venueName: string }) {
    const [current, setCurrent] = useState(0)
    const sorted = [...images].sort((a, b) => a.displayOrder - b.displayOrder)

    if (sorted.length === 0) {
        return (
            <div className="flex aspect-[21/9] items-center justify-center rounded-xl border border-border/30 bg-secondary/20">
                <LayoutGrid className="h-16 w-16 text-muted-foreground/20" />
            </div>
        )
    }

    return (
        <div className="relative overflow-hidden rounded-xl">
            <div className="relative aspect-[21/9]">
                <Image
                    src={sorted[current].url}
                    alt={sorted[current].alt || venueName}
                    fill className="object-cover"
                    sizes="(max-width: 1024px) 100vw, 80vw" priority
                />
                <div className="absolute inset-0 bg-gradient-to-t from-background/60 via-transparent to-background/20" />
            </div>
            {sorted.length > 1 && (
                <>
                    <button
                        onClick={() => setCurrent((p) => (p === 0 ? sorted.length - 1 : p - 1))}
                        className="absolute left-3 top-1/2 -translate-y-1/2 rounded-full border border-border/50 bg-background/80 p-2 text-foreground backdrop-blur-sm transition-colors hover:bg-background"
                    >
                        <ChevronLeft className="h-5 w-5" />
                    </button>
                    <button
                        onClick={() => setCurrent((p) => (p === sorted.length - 1 ? 0 : p + 1))}
                        className="absolute right-3 top-1/2 -translate-y-1/2 rounded-full border border-border/50 bg-background/80 p-2 text-foreground backdrop-blur-sm transition-colors hover:bg-background"
                    >
                        <ChevronRight className="h-5 w-5" />
                    </button>
                    <div className="absolute bottom-3 left-1/2 flex -translate-x-1/2 gap-1.5">
                        {sorted.map((_, i) => (
                            <button
                                key={i}
                                onClick={() => setCurrent(i)}
                                className={`h-2 rounded-full transition-all ${i === current ? "w-6 bg-primary" : "w-2 bg-foreground/40"}`}
                            />
                        ))}
                    </div>
                </>
            )}
        </div>
    )
}

function Stars({ rating, size = 16 }: { rating: number; size?: number }) {
    return (
        <span className="flex items-center gap-0.5">
      {[1, 2, 3, 4, 5].map((star) => (
          <Star
              key={star}
              className={
                  star <= rating ? "fill-amber-400 text-amber-400"
                      : star - 0.5 <= rating ? "fill-amber-400/50 text-amber-400"
                          : "text-border"
              }
              style={{ width: size, height: size }}
          />
      ))}
    </span>
    )
}

function StarsInput({ value, onChange }: { value: number; onChange: (v: number) => void }) {
    const [hover, setHover] = useState(0)
    return (
        <div className="flex items-center gap-1">
            {[1, 2, 3, 4, 5].map((star) => (
                <button
                    key={star}
                    type="button"
                    onMouseEnter={() => setHover(star)}
                    onMouseLeave={() => setHover(0)}
                    onClick={() => onChange(star)}
                    className="transition-transform hover:scale-110"
                >
                    <Star className={star <= (hover || value) ? "h-6 w-6 fill-amber-400 text-amber-400" : "h-6 w-6 text-border"} />
                </button>
            ))}
        </div>
    )
}

function ReviewCard({ review }: { review: VenueReview }) {
    return (
        <div className="flex gap-3 rounded-xl border border-border/30 bg-card p-4">
            <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-primary/10 text-sm font-bold text-primary">
                {review.userAvatar
                    ? <Image src={review.userAvatar} alt={review.userName} width={40} height={40} className="rounded-full object-cover" />
                    : review.userName.charAt(0).toUpperCase()
                }
            </div>
            <div className="flex-1">
                <div className="flex items-center justify-between gap-2">
                    <span className="text-sm font-semibold text-foreground">{review.userName}</span>
                    <span className="text-xs text-muted-foreground">{getTimeAgo(new Date(review.createdAt))}</span>
                </div>
                <Stars rating={review.rating} size={14} />
                <p className="mt-2 text-sm leading-relaxed text-muted-foreground">{review.comment}</p>
            </div>
        </div>
    )
}

function ResourceCard({ resource, venueId }: { resource: Resource; venueId: string }) {
    const [expanded, setExpanded] = useState(false)
    const priceRange = getPriceRange(resource)
    const mainImage = resource.images?.[0]
    const sortedSchedules = [...resource.schedules].sort(
        (a, b) => dayOrder.indexOf(a.dayOfWeek) - dayOrder.indexOf(b.dayOfWeek)
    )

    return (
        <div className="group flex flex-col overflow-hidden rounded-xl border border-border/40 bg-card transition-all hover:border-primary/30 hover:shadow-lg hover:shadow-primary/5">
            <div className="relative aspect-[16/9] overflow-hidden bg-secondary/20">
                {mainImage ? (
                    <Image
                        src={mainImage.url} alt={resource.name} fill
                        className="object-cover transition-transform duration-500 group-hover:scale-105"
                        sizes="(max-width: 640px) 100vw, (max-width: 1024px) 50vw, 33vw"
                    />
                ) : (
                    <div className="flex h-full items-center justify-center bg-gradient-to-br from-secondary/30 to-secondary/10">
                        <LayoutGrid className="h-10 w-10 text-muted-foreground/20" />
                    </div>
                )}
                <div className="absolute inset-0 bg-gradient-to-t from-card/60 via-transparent to-transparent" />
                <div className="absolute left-3 top-3">
                    <Badge className="border-0 bg-primary/90 text-primary-foreground backdrop-blur-sm">
                        {sportLabels[resource.type] || resource.type}
                    </Badge>
                </div>
                <div className="absolute right-3 top-3">
                    <Badge variant="secondary" className="border-0 bg-background/80 text-foreground backdrop-blur-sm">
                        <Clock className="mr-1 h-3 w-3 text-primary" />
                        {resource.slotDurationMinutes} min
                    </Badge>
                </div>
            </div>

            <div className="flex flex-1 flex-col gap-3 p-4">
                <div>
                    <h3 className="font-[var(--font-space-grotesk)] text-lg font-bold text-foreground">{resource.name}</h3>
                    {resource.description && (
                        <p className="mt-1 line-clamp-2 text-sm leading-relaxed text-muted-foreground">{resource.description}</p>
                    )}
                </div>

                <div className="flex flex-wrap items-center gap-3 text-sm text-muted-foreground">
                    <div className="flex items-center gap-1.5">
                        <Euro className="h-3.5 w-3.5 text-primary/70" />
                        <span className="font-semibold text-foreground">
              {priceRange.min === priceRange.max ? `${priceRange.min}` : `${priceRange.min} - ${priceRange.max}`}
            </span>
                        <span className="text-xs">EUR</span>
                    </div>
                    <Separator orientation="vertical" className="h-4 bg-border/50" />
                    <div className="flex items-center gap-1.5">
                        <CalendarDays className="h-3.5 w-3.5 text-primary/70" />
                        <span>{sortedSchedules.length} dias abierto</span>
                    </div>
                </div>

                <div>
                    <button
                        onClick={() => setExpanded(!expanded)}
                        className="flex w-full items-center justify-between rounded-lg border border-border/30 bg-secondary/10 px-3 py-2 text-left text-sm transition-colors hover:bg-secondary/20"
                    >
            <span className="flex items-center gap-2 font-medium text-foreground">
              <Clock className="h-3.5 w-3.5 text-primary/70" />
              Horarios
            </span>
                        {expanded ? <ChevronUp className="h-4 w-4 text-muted-foreground" /> : <ChevronDown className="h-4 w-4 text-muted-foreground" />}
                    </button>
                    {expanded && (
                        <div className="mt-2 overflow-hidden rounded-lg border border-border/30 bg-secondary/5">
                            <table className="w-full text-sm">
                                <tbody>
                                {sortedSchedules.map((s, i) => (
                                    <tr key={s.dayOfWeek} className={i < sortedSchedules.length - 1 ? "border-b border-border/20" : ""}>
                                        <td className="px-3 py-2 font-medium text-foreground">{dayLabelsShort[s.dayOfWeek]}</td>
                                        <td className="px-3 py-2 text-right text-muted-foreground">
                                            {s.openingTime.slice(0, 5)} - {s.closingTime.slice(0, 5)}
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>

                <Link href={`/venue/${venueId}/resource/${resource.id}`} className="mt-auto block">
                    <Button className="h-10 w-full bg-primary font-medium text-primary-foreground hover:bg-primary/90">
                        Ver disponibilidad
                        <ArrowRight className="ml-2 h-4 w-4" />
                    </Button>
                </Link>
            </div>
        </div>
    )
}

// ── Main Component ───────────────────────────────────────────────

export function VenueDetailClient({ venue, resources }: Props) {
    const reviews = venueReviews[venue.id] ?? []
    const avgRating = getAverageRating(venue.id)

    const [newRating, setNewRating] = useState(0)
    const [newComment, setNewComment] = useState("")
    const [submittedReviews, setSubmittedReviews] = useState<VenueReview[]>([])

    const allReviews = [...submittedReviews, ...reviews]

    const handleSubmitReview = () => {
        if (newRating === 0 || !newComment.trim()) return
        setSubmittedReviews((prev) => [{
            id: `rev-new-${Date.now()}`,
            userId: "user-1",
            userName: "Tu nombre",
            userAvatar: null,
            rating: newRating,
            comment: newComment.trim(),
            createdAt: new Date().toISOString(),
        }, ...prev])
        setNewRating(0)
        setNewComment("")
    }

    const allPrices = resources.flatMap((r) => r.priceRules.map((p) => p.price))
    const priceMin = allPrices.length > 0 ? Math.min(...allPrices) : 0
    const priceMax = allPrices.length > 0 ? Math.max(...allPrices) : 0

    return (
        <main className="min-h-screen bg-background">
            {/* Top bar */}
            <div className="sticky top-0 z-30 border-b border-border/30 bg-background/80 backdrop-blur-xl">
                <div className="mx-auto flex h-14 max-w-6xl items-center gap-4 px-4">
                    <Link href="/dashboard" className="flex items-center gap-2 text-sm font-medium text-muted-foreground transition-colors hover:text-foreground">
                        <ArrowLeft className="h-4 w-4" />
                        <span className="hidden sm:inline">Dashboard</span>
                    </Link>
                    <Separator orientation="vertical" className="h-5 bg-border/50" />
                    <span className="truncate text-sm font-medium text-foreground">{venue.name}</span>
                </div>
            </div>

            <div className="mx-auto max-w-6xl px-4 py-6">
                <ImageGallery images={venue.images} venueName={venue.name} />

                {/* Venue info */}
                <div className="mt-6 flex flex-col gap-6 lg:flex-row lg:gap-10">
                    <div className="flex-1">
                        <h1 className="font-[var(--font-space-grotesk)] text-3xl font-bold tracking-tight text-foreground lg:text-4xl">
                            {venue.name}
                        </h1>
                        <div className="mt-3 flex flex-wrap items-center gap-3">
                            {venue.city && (
                                <div className="flex items-center gap-1.5 text-muted-foreground">
                                    <MapPin className="h-4 w-4 text-primary/70" />
                                    <span className="text-sm">
                    {[venue.street, venue.city, venue.country].filter(Boolean).join(", ")}
                  </span>
                                </div>
                            )}
                            <Separator orientation="vertical" className="h-4 bg-border/50" />
                            <div className="flex items-center gap-1.5">
                                <Stars rating={avgRating} size={14} />
                                <span className="text-sm font-semibold text-foreground">{avgRating}</span>
                                <span className="text-sm text-muted-foreground">({allReviews.length} opiniones)</span>
                            </div>
                        </div>
                        {venue.description && (
                            <p className="mt-4 text-base leading-relaxed text-muted-foreground">{venue.description}</p>
                        )}
                    </div>

                    {/* Quick stats */}
                    <div className="flex shrink-0 flex-wrap gap-3 lg:flex-col lg:gap-2">
                        {[
                            { icon: LayoutGrid, value: resources.length, label: "Pistas activas", className: "bg-primary/10 text-primary" },
                            { icon: Star,        value: avgRating,        label: "Valoracion",     className: "bg-amber-500/10 text-amber-400" },
                            { icon: Euro,        value: priceMin === priceMax ? `${priceMin}` : `${priceMin}-${priceMax}`, label: "EUR / sesion", className: "bg-primary/10 text-primary" },
                        ].map(({ icon: Icon, value, label, className }) => (
                            <div key={label} className="flex items-center gap-3 rounded-xl border border-border/40 bg-card px-4 py-3">
                                <div className={`flex h-10 w-10 items-center justify-center rounded-lg ${className.split(" ")[0]}`}>
                                    <Icon className={`h-5 w-5 ${className.split(" ")[1]}`} />
                                </div>
                                <div>
                                    <p className="text-2xl font-bold text-foreground">{value}</p>
                                    <p className="text-xs text-muted-foreground">{label}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Map */}
                {venue.latitude && venue.longitude && (
                    <>
                        <Separator className="my-8 bg-border/30" />
                        <div>
                            <h2 className="mb-4 font-[var(--font-space-grotesk)] text-2xl font-bold text-foreground">Ubicacion</h2>
                            <VenueMap latitude={venue.latitude} longitude={venue.longitude} venueName={venue.name} />
                            <p className="mt-2 flex items-center gap-1.5 text-sm text-muted-foreground">
                                <MapPin className="h-3.5 w-3.5 text-primary/70" />
                                {[venue.street, venue.postalCode, venue.city, venue.country].filter(Boolean).join(", ")}
                            </p>
                        </div>
                    </>
                )}

                {/* Resources */}
                <Separator className="my-8 bg-border/30" />
                <div>
                    <h2 className="font-[var(--font-space-grotesk)] text-2xl font-bold text-foreground">Pistas disponibles</h2>
                    <p className="mt-1 text-sm text-muted-foreground">Selecciona una pista para ver disponibilidad y reservar</p>

                    {resources.length === 0 ? (
                        <div className="mt-6 flex flex-col items-center justify-center rounded-xl border border-dashed border-border/50 bg-card/50 py-16 text-center">
                            <LayoutGrid className="mb-3 h-10 w-10 text-muted-foreground/30" />
                            <p className="font-medium text-foreground">No hay pistas disponibles</p>
                            <p className="mt-1 text-sm text-muted-foreground">Este venue no tiene pistas activas actualmente.</p>
                        </div>
                    ) : (
                        <div className="mt-6 grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
                            {resources.map((resource) => (
                                <ResourceCard key={resource.id} resource={resource} venueId={venue.id} />
                            ))}
                        </div>
                    )}
                </div>

                {/* Reviews */}
                <Separator className="my-8 bg-border/30" />
                <div>
                    <div className="mb-6 flex items-center justify-between">
                        <div>
                            <h2 className="font-[var(--font-space-grotesk)] text-2xl font-bold text-foreground">Opiniones</h2>
                            <p className="mt-1 flex items-center gap-2 text-sm text-muted-foreground">
                                <Stars rating={avgRating} size={14} />
                                <span className="font-semibold text-foreground">{avgRating}</span>
                                de 5 ({allReviews.length} opiniones)
                            </p>
                        </div>
                        <div className="flex items-center gap-1.5">
                            <MessageSquare className="h-5 w-5 text-primary/70" />
                            <span className="text-sm font-medium text-foreground">{allReviews.length}</span>
                        </div>
                    </div>

                    {/* Write review */}
                    <div className="mb-6 rounded-xl border border-border/40 bg-card p-4">
                        <div className="flex items-center gap-3">
                            <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-primary/10">
                                <User className="h-5 w-5 text-primary" />
                            </div>
                            <div className="flex-1">
                                <p className="text-sm font-semibold text-foreground">Deja tu opinion</p>
                                <StarsInput value={newRating} onChange={setNewRating} />
                            </div>
                        </div>
                        <div className="mt-3 flex flex-col gap-3 sm:flex-row">
                            <Textarea
                                placeholder="Cuenta tu experiencia en este venue..."
                                value={newComment}
                                onChange={(e) => setNewComment(e.target.value)}
                                rows={2}
                                className="flex-1 resize-none border-border/50 bg-secondary/20 text-foreground placeholder:text-muted-foreground"
                            />
                            <Button
                                onClick={handleSubmitReview}
                                disabled={newRating === 0 || !newComment.trim()}
                                className="h-auto self-end bg-primary px-5 text-primary-foreground hover:bg-primary/90 disabled:opacity-40 sm:self-stretch"
                            >
                                <Send className="mr-2 h-4 w-4" />
                                Enviar
                            </Button>
                        </div>
                    </div>

                    {allReviews.length === 0 ? (
                        <div className="flex flex-col items-center justify-center rounded-xl border border-dashed border-border/50 bg-card/50 py-12 text-center">
                            <MessageSquare className="mb-3 h-8 w-8 text-muted-foreground/30" />
                            <p className="font-medium text-foreground">Sin opiniones aun</p>
                        </div>
                    ) : (
                        <div className="flex flex-col gap-3">
                            {allReviews.map((review) => <ReviewCard key={review.id} review={review} />)}
                        </div>
                    )}
                </div>

                <div className="h-12" />
            </div>
        </main>
    )
}
