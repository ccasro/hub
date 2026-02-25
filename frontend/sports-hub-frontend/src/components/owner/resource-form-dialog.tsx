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
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue,} from "@/components/ui/select";
import {LayoutGrid, Loader2} from "lucide-react";
import type {Resource, Venue} from "@/types";

interface ResourceFormDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  venues: Venue[];
  preselectedVenueId?: string;
  onSubmit: (data: ResourceFormData) => void;
}

export interface ResourceFormData {
  venueId: string;
  name: string;
  description: string;
  type: Resource["type"];
  slotDurationMinutes: 60 | 90 | 120;
}

const sportTypes: { value: Resource["type"]; label: string }[] = [
  { value: "PADEL", label: "Padel" },
  { value: "TENNIS", label: "Tenis" },
  { value: "SQUASH", label: "Squash" },
  { value: "BADMINTON", label: "Badminton" },
  { value: "OTHER", label: "Otro" },
];

const slotOptions: { value: string; label: string }[] = [
  { value: "60", label: "60 min (1 hora)" },
  { value: "90", label: "90 min (1.5 horas)" },
  { value: "120", label: "120 min (2 horas)" },
];

export function ResourceFormDialog({
  open,
  onOpenChange,
  venues,
  preselectedVenueId,
  onSubmit,
}: ResourceFormDialogProps) {
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState<ResourceFormData>({
    venueId: preselectedVenueId ?? "",
    name: "",
    description: "",
    type: "PADEL",
    slotDurationMinutes: 90,
  });

  useEffect(() => {
    if (open) {
      setForm({
        venueId: preselectedVenueId ?? "",
        name: "",
        description: "",
        type: "PADEL",
        slotDurationMinutes: 90,
      });
    }
  }, [open, preselectedVenueId]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setTimeout(() => {
      onSubmit(form);
      setSaving(false);
      onOpenChange(false);
    }, 800);
  };

  const activeVenues = venues.filter(
    (v) => v.status === "ACTIVE" || v.status === "PENDING_REVIEW"
  );

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-lg border-border bg-card p-0 text-card-foreground">
        <DialogHeader className="border-b border-border/50 px-6 pt-6 pb-4">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10">
              <LayoutGrid className="h-5 w-5 text-primary" />
            </div>
            <div>
              <DialogTitle className="text-foreground">Crear Pista</DialogTitle>
              <DialogDescription>
                La pista sera enviada para revision antes de activarse.
              </DialogDescription>
            </div>
          </div>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="flex flex-col gap-5 px-6 py-4">
          {/* Venue select */}
          <div className="flex flex-col gap-1.5">
            <Label className="text-sm font-medium text-foreground">Venue *</Label>
            <Select
              value={form.venueId}
              onValueChange={(val) =>
                setForm((prev) => ({ ...prev, venueId: val }))
              }
            >
              <SelectTrigger className="h-10 border-border/50 bg-secondary/30 text-foreground">
                <SelectValue placeholder="Selecciona un venue" />
              </SelectTrigger>
              <SelectContent className="border-border bg-card text-card-foreground">
                {activeVenues.map((v) => (
                  <SelectItem key={v.id} value={v.id}>
                    {v.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          {/* Name */}
          <div className="flex flex-col gap-1.5">
            <Label className="text-sm font-medium text-foreground">
              Nombre de la pista *
            </Label>
            <Input
              value={form.name}
              onChange={(e) =>
                setForm((prev) => ({ ...prev, name: e.target.value }))
              }
              placeholder="Pista Central"
              required
              className="h-10 border-border/50 bg-secondary/30 text-foreground placeholder:text-muted-foreground"
            />
          </div>

          {/* Description */}
          <div className="flex flex-col gap-1.5">
            <Label className="text-sm font-medium text-foreground">Descripcion</Label>
            <Textarea
              value={form.description}
              onChange={(e) =>
                setForm((prev) => ({ ...prev, description: e.target.value }))
              }
              placeholder="Pista con cristal panoramico..."
              rows={2}
              className="border-border/50 bg-secondary/30 text-foreground placeholder:text-muted-foreground"
            />
          </div>

          {/* Type + Slot duration */}
          <div className="grid gap-4 sm:grid-cols-2">
            <div className="flex flex-col gap-1.5">
              <Label className="text-sm font-medium text-foreground">Deporte *</Label>
              <Select
                value={form.type}
                onValueChange={(val) =>
                  setForm((prev) => ({
                    ...prev,
                    type: val as Resource["type"],
                  }))
                }
              >
                <SelectTrigger className="h-10 border-border/50 bg-secondary/30 text-foreground">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent className="border-border bg-card text-card-foreground">
                  {sportTypes.map((s) => (
                    <SelectItem key={s.value} value={s.value}>
                      {s.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="flex flex-col gap-1.5">
              <Label className="text-sm font-medium text-foreground">
                Duracion del slot *
              </Label>
              <Select
                value={form.slotDurationMinutes.toString()}
                onValueChange={(val) =>
                  setForm((prev) => ({
                    ...prev,
                    slotDurationMinutes: parseInt(val) as 60 | 90 | 120,
                  }))
                }
              >
                <SelectTrigger className="h-10 border-border/50 bg-secondary/30 text-foreground">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent className="border-border bg-card text-card-foreground">
                  {slotOptions.map((s) => (
                    <SelectItem key={s.value} value={s.value}>
                      {s.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>
        </form>

        <DialogFooter className="border-t border-border/50 px-6 py-4">
          <Button
            variant="outline"
            onClick={() => onOpenChange(false)}
            className="border-border/60 text-foreground"
          >
            Cancelar
          </Button>
          <Button
            onClick={(e) => {
              const form2 = document.querySelector("form");
              form2?.requestSubmit();
            }}
            disabled={saving || !form.venueId || !form.name}
            className="bg-primary font-medium text-primary-foreground hover:bg-primary/90"
          >
            {saving && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
            Crear Pista
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
