"use client"

import {useMemo, useState} from "react"
import {Badge} from "@/components/ui/badge"
import {Button} from "@/components/ui/button"
import {Card, CardContent} from "@/components/ui/card"
import {Dialog, DialogContent, DialogHeader, DialogTitle} from "@/components/ui/dialog"
import {Input} from "@/components/ui/input"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"
import {AlertTriangle, CalendarDays, CheckCircle2, Clock, Euro, Eye, Search, XCircle} from "lucide-react"
import {AdminPagination} from "@/components/admin/admin-pagination"
import type {Booking} from "@/types"

const PAGE_SIZE = 10

interface Props {
    bookings: Booking[]
}

function BookingStatusBadge({ status }: { status: Booking["status"] }) {
    const config: Record<string, { className: string; label: string; icon: React.ElementType }> = {
        CONFIRMED:       { className: "bg-emerald-500/10 text-emerald-400", label: "Confirmada",    icon: CheckCircle2 },
        PENDING_PAYMENT: { className: "bg-amber-500/10 text-amber-400",    label: "Pendiente pago", icon: Clock },
        CANCELLED:       { className: "bg-red-500/10 text-red-400",        label: "Cancelada",      icon: XCircle },
        PENDING_MATCH:   { className: "bg-yellow-500/10 text-yellow-400",  label: "Pendiente partido", icon: Clock },
    }
    const c = config[status] ?? { className: "bg-gray-500/10 text-gray-400", label: status, icon: Clock };
    return (
        <Badge className={`gap-1 border-0 text-[10px] font-medium ${c.className}`}>
            <c.icon className="h-2.5 w-2.5" />
            {c.label}
        </Badge>
    )
}

function PaymentBadge({ status }: { status: Booking["paymentStatus"] }) {
    const config: Record<string, { className: string; label: string }> = {
        PAID:     { className: "bg-emerald-500/10 text-emerald-400", label: "Pagado" },
        PENDING:  { className: "bg-amber-500/10 text-amber-400",    label: "Pendiente" },
        FAILED:   { className: "bg-red-500/10 text-red-400",        label: "Fallido" },
        REFUNDED: { className: "bg-blue-500/10 text-blue-400",      label: "Reembolsado" },
    }
    const c = config[status] ?? { className: "bg-gray-500/10 text-gray-400", label: status };
    return <Badge className={`border-0 text-[10px] font-medium ${c.className}`}>{c.label}</Badge>
}

function formatDate(d: string) {
    return new Date(d + (d.length === 10 ? "T00:00:00" : "")).toLocaleDateString("es-ES", {
        day: "numeric", month: "short", year: "numeric",
    })
}

export function AdminBookingsClient({ bookings }: Props) {
    const [search, setSearch] = useState("")
    const [statusFilter, setStatusFilter] = useState("ALL")
    const [payFilter, setPayFilter] = useState("ALL")
    const [page, setPage] = useState(0)
    const [detailBooking, setDetailBooking] = useState<Booking | null>(null)

    const filtered = useMemo(() => {
        return bookings.filter((b) => {
            const q = search.toLowerCase()
            const matchesSearch =
                !q ||
                b.id.toLowerCase().includes(q) ||
                b.venueName?.toLowerCase().includes(q) ||
                b.resourceName?.toLowerCase().includes(q) ||
                b.playerId?.toLowerCase().includes(q) ||
                b.venueCity?.toLowerCase().includes(q)
            const matchesStatus = statusFilter === "ALL" || b.status === statusFilter
            const matchesPay    = payFilter === "ALL"    || b.paymentStatus === payFilter
            return matchesSearch && matchesStatus && matchesPay
        })
    }, [bookings, search, statusFilter, payFilter])

    const totalPages = Math.ceil(filtered.length / PAGE_SIZE)
    const paged = filtered.slice(page * PAGE_SIZE, (page + 1) * PAGE_SIZE)

    const totalRevenue = bookings
        .filter((b) => b.paymentStatus === "PAID")
        .reduce((s, b) => s + Number(b.pricePaid), 0)

    const confirmed = bookings.filter((b) => b.status === "CONFIRMED").length
    const pending   = bookings.filter((b) => b.status === "PENDING_PAYMENT").length

    return (
        <div className="flex flex-col gap-6 p-4 lg:p-8">
            <div>
                <h1 className="font-[var(--font-space-grotesk)] text-2xl font-bold tracking-tight text-foreground lg:text-3xl">
                    Reservas
                </h1>
                <p className="mt-1 text-sm text-muted-foreground">
                    Todas las reservas realizadas en la plataforma.
                </p>
            </div>

            {/* Stats */}
            <div className="grid gap-3 sm:grid-cols-4">
                {[
                    { label: "Total reservas",   value: bookings.length,                            icon: CalendarDays, color: "text-foreground" },
                    { label: "Confirmadas",       value: confirmed,                                  icon: CheckCircle2, color: "text-emerald-400" },
                    { label: "Pendientes pago",   value: pending,                                    icon: Clock,        color: "text-amber-400" },
                    { label: "Ingresos pagados",  value: `${totalRevenue.toLocaleString("es-ES")} EUR`, icon: Euro,      color: "text-primary" },
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
                        placeholder="Buscar por venue, pista, jugador o ciudad..."
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
                        <SelectItem value="CONFIRMED">Confirmadas</SelectItem>
                        <SelectItem value="PENDING_PAYMENT">Pendiente pago</SelectItem>
                        <SelectItem value="CANCELLED">Canceladas</SelectItem>
                    </SelectContent>
                </Select>
                <Select value={payFilter} onValueChange={(v) => { setPayFilter(v); setPage(0) }}>
                    <SelectTrigger className="h-10 w-full border-border/50 bg-secondary/30 text-foreground sm:w-[150px]">
                        <SelectValue placeholder="Pago" />
                    </SelectTrigger>
                    <SelectContent className="border-border bg-card text-card-foreground">
                        <SelectItem value="ALL">Todos</SelectItem>
                        <SelectItem value="PAID">Pagado</SelectItem>
                        <SelectItem value="PENDING">Pendiente</SelectItem>
                        <SelectItem value="FAILED">Fallido</SelectItem>
                        <SelectItem value="REFUNDED">Reembolsado</SelectItem>
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
                                <th className="px-4 py-3 font-medium">ID</th>
                                <th className="px-4 py-3 font-medium">Venue / Pista</th>
                                <th className="hidden px-4 py-3 font-medium md:table-cell">Ciudad</th>
                                <th className="px-4 py-3 font-medium">Fecha</th>
                                <th className="hidden px-4 py-3 font-medium lg:table-cell">Hora</th>
                                <th className="px-4 py-3 font-medium">Precio</th>
                                <th className="px-4 py-3 font-medium">Estado</th>
                                <th className="hidden px-4 py-3 font-medium md:table-cell">Pago</th>
                                <th className="px-4 py-3 text-right font-medium">Accion</th>
                            </tr>
                            </thead>
                            <tbody>
                            {paged.length === 0 ? (
                                <tr>
                                    <td colSpan={9} className="px-4 py-12 text-center text-muted-foreground">
                                        No se encontraron reservas.
                                    </td>
                                </tr>
                            ) : (
                                paged.map((b) => (
                                    <tr key={b.id} className="border-b border-border/30 last:border-0 transition-colors hover:bg-secondary/20">
                                        <td className="px-4 py-3">
                                                <span className="font-mono text-xs text-muted-foreground">
                                                    {b.id.slice(0, 8)}
                                                </span>
                                        </td>
                                        <td className="px-4 py-3">
                                            <p className="text-xs font-medium text-foreground">{b.venueName ?? "—"}</p>
                                            <p className="text-[10px] text-muted-foreground">{b.resourceName ?? "—"}</p>
                                        </td>
                                        <td className="hidden px-4 py-3 text-xs text-muted-foreground md:table-cell">
                                            {b.venueCity ?? "—"}
                                        </td>
                                        <td className="px-4 py-3 text-xs text-foreground">
                                            {formatDate(b.bookingDate)}
                                        </td>
                                        <td className="hidden px-4 py-3 text-xs text-muted-foreground lg:table-cell">
                                            {b.startTime.slice(0, 5)} - {b.endTime.slice(0, 5)}
                                        </td>
                                        <td className="px-4 py-3 text-xs font-medium text-foreground">
                                            {b.pricePaid} {b.currency}
                                        </td>
                                        <td className="px-4 py-3">
                                            <BookingStatusBadge status={b.status} />
                                        </td>
                                        <td className="hidden px-4 py-3 md:table-cell">
                                            <PaymentBadge status={b.paymentStatus} />
                                        </td>
                                        <td className="px-4 py-3 text-right">
                                            <Button
                                                variant="ghost"
                                                size="icon"
                                                className="h-8 w-8 text-muted-foreground hover:text-foreground"
                                                onClick={() => setDetailBooking(b)}
                                            >
                                                <Eye className="h-4 w-4" />
                                            </Button>
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
            <Dialog open={!!detailBooking} onOpenChange={(open) => { if (!open) setDetailBooking(null) }}>
                <DialogContent className="max-w-md border-border bg-card text-card-foreground">
                    <DialogHeader>
                        <DialogTitle className="text-foreground">Detalle de Reserva</DialogTitle>
                    </DialogHeader>
                    {detailBooking && (
                        <div className="flex flex-col gap-4">
                            <div className="flex items-center justify-between">
                                <div className="flex items-center gap-2">
                                    <BookingStatusBadge status={detailBooking.status} />
                                    <PaymentBadge status={detailBooking.paymentStatus} />
                                </div>
                                <span className="font-mono text-xs text-muted-foreground">
                                    {detailBooking.id.slice(0, 16)}
                                </span>
                            </div>
                            <div className="grid gap-3 rounded-xl border border-border/50 bg-secondary/20 p-4 text-sm">
                                <div className="grid grid-cols-2 gap-3">
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Venue</p>
                                        <p className="mt-0.5 text-foreground">{detailBooking.venueName ?? "—"}</p>
                                    </div>
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Pista</p>
                                        <p className="mt-0.5 text-foreground">{detailBooking.resourceName ?? "—"}</p>
                                    </div>
                                </div>
                                <div className="grid grid-cols-2 gap-3">
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Ciudad</p>
                                        <p className="mt-0.5 text-foreground">{detailBooking.venueCity ?? "—"}</p>
                                    </div>
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Player ID</p>
                                        <p className="mt-0.5 font-mono text-xs text-foreground">
                                            {detailBooking.playerId?.slice(0, 16) ?? "—"}
                                        </p>
                                    </div>
                                </div>
                                <div className="grid grid-cols-2 gap-3">
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Fecha</p>
                                        <p className="mt-0.5 text-foreground">{formatDate(detailBooking.bookingDate)}</p>
                                    </div>
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Horario</p>
                                        <p className="mt-0.5 text-foreground">
                                            {detailBooking.startTime.slice(0, 5)} - {detailBooking.endTime.slice(0, 5)}
                                        </p>
                                    </div>
                                </div>
                                <div className="grid grid-cols-2 gap-3">
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Precio</p>
                                        <p className="mt-0.5 text-lg font-bold text-foreground">
                                            {detailBooking.pricePaid} {detailBooking.currency}
                                        </p>
                                    </div>
                                    <div>
                                        <p className="text-xs font-medium text-muted-foreground">Creada</p>
                                        <p className="mt-0.5 text-foreground">
                                            {detailBooking.createdAt ? formatDate(String(detailBooking.createdAt)) : "—"}
                                        </p>
                                    </div>
                                </div>
                                {detailBooking.cancelReason && (
                                    <div className="rounded-lg border border-red-500/20 bg-red-500/5 p-3">
                                        <p className="flex items-center gap-1 text-xs font-medium text-red-400">
                                            <AlertTriangle className="h-3 w-3" />
                                            Motivo cancelacion
                                        </p>
                                        <p className="mt-0.5 text-sm text-red-300">{detailBooking.cancelReason}</p>
                                    </div>
                                )}
                                {detailBooking.expiresAt && (
                                    <div className="rounded-lg border border-amber-500/20 bg-amber-500/5 p-3">
                                        <p className="flex items-center gap-1 text-xs font-medium text-amber-400">
                                            <Clock className="h-3 w-3" />
                                            Expira
                                        </p>
                                        <p className="mt-0.5 text-sm text-amber-300">
                                            {new Date(detailBooking.expiresAt).toLocaleString("es-ES")}
                                        </p>
                                    </div>
                                )}
                            </div>
                        </div>
                    )}
                </DialogContent>
            </Dialog>
        </div>
    )
}
