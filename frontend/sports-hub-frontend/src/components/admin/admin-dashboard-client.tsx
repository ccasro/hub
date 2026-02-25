"use client";

import {useState} from "react";
import Image from "next/image";
import {Badge} from "@/components/ui/badge";
import {Button} from "@/components/ui/button";
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";
import {Label} from "@/components/ui/label";
import {Textarea} from "@/components/ui/textarea";
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs";
import {
    AlertTriangle,
    Building2,
    CalendarDays,
    CheckCircle2,
    Clock,
    Euro,
    Eye,
    LayoutGrid,
    Mail,
    MapPin,
    Phone,
    Users,
    XCircle,
} from "lucide-react";
import type {AdminStats, AdminUserProfile, Booking, Resource, Venue} from "@/types";

// ── Props ────────────────────────────────────────────────────────
interface Props {
    stats: AdminStats;
    pendingVenues: Venue[];
    pendingResources: Resource[];
    pendingOwners: AdminUserProfile[];
    recentBookings: Booking[];
}

// ── Stat Card ────────────────────────────────────────────────────
function StatCard({
                      icon: Icon,
                      label,
                      value,
                      sub,
                      color = "primary",
                  }: {
    icon: React.ElementType;
    label: string;
    value: string | number;
    sub?: string;
    color?: "primary" | "amber" | "emerald" | "red";
}) {
    const colorMap = {
        primary: "bg-primary/10 text-primary",
        amber: "bg-amber-500/10 text-amber-400",
        emerald: "bg-emerald-500/10 text-emerald-400",
        red: "bg-red-500/10 text-red-400",
    };
    return (
        <Card className="border-border/50 bg-card">
            <CardContent className="flex items-center gap-4 p-4">
                <div className={`flex h-11 w-11 shrink-0 items-center justify-center rounded-xl ${colorMap[color]}`}>
                    <Icon className="h-5 w-5" />
                </div>
                <div className="min-w-0">
                    <p className="text-2xl font-bold tracking-tight text-foreground">{value}</p>
                    <p className="truncate text-xs text-muted-foreground">{label}</p>
                    {sub && <p className="mt-0.5 truncate text-[10px] text-muted-foreground/70">{sub}</p>}
                </div>
            </CardContent>
        </Card>
    );
}

// ── Reject Dialog ────────────────────────────────────────────────
function RejectDialog({
                          open,
                          onOpenChange,
                          itemName,
                          onConfirm,
                      }: {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    itemName: string;
    onConfirm: (reason: string) => void;
}) {
    const [reason, setReason] = useState("");

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="max-w-md border-border bg-card text-card-foreground">
                <DialogHeader>
                    <DialogTitle className="text-foreground">Rechazar: {itemName}</DialogTitle>
                    <DialogDescription>
                        Indica el motivo del rechazo. El propietario recibira esta informacion.
                    </DialogDescription>
                </DialogHeader>
                <div className="flex flex-col gap-2 py-2">
                    <Label className="text-sm font-medium text-foreground">Motivo del rechazo *</Label>
                    <Textarea
                        value={reason}
                        onChange={(e) => setReason(e.target.value)}
                        placeholder="Ej: Falta documentacion legal, la licencia de actividad no es valida..."
                        rows={3}
                        className="border-border/50 bg-secondary/30 text-foreground placeholder:text-muted-foreground"
                    />
                </div>
                <DialogFooter>
                    <Button variant="outline" onClick={() => onOpenChange(false)} className="border-border/60 text-foreground">
                        Cancelar
                    </Button>
                    <Button
                        variant="destructive"
                        disabled={!reason.trim()}
                        onClick={() => {
                            onConfirm(reason);
                            setReason("");
                            onOpenChange(false);
                        }}
                    >
                        Confirmar Rechazo
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}

// ── Detail Dialog ────────────────────────────────────────────────
function DetailDialog({
                          open,
                          onOpenChange,
                          title,
                          children,
                      }: {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    title: string;
    children: React.ReactNode;
}) {
    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="max-h-[85vh] max-w-lg overflow-y-auto border-border bg-card text-card-foreground">
                <DialogHeader>
                    <DialogTitle className="text-foreground">{title}</DialogTitle>
                </DialogHeader>
                {children}
            </DialogContent>
        </Dialog>
    );
}

// ── Main Component ───────────────────────────────────────────────
export function AdminDashboardClient({
                                         stats,
                                         pendingVenues,
                                         pendingResources,
                                         pendingOwners,
                                         recentBookings,
                                     }: Props) {
    const [rejectDialog, setRejectDialog] = useState<{
        open: boolean;
        name: string;
        type: "venue" | "resource" | "owner";
        id: string;
    }>({ open: false, name: "", type: "venue", id: "" });

    const [detailDialog, setDetailDialog] = useState<{
        open: boolean;
        title: string;
        content: React.ReactNode;
    }>({ open: false, title: "", content: null });

    const [approvedIds, setApprovedIds] = useState<Set<string>>(new Set());
    const [rejectedIds, setRejectedIds] = useState<Set<string>>(new Set());

    const handleApprove = async (id: string, type: "venue" | "resource" | "owner") => {
        const endpoints: Record<string, string> = {
            venue: `/api/proxy/api/admin/venues/${id}/approve`,
            resource: `/api/proxy/api/admin/resources/${id}/approve`,
            owner: `/api/proxy/api/admin/users/${id}/approve-owner`,
        };
        await fetch(endpoints[type], { method: "PATCH" });
        setApprovedIds((prev) => new Set(prev).add(id));
    };

    const handleReject = async (id: string, type: "venue" | "resource" | "owner", reason: string) => {
        const endpoints: Record<string, string> = {
            venue: `/api/proxy/api/admin/venues/${id}/reject`,
            resource: `/api/proxy/api/admin/resources/${id}/reject`,
            owner: `/api/proxy/api/admin/users/${id}/reject-owner`,
        };
        await fetch(endpoints[type], {
            method: "PATCH",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ reason }),
        });
        setRejectedIds((prev) => new Set(prev).add(id));
    };

    const isHandled = (id: string) => approvedIds.has(id) || rejectedIds.has(id);

    const formatDate = (d: string) =>
        new Date(d).toLocaleDateString("es-ES", {
            day: "numeric",
            month: "short",
            year: "numeric",
        });

    const totalPending = stats.pendingVenues + stats.pendingResources + stats.pendingOwnerRequests;

    return (
        <div className="flex flex-col gap-6 p-4 lg:p-8">
            {/* Header */}
            <div>
                <h1 className="font-[var(--font-space-grotesk)] text-2xl font-bold tracking-tight text-foreground lg:text-3xl">
                    Panel de Administracion
                </h1>
                <p className="mt-1 text-sm text-muted-foreground">
                    Gestiona solicitudes, venues, pistas y usuarios de la plataforma.
                </p>
            </div>

            {/* Alert banner */}
            {totalPending > 0 && (
                <div className="flex items-center gap-3 rounded-xl border border-amber-500/20 bg-amber-500/5 px-4 py-3">
                    <AlertTriangle className="h-5 w-5 shrink-0 text-amber-400" />
                    <p className="text-sm text-amber-300">
                        Tienes <span className="font-semibold">{totalPending}</span> solicitudes pendientes de revision.
                    </p>
                </div>
            )}

            {/* Stats row */}
            <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
                <StatCard
                    icon={Users}
                    label="Usuarios totales"
                    value={stats.totalUsers}
                    sub={`${stats.totalOwners} owners / ${stats.totalPlayers} players`}
                    color="primary"
                />
                <StatCard
                    icon={Building2}
                    label="Venues"
                    value={stats.totalVenues}
                    sub={`${stats.activeVenues} activos / ${stats.pendingVenues} pendientes`}
                    color="emerald"
                />
                <StatCard
                    icon={LayoutGrid}
                    label="Pistas"
                    value={stats.totalResources}
                    sub={`${stats.activeResources} activas / ${stats.pendingResources} pendientes`}
                    color="amber"
                />
                <StatCard
                    icon={Euro}
                    label="Ingresos este mes"
                    value={`${stats.revenueThisMonth.toLocaleString("es-ES")} EUR`}
                    sub={`${stats.totalBookings} reservas totales`}
                    color="primary"
                />
            </div>

            {/* Tabs */}
            <Tabs defaultValue="venues" className="w-full">
                <TabsList className="h-10 w-full justify-start gap-1 bg-secondary/30 px-1">
                    <TabsTrigger value="venues" className="gap-1.5 data-[state=active]:bg-card data-[state=active]:text-foreground">
                        <Building2 className="h-3.5 w-3.5" />
                        Venues
                        {stats.pendingVenues > 0 && (
                            <Badge className="ml-1 h-5 border-0 bg-amber-500/20 px-1.5 text-[10px] font-bold text-amber-400">
                                {stats.pendingVenues}
                            </Badge>
                        )}
                    </TabsTrigger>
                    <TabsTrigger value="resources" className="gap-1.5 data-[state=active]:bg-card data-[state=active]:text-foreground">
                        <LayoutGrid className="h-3.5 w-3.5" />
                        Pistas
                        {stats.pendingResources > 0 && (
                            <Badge className="ml-1 h-5 border-0 bg-amber-500/20 px-1.5 text-[10px] font-bold text-amber-400">
                                {stats.pendingResources}
                            </Badge>
                        )}
                    </TabsTrigger>
                    <TabsTrigger value="owners" className="gap-1.5 data-[state=active]:bg-card data-[state=active]:text-foreground">
                        <Users className="h-3.5 w-3.5" />
                        Owners
                        {stats.pendingOwnerRequests > 0 && (
                            <Badge className="ml-1 h-5 border-0 bg-amber-500/20 px-1.5 text-[10px] font-bold text-amber-400">
                                {stats.pendingOwnerRequests}
                            </Badge>
                        )}
                    </TabsTrigger>
                    <TabsTrigger value="bookings" className="gap-1.5 data-[state=active]:bg-card data-[state=active]:text-foreground">
                        <CalendarDays className="h-3.5 w-3.5" />
                        Reservas
                    </TabsTrigger>
                </TabsList>

                {/* ── VENUES TAB ── */}
                <TabsContent value="venues" className="mt-4">
                    <Card className="border-border/50 bg-card">
                        <CardHeader className="pb-3">
                            <CardTitle className="text-base text-foreground">Venues pendientes de aprobacion</CardTitle>
                            <CardDescription>Revisa la informacion del venue antes de aprobar o rechazar.</CardDescription>
                        </CardHeader>
                        <CardContent className="flex flex-col gap-3">
                            {pendingVenues.length === 0 ? (
                                <p className="py-8 text-center text-sm text-muted-foreground">No hay venues pendientes.</p>
                            ) : (
                                pendingVenues.map((venue) => (
                                    <div
                                        key={venue.id}
                                        className={`flex flex-col gap-3 rounded-xl border border-border/50 p-4 transition-colors sm:flex-row sm:items-center ${
                                            isHandled(venue.id) ? "opacity-50" : ""
                                        }`}
                                    >
                                        <div className="relative h-20 w-full shrink-0 overflow-hidden rounded-lg bg-secondary/30 sm:h-20 sm:w-28">
                                            {venue.images[0] ? (
                                                <Image src={venue.images[0].url} alt={venue.name} fill className="object-cover" />
                                            ) : (
                                                <div className="flex h-full items-center justify-center">
                                                    <Building2 className="h-6 w-6 text-muted-foreground/40" />
                                                </div>
                                            )}
                                        </div>
                                        <div className="min-w-0 flex-1">
                                            <div className="flex items-start justify-between gap-2">
                                                <div>
                                                    <p className="text-sm font-semibold text-foreground">{venue.name}</p>
                                                    <div className="mt-0.5 flex items-center gap-1 text-xs text-muted-foreground">
                                                        <MapPin className="h-3 w-3" />
                                                        {venue.city}, {venue.country}
                                                    </div>
                                                </div>
                                                <Badge className="shrink-0 border-0 bg-amber-500/10 text-[10px] font-medium text-amber-400">
                                                    <Clock className="mr-1 h-2.5 w-2.5" />
                                                    Pendiente
                                                </Badge>
                                            </div>
                                            {venue.description && (
                                                <p className="mt-1.5 line-clamp-2 text-xs leading-relaxed text-muted-foreground">
                                                    {venue.description}
                                                </p>
                                            )}
                                            <p className="mt-1 text-[10px] text-muted-foreground/60">
                                                Creado: {formatDate(venue.createdAt)} | Owner: {venue.ownerId}
                                            </p>
                                        </div>
                                        <div className="flex shrink-0 items-center gap-2 sm:flex-col">
                                            <Button
                                                size="sm"
                                                variant="ghost"
                                                className="h-8 gap-1 text-xs text-muted-foreground hover:text-foreground"
                                                onClick={() =>
                                                    setDetailDialog({
                                                        open: true,
                                                        title: venue.name,
                                                        content: <VenueDetail venue={venue} />,
                                                    })
                                                }
                                            >
                                                <Eye className="h-3 w-3" />
                                                Ver
                                            </Button>
                                            <Button
                                                size="sm"
                                                className="h-8 gap-1 bg-emerald-600 text-xs text-white hover:bg-emerald-700"
                                                disabled={isHandled(venue.id)}
                                                onClick={() => handleApprove(venue.id, "venue")}
                                            >
                                                <CheckCircle2 className="h-3 w-3" />
                                                Aprobar
                                            </Button>
                                            <Button
                                                size="sm"
                                                variant="destructive"
                                                className="h-8 gap-1 text-xs"
                                                disabled={isHandled(venue.id)}
                                                onClick={() =>
                                                    setRejectDialog({ open: true, name: venue.name, type: "venue", id: venue.id })
                                                }
                                            >
                                                <XCircle className="h-3 w-3" />
                                                Rechazar
                                            </Button>
                                        </div>
                                    </div>
                                ))
                            )}
                        </CardContent>
                    </Card>
                </TabsContent>

                {/* ── RESOURCES TAB ── */}
                <TabsContent value="resources" className="mt-4">
                    <Card className="border-border/50 bg-card">
                        <CardHeader className="pb-3">
                            <CardTitle className="text-base text-foreground">Pistas pendientes de aprobacion</CardTitle>
                            <CardDescription>Verifica que la pista cumple los requisitos antes de activarla.</CardDescription>
                        </CardHeader>
                        <CardContent className="flex flex-col gap-3">
                            {pendingResources.length === 0 ? (
                                <p className="py-8 text-center text-sm text-muted-foreground">No hay pistas pendientes.</p>
                            ) : (
                                pendingResources.map((resource) => (
                                    <div
                                        key={resource.id}
                                        className={`flex flex-col gap-3 rounded-xl border border-border/50 p-4 transition-colors sm:flex-row sm:items-center ${
                                            isHandled(resource.id) ? "opacity-50" : ""
                                        }`}
                                    >
                                        <div className="relative h-20 w-full shrink-0 overflow-hidden rounded-lg bg-secondary/30 sm:h-20 sm:w-28">
                                            {resource.images[0] ? (
                                                <Image src={resource.images[0].url} alt={resource.name} fill className="object-cover" />
                                            ) : (
                                                <div className="flex h-full items-center justify-center">
                                                    <LayoutGrid className="h-6 w-6 text-muted-foreground/40" />
                                                </div>
                                            )}
                                        </div>
                                        <div className="min-w-0 flex-1">
                                            <div className="flex items-start justify-between gap-2">
                                                <div>
                                                    <p className="text-sm font-semibold text-foreground">{resource.name}</p>
                                                    <div className="mt-0.5 flex flex-wrap items-center gap-2 text-xs text-muted-foreground">
                                                        <Badge variant="secondary" className="h-[18px] border-0 bg-secondary px-1.5 text-[10px] text-secondary-foreground">
                                                            {resource.type}
                                                        </Badge>
                                                        <span>{resource.slotDurationMinutes} min/slot</span>
                                                        <span>Venue: {resource.venueId}</span>
                                                    </div>
                                                </div>
                                                <Badge className="shrink-0 border-0 bg-amber-500/10 text-[10px] font-medium text-amber-400">
                                                    <Clock className="mr-1 h-2.5 w-2.5" />
                                                    Pendiente
                                                </Badge>
                                            </div>
                                            {resource.description && (
                                                <p className="mt-1.5 line-clamp-2 text-xs leading-relaxed text-muted-foreground">
                                                    {resource.description}
                                                </p>
                                            )}
                                            <p className="mt-1 text-[10px] text-muted-foreground/60">
                                                Creado: {formatDate(resource.createdAt)}
                                            </p>
                                        </div>
                                        <div className="flex shrink-0 items-center gap-2 sm:flex-col">
                                            <Button
                                                size="sm"
                                                className="h-8 gap-1 bg-emerald-600 text-xs text-white hover:bg-emerald-700"
                                                disabled={isHandled(resource.id)}
                                                onClick={() => handleApprove(resource.id, "resource")}
                                            >
                                                <CheckCircle2 className="h-3 w-3" />
                                                Aprobar
                                            </Button>
                                            <Button
                                                size="sm"
                                                variant="destructive"
                                                className="h-8 gap-1 text-xs"
                                                disabled={isHandled(resource.id)}
                                                onClick={() =>
                                                    setRejectDialog({ open: true, name: resource.name, type: "resource", id: resource.id })
                                                }
                                            >
                                                <XCircle className="h-3 w-3" />
                                                Rechazar
                                            </Button>
                                        </div>
                                    </div>
                                ))
                            )}
                        </CardContent>
                    </Card>
                </TabsContent>

                {/* ── OWNERS TAB ── */}
                <TabsContent value="owners" className="mt-4">
                    <Card className="border-border/50 bg-card">
                        <CardHeader className="pb-3">
                            <CardTitle className="text-base text-foreground">Solicitudes de rol Owner</CardTitle>
                            <CardDescription>Usuarios que han solicitado convertirse en propietarios.</CardDescription>
                        </CardHeader>
                        <CardContent className="flex flex-col gap-3">
                            {pendingOwners.length === 0 ? (
                                <p className="py-8 text-center text-sm text-muted-foreground">No hay solicitudes pendientes.</p>
                            ) : (
                                pendingOwners.map((owner) => (
                                    <div
                                        key={owner.id}
                                        className={`flex flex-col gap-3 rounded-xl border border-border/50 p-4 transition-colors sm:flex-row sm:items-center ${
                                            isHandled(owner.id) ? "opacity-50" : ""
                                        }`}
                                    >
                                        <div className="flex h-14 w-14 shrink-0 items-center justify-center rounded-full bg-amber-500/10 text-lg font-bold text-amber-400">
                                            {owner.displayName
                                                ?.split(" ")
                                                .map((n) => n[0])
                                                .join("")
                                                .slice(0, 2)}
                                        </div>
                                        <div className="min-w-0 flex-1">
                                            <div className="flex items-start justify-between gap-2">
                                                <p className="text-sm font-semibold text-foreground">{owner.displayName}</p>
                                                <Badge className="shrink-0 border-0 bg-amber-500/10 text-[10px] font-medium text-amber-400">
                                                    <Clock className="mr-1 h-2.5 w-2.5" />
                                                    Pendiente
                                                </Badge>
                                            </div>
                                            <div className="mt-1 flex flex-wrap items-center gap-3 text-xs text-muted-foreground">
                        <span className="flex items-center gap-1">
                          <Mail className="h-3 w-3" />
                            {owner.email}
                        </span>
                                                {owner.phoneNumber && (
                                                    <span className="flex items-center gap-1">
                            <Phone className="h-3 w-3" />
                                                        {owner.phoneNumber}
                          </span>
                                                )}
                                                {owner.city && (
                                                    <span className="flex items-center gap-1">
                            <MapPin className="h-3 w-3" />
                                                        {owner.city}
                          </span>
                                                )}
                                            </div>
                                            {owner.description && (
                                                <p className="mt-1.5 line-clamp-2 text-xs leading-relaxed text-muted-foreground">
                                                    {owner.description}
                                                </p>
                                            )}
                                            <div className="mt-1 flex flex-wrap gap-2">
                                                {owner.preferredSport && (
                                                    <Badge variant="secondary" className="h-[18px] border-0 bg-secondary px-1.5 text-[10px] text-secondary-foreground">
                                                        {owner.preferredSport}
                                                    </Badge>
                                                )}
                                                <span className="text-[10px] text-muted-foreground/60">
                          Registrado: {formatDate(owner.createdAt)} | Verificado:{" "}
                                                    {owner.emailVerified ? "Si" : "No"}
                        </span>
                                            </div>
                                        </div>
                                        <div className="flex shrink-0 items-center gap-2 sm:flex-col">
                                            <Button
                                                size="sm"
                                                className="h-8 gap-1 bg-emerald-600 text-xs text-white hover:bg-emerald-700"
                                                disabled={isHandled(owner.id)}
                                                onClick={() => handleApprove(owner.id, "owner")}
                                            >
                                                <CheckCircle2 className="h-3 w-3" />
                                                Aprobar Owner
                                            </Button>
                                            <Button
                                                size="sm"
                                                variant="destructive"
                                                className="h-8 gap-1 text-xs"
                                                disabled={isHandled(owner.id)}
                                                onClick={() =>
                                                    setRejectDialog({
                                                        open: true,
                                                        name: owner.displayName ?? owner.email,
                                                        type: "owner",
                                                        id: owner.id,
                                                    })
                                                }
                                            >
                                                <XCircle className="h-3 w-3" />
                                                Rechazar
                                            </Button>
                                        </div>
                                    </div>
                                ))
                            )}
                        </CardContent>
                    </Card>
                </TabsContent>

                {/* ── BOOKINGS TAB ── */}
                <TabsContent value="bookings" className="mt-4">
                    <Card className="border-border/50 bg-card">
                        <CardHeader className="pb-3">
                            <CardTitle className="text-base text-foreground">Reservas recientes</CardTitle>
                            <CardDescription>Ultimas reservas realizadas en la plataforma.</CardDescription>
                        </CardHeader>
                        <CardContent>
                            <div className="overflow-x-auto">
                                <table className="w-full text-sm">
                                    <thead>
                                    <tr className="border-b border-border/50 text-left text-xs text-muted-foreground">
                                        <th className="pb-2 pr-4 font-medium">Reserva</th>
                                        <th className="pb-2 pr-4 font-medium">Venue / Pista</th>
                                        <th className="pb-2 pr-4 font-medium">Fecha</th>
                                        <th className="pb-2 pr-4 font-medium">Hora</th>
                                        <th className="pb-2 pr-4 font-medium">Precio</th>
                                        <th className="pb-2 pr-4 font-medium">Estado</th>
                                        <th className="pb-2 font-medium">Pago</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {recentBookings.map((b) => (
                                        <tr key={b.id} className="border-b border-border/30 last:border-0">
                                            <td className="py-3 pr-4">
                          <span className="font-mono text-xs text-muted-foreground">
                            {b.id.slice(0, 8)}
                          </span>
                                            </td>
                                            <td className="py-3 pr-4">
                                                <p className="text-xs font-medium text-foreground">{b.venueName}</p>
                                                <p className="text-[10px] text-muted-foreground">{b.resourceName}</p>
                                            </td>
                                            <td className="py-3 pr-4 text-xs text-foreground">
                                                {formatDate(b.bookingDate)}
                                            </td>
                                            <td className="py-3 pr-4 text-xs text-muted-foreground">
                                                {b.startTime.slice(0, 5)} - {b.endTime.slice(0, 5)}
                                            </td>
                                            <td className="py-3 pr-4 text-xs font-medium text-foreground">
                                                {b.pricePaid} {b.currency}
                                            </td>
                                            <td className="py-3 pr-4">
                                                <BookingStatusBadge status={b.status} />
                                            </td>
                                            <td className="py-3">
                                                <PaymentStatusBadge status={b.paymentStatus} />
                                            </td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            </div>
                        </CardContent>
                    </Card>
                </TabsContent>
            </Tabs>

            {/* Reject dialog */}
            <RejectDialog
                open={rejectDialog.open}
                onOpenChange={(open) => setRejectDialog((prev) => ({ ...prev, open }))}
                itemName={rejectDialog.name}
                onConfirm={(reason) => handleReject(rejectDialog.id, rejectDialog.type, reason)}
            />

            {/* Detail dialog */}
            <DetailDialog
                open={detailDialog.open}
                onOpenChange={(open) => setDetailDialog((prev) => ({ ...prev, open }))}
                title={detailDialog.title}
            >
                {detailDialog.content}
            </DetailDialog>
        </div>
    );
}

// ── Sub-components ───────────────────────────────────────────────

function VenueDetail({ venue }: { venue: Venue }) {
    return (
        <div className="flex flex-col gap-4">
            {venue.images.length > 0 && (
                <div className="flex gap-2 overflow-x-auto pb-1">
                    {venue.images.map((img) => (
                        <div key={img.id} className="relative h-32 w-48 shrink-0 overflow-hidden rounded-lg">
                            <Image src={img.url} alt={venue.name} fill className="object-cover" />
                        </div>
                    ))}
                </div>
            )}
            <div className="grid gap-3 text-sm">
                <div>
                    <p className="text-xs font-medium text-muted-foreground">Descripcion</p>
                    <p className="mt-0.5 text-foreground">{venue.description || "Sin descripcion"}</p>
                </div>
                <div className="grid grid-cols-2 gap-3">
                    <div>
                        <p className="text-xs font-medium text-muted-foreground">Direccion</p>
                        <p className="mt-0.5 text-foreground">{venue.street}, {venue.postalCode}</p>
                    </div>
                    <div>
                        <p className="text-xs font-medium text-muted-foreground">Ciudad</p>
                        <p className="mt-0.5 text-foreground">{venue.city}, {venue.country}</p>
                    </div>
                </div>
                <div className="grid grid-cols-2 gap-3">
                    <div>
                        <p className="text-xs font-medium text-muted-foreground">Latitud</p>
                        <p className="mt-0.5 font-mono text-xs text-foreground">{venue.latitude}</p>
                    </div>
                    <div>
                        <p className="text-xs font-medium text-muted-foreground">Longitud</p>
                        <p className="mt-0.5 font-mono text-xs text-foreground">{venue.longitude}</p>
                    </div>
                </div>
                <div className="grid grid-cols-2 gap-3">
                    <div>
                        <p className="text-xs font-medium text-muted-foreground">Owner ID</p>
                        <p className="mt-0.5 font-mono text-xs text-foreground">{venue.ownerId}</p>
                    </div>
                    <div>
                        <p className="text-xs font-medium text-muted-foreground">Creado</p>
                        <p className="mt-0.5 text-foreground">{new Date(venue.createdAt).toLocaleString("es-ES")}</p>
                    </div>
                </div>
            </div>
        </div>
    );
}

function BookingStatusBadge({ status }: { status: "PENDING_PAYMENT" | "CONFIRMED" | "CANCELLED" }) {
    const config = {
        CONFIRMED: { className: "bg-emerald-500/10 text-emerald-400", label: "Confirmada" },
        PENDING_PAYMENT: { className: "bg-amber-500/10 text-amber-400", label: "Pendiente pago" },
        CANCELLED: { className: "bg-red-500/10 text-red-400", label: "Cancelada" },
    };
    const c = config[status];
    return <Badge className={`border-0 text-[10px] font-medium ${c.className}`}>{c.label}</Badge>;
}

function PaymentStatusBadge({ status }: { status: "PENDING" | "PAID" | "FAILED" | "REFUNDED" }) {
    const config = {
        PAID: { className: "bg-emerald-500/10 text-emerald-400", label: "Pagado" },
        PENDING: { className: "bg-amber-500/10 text-amber-400", label: "Pendiente" },
        FAILED: { className: "bg-red-500/10 text-red-400", label: "Fallido" },
        REFUNDED: { className: "bg-blue-500/10 text-blue-400", label: "Reembolsado" },
    };
    const c = config[status];
    return <Badge className={`border-0 text-[10px] font-medium ${c.className}`}>{c.label}</Badge>;
}
