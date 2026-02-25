"use client";

import {useCallback, useRef, useState} from "react";
import Image from "next/image";
import {GripVertical, ImagePlus, Loader2, X} from "lucide-react";

export interface UploadedImage {
  id: string;
  url: string;
  displayOrder: number;
  file?: File; // present only for new uploads not yet saved
}

interface ImageUploaderProps {
  images: UploadedImage[];
  onChange: (images: UploadedImage[]) => void;
  maxImages?: number;
  label?: string;
}

export function ImageUploader({
  images,
  onChange,
  maxImages = 5,
  label = "Imagenes",
}: ImageUploaderProps) {
  const [uploading, setUploading] = useState(false);
  const [dragOver, setDragOver] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFiles = useCallback(
    (files: FileList | null) => {
      if (!files || files.length === 0) return;

      const remaining = maxImages - images.length;
      if (remaining <= 0) return;

      setUploading(true);

      const newFiles = Array.from(files).slice(0, remaining);
      const newImages: UploadedImage[] = [];

      let processed = 0;
      newFiles.forEach((file, index) => {
        const reader = new FileReader();
        reader.onload = () => {
          newImages.push({
            id: `temp-${Date.now()}-${index}`,
            url: reader.result as string,
            displayOrder: images.length + index,
            file,
          });
          processed++;
          if (processed === newFiles.length) {
            onChange([...images, ...newImages]);
            setUploading(false);
          }
        };
        reader.readAsDataURL(file);
      });
    },
    [images, maxImages, onChange]
  );

  const handleDrop = useCallback(
    (e: React.DragEvent) => {
      e.preventDefault();
      setDragOver(false);
      handleFiles(e.dataTransfer.files);
    },
    [handleFiles]
  );

  const removeImage = (id: string) => {
    const updated = images
      .filter((img) => img.id !== id)
      .map((img, i) => ({ ...img, displayOrder: i }));
    onChange(updated);
  };

  return (
    <div className="flex flex-col gap-2">
      <label className="text-sm font-medium text-foreground">
        {label}{" "}
        <span className="font-normal text-muted-foreground">
          ({images.length}/{maxImages})
        </span>
      </label>

      {/* Preview grid */}
      {images.length > 0 && (
        <div className="flex flex-wrap gap-2">
          {images.map((img) => (
            <div
              key={img.id}
              className="group relative h-24 w-24 overflow-hidden rounded-lg border border-border/50 bg-secondary/30"
            >
              <Image
                src={img.url}
                alt={`Imagen ${img.displayOrder + 1}`}
                fill
                className="object-cover"
              />
              <div className="absolute inset-0 bg-black/0 transition-colors group-hover:bg-black/40" />
              <button
                type="button"
                onClick={() => removeImage(img.id)}
                className="absolute top-1 right-1 flex h-6 w-6 items-center justify-center rounded-full bg-black/70 text-foreground opacity-0 transition-opacity group-hover:opacity-100"
                aria-label="Eliminar imagen"
              >
                <X className="h-3.5 w-3.5" />
              </button>
              <div className="absolute bottom-1 left-1 flex h-5 w-5 items-center justify-center rounded bg-black/60">
                <GripVertical className="h-3 w-3 text-muted-foreground" />
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Drop zone */}
      {images.length < maxImages && (
        <div
          onDragOver={(e) => {
            e.preventDefault();
            setDragOver(true);
          }}
          onDragLeave={() => setDragOver(false)}
          onDrop={handleDrop}
          className={`flex cursor-pointer flex-col items-center justify-center rounded-lg border-2 border-dashed px-4 py-6 transition-colors ${
            dragOver
              ? "border-primary/60 bg-primary/5"
              : "border-border/50 bg-secondary/20 hover:border-primary/30 hover:bg-secondary/30"
          }`}
          onClick={() => fileInputRef.current?.click()}
          role="button"
          tabIndex={0}
          onKeyDown={(e) => {
            if (e.key === "Enter" || e.key === " ") fileInputRef.current?.click();
          }}
        >
          {uploading ? (
            <Loader2 className="h-6 w-6 animate-spin text-primary" />
          ) : (
            <ImagePlus className="h-6 w-6 text-muted-foreground" />
          )}
          <p className="mt-2 text-xs text-muted-foreground">
            {uploading
              ? "Procesando..."
              : "Arrastra imagenes o haz clic para seleccionar"}
          </p>
          <p className="mt-0.5 text-[10px] text-muted-foreground/60">
            JPG, PNG, WebP. Max 5MB por imagen.
          </p>
          <input
            ref={fileInputRef}
            type="file"
            accept="image/jpeg,image/png,image/webp"
            multiple
            className="hidden"
            onChange={(e) => {
              handleFiles(e.target.files);
              e.target.value = "";
            }}
          />
        </div>
      )}
    </div>
  );
}
