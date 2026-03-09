"use client"

import React, {useMemo, useState} from "react"
import Image from "next/image"
import Link from "next/link"
import {OwnerSidebar} from "@/components/owner/owner-sidebar"
import type {VenueFormData} from "@/components/owner/venue-form-dialog"
import {VenueFormDialog} from "@/components/owner/venue-form-dialog"
import {VenueImageManager} from "@/components/owner/venue-image-manager"
import {Button} from "@/components/ui/button"
import {Badge} from "@/components/ui/badge"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import {
    AlertTriangle,
    Building2,
    Calendar,
    CheckCircle2,
    Clock,
    Edit,
    ImagePlus,
    LayoutGrid,
    MapPin,
    MoreVertical,
    Pause,
    Play,
    Plus,
    XCircle,
} from "lucide-react"
import type {UserProfile, Venue} from "@/types"

// ── Types ────────────────────────────────────────────────────────

type VenueWithCount = Venue & { resourceCount: number }

interface Props {
    user: UserProfile
    venues: VenueWithCount[]
}

type VenueStatus = "ACTIVE" | "PENDING_REVIEW" | "SUSPENDED" | "REJECTED"

// ── Config ───────────────────────────────────────────────────────

const statusConfig: Record<VenueStatus, {
    label: string
    color: string
    icon: React.ComponentType<{ className?: string }>
}> = {
    ACTIVE:         { label: "Activo",     color: "bg-green-500/10 text-green-400 border-green-500/20",   icon: CheckCircle2 },
    PENDING_REVIEW: { label: "Pendiente",  color: "bg-yellow-500/10 text-yellow-400 border-yellow-500/20", icon: Clock },
    SUSPENDED:      { label: "Suspendido", color: "bg-orange-500/10 text-orange-400 border-orange-500/20", icon: Pause },
    REJECTED:       { label: "Rechazado",  color: "bg-red-500/10 text-red-400 border-red-500/20",          icon: XCircle },
}

// ── Component ────────────────────────────────────────────────────

export function OwnerVenuesClient({ user, venues: initialVenues }: Props) {
    const [venues, setVenues] = useState<VenueWithCount[]>(initialVenues)
    const [formOpen, setFormOpen] = useState(false)
    const [editingVenue, setEditingVenue] = useState<VenueWithCount | null>(null)
    const [imageManagerVenue, setImageManagerVenue] = useState<VenueWithCount | null>(null)
    const [errorMsg, setErrorMsg] = useState<string | null>(null)
    const [actionLoading, setActionLoading] = useState(false)
    const [confirmDialog, setConfirmDialog] = useState<{
        open: boolean
        venue: VenueWithCount | null
        action: "suspend" | "reactivate"
    }>({ open: false, venue: null, action: "suspend" })

    const activeCount  = useMemo(() => venues.filter((v) => v.status === "ACTIVE").length, [venues])
    const pendingCount = useMemo(() => venues.filter((v) => v.status === "PENDING_REVIEW").length, [venues])
    const totalPistas  = useMemo(() => venues.reduce((s, v) => s + v.resourceCount, 0), [venues])

    // ── Handlers ─────────────────────────────────────────────────

    const handleCreate = async (data: VenueFormData) => {
        setErrorMsg(null)
        try {
            const res = await fetch("/api/proxy/api/owner/venues", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    name: data.name,
                    description: data.description || undefined,
                    street: data.street,
                    city: data.city,
                    country: data.country,
                    postalCode: data.postalCode || undefined,
                    latitude: data.latitude,
                    longitude: data.longitude,
                }),
            })
            if (!res.ok) {
                const body = await res.json().catch(() => null)
                throw new Error(body?.message ?? `Error ${res.status}`)
            }
            const created: Venue = await res.json()
            setVenues((prev) => [{ ...created, resourceCount: 0 }, ...prev])
            setFormOpen(false)
        } catch (e) {
            setErrorMsg(e instanceof Error ? e.message : "Error creando venue")
        }
    }

    const handleEdit = async (data: VenueFormData) => {
        if (!editingVenue) return
        setErrorMsg(null)
        try {
            const res = await fetch(`/api/proxy/api/owner/venues/${editingVenue.id}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    name: data.name,
                    description: data.description || undefined,
                    street: data.street,
                    city: data.city,
                    country: data.country,
                    postalCode: data.postalCode || undefined,
                    latitude: data.latitude,
                    longitude: data.longitude,
                }),
            })
            if (!res.ok) {
                const body = await res.json().catch(() => null)
                throw new Error(body?.message ?? `Error ${res.status}`)
            }
            const updated: Venue = await res.json()
            setVenues((prev) =>
                prev.map((v) =>
                    v.id === editingVenue.id
                        ? { ...updated, resourceCount: v.resourceCount }
                        : v
                )
            )
            setEditingVenue(null)
            setFormOpen(false)
        } catch (e) {
            setErrorMsg(e instanceof Error ? e.message : "Error actualizando venue")
        }
    }

    const handleVenueImageUpdate = (updated: Venue) => {
        setVenues((prev) =>
            prev.map((v) =>
                v.id === updated.id
                    ? { ...updated, resourceCount: v.resourceCount }
                    : v
            )
        )
        // Sync imageManagerVenue so the dialog reflects the new images
        setImageManagerVenue((prev) =>
            prev?.id === updated.id ? { ...updated, resourceCount: prev.resourceCount } : prev
        )
    }

    const handleAction = async () => {
        if (!confirmDialog.venue) return
        setActionLoading(true)
        setErrorMsg(null)
        const { id } = confirmDialog.venue
        const endpoint = confirmDialog.action === "suspend"
            ? `/api/proxy/api/owner/venues/${id}/suspend`
            : `/api/proxy/api/owner/venues/${id}/reactivate`

        try {
            const res = await fetch(endpoint, { method: "PATCH" })
            if (!res.ok) {
                const body = await res.json().catch(() => null)
                throw new Error(body?.message ?? `Error ${res.status}`)
            }
            const newStatus = confirmDialog.action === "suspend" ? "SUSPENDED" : "ACTIVE"
            setVenues((prev) =>
                prev.map((v) => v.id === id ? { ...v, status: newStatus } : v)
            )
            setConfirmDialog({ open: false, venue: null, action: "suspend" })
        } catch (e) {
            setErrorMsg(e instanceof Error ? e.message : "Error ejecutando accion")
        } finally {
            setActionLoading(false)
        }
    }

    // ── Render ───────────────────────────────────────────────────

    return (
        <div className="flex min-h-screen bg-background">
            <OwnerSidebar user={user} />

            <main className="flex-1 min-w-0">
                <div className="mx-auto max-w-6xl px-4 py-6 lg:px-8 lg:py-8">

                    {/* Header */}
                    <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                        <div>
                            <h1 className="font-[var(--font-space-grotesk)] text-3xl font-bold tracking-tight text-foreground">
                                Mis Venues
                            </h1>
                            <p className="mt-1 text-sm text-muted-foreground">
                                {venues.length} venues · {activeCount} activos
                                {pendingCount > 0 && ` · ${pendingCount} pendientes`}
                            </p>
                        </div>
                        <Button
                            onClick={() => { setEditingVenue(null); setFormOpen(true) }}
                            className="gap-2 bg-primary font-medium text-primary-foreground hover:bg-primary/90"
                        >
                            <Plus className="h-4 w-4" />
                            Crear Venue
                        </Button>
                    </div>

                    {/* Error */}
                    {errorMsg && (
                        <div className="mt-6 rounded-xl border border-destructive/30 bg-destructive/5 p-4 text-sm text-destructive">
                            {errorMsg}
                        </div>
                    )}

                    {/* Stats */}
                    <div className="mt-6 grid grid-cols-2 gap-3 sm:grid-cols-4">
                        {[
                            { label: "Total",          value: venues.length, color: "text-foreground" },
                            { label: "Activos",        value: activeCount,   color: "text-green-400" },
                            { label: "Pendientes",     value: pendingCount,  color: "text-yellow-400" },
                            { label: "Pistas totales", value: totalPistas,   color: "text-primary" },
                        ].map(({ label, value, color }) => (
                            <div key={label} className="rounded-xl border border-border/30 bg-card p-4">
                                <p className="text-xs text-muted-foreground">{label}</p>
                                <p className={`mt-1 text-2xl font-bold ${color}`}>{value}</p>
                            </div>
                        ))}
                    </div>

                    {/* Venues list */}
                    <div className="mt-8 flex flex-col gap-4">
                        {venues.length === 0 ? (
                            <div className="rounded-xl border border-dashed border-border/50 bg-card/50 py-16 text-center">
                                <Building2 className="mx-auto h-12 w-12 text-muted-foreground/30" />
                                <h3 className="mt-4 font-[var(--font-space-grotesk)] text-lg font-semibold text-foreground">
                                    Aun no tienes venues
                                </h3>
                                <p className="mt-1 text-sm text-muted-foreground">
                                    Crea tu primer venue para empezar a recibir reservas.
                                </p>
                                <Button
                                    className="mt-4 gap-2 bg-primary text-primary-foreground hover:bg-primary/90"
                                    onClick={() => setFormOpen(true)}
                                >
                                    <Plus className="h-4 w-4" />
                                    Crear Venue
                                </Button>
                            </div>
                        ) : (
                            venues.map((venue) => {
                                const statusKey = venue.status in statusConfig
                                    ? (venue.status as VenueStatus)
                                    : "PENDING_REVIEW"
                                const status = statusConfig[statusKey]
                                const StatusIcon = status.icon
                                const mainImage = [...(venue.images ?? [])].sort(
                                    (a, b) => a.displayOrder - b.displayOrder
                                )[0]

                                return (
                                    <div
                                        key={venue.id}
                                        className="group flex flex-col overflow-hidden rounded-xl border border-border/50 bg-card transition-colors hover:border-primary/20 sm:flex-row"
                                    >
                                        {/* Image */}
                                        <div className="relative h-48 w-full shrink-0 bg-secondary/30 sm:h-auto sm:w-48 lg:w-56">
                                            {mainImage ? (
                                                <Image
                                                    src={mainImage.url}
                                                    alt={venue.name}
                                                    fill
                                                    className="object-cover"
                                                    sizes="(max-width: 640px) 100vw, 224px"
                                                />
                                            ) : (
                                                <div className="flex h-full items-center justify-center">
                                                    <Building2 className="h-10 w-10 text-muted-foreground/20" />
                                                </div>
                                            )}
                                        </div>

                                        {/* Content */}
                                        <div className="flex flex-1 flex-col gap-3 p-5">
                                            <div className="flex items-start justify-between gap-3">
                                                <div className="flex-1 min-w-0">
                                                    <div className="flex flex-wrap items-center gap-2">
                                                        <h3 className="font-[var(--font-space-grotesk)] text-lg font-bold text-foreground">
                                                            {venue.name}
                                                        </h3>
                                                        <Badge variant="outline" className={`${status.color} gap-1 text-xs`}>
                                                            <StatusIcon className="h-3 w-3" />
                                                            {status.label}
                                                        </Badge>
                                                    </div>
                                                    {venue.city && (
                                                        <div className="mt-1 flex items-center gap-1 text-sm text-muted-foreground">
                                                            <MapPin className="h-3.5 w-3.5 text-primary/70" />
                                                            {venue.street ? `${venue.street}, ` : ""}{venue.city}
                                                        </div>
                                                    )}
                                                </div>

                                                {/* Dropdown */}
                                                <DropdownMenu>
                                                    <DropdownMenuTrigger asChild>
                                                        <Button variant="ghost" size="icon" className="h-8 w-8 shrink-0 text-muted-foreground hover:text-foreground">
                                                            <MoreVertical className="h-4 w-4" />
                                                        </Button>
                                                    </DropdownMenuTrigger>
                                                    <DropdownMenuContent align="end" className="border-border bg-card text-card-foreground">
                                                        <DropdownMenuItem
                                                            className="cursor-pointer gap-2 text-foreground focus:bg-secondary/50"
                                                            onClick={() => { setEditingVenue(venue); setFormOpen(true) }}
                                                        >
                                                            <Edit className="h-4 w-4 text-muted-foreground" />
                                                            Editar
                                                        </DropdownMenuItem>
                                                        <DropdownMenuItem
                                                            className="cursor-pointer gap-2 text-foreground focus:bg-secondary/50"
                                                            onClick={() => setImageManagerVenue(venue)}
                                                        >
                                                            <ImagePlus className="h-4 w-4 text-muted-foreground" />
                                                            Gestionar imagenes
                                                        </DropdownMenuItem>
                                                        <DropdownMenuItem asChild>
                                                            <Link href={`/owner/resources?venue=${venue.id}`} className="cursor-pointer gap-2 text-foreground focus:bg-secondary/50">
                                                                <LayoutGrid className="h-4 w-4 text-muted-foreground" />
                                                                Ver pistas
                                                            </Link>
                                                        </DropdownMenuItem>
                                                        <DropdownMenuSeparator className="bg-border/50" />
                                                        {venue.status === "ACTIVE" && (
                                                            <DropdownMenuItem
                                                                className="cursor-pointer gap-2 text-orange-400 focus:bg-orange-500/10 focus:text-orange-400"
                                                                onClick={() => setConfirmDialog({ open: true, venue, action: "suspend" })}
                                                            >
                                                                <Pause className="h-4 w-4" />
                                                                Suspender
                                                            </DropdownMenuItem>
                                                        )}
                                                        {venue.status === "SUSPENDED" && (
                                                            <DropdownMenuItem
                                                                className="cursor-pointer gap-2 text-green-400 focus:bg-green-500/10 focus:text-green-400"
                                                                onClick={() => setConfirmDialog({ open: true, venue, action: "reactivate" })}
                                                            >
                                                                <Play className="h-4 w-4" />
                                                                Reactivar
                                                            </DropdownMenuItem>
                                                        )}
                                                    </DropdownMenuContent>
                                                </DropdownMenu>
                                            </div>

                                            {venue.description && (
                                                <p className="line-clamp-2 text-sm leading-relaxed text-muted-foreground">
                                                    {venue.description}
                                                </p>
                                            )}

                                            {venue.status === "REJECTED" && venue.rejectReason && (
                                                <div className="flex items-start gap-2 rounded-lg border border-red-500/20 bg-red-500/5 px-3 py-2.5">
                                                    <AlertTriangle className="mt-0.5 h-4 w-4 shrink-0 text-red-400" />
                                                    <div>
                                                        <p className="text-xs font-medium text-red-400">Motivo del rechazo</p>
                                                        <p className="mt-0.5 text-xs text-red-400/80">{venue.rejectReason}</p>
                                                    </div>
                                                </div>
                                            )}

                                            {/* Meta */}
                                            <div className="mt-auto flex flex-wrap items-center gap-3 pt-1">
                                                <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                                                    <LayoutGrid className="h-3.5 w-3.5 text-primary/70" />
                                                    {venue.resourceCount} {venue.resourceCount === 1 ? "pista" : "pistas"}
                                                </div>
                                                <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                                                    <Calendar className="h-3.5 w-3.5 text-primary/70" />
                                                    Creado {new Date(venue.createdAt ?? "").toLocaleDateString("es-ES", {
                                                    day: "numeric", month: "short", year: "numeric",
                                                })}
                                                </div>
                                                <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                                                    <MapPin className="h-3.5 w-3.5 text-primary/70" />
                                                    {venue.latitude.toFixed(4)}, {venue.longitude.toFixed(4)}
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                )
                            })
                        )}
                    </div>
                </div>
            </main>

            {/* Create / Edit dialog */}
            <VenueFormDialog
                open={formOpen}
                onOpenChange={(open) => { setFormOpen(open); if (!open) setEditingVenue(null) }}
                venue={editingVenue}
                onSubmit={editingVenue ? handleEdit : handleCreate}
            />

            {/* Image manager dialog */}
            {imageManagerVenue && (
                <VenueImageManager
                    venue={imageManagerVenue}
                    open={!!imageManagerVenue}
                    onOpenChange={(open) => { if (!open) setImageManagerVenue(null) }}
                    onUpdate={handleVenueImageUpdate}
                />
            )}

            {/* Confirm dialog */}
            <Dialog
                open={confirmDialog.open}
                onOpenChange={(open) => setConfirmDialog((prev) => ({ ...prev, open }))}
            >
                <DialogContent className="max-w-sm border-border bg-card text-card-foreground">
                    <DialogHeader>
                        <DialogTitle className="text-foreground">
                            {confirmDialog.action === "suspend" ? "Suspender venue" : "Reactivar venue"}
                        </DialogTitle>
                        <DialogDescription>
                            {confirmDialog.action === "suspend"
                                ? `"${confirmDialog.venue?.name}" dejara de ser visible para los jugadores.`
                                : `"${confirmDialog.venue?.name}" volvera a ser visible para los jugadores.`}
                        </DialogDescription>
                    </DialogHeader>
                    <DialogFooter>
                        <Button
                            variant="outline"
                            onClick={() => setConfirmDialog({ open: false, venue: null, action: "suspend" })}
                            className="border-border/60 text-foreground"
                            disabled={actionLoading}
                        >
                            Cancelar
                        </Button>
                        <Button
                            onClick={handleAction}
                            disabled={actionLoading}
                            className={
                                confirmDialog.action === "suspend"
                                    ? "bg-orange-500 text-white hover:bg-orange-600"
                                    : "bg-green-600 text-white hover:bg-green-700"
                            }
                        >
                            {actionLoading
                                ? "Procesando..."
                                : confirmDialog.action === "suspend" ? "Suspender" : "Reactivar"
                            }
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    )
}
