"use client"

import {useMemo, useRef, useState} from "react"
import Link from "next/link"
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar"
import {Badge} from "@/components/ui/badge"
import {Button} from "@/components/ui/button"
import {Input} from "@/components/ui/input"
import {Label} from "@/components/ui/label"
import {Textarea} from "@/components/ui/textarea"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"
import {Separator} from "@/components/ui/separator"
import {DashboardNavbar} from "@/components/dashboard/dashboard-navbar"
import {uploadToCloudinary} from "@/lib/cloudinary-upload"
import {ArrowLeft, Camera, Check, Loader2, Trash2} from "lucide-react"
import type {UserProfile} from "@/types"


interface Props {
    user: UserProfile
    upcomingCount: number
}

type FormState = {
    displayName: string
    description: string
    phoneNumber: string
    preferredSport: "" | "PADEL" | "TENNIS" | "SQUASH"
    skillLevel: "" | "BEGINNER" | "INTERMEDIATE" | "ADVANCED"
    city: string
    countryCode: string
}


const SPORTS: Array<{ value: "PADEL" | "TENNIS" | "SQUASH" | "BADMINTON"; label: string }> = [
    { value: "PADEL", label: "Padel" },
    { value: "TENNIS", label: "Tenis" },
    { value: "SQUASH", label: "Squash" },
    { value: "BADMINTON", label: "Badminton" }
]

const SKILL_LEVELS: Array<{ value: "BEGINNER" | "INTERMEDIATE" | "ADVANCED"; label: string }> = [
    { value: "BEGINNER", label: "Principiante" },
    { value: "INTERMEDIATE", label: "Intermedio" },
    { value: "ADVANCED", label: "Avanzado" },
]

const COUNTRIES = [
    { value: "ES", label: "Espana" },
    { value: "MX", label: "Mexico" },
    { value: "AR", label: "Argentina" },
    { value: "CO", label: "Colombia" },
    { value: "CL", label: "Chile" },
    { value: "PE", label: "Peru" },
    { value: "US", label: "Estados Unidos" },
    { value: "PT", label: "Portugal" },
    { value: "IT", label: "Italia" },
    { value: "FR", label: "Francia" },
] as const


function safeInitials(name: string): string {
    const n = (name ?? "").trim()
    if (!n) return "U"
    return n.split(/\s+/).filter(Boolean).slice(0, 2).map((p) => p[0]).join("").toUpperCase() || "U"
}

function userToForm(user: UserProfile): FormState {
    return {
        displayName: (user.displayName ?? "").trim(),
        description: user.description ?? "",
        phoneNumber: user.phoneNumber ?? "",
        preferredSport:
            user.preferredSport === "PADEL" || user.preferredSport === "TENNIS" || user.preferredSport === "SQUASH"
                ? user.preferredSport
                : "",
        skillLevel:
            user.skillLevel === "BEGINNER" || user.skillLevel === "INTERMEDIATE" || user.skillLevel === "ADVANCED"
                ? user.skillLevel
                : "",
        city: user.city ?? "",
        countryCode: user.countryCode ?? "",
    }
}


export function ProfileClient({ user: initialUser, upcomingCount }: Props) {
    const [me, setMe] = useState<UserProfile>(initialUser)
    const [form, setForm] = useState<FormState>(userToForm(initialUser))
    const [avatarPreview, setAvatarPreview] = useState<string | null>(initialUser.avatarUrl ?? null)

    const [saving, setSaving] = useState(false)
    const [saved, setSaved] = useState(false)
    const [avatarUploading, setAvatarUploading] = useState(false)

    const fileInputRef = useRef<HTMLInputElement>(null)

    const update = (fields: Partial<FormState>) => {
        setForm((p) => ({ ...p, ...fields }))
        setSaved(false)
    }

    const initials = useMemo(() => safeInitials(form.displayName), [form.displayName])
    const roleBadgeLabel = me.role === "PLAYER" ? "Jugador" : me.role === "OWNER" ? "Propietario" : "Admin"
    const memberSinceLabel = useMemo(() => {
        if (!me.lastLoginAt) return "—"
        return new Date(me.lastLoginAt).toLocaleDateString("es-ES", { month: "long", year: "numeric" })
    }, [me.lastLoginAt])


    async function handleAvatarUpload(e: React.ChangeEvent<HTMLInputElement>) {
        const file = e.target.files?.[0]
        if (!file) return

        const localUrl = URL.createObjectURL(file)
        setAvatarPreview(localUrl)
        setAvatarUploading(true)
        setSaved(false)

        try {
            // 1+2. Firma + subida a Cloudinary via helper
            const { url, publicId } = await uploadToCloudinary(file, "AVATAR")

            // 3. Guardar en backend
            const patchRes = await fetch("/api/proxy/api/me/avatar", {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ publicId, url }),
            })
            if (!patchRes.ok) throw new Error(`Avatar patch error ${patchRes.status}`)
            const updatedProfile: UserProfile = await patchRes.json()

            setMe(updatedProfile)
            setAvatarPreview(updatedProfile.avatarUrl ?? url)
        } catch (err) {
            console.error(err)
            setAvatarPreview(me.avatarUrl ?? null)
        } finally {
            setAvatarUploading(false)
            if (fileInputRef.current) fileInputRef.current.value = ""
            URL.revokeObjectURL(localUrl)
        }
    }

    function removeAvatar() {
        setAvatarPreview(null)
        setSaved(false)
    }


    async function handleSave() {
        if (!form.displayName.trim()) return
        setSaving(true)
        setSaved(false)

        try {
            const res = await fetch("/api/proxy/api/me", {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    displayName: form.displayName.trim(),
                    description: form.description || undefined,
                    phoneNumber: form.phoneNumber || undefined,
                    city: form.city || undefined,
                    countryCode: form.countryCode || undefined,
                    preferredSport: form.preferredSport || undefined,
                    skillLevel: form.skillLevel || undefined,
                }),
            })
            if (!res.ok) throw new Error(`Save error ${res.status}`)
            const updated: UserProfile = await res.json()

            setMe(updated)
            setAvatarPreview(updated.avatarUrl ?? null)
            setForm(userToForm(updated))
            setSaved(true)
        } catch (e) {
            console.error(e)
        } finally {
            setSaving(false)
        }
    }

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
                            Editar perfil
                        </h1>
                        <p className="mt-0.5 text-sm text-muted-foreground">
                            Actualiza tu informacion personal y preferencias
                        </p>
                    </div>
                </div>

                {/* Avatar */}
                <section className="mb-8 rounded-xl border border-border/50 bg-card p-6">
                    <h2 className="mb-4 text-sm font-semibold text-foreground">Foto de perfil</h2>
                    <div className="flex items-center gap-5">
                        <div className="relative">
                            <Avatar className="h-24 w-24 border-2 border-border/50">
                                <AvatarImage src={avatarPreview ?? undefined} alt={form.displayName} />
                                <AvatarFallback className="bg-primary/10 text-lg font-semibold text-primary">
                                    {initials}
                                </AvatarFallback>
                            </Avatar>
                            <button
                                onClick={() => fileInputRef.current?.click()}
                                disabled={avatarUploading}
                                className="absolute -bottom-1 -right-1 flex h-8 w-8 items-center justify-center rounded-full border-2 border-card bg-primary text-primary-foreground transition-transform hover:scale-110 disabled:opacity-60"
                                aria-label="Cambiar foto de perfil"
                            >
                                {avatarUploading ? <Loader2 className="h-3.5 w-3.5 animate-spin" /> : <Camera className="h-3.5 w-3.5" />}
                            </button>
                            <input
                                ref={fileInputRef}
                                type="file"
                                accept="image/jpeg,image/png,image/webp"
                                className="hidden"
                                onChange={handleAvatarUpload}
                            />
                        </div>

                        <div className="flex flex-col gap-2">
                            <p className="text-sm font-medium text-foreground">{form.displayName || "Tu nombre"}</p>
                            <p className="text-xs text-muted-foreground">JPG, PNG o WebP. Max 5MB.</p>
                            <div className="flex items-center gap-2">
                                <Button
                                    variant="outline"
                                    size="sm"
                                    className="gap-1.5 border-border/60 bg-secondary/30 text-foreground hover:bg-secondary/50 text-xs"
                                    onClick={() => fileInputRef.current?.click()}
                                    disabled={avatarUploading}
                                >
                                    <Camera className="h-3 w-3" />
                                    Cambiar
                                </Button>
                                {avatarPreview && (
                                    <Button
                                        variant="ghost"
                                        size="sm"
                                        className="gap-1.5 text-xs text-destructive hover:bg-destructive/10 hover:text-destructive"
                                        onClick={removeAvatar}
                                        disabled={avatarUploading}
                                    >
                                        <Trash2 className="h-3 w-3" />
                                        Eliminar
                                    </Button>
                                )}
                            </div>
                        </div>
                    </div>
                </section>

                {/* Personal info */}
                <section className="mb-6 rounded-xl border border-border/50 bg-card p-6">
                    <h2 className="mb-1 text-sm font-semibold text-foreground">Informacion personal</h2>
                    <p className="mb-5 text-xs text-muted-foreground">Esta informacion sera visible para otros jugadores</p>

                    <div className="flex flex-col gap-5">
                        <div className="flex flex-col gap-2">
                            <Label htmlFor="displayName" className="text-sm font-medium text-foreground">Nombre visible *</Label>
                            <Input
                                id="displayName"
                                value={form.displayName}
                                onChange={(e) => update({ displayName: e.target.value })}
                                placeholder="Como quieres que te llamen"
                                className="h-11 border-border/60 bg-secondary/30 text-foreground placeholder:text-muted-foreground focus-visible:border-primary/50 focus-visible:ring-primary/20"
                            />
                        </div>

                        <div className="flex flex-col gap-2">
                            <Label htmlFor="phone" className="text-sm font-medium text-foreground">Telefono</Label>
                            <Input
                                id="phone"
                                type="tel"
                                value={form.phoneNumber}
                                onChange={(e) => update({ phoneNumber: e.target.value })}
                                placeholder="+34 600 000 000"
                                className="h-11 border-border/60 bg-secondary/30 text-foreground placeholder:text-muted-foreground focus-visible:border-primary/50 focus-visible:ring-primary/20"
                            />
                        </div>

                        <div className="flex flex-col gap-2">
                            <Label htmlFor="description" className="text-sm font-medium text-foreground">Sobre ti</Label>
                            <Textarea
                                id="description"
                                value={form.description}
                                onChange={(e) => update({ description: e.target.value })}
                                placeholder="Cuenta tu experiencia, cuando juegas, que buscas..."
                                rows={3}
                                className="resize-none border-border/60 bg-secondary/30 text-foreground placeholder:text-muted-foreground focus-visible:border-primary/50 focus-visible:ring-primary/20"
                            />
                        </div>
                    </div>
                </section>

                {/* Sport & Level */}
                <section className="mb-6 rounded-xl border border-border/50 bg-card p-6">
                    <h2 className="mb-1 text-sm font-semibold text-foreground">Deporte y nivel</h2>
                    <p className="mb-5 text-xs text-muted-foreground">Ayuda a encontrar jugadores de tu nivel</p>

                    <div className="flex flex-col gap-5">
                        <div className="flex flex-col gap-2">
                            <Label className="text-sm font-medium text-foreground">Deporte principal</Label>
                            <Select
                                value={form.preferredSport}
                                onValueChange={(v) => update({ preferredSport: v as "PADEL" | "TENNIS" | "SQUASH" })}
                            >
                                <SelectTrigger className="h-11 border-border/60 bg-secondary/30 text-foreground">
                                    <SelectValue placeholder="Selecciona un deporte" />
                                </SelectTrigger>
                                <SelectContent className="border-border bg-card text-card-foreground">
                                    {SPORTS.map((s) => <SelectItem key={s.value} value={s.value}>{s.label}</SelectItem>)}
                                </SelectContent>
                            </Select>
                        </div>

                        <div className="flex flex-col gap-2">
                            <Label className="text-sm font-medium text-foreground">Nivel de juego</Label>
                            <div className="grid grid-cols-3 gap-2">
                                {SKILL_LEVELS.map((level) => (
                                    <button
                                        key={level.value}
                                        type="button"
                                        onClick={() => update({ skillLevel: level.value })}
                                        className={`rounded-lg border px-3 py-2.5 text-center text-sm font-medium transition-all ${
                                            form.skillLevel === level.value
                                                ? "border-primary bg-primary/10 text-primary"
                                                : "border-border/50 bg-secondary/20 text-muted-foreground hover:border-border hover:bg-secondary/40"
                                        }`}
                                    >
                                        {level.label}
                                    </button>
                                ))}
                            </div>
                        </div>
                    </div>
                </section>

                {/* Location */}
                <section className="mb-8 rounded-xl border border-border/50 bg-card p-6">
                    <h2 className="mb-1 text-sm font-semibold text-foreground">Ubicacion</h2>
                    <p className="mb-5 text-xs text-muted-foreground">Para encontrar venues y jugadores cerca de ti</p>

                    <div className="flex flex-col gap-5">
                        <div className="flex flex-col gap-2">
                            <Label className="text-sm font-medium text-foreground">Pais</Label>
                            <Select value={form.countryCode} onValueChange={(v) => update({ countryCode: v })}>
                                <SelectTrigger className="h-11 border-border/60 bg-secondary/30 text-foreground">
                                    <SelectValue placeholder="Selecciona tu pais" />
                                </SelectTrigger>
                                <SelectContent className="border-border bg-card text-card-foreground">
                                    {COUNTRIES.map((c) => <SelectItem key={c.value} value={c.value}>{c.label}</SelectItem>)}
                                </SelectContent>
                            </Select>
                        </div>

                        <div className="flex flex-col gap-2">
                            <Label htmlFor="city" className="text-sm font-medium text-foreground">Ciudad</Label>
                            <Input
                                id="city"
                                value={form.city}
                                onChange={(e) => update({ city: e.target.value })}
                                placeholder="Ej: Madrid, Barcelona, Sevilla..."
                                className="h-11 border-border/60 bg-secondary/30 text-foreground placeholder:text-muted-foreground focus-visible:border-primary/50 focus-visible:ring-primary/20"
                            />
                        </div>
                    </div>
                </section>

                {/* Account info */}
                <section className="mb-8 rounded-xl border border-border/50 bg-card p-6">
                    <h2 className="mb-4 text-sm font-semibold text-foreground">Cuenta</h2>
                    <div className="flex flex-col gap-3">
                        <div className="flex items-center justify-between">
                            <span className="text-sm text-muted-foreground">Email</span>
                            <span className="text-sm text-foreground">{me.email}</span>
                        </div>
                        <Separator className="bg-border/50" />
                        <div className="flex items-center justify-between">
                            <span className="text-sm text-muted-foreground">Rol</span>
                            <Badge variant="secondary" className="border-0 bg-primary/10 text-xs font-medium text-primary">
                                {roleBadgeLabel}
                            </Badge>
                        </div>
                        <Separator className="bg-border/50" />
                        <div className="flex items-center justify-between">
                            <span className="text-sm text-muted-foreground">Miembro desde</span>
                            <span className="text-sm text-foreground">{memberSinceLabel}</span>
                        </div>
                    </div>
                </section>

                {/* Save bar */}
                <div className="sticky bottom-0 -mx-4 border-t border-border/50 bg-background/95 px-4 py-4 backdrop-blur-xl lg:-mx-6 lg:px-6">
                    <div className="flex items-center justify-between">
                        <div>
                            {saved && (
                                <p className="flex items-center gap-1.5 text-sm text-green-400">
                                    <Check className="h-4 w-4" />
                                    Cambios guardados
                                </p>
                            )}
                        </div>
                        <div className="flex items-center gap-3">
                            <Link href="/dashboard">
                                <Button variant="outline" className="border-border/60 bg-secondary/30 text-foreground hover:bg-secondary/50">
                                    Cancelar
                                </Button>
                            </Link>
                            <Button
                                onClick={handleSave}
                                disabled={saving || !form.displayName.trim()}
                                className="gap-2 bg-primary text-primary-foreground hover:bg-primary/90"
                            >
                                {saving
                                    ? <Loader2 className="h-4 w-4 animate-spin" />
                                    : <><Check className="h-4 w-4" />Guardar cambios</>
                                }
                            </Button>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    )
}
