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
import {
    Ban,
    Building2,
    CheckCircle2,
    Eye,
    LayoutGrid,
    MapPin,
    MoreVertical,
    RotateCcw,
    Search,
    XCircle
} from "lucide-react"
import {AdminPagination} from "@/components/admin/admin-pagination"
import type {Venue} from "@/types"

const PAGE_SIZE = 8

interface Props {
    venues: Venue[]
}

type Action = "approve" | "reject" | "suspend" | "reactivate"

function StatusBadge({ status }: { status: Venue["status"] }) {
    const config: Record<string, { className: string; label: string }> = {
        ACTIVE:         { className: "bg-emerald-500/10 text-emerald-400", label: "Activo" },
        PENDING_REVIEW: { className: "bg-amber-500/10 text-amber-400",    label: "Pendiente" },
        SUSPENDED:      { className: "bg-red-500/10 text-red-400",        label: "Suspendido" },
        REJECTED:       { className: "bg-red-500/10 text-red-400",        label: "Rechazado" },
    }
    const c = config[status]
    return <Badge className={`border-0 text-[10px] font-medium ${c.className}`}>{c.label}</Badge>
}

function formatDate(d: string) {
    return new Date(d).toLocaleDateString("es-ES", { day: "numeric", month: "short", year: "numeric" })
}

export function AdminVenuesClient({ venues: initialVenues }: Props) {
    const [venues, setVenues] = useState<Venue[]>(initialVenues)
    const [search, setSearch] = useState("")
    const [statusFilter, setStatusFilter] = useState("ALL")
    const [page, setPage] = useState(0)
    const [detailVenue, setDetailVenue] = useState<Venue | null>(null)
    const [actionDialog, setActionDialog] = useState<{
        open: boolean
        venue: Venue | null
        action: Action
    }>({ open: false, venue: null, action: "approve" })
    const [rejectReason, setRejectReason] = useState("")
    const [actionLoading, setActionLoading] = useState(false)
    const [actionError, setActionError] = useState<string | null>(null)

    const filtered = useMemo(() =>
            venues.filter((v) => {
                const q = search.toLowerCase()
                const matchesSearch =
                    !q ||
                    v.name.toLowerCase().includes(q) ||
                    v.city?.toLowerCase().includes(q) ||
                    v.ownerId.toLowerCase().includes(q) ||
                    v.id.toLowerCase().includes(q)
                const matchesStatus = statusFilter === "ALL" || v.status === statusFilter
                return matchesSearch && matchesStatus
            }),
        [venues, search, statusFilter]
    )

    const totalPages = Math.ceil(filtered.length / PAGE_SIZE)
    const paged = filtered.slice(page * PAGE_SIZE, (page + 1) * PAGE_SIZE)

    const counts = useMemo(() => ({
        total:     venues.length,
        active:    venues.filter((v) => v.status === "ACTIVE").length,
        pending:   venues.filter((v) => v.status === "PENDING_REVIEW").length,
        suspended: venues.filter((v) => v.status === "SUSPENDED").length,
    }), [venues])

    // ── Actions ───────────────────────────────────────────────────

    const handleAction = async () => {
        if (!actionDialog.venue) return
        setActionLoading(true)
        setActionError(null)

        const { id } = actionDialog.venue
        const endpoints: Record<Action, string> = {
            approve:    `/api/proxy/api/admin/venues/${id}/approve`,
            reject:     `/api/proxy/api/admin/venues/${id}/reject`,
            suspend:    `/api/proxy/api/admin/venues/${id}/suspend`,
            reactivate: `/api/proxy/api/admin/venues/${id}/reactivate`,
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

            const newStatus: Venue["status"] =
                actionDialog.action === "approve" || actionDialog.action === "reactivate"
                    ? "ACTIVE"
                    : actionDialog.action === "reject"
                        ? "REJECTED"
                        : "SUSPENDED"

            setVenues((prev) =>
                prev.map((v) =>
                    v.id === id
                        ? {
                            ...v,
                            status: newStatus,
                            rejectReason: actionDialog.action === "reject" ? rejectReason : v.rejectReason,
                        }
                        : v
                )
            )
            setDetailVenue((prev) =>
                prev?.id === id ? { ...prev, status: newStatus } : prev
            )
            setActionDialog({ open: false, venue: null, action: "approve" })
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
                    Venues
                </h1>
                <p className="mt-1 text-sm text-muted-foreground">
                    Gestiona todas las instalaciones deportivas de la plataforma.
                </p>
            </div>

            {/* Stats */}
            <div className="grid gap-3 sm:grid-cols-4">
                {[
                    { label: "Total",       value: counts.total,     color: "text-foreground" },
                    { label: "Activos",     value: counts.active,    color: "text-emerald-400" },
                    { label: "Pendientes",  value: counts.pending,   color: "text-amber-400" },
                    { label: "Suspendidos", value: counts.suspended, color: "text-red-400" },
                ].map((s) => (
                    <Card key={s.label} className="border-border/50 bg-card">
                        <CardContent className="flex items-center gap-3 p-4">
                            <Building2 className={`h-5 w-5 ${s.color}`} />
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
                        placeholder="Buscar por nombre, ciudad, owner ID..."
                        value={search}
                        onChange={(e) => { setSearch(e.target.value); setPage(0) }}
                        className="h-10 border-border/50 bg-secondary/30 pl-9 text-foreground placeholder:text-muted-foreground"
                    />
                </div>
                <Select value={statusFilter} onValueChange={(v) => { setStatusFilter(v); setPage(0) }}>
                    <SelectTrigger className="h-10 w-full border-border/50 bg-secondary/30 text-foreground sm:w-[160px]">
                        <SelectValue placeholder="Estado" />
                    </SelectTrigger>
                    <SelectContent className="border-border bg-card text-card-foreground">
                        <SelectItem value="ALL">Todos</SelectItem>
                        <SelectItem value="ACTIVE">Activos</SelectItem>
                        <SelectItem value="PENDING_REVIEW">Pendientes</SelectItem>
                        <SelectItem value="SUSPENDED">Suspendidos</SelectItem>
                        <SelectItem value="REJECTED">Rechazados</SelectItem>
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
                                <th className="px-4 py-3 font-medium">Venue</th>
                                <th className="hidden px-4 py-3 font-medium md:table-cell">Ciudad</th>
                                <th className="hidden px-4 py-3 font-medium lg:table-cell">Owner ID</th>
                                <th className="px-4 py-3 font-medium">Pistas</th>
                                <th className="px-4 py-3 font-medium">Estado</th>
                                <th className="hidden px-4 py-3 font-medium lg:table-cell">Creado</th>
                                <th className="px-4 py-3 text-right font-medium">Acciones</th>
                            </tr>
                            </thead>
                            <tbody>
                            {paged.length === 0 ? (
                                <tr>
                                    <td colSpan={7} className="px-4 py-12 text-center text-muted-foreground">
                                        No se encontraron venues.
                                    </td>
                                </tr>
                            ) : (
                                paged.map((v) => (
                                    <tr key={v.id} className="border-b border-border/30 last:border-0 transition-colors hover:bg-secondary/20">
                                        <td className="px-4 py-3">
                                            <div className="flex items-center gap-3">
                                                <div className="relative h-10 w-14 shrink-0 overflow-hidden rounded-lg bg-secondary/30">
                                                    {v.images[0] ? (
                                                        <Image src={v.images[0].url} alt={v.name} fill className="object-cover" />
                                                    ) : (
                                                        <div className="flex h-full items-center justify-center">
                                                            <Building2 className="h-4 w-4 text-muted-foreground/40" />
                                                        </div>
                                                    )}
                                                </div>
                                                <div className="min-w-0">
                                                    <p className="truncate text-sm font-medium text-foreground">{v.name}</p>
                                                    <p className="truncate font-mono text-[10px] text-muted-foreground">
                                                        {v.id.slice(0, 16)}
                                                    </p>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="hidden px-4 py-3 md:table-cell">
                                            <div className="flex items-center gap-1 text-xs text-muted-foreground">
                                                <MapPin className="h-3 w-3" />
                                                {v.city}
                                            </div>
                                        </td>
                                        <td className="hidden px-4 py-3 lg:table-cell">
                                                <span className="font-mono text-xs text-muted-foreground">
                                                    {v.ownerId.slice(0, 20)}
                                                </span>
                                        </td>
                                        <td className="px-4 py-3">
                                            <div className="flex items-center gap-1 text-xs text-muted-foreground">
                                                <LayoutGrid className="h-3 w-3" />
                                                {(v as any).resourceCount ?? "—"}
                                            </div>
                                        </td>
                                        <td className="px-4 py-3">
                                            <StatusBadge status={v.status} />
                                        </td>
                                        <td className="hidden px-4 py-3 text-xs text-muted-foreground lg:table-cell">
                                            {formatDate(v.createdAt)}
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
                                                        onClick={() => setDetailVenue(v)}
                                                    >
                                                        <Eye className="h-3.5 w-3.5 text-muted-foreground" />
                                                        Ver detalle
                                                    </DropdownMenuItem>
                                                    {v.status === "PENDING_REVIEW" && (
                                                        <>
                                                            <DropdownMenuSeparator className="bg-border/50" />
                                                            <DropdownMenuItem
                                                                className="cursor-pointer gap-2 text-emerald-400 focus:bg-emerald-500/10"
                                                                onClick={() => setActionDialog({ open: true, venue: v, action: "approve" })}
                                                            >
                                                                <CheckCircle2 className="h-3.5 w-3.5" /> Aprobar
                                                            </DropdownMenuItem>
                                                            <DropdownMenuItem
                                                                className="cursor-pointer gap-2 text-destructive focus:bg-destructive/10"
                                                                onClick={() => setActionDialog({ open: true, venue: v, action: "reject" })}
                                                            >
                                                                <XCircle className="h-3.5 w-3.5" /> Rechazar
                                                            </DropdownMenuItem>
                                                        </>
                                                    )}
                                                    {v.status === "ACTIVE" && (
                                                        <>
                                                            <DropdownMenuSeparator className="bg-border/50" />
                                                            <DropdownMenuItem
                                                                className="cursor-pointer gap-2 text-destructive focus:bg-destructive/10"
                                                                onClick={() => setActionDialog({ open: true, venue: v, action: "suspend" })}
                                                            >
                                                                <Ban className="h-3.5 w-3.5" /> Suspender
                                                            </DropdownMenuItem>
                                                        </>
                                                    )}
                                                    {(v.status === "SUSPENDED" || v.status === "REJECTED") && (
                                                        <>
                                                            <DropdownMenuSeparator className="bg-border/50" />
                                                            <DropdownMenuItem
                                                                className="cursor-pointer gap-2 text-emerald-400 focus:bg-emerald-500/10"
                                                                onClick={() => setActionDialog({ open: true, venue: v, action: "reactivate" })}
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
            <Dialog open={!!detailVenue} onOpenChange={(open) => { if (!open) setDetailVenue(null) }}>
                <DialogContent className="max-h-[85vh] max-w-lg overflow-y-auto border-border bg-card text-card-foreground">
                    <DialogHeader>
                        <DialogTitle className="text-foreground">Detalle del Venue</DialogTitle>
                    </DialogHeader>
                    {detailVenue && (
                        <div className="flex flex-col gap-4">
                            {detailVenue.images.length > 0 && (
                                <div className="flex gap-2 overflow-x-auto pb-1">
                                    {detailVenue.images.map((img) => (
                                        <div key={img.id} className="relative h-32 w-48 shrink-0 overflow-hidden rounded-lg">
                                            <Image src={img.url} alt={detailVenue.name} fill className="object-cover" />
                                        </div>
                                    ))}
                                </div>
                            )}
                            <div className="flex items-center gap-3">
                                <h3 className="text-lg font-bold text-foreground">{detailVenue.name}</h3>
                                <StatusBadge status={detailVenue.status} />
                            </div>
                            <div className="grid gap-3 rounded-xl border border-border/50 bg-secondary/20 p-4 text-sm">
                                <div>
                                    <p className="text-xs font-medium text-muted-foreground">Descripcion</p>
                                    <p className="mt-0.5 leading-relaxed text-foreground">{detailVenue.description || "—"}</p>
                                </div>
                                <div className="grid grid-cols-2 gap-3">
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Direccion</p>
                                        <p className="mt-0.5 text-foreground">{detailVenue.street}, {detailVenue.postalCode}</p>
                                    </div>
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Ciudad</p>
                                        <p className="mt-0.5 text-foreground">{detailVenue.city}, {detailVenue.country}</p>
                                    </div>
                                </div>
                                <div className="grid grid-cols-2 gap-3">
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Lat / Lng</p>
                                        <p className="mt-0.5 font-mono text-xs text-foreground">
                                            {detailVenue.latitude}, {detailVenue.longitude}
                                        </p>
                                    </div>
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Owner ID</p>
                                        <p className="mt-0.5 font-mono text-xs text-foreground">
                                            {detailVenue.ownerId.slice(0, 20)}
                                        </p>
                                    </div>
                                </div>
                                <div className="grid grid-cols-2 gap-3">
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Pistas</p>
                                        <p className="mt-0.5 text-foreground">{(detailVenue as any).resourceCount ?? "—"}</p>
                                    </div>
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Creado</p>
                                        <p className="mt-0.5 text-foreground">{formatDate(detailVenue.createdAt)}</p>
                                    </div>
                                </div>
                                {detailVenue.rejectReason && (
                                    <div className="rounded-lg border border-red-500/20 bg-red-500/5 p-3">
                                        <p className="text-xs font-medium text-red-400">Motivo de rechazo</p>
                                        <p className="mt-0.5 text-sm text-red-300">{detailVenue.rejectReason}</p>
                                    </div>
                                )}
                                <p className="text-center font-mono text-[10px] text-muted-foreground/50">
                                    ID: {detailVenue.id}
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
                            {actionDialog.action === "approve"    && "Aprobar venue"}
                            {actionDialog.action === "reject"     && "Rechazar venue"}
                            {actionDialog.action === "suspend"    && "Suspender venue"}
                            {actionDialog.action === "reactivate" && "Reactivar venue"}
                        </DialogTitle>
                        <DialogDescription>{actionDialog.venue?.name}</DialogDescription>
                    </DialogHeader>

                    {actionDialog.action === "reject" && (
                        <div className="flex flex-col gap-2 py-2">
                            <Label className="text-sm font-medium text-foreground">Motivo del rechazo *</Label>
                            <Textarea
                                value={rejectReason}
                                onChange={(e) => setRejectReason(e.target.value)}
                                placeholder="Indica el motivo..."
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
