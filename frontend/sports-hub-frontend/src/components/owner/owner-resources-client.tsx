"use client"

import {useState} from "react"
import {OwnerSidebar} from "@/components/owner/owner-sidebar"
import type {ResourceFormData} from "@/components/owner/resource-form-dialog"
import {ResourceFormDialog} from "@/components/owner/resource-form-dialog"
import type {DayScheduleUpdate} from "@/components/owner/schedule-editor"
import {ScheduleEditor} from "@/components/owner/schedule-editor"
import {PriceRulesEditor} from "@/components/owner/price-rules-editor"
import {Button} from "@/components/ui/button"
import {Badge} from "@/components/ui/badge"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"
import {
    AlertTriangle,
    Building2,
    CheckCircle2,
    ChevronDown,
    ChevronUp,
    Clock,
    LayoutGrid,
    Pause,
    Play,
    Plus,
    Timer,
    XCircle,
} from "lucide-react"
import type {PriceRule, Resource, UserProfile, Venue} from "@/types"
import {ResourceImageUploader} from "@/components/owner/resource-image-uploader"

// ── Props ────────────────────────────────────────────────────────

interface Props {
    user: UserProfile
    venues: Venue[]
    resources: Resource[]
}

// ── Config ───────────────────────────────────────────────────────

const statusConfig: Record<Resource["status"], {
    label: string
    color: string
    icon: React.ComponentType<{ className?: string }>
}> = {
    ACTIVE:         { label: "Activa",     color: "bg-green-500/10 text-green-400 border-green-500/20",   icon: CheckCircle2 },
    PENDING_REVIEW: { label: "Pendiente",  color: "bg-yellow-500/10 text-yellow-400 border-yellow-500/20", icon: Clock },
    SUSPENDED:      { label: "Suspendida", color: "bg-orange-500/10 text-orange-400 border-orange-500/20", icon: Pause },
    REJECTED:       { label: "Rechazada",  color: "bg-red-500/10 text-red-400 border-red-500/20",          icon: XCircle },
}

// ── Component ────────────────────────────────────────────────────

export function OwnerResourcesClient({ user, venues, resources: initialResources }: Props) {
    const [resources, setResources] = useState<Resource[]>(initialResources)
    const [createOpen, setCreateOpen] = useState(false)
    const [filterVenue, setFilterVenue] = useState("all")
    const [expandedResource, setExpandedResource] = useState<string | null>(null)
    const [confirmDialog, setConfirmDialog] = useState<{
        open: boolean
        resource: Resource | null
        action: "suspend" | "reactivate"
    }>({ open: false, resource: null, action: "suspend" })
    const [acting, setActing] = useState(false)

    const filtered = filterVenue === "all"
        ? resources
        : resources.filter((r) => r.venueId === filterVenue)

    const venueName = (venueId: string) =>
        venues.find((v) => v.id === venueId)?.name ?? "Venue"

    // ── Handlers ─────────────────────────────────────────────────

    const handleCreate = async (data: ResourceFormData) => {
        try {
            const res = await fetch(`/api/proxy/api/owner/venues/${data.venueId}/resources`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    name: data.name,
                    description: data.description || undefined,
                    type: data.type,
                    slotDurationMinutes: data.slotDurationMinutes,
                }),
            })
            if (!res.ok) throw new Error(`Error ${res.status}`)
            const created: Resource = await res.json()
            setResources((prev) => [created, ...prev])
        } catch (e) {
            console.error(e)
        }
    }

    const handleSaveSchedule = async (resourceId: string, updates: DayScheduleUpdate[]) => {
        try {
            // Enviamos los 7 días en paralelo:
            // openingTime !== null → PUT upsert
            // openingTime === null → PUT con null → backend hace DELETE
            await Promise.all(
                updates.map((u) =>
                    fetch(`/api/proxy/api/owner/resources/${resourceId}/schedules`, {
                        method: "PUT",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({
                            dayOfWeek: u.dayOfWeek,
                            openingTime: u.openingTime,
                            closingTime: u.closingTime,
                        }),
                    })
                )
            )
            // Actualizar estado local: solo los días con horario (no null)
            setResources((prev) =>
                prev.map((r) =>
                    r.id === resourceId
                        ? {
                            ...r,
                            schedules: updates
                                .filter((u) => u.openingTime !== null)
                                .map((u) => ({
                                    dayOfWeek: u.dayOfWeek,
                                    openingTime: u.openingTime!,
                                    closingTime: u.closingTime!,
                                })),
                        }
                        : r
                )
            )
        } catch (e) {
            console.error(e)
        }
    }

    const handleAddPriceRule = async (resourceId: string, rule: Omit<PriceRule, "id">) => {
        try {
            const res = await fetch(`/api/proxy/api/owner/resources/${resourceId}/price-rules`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(rule),
            })
            if (!res.ok) throw new Error(`Error ${res.status}`)
            const updated: Resource = await res.json()
            setResources((prev) => prev.map((r) => (r.id === resourceId ? updated : r)))
        } catch (e) {
            console.error(e)
        }
    }

    const handleDeletePriceRule = async (resourceId: string, ruleId: string) => {
        try {
            await fetch(`/api/proxy/api/owner/resources/${resourceId}/price-rules/${ruleId}`, {
                method: "DELETE",
            })
            setResources((prev) =>
                prev.map((r) =>
                    r.id === resourceId
                        ? { ...r, priceRules: r.priceRules.filter((pr) => pr.id !== ruleId) }
                        : r
                )
            )
        } catch (e) {
            console.error(e)
        }
    }

    const handleAction = async () => {
        if (!confirmDialog.resource) return
        const { id } = confirmDialog.resource
        const endpoint = confirmDialog.action === "suspend"
            ? `/api/proxy/api/owner/resources/${id}/suspend`
            : `/api/proxy/api/owner/resources/${id}/reactivate`

        setActing(true)
        try {
            const res = await fetch(endpoint, { method: "PATCH" })
            if (!res.ok) throw new Error(`Error ${res.status}`)
            setResources((prev) =>
                prev.map((r) =>
                    r.id === id
                        ? { ...r, status: confirmDialog.action === "suspend" ? "SUSPENDED" : "ACTIVE" }
                        : r
                )
            )
            setConfirmDialog({ open: false, resource: null, action: "suspend" })
        } catch (e) {
            console.error(e)
        } finally {
            setActing(false)
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
                                Pistas / Resources
                            </h1>
                            <p className="mt-1 text-sm text-muted-foreground">
                                {resources.length} pistas en {venues.length} venues
                            </p>
                        </div>
                        <div className="flex items-center gap-3">
                            <Select value={filterVenue} onValueChange={setFilterVenue}>
                                <SelectTrigger className="h-9 w-[200px] border-border/50 bg-secondary/30 text-sm text-foreground">
                                    <Building2 className="mr-2 h-3.5 w-3.5 text-primary" />
                                    <SelectValue placeholder="Filtrar por venue" />
                                </SelectTrigger>
                                <SelectContent className="border-border bg-card text-card-foreground">
                                    <SelectItem value="all">Todos los venues</SelectItem>
                                    {venues.map((v) => (
                                        <SelectItem key={v.id} value={v.id}>{v.name}</SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                            <Button
                                onClick={() => setCreateOpen(true)}
                                className="gap-2 bg-primary font-medium text-primary-foreground hover:bg-primary/90"
                            >
                                <Plus className="h-4 w-4" />
                                Crear Pista
                            </Button>
                        </div>
                    </div>

                    {/* Resources list */}
                    <div className="mt-8 flex flex-col gap-4">
                        {filtered.map((resource) => {
                            const status = statusConfig[resource.status as Resource["status"]]
                            const StatusIcon = status.icon
                            const isExpanded = expandedResource === resource.id

                            return (
                                <div key={resource.id} className="overflow-hidden rounded-xl border border-border/50 bg-card">
                                    {/* Header row */}
                                    <div className="flex items-center gap-4 p-5">
                                        <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-lg bg-primary/10">
                                            <LayoutGrid className="h-5 w-5 text-primary" />
                                        </div>

                                        <div className="flex-1 min-w-0">
                                            <div className="flex flex-wrap items-center gap-2">
                                                <h3 className="font-[var(--font-space-grotesk)] text-base font-bold text-foreground">
                                                    {resource.name}
                                                </h3>
                                                <Badge variant="outline" className={`${status.color} gap-1 text-xs`}>
                                                    <StatusIcon className="h-3 w-3" />
                                                    {status.label}
                                                </Badge>
                                                <Badge variant="secondary" className="border-0 bg-secondary/50 text-xs text-muted-foreground">
                                                    {resource.type}
                                                </Badge>
                                            </div>
                                            <div className="mt-1 flex flex-wrap items-center gap-3 text-xs text-muted-foreground">
                        <span className="flex items-center gap-1">
                          <Building2 className="h-3 w-3 text-primary/70" />
                            {venueName(resource.venueId)}
                        </span>
                                                <span className="flex items-center gap-1">
                          <Timer className="h-3 w-3 text-primary/70" />
                                                    {resource.slotDurationMinutes} min/slot
                        </span>
                                                <span className="flex items-center gap-1">
                          <Clock className="h-3 w-3 text-primary/70" />
                                                    {resource.schedules.length}/7 dias
                        </span>
                                            </div>
                                        </div>

                                        <div className="flex items-center gap-2">
                                            {resource.status === "ACTIVE" && (
                                                <Button
                                                    variant="ghost"
                                                    size="sm"
                                                    className="text-xs text-orange-400 hover:bg-orange-500/10 hover:text-orange-400"
                                                    onClick={() => setConfirmDialog({ open: true, resource, action: "suspend" })}
                                                >
                                                    <Pause className="mr-1 h-3 w-3" />
                                                    Suspender
                                                </Button>
                                            )}
                                            {resource.status === "SUSPENDED" && (
                                                <Button
                                                    variant="ghost"
                                                    size="sm"
                                                    className="text-xs text-green-400 hover:bg-green-500/10 hover:text-green-400"
                                                    onClick={() => setConfirmDialog({ open: true, resource, action: "reactivate" })}
                                                >
                                                    <Play className="mr-1 h-3 w-3" />
                                                    Reactivar
                                                </Button>
                                            )}
                                            <Button
                                                variant="ghost"
                                                size="sm"
                                                onClick={() => setExpandedResource(isExpanded ? null : resource.id)}
                                                className="gap-1 text-xs text-muted-foreground hover:text-foreground"
                                            >
                                                {isExpanded ? "Cerrar" : "Configurar"}
                                                {isExpanded ? <ChevronUp className="h-3 w-3" /> : <ChevronDown className="h-3 w-3" />}
                                            </Button>
                                        </div>
                                    </div>

                                    {/* Reject reason */}
                                    {resource.status === "REJECTED" && resource.rejectReason && (
                                        <div className="mx-5 mb-4 flex items-start gap-2 rounded-lg border border-red-500/20 bg-red-500/5 px-3 py-2.5">
                                            <AlertTriangle className="mt-0.5 h-4 w-4 shrink-0 text-red-400" />
                                            <p className="text-xs text-red-400/80">
                                                <span className="font-medium text-red-400">Motivo:</span>{" "}
                                                {resource.rejectReason}
                                            </p>
                                        </div>
                                    )}

                                    {/* Expanded panels */}
                                    {isExpanded && (
                                        <div className="border-t border-border/50 bg-secondary/5 p-5">
                                            <div className="grid gap-5 lg:grid-cols-2">
                                                <ScheduleEditor
                                                    schedules={resource.schedules}
                                                    onSave={(updates) => handleSaveSchedule(resource.id, updates)}
                                                />
                                                <PriceRulesEditor
                                                    priceRules={resource.priceRules}
                                                    onAdd={(rule) => handleAddPriceRule(resource.id, rule)}
                                                    onDelete={(ruleId) => handleDeletePriceRule(resource.id, ruleId)}
                                                />
                                            </div>

                                            <div className="mt-5">
                                                <ResourceImageUploader
                                                    resource={resource}
                                                    onUpdate={(updated) =>
                                                        setResources((prev) =>
                                                            prev.map((r) => (r.id === updated.id ? updated : r))
                                                        )
                                                    }
                                                />
                                            </div>
                                        </div>
                                    )}
                                </div>
                            )
                        })}

                        {filtered.length === 0 && (
                            <div className="flex flex-col items-center justify-center rounded-xl border border-dashed border-border/50 bg-card/50 py-20">
                                <LayoutGrid className="h-12 w-12 text-muted-foreground/30" />
                                <h3 className="mt-4 font-[var(--font-space-grotesk)] text-lg font-semibold text-foreground">
                                    Sin pistas
                                </h3>
                                <p className="mt-1 text-sm text-muted-foreground">
                                    {filterVenue !== "all"
                                        ? "Este venue no tiene pistas todavia."
                                        : "Crea tu primera pista para empezar a recibir reservas."}
                                </p>
                            </div>
                        )}
                    </div>
                </div>
            </main>

            {/* Create dialog */}
            <ResourceFormDialog
                open={createOpen}
                onOpenChange={setCreateOpen}
                venues={venues}
                preselectedVenueId={filterVenue !== "all" ? filterVenue : undefined}
                onSubmit={handleCreate}
            />

            {/* Confirm action dialog */}
            <Dialog
                open={confirmDialog.open}
                onOpenChange={(open) => setConfirmDialog((prev) => ({ ...prev, open }))}
            >
                <DialogContent className="max-w-sm border-border bg-card text-card-foreground">
                    <DialogHeader>
                        <DialogTitle className="text-foreground">
                            {confirmDialog.action === "suspend" ? "Suspender pista" : "Reactivar pista"}
                        </DialogTitle>
                        <DialogDescription>
                            {confirmDialog.action === "suspend"
                                ? `"${confirmDialog.resource?.name}" dejara de aceptar reservas.`
                                : `"${confirmDialog.resource?.name}" volvera a aceptar reservas.`}
                        </DialogDescription>
                    </DialogHeader>
                    <DialogFooter>
                        <Button
                            variant="outline"
                            onClick={() => setConfirmDialog({ open: false, resource: null, action: "suspend" })}
                            className="border-border/60 text-foreground"
                        >
                            Cancelar
                        </Button>
                        <Button
                            onClick={handleAction}
                            disabled={acting}
                            className={
                                confirmDialog.action === "suspend"
                                    ? "bg-orange-500 text-white hover:bg-orange-600"
                                    : "bg-green-600 text-white hover:bg-green-700"
                            }
                        >
                            {acting
                                ? "..."
                                : confirmDialog.action === "suspend" ? "Suspender" : "Reactivar"
                            }
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    )
}
