"use client"

import {useRef, useState} from "react"
import Image from "next/image"
import {Button} from "@/components/ui/button"
import {Dialog, DialogContent, DialogHeader, DialogTitle,} from "@/components/ui/dialog"
import {ImagePlus, Loader2, Trash2} from "lucide-react"
import {uploadToCloudinary} from "@/lib/cloudinary-upload"
import type {Venue} from "@/types"

interface Props {
    venue: Venue
    open: boolean
    onOpenChange: (open: boolean) => void
    onUpdate: (updated: Venue) => void
}

export function VenueImageManager({ venue, open, onOpenChange, onUpdate }: Props) {
    const [uploading, setUploading] = useState(false)
    const [deletingId, setDeletingId] = useState<string | null>(null)
    const [error, setError] = useState<string | null>(null)
    const fileInputRef = useRef<HTMLInputElement>(null)

    const canAddMore = (venue.images ?? []).length < 5

    // ── Upload ────────────────────────────────────────────────────

    const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0]
        if (!file) return
        e.target.value = ""

        setUploading(true)
        setError(null)

        try {
            // 1. Subir a Cloudinary
            const { url, publicId } = await uploadToCloudinary(file, "VENUE_IMAGE", {
                venueId: venue.id,
            })

            // 2. Guardar en backend
            const res = await fetch(`/api/proxy/api/owner/venues/${venue.id}/images`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ url, publicId }),
            })
            if (!res.ok) throw new Error(`Error ${res.status}`)
            const updated: Venue = await res.json()
            onUpdate(updated)
        } catch (err) {
            setError(err instanceof Error ? err.message : "Error al subir imagen")
        } finally {
            setUploading(false)
        }
    }

    // ── Delete ────────────────────────────────────────────────────

    const handleDelete = async (imageId: string) => {
        setDeletingId(imageId)
        setError(null)
        try {
            const res = await fetch(
                `/api/proxy/api/owner/venues/${venue.id}/images/${imageId}`,
                { method: "DELETE" }
            )
            if (!res.ok) throw new Error(`Error ${res.status}`)
            onUpdate({
                ...venue,
                images: (venue.images ?? [])
                    .filter((img) => img.id !== imageId)
                    .map((img, i) => ({ ...img, displayOrder: i })),
            })
        } catch (err) {
            setError(err instanceof Error ? err.message : "Error al eliminar imagen")
        } finally {
            setDeletingId(null)
        }
    }

    const sortedImages = [...(venue.images ?? [])].sort(
        (a, b) => a.displayOrder - b.displayOrder
    )

    // ── Render ────────────────────────────────────────────────────

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="max-w-lg border-border bg-card text-card-foreground">
                <DialogHeader>
                    <DialogTitle className="text-foreground">
                        Imagenes de {venue.name}
                    </DialogTitle>
                </DialogHeader>

                <div className="flex flex-col gap-4">
                    {/* Error */}
                    {error && (
                        <p className="rounded-lg border border-destructive/30 bg-destructive/5 px-3 py-2 text-xs text-destructive">
                            {error}
                        </p>
                    )}

                    {/* Images grid */}
                    {sortedImages.length > 0 ? (
                        <div className="flex flex-wrap gap-2">
                            {sortedImages.map((img) => (
                                <div
                                    key={img.id}
                                    className="group relative h-28 w-28 overflow-hidden rounded-lg border border-border/50 bg-secondary/30"
                                >
                                    <Image
                                        src={img.url}
                                        alt={`Imagen ${img.displayOrder + 1}`}
                                        fill
                                        className="object-cover"
                                        sizes="112px"
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
                            className="flex cursor-pointer flex-col items-center justify-center rounded-lg border-2 border-dashed border-border/40 py-10 transition-colors hover:border-primary/30 hover:bg-secondary/10"
                        >
                            <ImagePlus className="h-8 w-8 text-muted-foreground/30" />
                            <p className="mt-2 text-xs text-muted-foreground">
                                Haz clic para subir la primera imagen
                            </p>
                        </div>
                    )}

                    {/* Upload button */}
                    <div className="flex items-center justify-between border-t border-border/50 pt-3">
                        <p className="text-xs text-muted-foreground">
                            {sortedImages.length}/5 imagenes · JPG, PNG, WebP · Max 5MB
                        </p>
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
                    </div>

                    <input
                        ref={fileInputRef}
                        type="file"
                        accept="image/jpeg,image/png,image/webp"
                        className="hidden"
                        onChange={handleFileChange}
                    />
                </div>
            </DialogContent>
        </Dialog>
    )
}
