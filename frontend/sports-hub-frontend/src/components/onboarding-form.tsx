"use client"

import {CityCombobox} from "@/components/forms/CityCombobox"
import {useCallback, useRef, useState} from "react"
import {Button} from "@/components/ui/button"
import {Input} from "@/components/ui/input"
import {Label} from "@/components/ui/label"
import {Textarea} from "@/components/ui/textarea"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue,} from "@/components/ui/select"
import {ArrowLeft, ArrowRight, Camera, Check, Loader2, MapPin, Trophy, User,} from "lucide-react"
import {useRouter} from "next/navigation";

const SPORTS = [
    {value: "PADEL", label: "Padel", icon: "🏓"},
    {value: "TENNIS", label: "Tenis", icon: "🎾"},
    {value: "SQUASH", label: "Squash", icon: "🏸"},
    {value: "BADMINTON", label: "Badminton", icon: "🪶"},
] as const

const SKILL_LEVELS = [
    {
        value: "BEGINNER",
        label: "Principiante",
        description: "Estoy empezando a jugar",
    },
    {
        value: "INTERMEDIATE",
        label: "Intermedio",
        description: "Juego regularmente y conozco la tecnica",
    },
    {
        value: "ADVANCED",
        label: "Avanzado",
        description: "Compito en torneos o tengo alto nivel",
    },
] as const

interface OnboardingData {
    displayName: string
    description: string
    phoneNumber: string
    preferredSport: string
    skillLevel: string
    city: string
    countryCode: string
    avatarFile: File | null
    avatarPreview: string
}

const TOTAL_STEPS = 3

export function OnboardingForm() {
    const [step, setStep] = useState(1)
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const fileInputRef = useRef<HTMLInputElement>(null)
    const router = useRouter()

    const [data, setData] = useState<OnboardingData>({
        displayName: "",
        description: "",
        phoneNumber: "",
        preferredSport: "",
        skillLevel: "",
        city: "",
        countryCode: "",
        avatarFile: null,
        avatarPreview: "",
    })

    const update = useCallback(
        (fields: Partial<OnboardingData>) =>
            setData((prev) => ({...prev, ...fields})),
        []
    )

    const handleAvatarChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0]
        if (!file) return

        if (file.size > 5 * 1024 * 1024) {
            setError("La imagen supera 5MB.")
            return
        }
        if (data.avatarPreview) URL.revokeObjectURL(data.avatarPreview)

        const url = URL.createObjectURL(file)
        update({avatarFile: file, avatarPreview: url})
    }

    const PHONE_RE = /^\+[1-9]\d{6,14}$/
    const phoneError =
        data.phoneNumber.trim()
            ? (() => {
                const n = data.phoneNumber.trim().replace(/\s/g, "")
                const withPlus = n.startsWith("+") ? n : "+" + n
                return PHONE_RE.test(withPlus) ? null : "Formato inválido. Usa el formato internacional: +34 600 000 000"
            })()
            : null

    const canContinue = () => {
        if (step === 1) return data.displayName.trim().length >= 2 && !phoneError
        if (step === 2) return !!data.preferredSport && !!data.skillLevel
        if (step === 3) return !!data.countryCode && data.city.trim().length >= 2
        return true
    }

    const ensureOk = async (res: Response) => {
        if (res.ok) return
        let msg = `HTTP ${res.status}`
        try {
            const j = await res.json()
            msg = j?.message || j?.error || msg
        } catch {
        }
        throw new Error(msg)
    }


    const handleSubmit = async () => {
        setIsSubmitting(true)
        setError(null)
        try {
            // 1. Subir avatar a Cloudinary si hay imagen
            let avatarUrl: string | null = null
            let avatarPublicId: string | null = null

            if (data.avatarFile) {
                const signatureRes = await fetch("/api/proxy/api/uploads/signature", {
                    method: "POST",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify({
                        purpose: "AVATAR",
                    }),
                })
                await ensureOk(signatureRes)
                const signature = await signatureRes.json()

                const formData = new FormData()
                formData.append("file", data.avatarFile)
                formData.append("signature", signature.signature)
                formData.append("timestamp", signature.timestamp.toString())
                formData.append("api_key", signature.apiKey)
                formData.append("folder", signature.folder)

                if (signature.publicId) formData.append("public_id", signature.publicId)

                if (typeof signature.overwrite === "boolean") {
                    formData.append("overwrite", signature.overwrite ? "true" : "false")
                }

                formData.append("resource_type", "image")

                const cloudinaryRes = await fetch(
                    `https://api.cloudinary.com/v1_1/${signature.cloudName}/image/upload`,
                    {method: "POST", body: formData}
                )
                await ensureOk(cloudinaryRes)
                const cloudinaryData = await cloudinaryRes.json()

                avatarUrl = cloudinaryData.secure_url
                avatarPublicId = cloudinaryData.public_id
            }

            const meRes = await fetch("/api/proxy/api/me", {
                method: "PUT",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    displayName: data.displayName,
                    description: data.description || null,
                    phoneNumber: data.phoneNumber || null,
                    city: data.city,
                    countryCode: data.countryCode,
                    preferredSport: data.preferredSport,
                    skillLevel: data.skillLevel,
                }),
            })
            await ensureOk(meRes)

            if (avatarUrl && avatarPublicId) {
                const avatarRes = await fetch("/api/proxy/api/me/avatar", {
                    method: "PATCH",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify({
                        url: avatarUrl,
                        publicId: avatarPublicId,
                    }),
                })
                await ensureOk(avatarRes)
            }

            void router.replace("/dashboard")

        } catch (err) {
            console.error("Error onboarding:", err)
            setError(
                err instanceof Error ? err.message : "An error has occurred. Try again."
            )
        } finally {
            setIsSubmitting(false)
        }
    }


    return (
        <div className="w-full">
            {/* Progress bar */}
            <div className="mb-8">
                <div className="flex items-center justify-between mb-3">
                    {Array.from({length: TOTAL_STEPS}, (_, i) => i + 1).map((s) => (
                        <div key={s} className="flex items-center gap-2">
                            <div
                                className={`flex h-8 w-8 items-center justify-center rounded-full text-xs font-bold transition-all ${
                                    s < step
                                        ? "bg-primary text-primary-foreground"
                                        : s === step
                                            ? "bg-primary/20 text-primary ring-2 ring-primary/40"
                                            : "bg-secondary/50 text-muted-foreground"
                                }`}
                            >
                                {s < step ? <Check className="h-4 w-4"/> : s}
                            </div>
                            {s < TOTAL_STEPS && (
                                <div className="hidden sm:block w-16 lg:w-24 h-px mx-1">
                                    <div
                                        className={`h-full transition-colors ${
                                            s < step ? "bg-primary" : "bg-border/50"
                                        }`}
                                    />
                                </div>
                            )}
                        </div>
                    ))}
                </div>
                <p className="text-xs text-muted-foreground">
                    Paso {step} de {TOTAL_STEPS}
                </p>
            </div>

            {/* Step 1 - Personal info + avatar */}
            {step === 1 && (
                <div className="flex flex-col gap-6 animate-in fade-in slide-in-from-right-4 duration-300">
                    <div>
                        <h2 className="font-[var(--font-space-grotesk)] text-2xl font-bold tracking-tight text-foreground">
                            Tu perfil
                        </h2>
                        <p className="mt-1.5 text-sm leading-relaxed text-muted-foreground">
                            Cuentanos un poco sobre ti para personalizar tu experiencia.
                        </p>
                    </div>

                    {/* Avatar Upload */}
                    <div className="flex items-center gap-5">
                        <button
                            type="button"
                            onClick={() => fileInputRef.current?.click()}
                            className="group relative flex h-20 w-20 shrink-0 items-center justify-center overflow-hidden rounded-full border-2 border-dashed border-border/60 bg-secondary/30 transition-all hover:border-primary/50 hover:bg-secondary/50 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary/50"
                            aria-label="Subir foto de perfil"
                        >
                            {data.avatarPreview ? (
                                <img
                                    src={data.avatarPreview}
                                    alt="Vista previa del avatar"
                                    className="h-full w-full object-cover"
                                />
                            ) : (
                                <div
                                    className="flex flex-col items-center gap-1 text-muted-foreground transition-colors group-hover:text-primary">
                                    <Camera className="h-5 w-5"/>
                                </div>
                            )}
                            {/* Overlay on hover */}
                            <div
                                className="absolute inset-0 flex items-center justify-center rounded-full bg-background/60 opacity-0 transition-opacity group-hover:opacity-100">
                                <Camera className="h-5 w-5 text-primary"/>
                            </div>
                        </button>
                        <input
                            ref={fileInputRef}
                            type="file"
                            accept="image/*"
                            className="hidden"
                            onChange={handleAvatarChange}
                            aria-label="Seleccionar imagen de avatar"
                        />
                        <div>
                            <p className="text-sm font-medium text-foreground">
                                Foto de perfil
                            </p>
                            <p className="text-xs text-muted-foreground">
                                JPG, PNG o WebP. Max 5MB.
                            </p>
                        </div>
                    </div>

                    {/* Display Name */}
                    <div className="flex flex-col gap-2">
                        <Label
                            htmlFor="displayName"
                            className="text-sm font-medium text-foreground"
                        >
                            <User className="h-3.5 w-3.5 text-primary"/>
                            Nombre visible
                        </Label>
                        <Input
                            id="displayName"
                            value={data.displayName}
                            onChange={(e) => update({displayName: e.target.value})}
                            placeholder="Como quieres que te llamen"
                            className="h-12 border-border/60 bg-secondary/30 text-foreground placeholder:text-muted-foreground focus-visible:border-primary/50 focus-visible:ring-primary/20"
                            required
                        />
                    </div>

                    {/* Phone */}
                    <div className="flex flex-col gap-2">
                        <Label
                            htmlFor="phone"
                            className="text-sm font-medium text-foreground"
                        >
                            Telefono
                            <span className="text-muted-foreground font-normal">
                (opcional)
              </span>
                        </Label>
                        <Input
                            id="phone"
                            type="tel"
                            value={data.phoneNumber}
                            onChange={(e) => update({phoneNumber: e.target.value})}
                            placeholder="+34 600 000 000"
                            className={`h-12 border-border/60 bg-secondary/30 text-foreground placeholder:text-muted-foreground focus-visible:ring-primary/20 ${
                                phoneError ? "border-destructive focus-visible:border-destructive" : "focus-visible:border-primary/50"
                            }`}
                        />
                        {phoneError && (
                            <p className="text-xs text-destructive">{phoneError}</p>
                        )}
                    </div>

                    {/* Description */}
                    <div className="flex flex-col gap-2">
                        <Label
                            htmlFor="description"
                            className="text-sm font-medium text-foreground"
                        >
                            Sobre ti
                            <span className="text-muted-foreground font-normal">
                (opcional)
              </span>
                        </Label>
                        <Textarea
                            id="description"
                            value={data.description}
                            onChange={(e) => update({description: e.target.value})}
                            placeholder="Cuanta tu experiencia, cuando juegas, que buscas..."
                            rows={3}
                            className="border-border/60 bg-secondary/30 text-foreground placeholder:text-muted-foreground focus-visible:border-primary/50 focus-visible:ring-primary/20 resize-none"
                        />
                    </div>
                </div>
            )}

            {/* Step 2 - Sport & Level */}
            {step === 2 && (
                <div className="flex flex-col gap-6 animate-in fade-in slide-in-from-right-4 duration-300">
                    <div>
                        <h2 className="font-[var(--font-space-grotesk)] text-2xl font-bold tracking-tight text-foreground">
                            Tu deporte
                        </h2>
                        <p className="mt-1.5 text-sm leading-relaxed text-muted-foreground">
                            Selecciona tu deporte principal y tu nivel para encontrar los
                            mejores rivales.
                        </p>
                    </div>

                    {/* Preferred Sport - visual cards */}
                    <div>
                        <Label className="text-sm font-medium text-foreground mb-3">
                            <Trophy className="h-3.5 w-3.5 text-primary"/>
                            Deporte principal
                        </Label>
                        <div className="grid grid-cols-2 gap-3">
                            {SPORTS.map((sport) => (
                                <button
                                    key={sport.value}
                                    type="button"
                                    onClick={() => update({preferredSport: sport.value})}
                                    className={`flex items-center gap-3 rounded-xl border p-4 text-left transition-all focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary/50 ${
                                        data.preferredSport === sport.value
                                            ? "border-primary bg-primary/10 text-foreground ring-1 ring-primary/30"
                                            : "border-border/50 bg-secondary/20 text-muted-foreground hover:border-border hover:bg-secondary/40"
                                    }`}
                                >
                  <span className="text-2xl" aria-hidden="true">
                    {sport.icon}
                  </span>
                                    <span className="text-sm font-medium">{sport.label}</span>
                                </button>
                            ))}
                        </div>
                    </div>

                    {/* Skill Level */}
                    <div>
                        <Label className="text-sm font-medium text-foreground mb-3">
                            Nivel de juego
                        </Label>
                        <div className="flex flex-col gap-3">
                            {SKILL_LEVELS.map((level) => (
                                <button
                                    key={level.value}
                                    type="button"
                                    onClick={() => update({skillLevel: level.value})}
                                    className={`flex flex-col items-start gap-0.5 rounded-xl border p-4 text-left transition-all focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary/50 ${
                                        data.skillLevel === level.value
                                            ? "border-primary bg-primary/10 ring-1 ring-primary/30"
                                            : "border-border/50 bg-secondary/20 hover:border-border hover:bg-secondary/40"
                                    }`}
                                >
                  <span
                      className={`text-sm font-semibold ${data.skillLevel === level.value ? "text-foreground" : "text-muted-foreground"}`}
                  >
                    {level.label}
                  </span>
                                    <span className="text-xs text-muted-foreground">
                    {level.description}
                  </span>
                                </button>
                            ))}
                        </div>
                    </div>
                </div>
            )}

            {/* Step 3 - Location */}
            {step === 3 && (
                <div className="flex flex-col gap-6 animate-in fade-in slide-in-from-right-4 duration-300">
                    <div>
                        <h2 className="font-[var(--font-space-grotesk)] text-2xl font-bold tracking-tight text-foreground">
                            Tu ubicacion
                        </h2>
                        <p className="mt-1.5 text-sm leading-relaxed text-muted-foreground">
                            Te ayudamos a encontrar pistas y jugadores cerca de ti.
                        </p>
                    </div>

                    {/* Country */}
                    <div className="flex flex-col gap-2">
                        <Label className="flex items-center gap-1.5 text-sm font-medium text-foreground">
                            <MapPin className="h-3.5 w-3.5 text-primary"/>
                            Pais
                            <span className="text-destructive">*</span>
                        </Label>
                        <Select
                            value={data.countryCode}
                            onValueChange={(v) => update({countryCode: v})}
                        >
                            <SelectTrigger
                                className="h-12 w-full border-border/60 bg-secondary/30 text-foreground focus-visible:border-primary/50 focus-visible:ring-primary/20">
                                <SelectValue placeholder="Selecciona tu pais"/>
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="ES">España</SelectItem>
                                <SelectItem value="MX">Mexico</SelectItem>
                                <SelectItem value="AR">Argentina</SelectItem>
                                <SelectItem value="CO">Colombia</SelectItem>
                                <SelectItem value="CL">Chile</SelectItem>
                                <SelectItem value="PE">Peru</SelectItem>
                                <SelectItem value="US">Estados Unidos</SelectItem>
                                <SelectItem value="PT">Portugal</SelectItem>
                                <SelectItem value="IT">Italia</SelectItem>
                                <SelectItem value="FR">Francia</SelectItem>
                            </SelectContent>
                        </Select>
                    </div>

                    {/* City - con búsqueda */}
                    <div className="flex flex-col gap-2">
                        <Label htmlFor="citySearch" className="flex items-center gap-1.5 text-sm font-medium text-foreground">
                            Ciudad
                            <span className="text-destructive">*</span>
                        </Label>

                        {data.countryCode ? (
                            <CityCombobox
                                countryCode={data.countryCode}
                                value={data.city}
                                onChange={(city) => update({ city })}
                            />
                        ) : (
                            <p className="text-xs text-muted-foreground italic">
                                Selecciona un país primero
                            </p>
                        )}
                    </div>

                    {/* Summary preview */}
                    <div className="mt-2 rounded-xl border border-border/50 bg-secondary/20 p-5">
                        <p className="text-xs font-medium uppercase tracking-wider text-muted-foreground mb-4">
                            Resumen de tu perfil
                        </p>
                        <div className="flex items-start gap-4">
                            <div
                                className="flex h-14 w-14 shrink-0 items-center justify-center overflow-hidden rounded-full bg-secondary/50 ring-2 ring-primary/20">
                                {data.avatarPreview ? (
                                    <img
                                        src={data.avatarPreview}
                                        alt="Avatar"
                                        className="h-full w-full object-cover"
                                    />
                                ) : (
                                    <User className="h-6 w-6 text-muted-foreground"/>
                                )}
                            </div>
                            <div className="flex flex-col gap-1.5 min-w-0">
                                <p className="font-[var(--font-space-grotesk)] font-semibold text-foreground truncate">
                                    {data.displayName || "Tu nombre"}
                                </p>
                                <div className="flex flex-wrap items-center gap-2">
                                    {data.preferredSport && (
                                        <span
                                            className="inline-flex items-center gap-1 rounded-full bg-primary/10 px-2.5 py-0.5 text-xs font-medium text-primary">
                      {
                          SPORTS.find((s) => s.value === data.preferredSport)
                              ?.label
                      }
                    </span>
                                    )}
                                    {data.skillLevel && (
                                        <span
                                            className="inline-flex items-center rounded-full bg-secondary/60 px-2.5 py-0.5 text-xs font-medium text-secondary-foreground">
                      {
                          SKILL_LEVELS.find((l) => l.value === data.skillLevel)
                              ?.label
                      }
                    </span>
                                    )}
                                    {data.city && (
                                        <span className="inline-flex items-center gap-1 text-xs text-muted-foreground">
                      <MapPin className="h-3 w-3"/>
                                            {data.city}
                    </span>
                                    )}
                                </div>
                                {data.description && (
                                    <p className="text-xs text-muted-foreground line-clamp-2 mt-0.5">
                                        {data.description}
                                    </p>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* Navigation buttons */}
            <div className="mt-8 flex items-center gap-3">
                {step > 1 && (
                    <Button
                        type="button"
                        variant="outline"
                        className="h-12 border-border/60 bg-secondary/30 text-foreground hover:bg-secondary/50"
                        onClick={() => setStep(step - 1)}
                    >
                        <ArrowLeft className="mr-2 h-4 w-4"/>
                        Atras
                    </Button>
                )}

                {step < TOTAL_STEPS ? (
                    <Button
                        type="button"
                        className="h-12 flex-1 bg-primary font-semibold text-primary-foreground hover:bg-primary/90"
                        disabled={!canContinue()}
                        onClick={() => setStep(step + 1)}
                    >
                        Continuar
                        <ArrowRight className="ml-2 h-4 w-4"/>
                    </Button>
                ) : (
                    <Button
                        type="button"
                        className="h-12 flex-1 bg-primary font-semibold text-primary-foreground hover:bg-primary/90"
                        disabled={isSubmitting || !canContinue()}
                        onClick={handleSubmit}
                    >
                        {isSubmitting ? (
                            <Loader2 className="h-4 w-4 animate-spin"/>
                        ) : (
                            <>
                                Completar perfil
                                <Check className="ml-2 h-4 w-4"/>
                            </>
                        )}
                    </Button>
                )}
            </div>

        </div>
    )
}
