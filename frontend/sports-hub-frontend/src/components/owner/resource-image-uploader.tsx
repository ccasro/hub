// src/components/owner/resource-image-uploader.tsx
"use client"

import {useRef, useState} from "react"
import Image from "next/image"
import {Button} from "@/components/ui/button"
import {ImagePlus, Loader2, Trash2} from "lucide-react"
import {uploadToCloudinary} from "@/lib/cloudinary-upload"
import type {Resource} from "@/types"

interface Props {
    resource: Resource
    onUpdate: (updated: Resource) => void
}

export function ResourceImageUploader({ resource, onUpdate }: Props) {
    const [uploading, setUploading] = useState(false)
    const [deletingId, setDeletingId] = useState<string | null>(null)
    const [error, setError] = useState<string | null>(null)
    const fileInputRef = useRef<HTMLInputElement>(null)

    const canAddMore = resource.images.length < 5


    const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0]
        if (!file) return
        e.target.value = ""

        setUploading(true)
        setError(null)

        try {
            // 1. Subir a Cloudinary
            const { url, publicId } = await uploadToCloudinary(file, "RESOURCE_IMAGE", {
                resourceId: resource.id,
            })

            // 2. Guardar en backend
            const res = await fetch(`/api/proxy/api/owner/resources/${resource.id}/images`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ url, publicId }),
            })
            if (!res.ok) throw new Error(`Error ${res.status}`)
            const updated: Resource = await res.json()
            onUpdate(updated)
        } catch (err) {
            setError(err instanceof Error ? err.message : "Error al subir imagen")
        } finally {
            setUploading(false)
        }
    }


    const handleDelete = async (imageId: string) => {
        setDeletingId(imageId)
        setError(null)
        try {
            const res = await fetch(
                `/api/proxy/api/owner/resources/${resource.id}/images/${imageId}`,
                { method: "DELETE" }
            )
            if (!res.ok) throw new Error(`Error ${res.status}`)
            // El endpoint devuelve 200 sin body — actualizamos local
            onUpdate({
                ...resource,
                images: resource.images
                    .filter((img) => img.id !== imageId)
                    .map((img, i) => ({ ...img, displayOrder: i })),
            })
        } catch (err) {
            setError(err instanceof Error ? err.message : "Error al eliminar imagen")
        } finally {
            setDeletingId(null)
        }
    }


    return (
        <div className="rounded-xl border border-border/50 bg-card p-5">
            <div className="mb-4 flex items-center justify-between">
                <div>
                    <p className="text-sm font-semibold text-foreground">Imagenes de la pista</p>
                    <p className="text-xs text-muted-foreground">
                        {resource.images.length}/5 imagenes
                    </p>
                </div>
                {canAddMore && (
                    <Button
                        size="sm"
                        variant="outline"
                        onClick={() => fileInputRef.current?.click()}
                        disabled={uploading}
                        className="gap-1.5 border-border/50 text-xs text-foreground hover:bg-secondary/30"
                    >
                        {uploading ? (
                            <Loader2 className="h-3.5 w-3.5 animate-spin" />
                        ) : (
                            <ImagePlus className="h-3.5 w-3.5" />
                        )}
                        {uploading ? "Subiendo..." : "Subir imagen"}
                    </Button>
                )}
                <input
                    ref={fileInputRef}
                    type="file"
                    accept="image/jpeg,image/png,image/webp"
                    className="hidden"
                    onChange={handleFileChange}
                />
            </div>

            {/* Error */}
            {error && (
                <p className="mb-3 rounded-lg border border-destructive/30 bg-destructive/5 px-3 py-2 text-xs text-destructive">
                    {error}
                </p>
            )}

            {/* Images grid */}
            {resource.images.length > 0 ? (
                <div className="flex flex-wrap gap-2">
                    {resource.images
                        .sort((a, b) => a.displayOrder - b.displayOrder)
                        .map((img) => (
                            <div
                                key={img.id}
                                className="group relative h-24 w-24 overflow-hidden rounded-lg border border-border/50 bg-secondary/30"
                            >
                                <Image
                                    src={img.url}
                                    alt={`Imagen ${img.displayOrder + 1}`}
                                    fill
                                    className="object-cover"
                                    sizes="96px"
                                />
                                <div className="absolute inset-0 bg-black/0 transition-colors group-hover:bg-black/50" />
                                <button
                                    type="button"
                                    onClick={() => handleDelete(img.id)}
                                    disabled={deletingId === img.id}
                                    className="absolute inset-0 flex items-center justify-center opacity-0 transition-opacity group-hover:opacity-100"
                                    aria-label="Eliminar imagen"
                                >
                                    {deletingId === img.id ? (
                                        <Loader2 className="h-5 w-5 animate-spin text-white" />
                                    ) : (
                                        <Trash2 className="h-5 w-5 text-white" />
                                    )}
                                </button>
                            </div>
                        ))}
                </div>
            ) : (
                <div
                    onClick={() => canAddMore && fileInputRef.current?.click()}
                    className="flex cursor-pointer flex-col items-center justify-center rounded-lg border-2 border-dashed border-border/40 py-8 transition-colors hover:border-primary/30 hover:bg-secondary/10"
                >
                    <ImagePlus className="h-8 w-8 text-muted-foreground/30" />
                    <p className="mt-2 text-xs text-muted-foreground">
                        Haz clic para subir la primera imagen
                    </p>
                </div>
            )}
        </div>
    )
}
