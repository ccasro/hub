"use client"

import {useMemo, useState} from "react"
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
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {
    CalendarDays,
    CheckCircle2,
    Clock,
    Crown,
    Eye,
    Mail,
    MapPin,
    MoreVertical,
    Phone,
    Search,
    ShieldOff,
    UserCheck,
    Users,
    UserX,
    XCircle,
} from "lucide-react"
import {AdminPagination} from "@/components/admin/admin-pagination"
import type {AdminUserProfile} from "@/types"

const PAGE_SIZE = 8

interface Props {
    users: AdminUserProfile[]
}

type Action = "suspend" | "activate" | "promote" | "demote"

function RoleBadge({ role }: { role: string }) {
    const config: Record<string, { className: string; label: string }> = {
        ADMIN:  { className: "bg-amber-500/10 text-amber-400",     label: "Admin" },
        OWNER:  { className: "bg-primary/10 text-primary",         label: "Owner" },
        PLAYER: { className: "bg-secondary text-secondary-foreground", label: "Player" },
    }
    const c = config[role] ?? config.PLAYER
    return <Badge className={`border-0 text-[10px] font-medium ${c.className}`}>{c.label}</Badge>
}

function StatusDot({ active }: { active: boolean }) {
    return <span className={`inline-block h-2 w-2 rounded-full ${active ? "bg-emerald-400" : "bg-red-400"}`} />
}

function formatDate(d: string) {
    return new Date(d).toLocaleDateString("es-ES", { day: "numeric", month: "short", year: "numeric" })
}

function initials(name: string | null | undefined) {
    return name ? name.split(" ").map((n) => n[0]).join("").toUpperCase().slice(0, 2) : "?"
}

const ACTION_LABELS: Record<Action, { title: string; desc: string; btn: string; variant: "default" | "destructive" }> = {
    suspend:  { title: "Suspender usuario",  desc: "El usuario no podra acceder a la plataforma.", btn: "Suspender",  variant: "destructive" },
    activate: { title: "Reactivar usuario",  desc: "El usuario volvera a tener acceso.",           btn: "Reactivar",  variant: "default" },
    promote:  { title: "Promover a Owner",   desc: "El usuario obtendra permisos de propietario.", btn: "Promover",   variant: "default" },
    demote:   { title: "Revocar rol Owner",  desc: "El usuario volvera a ser Player.",             btn: "Revocar",    variant: "destructive" },
}

export function AdminUsersClient({ users: initialUsers }: Props) {
    const [users, setUsers] = useState<AdminUserProfile[]>(initialUsers)
    const [search, setSearch] = useState("")
    const [roleFilter, setRoleFilter] = useState("ALL")
    const [statusFilter, setStatusFilter] = useState("ALL")
    const [page, setPage] = useState(0)
    const [detailUser, setDetailUser] = useState<AdminUserProfile | null>(null)
    const [confirmDialog, setConfirmDialog] = useState<{
        open: boolean
        user: AdminUserProfile | null
        action: Action
    }>({ open: false, user: null, action: "suspend" })
    const [actionLoading, setActionLoading] = useState(false)
    const [actionError, setActionError] = useState<string | null>(null)

    const filtered = useMemo(() =>
            users.filter((u) => {
                const q = search.toLowerCase()
                const matchesSearch =
                    !q ||
                    u.displayName?.toLowerCase().includes(q) ||
                    u.email.toLowerCase().includes(q) ||
                    u.city?.toLowerCase().includes(q) ||
                    u.id.toLowerCase().includes(q)
                const matchesRole   = roleFilter === "ALL"   || u.role === roleFilter
                const matchesStatus =
                    statusFilter === "ALL" ||
                    (statusFilter === "ACTIVE"     && u.active) ||
                    (statusFilter === "SUSPENDED"  && !u.active)
                return matchesSearch && matchesRole && matchesStatus
            }),
        [users, search, roleFilter, statusFilter]
    )

    const totalPages = Math.ceil(filtered.length / PAGE_SIZE)
    const paged = filtered.slice(page * PAGE_SIZE, (page + 1) * PAGE_SIZE)

    const counts = useMemo(() => ({
        total:   users.length,
        active:  users.filter((u) => u.active).length,
        owners:  users.filter((u) => u.role === "OWNER").length,
        pending: users.filter((u) => u.ownerRequestStatus === "PENDING").length,
    }), [users])

    // ── Actions ───────────────────────────────────────────────────

    const handleConfirmAction = async () => {
        if (!confirmDialog.user) return
        setActionLoading(true)
        setActionError(null)

        const { id } = confirmDialog.user
        const endpoints: Record<Action, string> = {
            suspend:  `/api/proxy/api/admin/users/${id}/suspend`,
            activate: `/api/proxy/api/admin/users/${id}/activate`,
            promote:  `/api/proxy/api/admin/users/${id}/approve-owner`,
            demote:   `/api/proxy/api/admin/users/${id}/revoke-owner`,
        }

        try {
            const res = await fetch(endpoints[confirmDialog.action], { method: "PATCH" })
            if (!res.ok) {
                const body = await res.json().catch(() => null)
                throw new Error(body?.detail || `Error ${res.status}`)
            }

            setUsers((prev) =>
                prev.map((u) => {
                    if (u.id !== id) return u
                    switch (confirmDialog.action) {
                        case "suspend":  return { ...u, active: false }
                        case "activate": return { ...u, active: true }
                        case "promote":  return { ...u, role: "OWNER", ownerRequestStatus: "APPROVED" }
                        case "demote":   return { ...u, role: "PLAYER", ownerRequestStatus: "NONE" }
                        default:         return u
                    }
                })
            )
            // Sync detail dialog
            setDetailUser((prev) => {
                if (prev?.id !== id) return prev
                switch (confirmDialog.action) {
                    case "suspend":  return { ...prev, active: false }
                    case "activate": return { ...prev, active: true }
                    case "promote":  return { ...prev, role: "OWNER", ownerRequestStatus: "APPROVED" }
                    case "demote":   return { ...prev, role: "PLAYER", ownerRequestStatus: "NONE" }
                    default:         return prev
                }
            })
            setConfirmDialog({ open: false, user: null, action: "suspend" })
        } catch (e) {
            setActionError(e instanceof Error ? e.message : "Error ejecutando accion")
        } finally {
            setActionLoading(false)
        }
    }

    const closeConfirmDialog = () => {
        setConfirmDialog((p) => ({ ...p, open: false }))
        setActionError(null)
    }

    // ── Render ────────────────────────────────────────────────────

    return (
        <div className="flex flex-col gap-6 p-4 lg:p-8">
            <div>
                <h1 className="font-[var(--font-space-grotesk)] text-2xl font-bold tracking-tight text-foreground lg:text-3xl">
                    Usuarios
                </h1>
                <p className="mt-1 text-sm text-muted-foreground">
                    Gestiona todos los usuarios registrados en la plataforma.
                </p>
            </div>

            {/* Stats */}
            <div className="grid gap-3 sm:grid-cols-4">
                {[
                    { label: "Total usuarios",         value: counts.total,   icon: Users,     color: "text-foreground" },
                    { label: "Activos",                value: counts.active,  icon: UserCheck, color: "text-emerald-400" },
                    { label: "Owners",                 value: counts.owners,  icon: Crown,     color: "text-primary" },
                    { label: "Solicitudes pendientes", value: counts.pending, icon: Clock,     color: "text-amber-400" },
                ].map((s) => (
                    <Card key={s.label} className="border-border/50 bg-card">
                        <CardContent className="flex items-center gap-3 p-4">
                            <s.icon className={`h-5 w-5 ${s.color}`} />
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
                        placeholder="Buscar por nombre, email, ciudad o ID..."
                        value={search}
                        onChange={(e) => { setSearch(e.target.value); setPage(0) }}
                        className="h-10 border-border/50 bg-secondary/30 pl-9 text-foreground placeholder:text-muted-foreground"
                    />
                </div>
                <Select value={roleFilter} onValueChange={(v) => { setRoleFilter(v); setPage(0) }}>
                    <SelectTrigger className="h-10 w-full border-border/50 bg-secondary/30 text-foreground sm:w-[140px]">
                        <SelectValue placeholder="Rol" />
                    </SelectTrigger>
                    <SelectContent className="border-border bg-card text-card-foreground">
                        <SelectItem value="ALL">Todos los roles</SelectItem>
                        <SelectItem value="PLAYER">Player</SelectItem>
                        <SelectItem value="OWNER">Owner</SelectItem>
                        <SelectItem value="ADMIN">Admin</SelectItem>
                    </SelectContent>
                </Select>
                <Select value={statusFilter} onValueChange={(v) => { setStatusFilter(v); setPage(0) }}>
                    <SelectTrigger className="h-10 w-full border-border/50 bg-secondary/30 text-foreground sm:w-[140px]">
                        <SelectValue placeholder="Estado" />
                    </SelectTrigger>
                    <SelectContent className="border-border bg-card text-card-foreground">
                        <SelectItem value="ALL">Todos</SelectItem>
                        <SelectItem value="ACTIVE">Activos</SelectItem>
                        <SelectItem value="SUSPENDED">Suspendidos</SelectItem>
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
                                <th className="px-4 py-3 font-medium">Usuario</th>
                                <th className="px-4 py-3 font-medium">Rol</th>
                                <th className="hidden px-4 py-3 font-medium md:table-cell">Ciudad</th>
                                <th className="hidden px-4 py-3 font-medium lg:table-cell">Registro</th>
                                <th className="px-4 py-3 font-medium">Estado</th>
                                <th className="px-4 py-3 text-right font-medium">Acciones</th>
                            </tr>
                            </thead>
                            <tbody>
                            {paged.length === 0 ? (
                                <tr>
                                    <td colSpan={6} className="px-4 py-12 text-center text-muted-foreground">
                                        No se encontraron usuarios.
                                    </td>
                                </tr>
                            ) : (
                                paged.map((u) => (
                                    <tr key={u.id} className="border-b border-border/30 last:border-0 transition-colors hover:bg-secondary/20">
                                        <td className="px-4 py-3">
                                            <div className="flex items-center gap-3">
                                                <Avatar className="h-9 w-9 border border-border/50">
                                                    <AvatarImage src={u.avatarUrl ?? undefined} alt={u.displayName ?? "Avatar"} />
                                                    <AvatarFallback className="bg-secondary text-xs font-semibold text-secondary-foreground">
                                                        {initials(u.displayName)}
                                                    </AvatarFallback>
                                                </Avatar>
                                                <div className="min-w-0">
                                                    <p className="truncate text-sm font-medium text-foreground">{u.displayName}</p>
                                                    <p className="truncate text-xs text-muted-foreground">{u.email}</p>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="px-4 py-3">
                                            <div className="flex flex-col gap-1">
                                                <RoleBadge role={u.role} />
                                                {u.ownerRequestStatus === "PENDING" && (
                                                    <Badge className="border-0 bg-amber-500/10 text-[9px] font-medium text-amber-400">
                                                        Solicitud Owner
                                                    </Badge>
                                                )}
                                            </div>
                                        </td>
                                        <td className="hidden px-4 py-3 text-xs text-muted-foreground md:table-cell">
                                            {u.city ?? "—"}
                                        </td>
                                        <td className="hidden px-4 py-3 text-xs text-muted-foreground lg:table-cell">
                                            {formatDate(u.createdAt)}
                                        </td>
                                        <td className="px-4 py-3">
                                            <div className="flex items-center gap-1.5">
                                                <StatusDot active={u.active} />
                                                <span className="text-xs text-muted-foreground">
                                                        {u.active ? "Activo" : "Suspendido"}
                                                    </span>
                                            </div>
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
                                                        onClick={() => setDetailUser(u)}
                                                    >
                                                        <Eye className="h-3.5 w-3.5 text-muted-foreground" />
                                                        Ver detalle
                                                    </DropdownMenuItem>
                                                    <DropdownMenuSeparator className="bg-border/50" />
                                                    {u.role === "PLAYER" && u.ownerRequestStatus === "PENDING" && (
                                                        <DropdownMenuItem
                                                            className="cursor-pointer gap-2 text-emerald-400 focus:bg-emerald-500/10"
                                                            onClick={() => setConfirmDialog({ open: true, user: u, action: "promote" })}
                                                        >
                                                            <Crown className="h-3.5 w-3.5" />
                                                            Aprobar como Owner
                                                        </DropdownMenuItem>
                                                    )}
                                                    {u.role === "PLAYER" && u.ownerRequestStatus !== "PENDING" && (
                                                        <DropdownMenuItem
                                                            className="cursor-pointer gap-2 text-primary focus:bg-primary/10"
                                                            onClick={() => setConfirmDialog({ open: true, user: u, action: "promote" })}
                                                        >
                                                            <Crown className="h-3.5 w-3.5" />
                                                            Promover a Owner
                                                        </DropdownMenuItem>
                                                    )}
                                                    {u.role === "OWNER" && (
                                                        <DropdownMenuItem
                                                            className="cursor-pointer gap-2 text-amber-400 focus:bg-amber-500/10"
                                                            onClick={() => setConfirmDialog({ open: true, user: u, action: "demote" })}
                                                        >
                                                            <ShieldOff className="h-3.5 w-3.5" />
                                                            Revocar rol Owner
                                                        </DropdownMenuItem>
                                                    )}
                                                    {u.role !== "ADMIN" && (
                                                        <>
                                                            <DropdownMenuSeparator className="bg-border/50" />
                                                            {u.active ? (
                                                                <DropdownMenuItem
                                                                    className="cursor-pointer gap-2 text-destructive focus:bg-destructive/10"
                                                                    onClick={() => setConfirmDialog({ open: true, user: u, action: "suspend" })}
                                                                >
                                                                    <UserX className="h-3.5 w-3.5" />
                                                                    Suspender usuario
                                                                </DropdownMenuItem>
                                                            ) : (
                                                                <DropdownMenuItem
                                                                    className="cursor-pointer gap-2 text-emerald-400 focus:bg-emerald-500/10"
                                                                    onClick={() => setConfirmDialog({ open: true, user: u, action: "activate" })}
                                                                >
                                                                    <UserCheck className="h-3.5 w-3.5" />
                                                                    Reactivar usuario
                                                                </DropdownMenuItem>
                                                            )}
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
            <Dialog open={!!detailUser} onOpenChange={(open) => { if (!open) setDetailUser(null) }}>
                <DialogContent className="max-h-[85vh] max-w-lg overflow-y-auto border-border bg-card text-card-foreground">
                    <DialogHeader>
                        <DialogTitle className="text-foreground">Detalle de usuario</DialogTitle>
                    </DialogHeader>
                    {detailUser && (
                        <div className="flex flex-col gap-5">
                            <div className="flex items-center gap-4">
                                <Avatar className="h-16 w-16 border-2 border-border/50">
                                    <AvatarImage src={detailUser.avatarUrl ?? undefined} alt={detailUser.displayName ?? "Avatar"} />
                                    <AvatarFallback className="bg-secondary text-lg font-bold text-secondary-foreground">
                                        {initials(detailUser.displayName)}
                                    </AvatarFallback>
                                </Avatar>
                                <div>
                                    <p className="text-lg font-bold text-foreground">{detailUser.displayName}</p>
                                    <div className="mt-1 flex items-center gap-2">
                                        <RoleBadge role={detailUser.role} />
                                        <div className="flex items-center gap-1.5">
                                            <StatusDot active={detailUser.active} />
                                            <span className="text-xs text-muted-foreground">
                                                {detailUser.active ? "Activo" : "Suspendido"}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div className="grid gap-4 rounded-xl border border-border/50 bg-secondary/20 p-4 text-sm">
                                <div className="grid grid-cols-2 gap-4">
                                    <div>
                                        <p className="flex items-center gap-1.5 text-xs font-medium text-muted-foreground">
                                            <Mail className="h-3 w-3" /> Email
                                        </p>
                                        <p className="mt-0.5 text-foreground">{detailUser.email}</p>
                                    </div>
                                    <div>
                                        <p className="flex items-center gap-1.5 text-xs font-medium text-muted-foreground">
                                            <Phone className="h-3 w-3" /> Telefono
                                        </p>
                                        <p className="mt-0.5 text-foreground">{detailUser.phoneNumber ?? "—"}</p>
                                    </div>
                                </div>
                                <div className="grid grid-cols-2 gap-4">
                                    <div>
                                        <p className="flex items-center gap-1.5 text-xs font-medium text-muted-foreground">
                                            <MapPin className="h-3 w-3" /> Ciudad
                                        </p>
                                        <p className="mt-0.5 text-foreground">
                                            {detailUser.city ?? "—"}{detailUser.countryCode ? `, ${detailUser.countryCode}` : ""}
                                        </p>
                                    </div>
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Deporte / Nivel</p>
                                        <p className="mt-0.5 text-foreground">
                                            {detailUser.preferredSport ?? "—"} / {detailUser.skillLevel ?? "—"}
                                        </p>
                                    </div>
                                </div>
                                <div className="grid grid-cols-2 gap-4">
                                    <div>
                                        <p className="flex items-center gap-1.5 text-xs font-medium text-muted-foreground">
                                            <CalendarDays className="h-3 w-3" /> Registro
                                        </p>
                                        <p className="mt-0.5 text-foreground">{formatDate(detailUser.createdAt)}</p>
                                    </div>
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Ultimo login</p>
                                        <p className="mt-0.5 text-foreground">
                                            {detailUser.lastLoginAt ? formatDate(detailUser.lastLoginAt) : "—"}
                                        </p>
                                    </div>
                                </div>
                                <div className="grid grid-cols-2 gap-4">
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Email verificado</p>
                                        <p className="mt-0.5 flex items-center gap-1 text-foreground">
                                            {detailUser.emailVerified
                                                ? <CheckCircle2 className="h-3.5 w-3.5 text-emerald-400" />
                                                : <XCircle className="h-3.5 w-3.5 text-red-400" />}
                                            {detailUser.emailVerified ? "Si" : "No"}
                                        </p>
                                    </div>
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Onboarding</p>
                                        <p className="mt-0.5 flex items-center gap-1 text-foreground">
                                            {detailUser.onboardingCompleted
                                                ? <CheckCircle2 className="h-3.5 w-3.5 text-emerald-400" />
                                                : <Clock className="h-3.5 w-3.5 text-amber-400" />}
                                            {detailUser.onboardingCompleted ? "Completado" : "Pendiente"}
                                        </p>
                                    </div>
                                </div>
                                {detailUser.ownerRequestStatus && detailUser.ownerRequestStatus !== "NONE" && (
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Solicitud Owner</p>
                                        <Badge className={`mt-1 border-0 text-[10px] font-medium ${
                                            detailUser.ownerRequestStatus === "PENDING"  ? "bg-amber-500/10 text-amber-400" :
                                                detailUser.ownerRequestStatus === "APPROVED" ? "bg-emerald-500/10 text-emerald-400" :
                                                    "bg-red-500/10 text-red-400"
                                        }`}>
                                            {detailUser.ownerRequestStatus}
                                        </Badge>
                                    </div>
                                )}
                                {detailUser.description && (
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Descripcion</p>
                                        <p className="mt-0.5 leading-relaxed text-foreground">{detailUser.description}</p>
                                    </div>
                                )}
                                <p className="text-center font-mono text-[10px] text-muted-foreground/50">
                                    ID: {detailUser.id}
                                </p>
                            </div>
                        </div>
                    )}
                </DialogContent>
            </Dialog>

            {/* Confirm action dialog */}
            <Dialog open={confirmDialog.open} onOpenChange={(open) => { if (!open) closeConfirmDialog() }}>
                <DialogContent className="max-w-sm border-border bg-card text-card-foreground">
                    <DialogHeader>
                        <DialogTitle className="text-foreground">
                            {ACTION_LABELS[confirmDialog.action].title}
                        </DialogTitle>
                        <DialogDescription>
                            {ACTION_LABELS[confirmDialog.action].desc}
                        </DialogDescription>
                    </DialogHeader>

                    {confirmDialog.user && (
                        <div className="flex items-center gap-3 rounded-lg border border-border/50 bg-secondary/20 p-3">
                            <Avatar className="h-10 w-10 border border-border/50">
                                <AvatarImage src={confirmDialog.user.avatarUrl ?? undefined} />
                                <AvatarFallback className="bg-secondary text-xs font-semibold text-secondary-foreground">
                                    {initials(confirmDialog.user.displayName)}
                                </AvatarFallback>
                            </Avatar>
                            <div>
                                <p className="text-sm font-medium text-foreground">{confirmDialog.user.displayName}</p>
                                <p className="text-xs text-muted-foreground">{confirmDialog.user.email}</p>
                            </div>
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
                            onClick={closeConfirmDialog}
                            disabled={actionLoading}
                            className="border-border/60 text-foreground"
                        >
                            Cancelar
                        </Button>
                        <Button
                            variant={ACTION_LABELS[confirmDialog.action].variant}
                            onClick={handleConfirmAction}
                            disabled={actionLoading}
                            className={
                                confirmDialog.action === "activate" || confirmDialog.action === "promote"
                                    ? "bg-emerald-600 text-white hover:bg-emerald-700"
                                    : ""
                            }
                        >
                            {actionLoading ? "Procesando..." : ACTION_LABELS[confirmDialog.action].btn}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    )
}
