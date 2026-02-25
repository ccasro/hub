// src/components/admin/admin-resources-client.tsx
"use client"

import {useMemo, useState} from "react"
import Image from "next/image"
import {Badge} from "@/components/ui/badge"
import {Button} from "@/components/ui/button"
import {Card, CardContent} from "@/components/ui/card"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import {Input} from "@/components/ui/input"
import {Textarea} from "@/components/ui/textarea"
import {Label} from "@/components/ui/label"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {Ban, CheckCircle2, Eye, LayoutGrid, MoreVertical, RotateCcw, Search, Timer, XCircle} from "lucide-react"
import {AdminPagination} from "@/components/admin/admin-pagination"
import type {Resource} from "@/types"

const PAGE_SIZE = 8

interface Props {
    resources: Resource[]
}

function StatusBadge({ status }: { status: Resource["status"] }) {
    const config: Record<string, { className: string; label: string }> = {
        ACTIVE:         { className: "bg-emerald-500/10 text-emerald-400", label: "Activo" },
        PENDING_REVIEW: { className: "bg-amber-500/10 text-amber-400",    label: "Pendiente" },
        SUSPENDED:      { className: "bg-red-500/10 text-red-400",        label: "Suspendido" },
        REJECTED:       { className: "bg-red-500/10 text-red-400",        label: "Rechazado" },
    }
    const c = config[status]
    return <Badge className={`border-0 text-[10px] font-medium ${c.className}`}>{c.label}</Badge>
}

function SportBadge({ type }: { type: string }) {
    return (
        <Badge variant="secondary" className="h-[18px] border-0 bg-secondary px-1.5 text-[10px] text-secondary-foreground">
            {type}
        </Badge>
    )
}

const DAY_LABELS: Record<string, string> = {
    MON: "Lun", TUE: "Mar", WED: "Mie", THU: "Jue", FRI: "Vie", SAT: "Sab", SUN: "Dom",
}

function formatDate(d: string) {
    return new Date(d).toLocaleDateString("es-ES", { day: "numeric", month: "short", year: "numeric" })
}

type Action = "approve" | "reject" | "suspend" | "reactivate"

export function AdminResourcesClient({ resources: initialResources }: Props) {
    const [resources, setResources] = useState<Resource[]>(initialResources)
    const [search, setSearch] = useState("")
    const [statusFilter, setStatusFilter] = useState("ALL")
    const [typeFilter, setTypeFilter] = useState("ALL")
    const [page, setPage] = useState(0)
    const [detailRes, setDetailRes] = useState<Resource | null>(null)
    const [actionDialog, setActionDialog] = useState<{
        open: boolean
        resource: Resource | null
        action: Action
    }>({ open: false, resource: null, action: "approve" })
    const [rejectReason, setRejectReason] = useState("")
    const [actionLoading, setActionLoading] = useState(false)
    const [actionError, setActionError] = useState<string | null>(null)

    const filtered = useMemo(() =>
            resources.filter((r) => {
                const q = search.toLowerCase()
                const matchesSearch =
                    !q ||
                    r.name.toLowerCase().includes(q) ||
                    r.venueId.toLowerCase().includes(q)
                const matchesStatus = statusFilter === "ALL" || r.status === statusFilter
                const matchesType   = typeFilter === "ALL"   || r.type === typeFilter
                return matchesSearch && matchesStatus && matchesType
            }),
        [resources, search, statusFilter, typeFilter]
    )

    const totalPages = Math.ceil(filtered.length / PAGE_SIZE)
    const paged = filtered.slice(page * PAGE_SIZE, (page + 1) * PAGE_SIZE)

    const counts = useMemo(() => ({
        total:   resources.length,
        active:  resources.filter((r) => r.status === "ACTIVE").length,
        pending: resources.filter((r) => r.status === "PENDING_REVIEW").length,
    }), [resources])

    // ── Actions ───────────────────────────────────────────────────

    const handleAction = async () => {
        if (!actionDialog.resource) return
        setActionLoading(true)
        setActionError(null)

        const { id } = actionDialog.resource
        const endpoints: Record<Action, string> = {
            approve:    `/api/proxy/api/admin/resources/${id}/approve`,
            reject:     `/api/proxy/api/admin/resources/${id}/reject`,
            suspend:    `/api/proxy/api/admin/resources/${id}/suspend`,
            reactivate: `/api/proxy/api/admin/resources/${id}/reactivate`,
        }

        try {
            const res = await fetch(endpoints[actionDialog.action], {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: actionDialog.action === "reject"
                    ? JSON.stringify({ reason: rejectReason })
                    : undefined,
            })
            if (!res.ok) {
                const body = await res.json().catch(() => null)
                throw new Error(body?.detail || `Error ${res.status}`)
            }

            // Actualizar estado local
            const newStatus: Resource["status"] =
                actionDialog.action === "approve" || actionDialog.action === "reactivate"
                    ? "ACTIVE"
                    : actionDialog.action === "reject"
                        ? "REJECTED"
                        : "SUSPENDED"

            setResources((prev) =>
                prev.map((r) =>
                    r.id === id
                        ? {
                            ...r,
                            status: newStatus,
                            rejectReason: actionDialog.action === "reject" ? rejectReason : r.rejectReason,
                        }
                        : r
                )
            )
            // Sync detail dialog if open
            setDetailRes((prev) =>
                prev?.id === id
                    ? { ...prev, status: newStatus }
                    : prev
            )
            setActionDialog({ open: false, resource: null, action: "approve" })
            setRejectReason("")
        } catch (e) {
            setActionError(e instanceof Error ? e.message : "Error ejecutando accion")
        } finally {
            setActionLoading(false)
        }
    }

    const closeActionDialog = () => {
        setActionDialog((p) => ({ ...p, open: false }))
        setRejectReason("")
        setActionError(null)
    }

    // ── Render ────────────────────────────────────────────────────

    return (
        <div className="flex flex-col gap-6 p-4 lg:p-8">
            <div>
                <h1 className="font-[var(--font-space-grotesk)] text-2xl font-bold tracking-tight text-foreground lg:text-3xl">
                    Pistas
                </h1>
                <p className="mt-1 text-sm text-muted-foreground">
                    Gestiona todas las pistas/recursos de la plataforma.
                </p>
            </div>

            {/* Stats */}
            <div className="grid gap-3 sm:grid-cols-3">
                {[
                    { label: "Total pistas", value: counts.total,   color: "text-foreground" },
                    { label: "Activas",       value: counts.active,  color: "text-emerald-400" },
                    { label: "Pendientes",    value: counts.pending, color: "text-amber-400" },
                ].map((s) => (
                    <Card key={s.label} className="border-border/50 bg-card">
                        <CardContent className="flex items-center gap-3 p-4">
                            <LayoutGrid className={`h-5 w-5 ${s.color}`} />
                            <div>
                                <p className="text-xl font-bold text-foreground">{s.value}</p>
                                <p className="text-xs text-muted-foreground">{s.label}</p>
                            </div>
                        </CardContent>
                    </Card>
                ))}
            </div>

            {/* Filters */}
            <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
                <div className="relative flex-1">
                    <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                    <Input
                        placeholder="Buscar por nombre o venue ID..."
                        value={search}
                        onChange={(e) => { setSearch(e.target.value); setPage(0) }}
                        className="h-10 border-border/50 bg-secondary/30 pl-9 text-foreground placeholder:text-muted-foreground"
                    />
                </div>
                <Select value={statusFilter} onValueChange={(v) => { setStatusFilter(v); setPage(0) }}>
                    <SelectTrigger className="h-10 w-full border-border/50 bg-secondary/30 text-foreground sm:w-[150px]">
                        <SelectValue placeholder="Estado" />
                    </SelectTrigger>
                    <SelectContent className="border-border bg-card text-card-foreground">
                        <SelectItem value="ALL">Todos</SelectItem>
                        <SelectItem value="ACTIVE">Activas</SelectItem>
                        <SelectItem value="PENDING_REVIEW">Pendientes</SelectItem>
                        <SelectItem value="SUSPENDED">Suspendidas</SelectItem>
                        <SelectItem value="REJECTED">Rechazadas</SelectItem>
                    </SelectContent>
                </Select>
                <Select value={typeFilter} onValueChange={(v) => { setTypeFilter(v); setPage(0) }}>
                    <SelectTrigger className="h-10 w-full border-border/50 bg-secondary/30 text-foreground sm:w-[130px]">
                        <SelectValue placeholder="Deporte" />
                    </SelectTrigger>
                    <SelectContent className="border-border bg-card text-card-foreground">
                        <SelectItem value="ALL">Todos</SelectItem>
                        <SelectItem value="PADEL">Padel</SelectItem>
                        <SelectItem value="TENNIS">Tennis</SelectItem>
                        <SelectItem value="SQUASH">Squash</SelectItem>
                        <SelectItem value="BADMINTON">Badminton</SelectItem>
                    </SelectContent>
                </Select>
            </div>

            {/* Table */}
            <Card className="border-border/50 bg-card">
                <CardContent className="p-0">
                    <div className="overflow-x-auto">
                        <table className="w-full text-sm">
                            <thead>
                            <tr className="border-b border-border/50 text-left text-xs text-muted-foreground">
                                <th className="px-4 py-3 font-medium">Pista</th>
                                <th className="hidden px-4 py-3 font-medium md:table-cell">Venue ID</th>
                                <th className="px-4 py-3 font-medium">Deporte</th>
                                <th className="hidden px-4 py-3 font-medium lg:table-cell">Duracion</th>
                                <th className="px-4 py-3 font-medium">Estado</th>
                                <th className="px-4 py-3 text-right font-medium">Acciones</th>
                            </tr>
                            </thead>
                            <tbody>
                            {paged.length === 0 ? (
                                <tr>
                                    <td colSpan={6} className="px-4 py-12 text-center text-muted-foreground">
                                        No se encontraron pistas.
                                    </td>
                                </tr>
                            ) : (
                                paged.map((r) => (
                                    <tr key={r.id} className="border-b border-border/30 last:border-0 transition-colors hover:bg-secondary/20">
                                        <td className="px-4 py-3">
                                            <div className="flex items-center gap-3">
                                                <div className="relative h-10 w-14 shrink-0 overflow-hidden rounded-lg bg-secondary/30">
                                                    {r.images[0] ? (
                                                        <Image src={r.images[0].url} alt={r.name} fill className="object-cover" />
                                                    ) : (
                                                        <div className="flex h-full items-center justify-center">
                                                            <LayoutGrid className="h-4 w-4 text-muted-foreground/40" />
                                                        </div>
                                                    )}
                                                </div>
                                                <div className="min-w-0">
                                                    <p className="truncate text-sm font-medium text-foreground">{r.name}</p>
                                                    <p className="truncate font-mono text-[10px] text-muted-foreground">
                                                        {r.id.slice(0, 16)}
                                                    </p>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="hidden px-4 py-3 md:table-cell">
                                                <span className="font-mono text-xs text-muted-foreground">
                                                    {r.venueId.slice(0, 16)}
                                                </span>
                                        </td>
                                        <td className="px-4 py-3">
                                            <SportBadge type={r.type} />
                                        </td>
                                        <td className="hidden px-4 py-3 lg:table-cell">
                                            <div className="flex items-center gap-1 text-xs text-muted-foreground">
                                                <Timer className="h-3 w-3" />
                                                {r.slotDurationMinutes} min
                                            </div>
                                        </td>
                                        <td className="px-4 py-3">
                                            <StatusBadge status={r.status} />
                                        </td>
                                        <td className="px-4 py-3 text-right">
                                            <DropdownMenu>
                                                <DropdownMenuTrigger asChild>
                                                    <Button variant="ghost" size="icon" className="h-8 w-8 text-muted-foreground hover:text-foreground">
                                                        <MoreVertical className="h-4 w-4" />
                                                    </Button>
                                                </DropdownMenuTrigger>
                                                <DropdownMenuContent align="end" className="w-48 border-border bg-card text-card-foreground">
                                                    <DropdownMenuItem
                                                        className="cursor-pointer gap-2 text-foreground focus:bg-secondary/50"
                                                        onClick={() => setDetailRes(r)}
                                                    >
                                                        <Eye className="h-3.5 w-3.5 text-muted-foreground" />
                                                        Ver detalle
                                                    </DropdownMenuItem>
                                                    {r.status === "PENDING_REVIEW" && (
                                                        <>
                                                            <DropdownMenuSeparator className="bg-border/50" />
                                                            <DropdownMenuItem
                                                                className="cursor-pointer gap-2 text-emerald-400 focus:bg-emerald-500/10"
                                                                onClick={() => setActionDialog({ open: true, resource: r, action: "approve" })}
                                                            >
                                                                <CheckCircle2 className="h-3.5 w-3.5" /> Aprobar
                                                            </DropdownMenuItem>
                                                            <DropdownMenuItem
                                                                className="cursor-pointer gap-2 text-destructive focus:bg-destructive/10"
                                                                onClick={() => setActionDialog({ open: true, resource: r, action: "reject" })}
                                                            >
                                                                <XCircle className="h-3.5 w-3.5" /> Rechazar
                                                            </DropdownMenuItem>
                                                        </>
                                                    )}
                                                    {r.status === "ACTIVE" && (
                                                        <>
                                                            <DropdownMenuSeparator className="bg-border/50" />
                                                            <DropdownMenuItem
                                                                className="cursor-pointer gap-2 text-destructive focus:bg-destructive/10"
                                                                onClick={() => setActionDialog({ open: true, resource: r, action: "suspend" })}
                                                            >
                                                                <Ban className="h-3.5 w-3.5" /> Suspender
                                                            </DropdownMenuItem>
                                                        </>
                                                    )}
                                                    {(r.status === "SUSPENDED" || r.status === "REJECTED") && (
                                                        <>
                                                            <DropdownMenuSeparator className="bg-border/50" />
                                                            <DropdownMenuItem
                                                                className="cursor-pointer gap-2 text-emerald-400 focus:bg-emerald-500/10"
                                                                onClick={() => setActionDialog({ open: true, resource: r, action: "reactivate" })}
                                                            >
                                                                <RotateCcw className="h-3.5 w-3.5" /> Reactivar
                                                            </DropdownMenuItem>
                                                        </>
                                                    )}
                                                </DropdownMenuContent>
                                            </DropdownMenu>
                                        </td>
                                    </tr>
                                ))
                            )}
                            </tbody>
                        </table>
                    </div>
                    {filtered.length > 0 && (
                        <div className="px-4 pb-4">
                            <AdminPagination
                                page={page}
                                totalPages={totalPages}
                                totalItems={filtered.length}
                                pageSize={PAGE_SIZE}
                                onPageChange={setPage}
                            />
                        </div>
                    )}
                </CardContent>
            </Card>

            {/* Detail dialog */}
            <Dialog open={!!detailRes} onOpenChange={(open) => { if (!open) setDetailRes(null) }}>
                <DialogContent className="max-h-[85vh] max-w-lg overflow-y-auto border-border bg-card text-card-foreground">
                    <DialogHeader>
                        <DialogTitle className="text-foreground">Detalle de la Pista</DialogTitle>
                    </DialogHeader>
                    {detailRes && (
                        <div className="flex flex-col gap-4">
                            {detailRes.images.length > 0 && (
                                <div className="flex gap-2 overflow-x-auto pb-1">
                                    {detailRes.images.map((img) => (
                                        <div key={img.id} className="relative h-32 w-48 shrink-0 overflow-hidden rounded-lg">
                                            <Image src={img.url} alt={detailRes.name} fill className="object-cover" />
                                        </div>
                                    ))}
                                </div>
                            )}
                            <div className="flex items-center gap-3">
                                <h3 className="text-lg font-bold text-foreground">{detailRes.name}</h3>
                                <StatusBadge status={detailRes.status} />
                                <SportBadge type={detailRes.type} />
                            </div>
                            <div className="grid gap-3 rounded-xl border border-border/50 bg-secondary/20 p-4 text-sm">
                                <div>
                                    <p className="text-xs font-medium text-muted-foreground">Descripcion</p>
                                    <p className="mt-0.5 leading-relaxed text-foreground">{detailRes.description || "—"}</p>
                                </div>
                                <div className="grid grid-cols-2 gap-3">
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Venue ID</p>
                                        <p className="mt-0.5 font-mono text-xs text-foreground">{detailRes.venueId.slice(0, 16)}</p>
                                    </div>
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Duracion slot</p>
                                        <p className="mt-0.5 text-foreground">{detailRes.slotDurationMinutes} min</p>
                                    </div>
                                </div>
                                {detailRes.schedules.length > 0 && (
                                    <div>
                                        <p className="mb-1 text-xs font-medium text-muted-foreground">Horarios</p>
                                        <div className="flex flex-wrap gap-1.5">
                                            {detailRes.schedules.map((s) => (
                                                <Badge key={s.dayOfWeek} variant="secondary" className="border-0 bg-secondary px-2 py-0.5 text-[10px] text-secondary-foreground">
                                                    {DAY_LABELS[s.dayOfWeek]} {s.openingTime.slice(0, 5)}-{s.closingTime.slice(0, 5)}
                                                </Badge>
                                            ))}
                                        </div>
                                    </div>
                                )}
                                {detailRes.priceRules.length > 0 && (
                                    <div>
                                        <p className="mb-1 text-xs font-medium text-muted-foreground">Tarifas</p>
                                        <div className="flex flex-col gap-1">
                                            {detailRes.priceRules.map((pr) => (
                                                <div key={pr.id} className="flex items-center justify-between text-xs">
                                                    <span className="text-muted-foreground">
                                                        {pr.dayType} {pr.startTime.slice(0, 5)}-{pr.endTime.slice(0, 5)}
                                                    </span>
                                                    <span className="font-medium text-foreground">{pr.price} {pr.currency}</span>
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                )}
                                {detailRes.rejectReason && (
                                    <div className="rounded-lg border border-red-500/20 bg-red-500/5 p-3">
                                        <p className="text-xs font-medium text-red-400">Motivo de rechazo</p>
                                        <p className="mt-0.5 text-sm text-red-300">{detailRes.rejectReason}</p>
                                    </div>
                                )}
                                <p className="text-center font-mono text-[10px] text-muted-foreground/50">
                                    Creado: {formatDate(detailRes.createdAt)}
                                </p>
                            </div>
                        </div>
                    )}
                </DialogContent>
            </Dialog>

            {/* Action dialog */}
            <Dialog open={actionDialog.open} onOpenChange={(open) => { if (!open) closeActionDialog() }}>
                <DialogContent className="max-w-sm border-border bg-card text-card-foreground">
                    <DialogHeader>
                        <DialogTitle className="text-foreground">
                            {actionDialog.action === "approve"    && "Aprobar pista"}
                            {actionDialog.action === "reject"     && "Rechazar pista"}
                            {actionDialog.action === "suspend"    && "Suspender pista"}
                            {actionDialog.action === "reactivate" && "Reactivar pista"}
                        </DialogTitle>
                        <DialogDescription>{actionDialog.resource?.name}</DialogDescription>
                    </DialogHeader>

                    {actionDialog.action === "reject" && (
                        <div className="flex flex-col gap-2 py-2">
                            <Label className="text-sm font-medium text-foreground">Motivo *</Label>
                            <Textarea
                                value={rejectReason}
                                onChange={(e) => setRejectReason(e.target.value)}
                                placeholder="Indica el motivo del rechazo..."
                                rows={3}
                                className="border-border/50 bg-secondary/30 text-foreground placeholder:text-muted-foreground"
                            />
                        </div>
                    )}

                    {actionError && (
                        <p className="rounded-lg border border-destructive/20 bg-destructive/5 px-3 py-2 text-xs text-destructive">
                            {actionError}
                        </p>
                    )}

                    <DialogFooter>
                        <Button
                            variant="outline"
                            onClick={closeActionDialog}
                            disabled={actionLoading}
                            className="border-border/60 text-foreground"
                        >
                            Cancelar
                        </Button>
                        <Button
                            onClick={handleAction}
                            disabled={actionLoading || (actionDialog.action === "reject" && !rejectReason.trim())}
                            className={
                                actionDialog.action === "approve" || actionDialog.action === "reactivate"
                                    ? "bg-emerald-600 text-white hover:bg-emerald-700"
                                    : "bg-destructive text-destructive-foreground hover:bg-destructive/90"
                            }
                        >
                            {actionLoading ? "Procesando..." : (
                                <>
                                    {actionDialog.action === "approve"    && "Aprobar"}
                                    {actionDialog.action === "reject"     && "Rechazar"}
                                    {actionDialog.action === "suspend"    && "Suspender"}
                                    {actionDialog.action === "reactivate" && "Reactivar"}
                                </>
                            )}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    )
}
