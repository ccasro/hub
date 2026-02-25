"use client"

import {useState} from "react"
import Link from "next/link"
import {Badge} from "@/components/ui/badge"
import {Button} from "@/components/ui/button"
import {Switch} from "@/components/ui/switch"
import {Label} from "@/components/ui/label"
import {Separator} from "@/components/ui/separator"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import {DashboardNavbar} from "@/components/dashboard/dashboard-navbar"
import {
    ArrowLeft,
    Bell,
    Building2,
    Check,
    ChevronRight,
    Crown,
    Globe,
    Loader2,
    Lock,
    Mail,
    Shield,
    ShieldAlert,
    Smartphone,
    Trash2,
} from "lucide-react"
import type {UserProfile} from "@/types"

// ── Types ────────────────────────────────────────────────────────

interface Props {
    user: UserProfile
    upcomingCount: number
}

type LocalSettings = {
    emailBookings: boolean
    emailMarketing: boolean
    emailMatching: boolean
    pushNotifications: boolean
    profileVisible: boolean
    showCity: boolean
    showLevel: boolean
}

// ── Constants ────────────────────────────────────────────────────

const SETTINGS_KEY = "sportsHub.settings.v1"

const DEFAULT_SETTINGS: LocalSettings = {
    emailBookings: true,
    emailMarketing: true,
    emailMatching: false,
    pushNotifications: true,
    profileVisible: true,
    showCity: true,
    showLevel: true,
}

// ── Helpers ──────────────────────────────────────────────────────

function loadLocalSettings(): LocalSettings {
    if (typeof window === "undefined") return DEFAULT_SETTINGS
    try {
        const raw = window.localStorage.getItem(SETTINGS_KEY)
        if (!raw) return DEFAULT_SETTINGS
        return { ...DEFAULT_SETTINGS, ...(JSON.parse(raw) as Partial<LocalSettings>) }
    } catch {
        return DEFAULT_SETTINGS
    }
}

function saveLocalSettings(s: LocalSettings) {
    if (typeof window === "undefined") return
    window.localStorage.setItem(SETTINGS_KEY, JSON.stringify(s))
}

// ── Component ────────────────────────────────────────────────────

export function SettingsClient({ user: initialUser, upcomingCount }: Props) {
    const [me, setMe] = useState<UserProfile>(initialUser)
    const [localSettings, setLocalSettings] = useState<LocalSettings>(() => loadLocalSettings())

    const [saving, setSaving] = useState(false)
    const [saved, setSaved] = useState(false)
    const [showOwnerDialog, setShowOwnerDialog] = useState(false)
    const [requesting, setRequesting] = useState(false)
    const [showDeleteDialog, setShowDeleteDialog] = useState(false)

    const ownerRequestStatus = me.ownerRequestStatus ?? "NONE"

    const setLS = (patch: Partial<LocalSettings>) => {
        setLocalSettings((prev) => ({ ...prev, ...patch }))
        setSaved(false)
    }

    const handleSave = async () => {
        setSaving(true)
        setSaved(false)
        try {
            saveLocalSettings(localSettings)
            setSaved(true)
            window.setTimeout(() => setSaved(false), 2000)
        } finally {
            setSaving(false)
        }
    }

    const handleOwnerRequest = async () => {
        setRequesting(true)
        try {
            await fetch("/api/proxy/api/me/request-owner", { method: "POST" })
            const res = await fetch("/api/proxy/api/me")
            const updated: UserProfile = await res.json()
            setMe(updated)
            setShowOwnerDialog(false)
        } catch (e) {
            console.error(e)
        } finally {
            setRequesting(false)
        }
    }

    const { emailBookings, emailMarketing, emailMatching, pushNotifications, profileVisible, showCity, showLevel } = localSettings

    return (
        <div className="flex min-h-screen flex-col bg-background">
            <DashboardNavbar
                user={me}
                selectedCity="Todas"
                onCityChange={() => {}}
                searchQuery=""
                onSearchChange={() => {}}
                upcomingBookingsCount={upcomingCount}
            />

            <main className="mx-auto w-full max-w-2xl flex-1 px-4 py-6 lg:px-6 lg:py-8">
                {/* Header */}
                <div className="mb-8 flex items-center gap-4">
                    <Link href="/dashboard">
                        <Button variant="ghost" size="icon" className="h-9 w-9 text-muted-foreground hover:text-foreground">
                            <ArrowLeft className="h-4 w-4" />
                        </Button>
                    </Link>
                    <div>
                        <h1 className="font-[var(--font-space-grotesk)] text-2xl font-bold tracking-tight text-foreground">
                            Configuracion
                        </h1>
                        <p className="mt-0.5 text-sm text-muted-foreground">
                            Gestiona tus preferencias, notificaciones y cuenta
                        </p>
                    </div>
                </div>

                {/* Owner request banner */}
                <section className="mb-6 overflow-hidden rounded-xl border border-primary/20 bg-primary/5">
                    <div className="flex items-start gap-4 p-5">
                        <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-primary/10">
                            <Crown className="h-5 w-5 text-primary" />
                        </div>
                        <div className="flex-1">
                            <h2 className="text-sm font-semibold text-foreground">Convertirte en propietario</h2>
                            <p className="mt-1 text-xs leading-relaxed text-muted-foreground">
                                Registra tus pistas, gestiona horarios y recibe reservas de jugadores de toda la plataforma.
                            </p>

                            {ownerRequestStatus === "NONE" && (
                                <Button
                                    size="sm"
                                    className="mt-3 gap-2 bg-primary text-primary-foreground hover:bg-primary/90"
                                    onClick={() => setShowOwnerDialog(true)}
                                >
                                    <Building2 className="h-3.5 w-3.5" />
                                    Solicitar acceso Owner
                                </Button>
                            )}

                            {ownerRequestStatus === "PENDING" && (
                                <div className="mt-3 flex items-center gap-2">
                                    <Badge className="border-0 bg-amber-500/10 text-amber-400 gap-1">
                                        <Loader2 className="h-3 w-3 animate-spin" />
                                        Solicitud pendiente
                                    </Badge>
                                    <span className="text-xs text-muted-foreground">El equipo de SportsHub revisara tu solicitud</span>
                                </div>
                            )}

                            {ownerRequestStatus === "APPROVED" && (
                                <div className="mt-3 flex items-center gap-2">
                                    <Badge className="border-0 bg-green-500/10 text-green-400 gap-1">
                                        <Check className="h-3 w-3" />
                                        Aprobado
                                    </Badge>
                                    <Link href="/owner/dashboard" className="text-xs font-medium text-primary hover:underline">
                                        Ir al panel de Owner
                                        <ChevronRight className="ml-0.5 inline h-3 w-3" />
                                    </Link>
                                </div>
                            )}

                            {ownerRequestStatus === "REJECTED" && (
                                <div className="mt-3 flex flex-col gap-2">
                                    <Badge className="w-fit border-0 bg-destructive/10 text-destructive gap-1">
                                        <ShieldAlert className="h-3 w-3" />
                                        Solicitud rechazada
                                    </Badge>
                                    <p className="text-xs text-muted-foreground">Puedes volver a solicitarlo si crees que fue un error.</p>
                                    <Button
                                        variant="outline"
                                        size="sm"
                                        className="w-fit gap-2 border-border/60 bg-secondary/30 text-foreground hover:bg-secondary/50 text-xs"
                                        onClick={() => setShowOwnerDialog(true)}
                                    >
                                        Reintentar solicitud
                                    </Button>
                                </div>
                            )}
                        </div>
                    </div>
                </section>

                {/* Email notifications */}
                <section className="mb-6 rounded-xl border border-border/50 bg-card p-6">
                    <div className="flex items-center gap-2 mb-1">
                        <Mail className="h-4 w-4 text-primary" />
                        <h2 className="text-sm font-semibold text-foreground">Notificaciones por email</h2>
                    </div>
                    <p className="text-xs text-muted-foreground mb-5">Controla que emails recibes de SportsHub</p>

                    <div className="flex flex-col gap-5">
                        {[
                            { id: "email-bookings", key: "emailBookings" as const, checked: emailBookings, label: "Confirmacion de reservas", desc: "Email de confirmacion al reservar o cancelar una pista" },
                            { id: "email-marketing", key: "emailMarketing" as const, checked: emailMarketing, label: "Ofertas y promociones", desc: "Descuentos especiales, nuevos venues y eventos" },
                            { id: "email-matching", key: "emailMatching" as const, checked: emailMatching, label: "Matching de jugadores", desc: "Notificaciones cuando un jugador de tu nivel busca companero" },
                        ].map((item, i) => (
                            <div key={item.id}>
                                {i > 0 && <Separator className="bg-border/30 mb-5" />}
                                <div className="flex items-center justify-between">
                                    <div className="flex-1">
                                        <Label htmlFor={item.id} className="text-sm font-medium text-foreground cursor-pointer">{item.label}</Label>
                                        <p className="text-xs text-muted-foreground mt-0.5">{item.desc}</p>
                                    </div>
                                    <Switch id={item.id} checked={item.checked} onCheckedChange={(v) => setLS({ [item.key]: v })} />
                                </div>
                            </div>
                        ))}
                    </div>
                </section>

                {/* Push notifications */}
                <section className="mb-6 rounded-xl border border-border/50 bg-card p-6">
                    <div className="flex items-center gap-2 mb-1">
                        <Bell className="h-4 w-4 text-primary" />
                        <h2 className="text-sm font-semibold text-foreground">Notificaciones push</h2>
                    </div>
                    <p className="text-xs text-muted-foreground mb-5">Notificaciones en tiempo real en tu dispositivo</p>
                    <div className="flex items-center justify-between">
                        <div className="flex-1">
                            <Label htmlFor="push-notifications" className="text-sm font-medium text-foreground cursor-pointer">
                                Activar notificaciones push
                            </Label>
                            <p className="text-xs text-muted-foreground mt-0.5">Recordatorios de reserva, actualizaciones de estado y mensajes</p>
                        </div>
                        <Switch id="push-notifications" checked={pushNotifications} onCheckedChange={(v) => setLS({ pushNotifications: v })} />
                    </div>
                </section>

                {/* Privacy */}
                <section className="mb-6 rounded-xl border border-border/50 bg-card p-6">
                    <div className="flex items-center gap-2 mb-1">
                        <Shield className="h-4 w-4 text-primary" />
                        <h2 className="text-sm font-semibold text-foreground">Privacidad</h2>
                    </div>
                    <p className="text-xs text-muted-foreground mb-5">Controla la visibilidad de tu perfil</p>

                    <div className="flex flex-col gap-5">
                        {[
                            { id: "profile-visible", key: "profileVisible" as const, checked: profileVisible, label: "Perfil visible", desc: "Otros jugadores pueden ver tu perfil en el matching" },
                            { id: "show-city", key: "showCity" as const, checked: showCity, label: "Mostrar ciudad", desc: "Tu ciudad sera visible en tu perfil publico" },
                            { id: "show-level", key: "showLevel" as const, checked: showLevel, label: "Mostrar nivel de juego", desc: "Tu nivel sera visible para otros jugadores" },
                        ].map((item, i) => (
                            <div key={item.id}>
                                {i > 0 && <Separator className="bg-border/30 mb-5" />}
                                <div className="flex items-center justify-between">
                                    <div className="flex-1">
                                        <Label htmlFor={item.id} className="text-sm font-medium text-foreground cursor-pointer">{item.label}</Label>
                                        <p className="text-xs text-muted-foreground mt-0.5">{item.desc}</p>
                                    </div>
                                    <Switch id={item.id} checked={item.checked} onCheckedChange={(v) => setLS({ [item.key]: v })} />
                                </div>
                            </div>
                        ))}
                    </div>
                </section>

                {/* Language */}
                <section className="mb-6 rounded-xl border border-border/50 bg-card p-6">
                    <div className="flex items-center gap-2 mb-1">
                        <Globe className="h-4 w-4 text-primary" />
                        <h2 className="text-sm font-semibold text-foreground">Idioma y region</h2>
                    </div>
                    <div className="mt-4 flex items-center justify-between">
                        <div>
                            <p className="text-sm font-medium text-foreground">Idioma</p>
                            <p className="text-xs text-muted-foreground">Idioma de la interfaz</p>
                        </div>
                        <Badge variant="secondary" className="border-0 bg-secondary/50 text-muted-foreground">Espanol</Badge>
                    </div>
                </section>

                {/* Security */}
                <section className="mb-6 rounded-xl border border-border/50 bg-card p-6">
                    <div className="flex items-center gap-2 mb-1">
                        <Lock className="h-4 w-4 text-primary" />
                        <h2 className="text-sm font-semibold text-foreground">Seguridad</h2>
                    </div>
                    <p className="text-xs text-muted-foreground mb-5">Gestiona la seguridad de tu cuenta</p>
                    <div className="flex flex-col gap-4">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm font-medium text-foreground">Cambiar contrasena</p>
                                <p className="text-xs text-muted-foreground">Gestionado a traves de Auth0</p>
                            </div>
                            <Button variant="outline" size="sm" className="border-border/60 bg-secondary/30 text-foreground hover:bg-secondary/50 text-xs">
                                Cambiar
                            </Button>
                        </div>
                        <Separator className="bg-border/30" />
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm font-medium text-foreground">Sesiones activas</p>
                                <p className="text-xs text-muted-foreground">1 sesion activa en este dispositivo</p>
                            </div>
                            <Badge variant="secondary" className="border-0 bg-green-500/10 text-green-400 text-xs">
                                <Smartphone className="mr-1 h-3 w-3" />
                                Activa
                            </Badge>
                        </div>
                    </div>
                </section>

                {/* Danger zone */}
                <section className="mb-8 rounded-xl border border-destructive/20 bg-destructive/5 p-6">
                    <h2 className="text-sm font-semibold text-destructive mb-1">Zona de peligro</h2>
                    <p className="text-xs text-muted-foreground mb-4">Acciones irreversibles sobre tu cuenta</p>
                    <Button
                        variant="outline"
                        size="sm"
                        className="gap-2 border-destructive/30 text-destructive hover:bg-destructive/10 hover:text-destructive text-xs"
                        onClick={() => setShowDeleteDialog(true)}
                    >
                        <Trash2 className="h-3.5 w-3.5" />
                        Eliminar mi cuenta
                    </Button>
                    <p className="mt-3 text-[11px] text-muted-foreground">
                        * No hay endpoint de borrado en la API todavia.
                    </p>
                </section>

                {/* Save bar */}
                <div className="sticky bottom-0 -mx-4 border-t border-border/50 bg-background/95 px-4 py-4 backdrop-blur-xl lg:-mx-6 lg:px-6">
                    <div className="flex items-center justify-between">
                        <div>
                            {saved && (
                                <p className="flex items-center gap-1.5 text-sm text-green-400">
                                    <Check className="h-4 w-4" />
                                    Configuracion guardada
                                </p>
                            )}
                        </div>
                        <Button onClick={handleSave} disabled={saving} className="gap-2 bg-primary text-primary-foreground hover:bg-primary/90">
                            {saving ? <Loader2 className="h-4 w-4 animate-spin" /> : <><Check className="h-4 w-4" />Guardar cambios</>}
                        </Button>
                    </div>
                </div>
            </main>

            {/* Owner request dialog */}
            <Dialog open={showOwnerDialog} onOpenChange={setShowOwnerDialog}>
                <DialogContent className="border-border bg-card text-card-foreground sm:max-w-md">
                    <DialogHeader>
                        <DialogTitle className="flex items-center gap-2 text-foreground">
                            <Crown className="h-5 w-5 text-primary" />
                            Solicitar acceso Owner
                        </DialogTitle>
                        <DialogDescription className="text-muted-foreground">
                            Al convertirte en Owner podras registrar tus instalaciones deportivas, crear pistas, definir horarios y precios, y recibir reservas de jugadores.
                        </DialogDescription>
                    </DialogHeader>
                    <div className="flex flex-col gap-3 py-2">
                        <div className="rounded-lg border border-border/50 bg-secondary/20 p-4">
                            <h4 className="text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-3">Que incluye ser Owner</h4>
                            <ul className="flex flex-col gap-2.5">
                                {[
                                    "Registrar venues con ubicacion en mapa",
                                    "Crear y gestionar pistas/resources",
                                    "Definir horarios de apertura por dia",
                                    "Configurar precios por tramo horario",
                                    "Recibir reservas y ver el calendario",
                                    "Subir imagenes de tus instalaciones",
                                ].map((item) => (
                                    <li key={item} className="flex items-start gap-2 text-xs text-foreground">
                                        <Check className="mt-0.5 h-3 w-3 shrink-0 text-primary" />
                                        {item}
                                    </li>
                                ))}
                            </ul>
                        </div>
                        <div className="rounded-lg border border-amber-500/20 bg-amber-500/5 p-3">
                            <p className="text-xs text-amber-400">
                                Tu solicitud sera revisada por el equipo de SportsHub. Te notificaremos por email cuando sea aprobada.
                            </p>
                        </div>
                    </div>
                    <DialogFooter className="gap-2 sm:gap-0">
                        <Button variant="outline" onClick={() => setShowOwnerDialog(false)} className="border-border/60 bg-secondary/30 text-foreground hover:bg-secondary/50">
                            Cancelar
                        </Button>
                        <Button onClick={handleOwnerRequest} disabled={requesting} className="gap-2 bg-primary text-primary-foreground hover:bg-primary/90">
                            {requesting ? <Loader2 className="h-4 w-4 animate-spin" /> : <><Crown className="h-4 w-4" />Enviar solicitud</>}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>

            {/* Delete account dialog */}
            <Dialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
                <DialogContent className="border-border bg-card text-card-foreground sm:max-w-md">
                    <DialogHeader>
                        <DialogTitle className="text-destructive">Eliminar cuenta</DialogTitle>
                        <DialogDescription className="text-muted-foreground">
                            Esta accion es permanente y no se puede deshacer. Se eliminaran todos tus datos, reservas e historial.
                        </DialogDescription>
                    </DialogHeader>
                    <div className="rounded-lg border border-destructive/20 bg-destructive/5 p-3">
                        <p className="text-xs text-destructive">* No hay endpoint de borrado en la API todavia.</p>
                    </div>
                    <DialogFooter className="gap-2 sm:gap-0">
                        <Button variant="outline" onClick={() => setShowDeleteDialog(false)} className="border-border/60 bg-secondary/30 text-foreground hover:bg-secondary/50">
                            Cancelar
                        </Button>
                        <Button variant="destructive" className="gap-2" onClick={() => setShowDeleteDialog(false)}>
                            <Trash2 className="h-4 w-4" />
                            Entendido
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    )
}
