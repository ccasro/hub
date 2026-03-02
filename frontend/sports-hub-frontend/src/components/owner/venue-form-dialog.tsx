"use client";

import {useEffect, useState} from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import {Button} from "@/components/ui/button";
import {Input} from "@/components/ui/input";
import {Label} from "@/components/ui/label";
import {Textarea} from "@/components/ui/textarea";
import {ScrollArea} from "@/components/ui/scroll-area";
import {MapPicker} from "@/components/owner/map-picker";
import {Building2, Loader2} from "lucide-react";
import type {Venue} from "@/types";

interface VenueFormDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  venue?: Venue | null;
  onSubmit: (data: VenueFormData) => void;
}

export interface VenueFormData {
  name: string;
  description: string;
  street: string;
  city: string;
  country: string;
  postalCode: string;
  latitude: number;
  longitude: number;
}

export function VenueFormDialog({
  open,
  onOpenChange,
  venue,
  onSubmit,
}: VenueFormDialogProps) {
  const isEditing = !!venue;
  const [saving, setSaving] = useState(false);

  const [form, setForm] = useState<VenueFormData>({
    name: "",
    description: "",
    street: "",
    city: "",
    country: "ES",
    postalCode: "",
    latitude: 40.4168,
    longitude: -3.7038,
  });

  useEffect(() => {
    if (venue) {
      setForm({
        name: venue.name,
        description: venue.description ?? "",
        street: venue.street ?? "",
        city: venue.city ?? "",
        country: venue.country ?? "ES",
        postalCode: venue.postalCode ?? "",
        latitude: venue.latitude ?? 40.4168,
        longitude: venue.longitude ?? -3.7038,
      });
    } else {
      setForm({
        name: "",
        description: "",
        street: "",
        city: "",
        country: "ES",
        postalCode: "",
        latitude: 40.4168,
        longitude: -3.7038,
      });
    }
  }, [venue, open]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setTimeout(() => {
      onSubmit(form);
      setSaving(false);
      onOpenChange(false);
    }, 800);
  };

  const update = (key: keyof VenueFormData, value: string | number) => {
    setForm((prev) => ({ ...prev, [key]: value }));
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-h-[90vh] max-w-2xl border-border bg-card p-0 text-card-foreground sm:max-w-2xl">
        <DialogHeader className="border-b border-border/50 px-6 pt-6 pb-4">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10">
              <Building2 className="h-5 w-5 text-primary" />
            </div>
            <div>
              <DialogTitle className="text-foreground">
                {isEditing ? "Editar Venue" : "Crear Nuevo Venue"}
              </DialogTitle>
              <DialogDescription>
                {isEditing
                  ? "Actualiza la informacion de tu venue."
                  : "El venue sera enviado para revision. Estado inicial: Pendiente de revision."}
              </DialogDescription>
            </div>
          </div>
        </DialogHeader>

        <ScrollArea className="max-h-[60vh]">
          <form id="venue-form" onSubmit={handleSubmit} className="flex flex-col gap-5 px-6 py-4">
            {/* Name */}
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="v-name" className="text-sm font-medium text-foreground">
                Nombre del venue *
              </Label>
              <Input
                id="v-name"
                value={form.name}
                onChange={(e) => update("name", e.target.value)}
                placeholder="Padel Club Madrid Centro"
                required
                className="h-10 border-border/50 bg-secondary/30 text-foreground placeholder:text-muted-foreground focus-visible:border-primary/50 focus-visible:ring-primary/20"
              />
            </div>

            {/* Description */}
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="v-desc" className="text-sm font-medium text-foreground">
                Descripcion
              </Label>
              <Textarea
                id="v-desc"
                value={form.description}
                onChange={(e) => update("description", e.target.value)}
                placeholder="Describe tu venue: instalaciones, servicios, ambiente..."
                rows={3}
                className="border-border/50 bg-secondary/30 text-foreground placeholder:text-muted-foreground focus-visible:border-primary/50 focus-visible:ring-primary/20"
              />
            </div>

            {/* Address row */}
            <div className="grid gap-4 sm:grid-cols-2">
              <div className="flex flex-col gap-1.5">
                <Label htmlFor="v-street" className="text-sm font-medium text-foreground">
                  Calle *
                </Label>
                <Input
                  id="v-street"
                  value={form.street}
                  onChange={(e) => update("street", e.target.value)}
                  placeholder="Calle Gran Via 45"
                  required
                  className="h-10 border-border/50 bg-secondary/30 text-foreground placeholder:text-muted-foreground focus-visible:border-primary/50 focus-visible:ring-primary/20"
                />
              </div>
              <div className="flex flex-col gap-1.5">
                <Label htmlFor="v-city" className="text-sm font-medium text-foreground">
                  Ciudad *
                </Label>
                <Input
                  id="v-city"
                  value={form.city}
                  onChange={(e) => update("city", e.target.value)}
                  placeholder="Madrid"
                  required
                  className="h-10 border-border/50 bg-secondary/30 text-foreground placeholder:text-muted-foreground focus-visible:border-primary/50 focus-visible:ring-primary/20"
                />
              </div>
            </div>

            <div className="grid gap-4 sm:grid-cols-3">
              <div className="flex flex-col gap-1.5">
                <Label htmlFor="v-country" className="text-sm font-medium text-foreground">
                  Pais *
                </Label>
                <Input
                  id="v-country"
                  value={form.country}
                  onChange={(e) => update("country", e.target.value)}
                  placeholder="ES"
                  required
                  className="h-10 border-border/50 bg-secondary/30 text-foreground placeholder:text-muted-foreground focus-visible:border-primary/50 focus-visible:ring-primary/20"
                />
              </div>
              <div className="flex flex-col gap-1.5 sm:col-span-2">
                <Label htmlFor="v-postal" className="text-sm font-medium text-foreground">
                  Codigo Postal
                </Label>
                <Input
                  id="v-postal"
                  value={form.postalCode}
                  onChange={(e) => update("postalCode", e.target.value)}
                  placeholder="28013"
                  className="h-10 border-border/50 bg-secondary/30 text-foreground placeholder:text-muted-foreground focus-visible:border-primary/50 focus-visible:ring-primary/20"
                />
              </div>
            </div>

            {/* Map */}
            <MapPicker
              latitude={form.latitude}
              longitude={form.longitude}
              onLocationChange={(lat, lng) => {
                setForm((prev) => ({ ...prev, latitude: lat, longitude: lng }));
              }}
              onAddressChange={(street, city, country, postalCode) => {
                setForm((prev) => ({
                  ...prev,
                  street:     street     || prev.street,
                  city:       city       || prev.city,
                  country:    country    || prev.country,
                  postalCode: postalCode || prev.postalCode,
                }));
              }}
            />
          </form>
        </ScrollArea>

        <DialogFooter className="border-t border-border/50 px-6 py-4">
          <Button
            variant="outline"
            onClick={() => onOpenChange(false)}
            className="border-border/60 text-foreground"
          >
            Cancelar
          </Button>
          <Button
            type="submit"
            form="venue-form"
            disabled={saving || !form.name || !form.street || !form.city || !form.country}
            className="bg-primary font-medium text-primary-foreground hover:bg-primary/90"
          >
            {saving ? (
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
            ) : null}
            {isEditing ? "Guardar cambios" : "Crear Venue"}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}