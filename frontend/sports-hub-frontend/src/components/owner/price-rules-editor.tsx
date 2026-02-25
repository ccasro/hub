"use client";

import {useState} from "react";
import {Button} from "@/components/ui/button";
import {Input} from "@/components/ui/input";
import {Label} from "@/components/ui/label";
import {Badge} from "@/components/ui/badge";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue,} from "@/components/ui/select";
import {Euro, Loader2, Plus, Trash2} from "lucide-react";
import type {PriceRule} from "@/types";

interface PriceRulesEditorProps {
  priceRules: PriceRule[];
  onAdd: (rule: Omit<PriceRule, "id">) => void;
  onDelete: (ruleId: string) => void;
}

const dayTypeOptions: { value: PriceRule["dayType"]; label: string }[] = [
  { value: "WEEKDAY", label: "Entre semana (L-V)" },
  { value: "WEEKEND", label: "Fin de semana (S-D)" },
  { value: "MON", label: "Lunes" },
  { value: "TUE", label: "Martes" },
  { value: "WED", label: "Miercoles" },
  { value: "THU", label: "Jueves" },
  { value: "FRI", label: "Viernes" },
  { value: "SAT", label: "Sabado" },
  { value: "SUN", label: "Domingo" },
];

function dayTypeLabel(dt: string): string {
  return dayTypeOptions.find((o) => o.value === dt)?.label ?? dt;
}

export function PriceRulesEditor({
  priceRules,
  onAdd,
  onDelete,
}: PriceRulesEditorProps) {
  const [showForm, setShowForm] = useState(false);
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState({
    dayType: "WEEKDAY" as PriceRule["dayType"],
    startTime: "08:00",
    endTime: "22:00",
    price: 20,
    currency: "EUR",
  });

  const handleAdd = () => {
    setSaving(true);
    setTimeout(() => {
      onAdd(form);
      setSaving(false);
      setShowForm(false);
      setForm({
        dayType: "WEEKDAY",
        startTime: "08:00",
        endTime: "22:00",
        price: 20,
        currency: "EUR",
      });
    }, 400);
  };

  return (
    <div className="rounded-xl border border-border/50 bg-card">
      <div className="flex items-center justify-between border-b border-border/50 px-5 py-4">
        <div className="flex items-center gap-2">
          <Euro className="h-4 w-4 text-primary" />
          <h3 className="font-[var(--font-space-grotesk)] text-sm font-bold text-foreground">
            Reglas de precio
          </h3>
          <Badge
            variant="secondary"
            className="border-0 bg-primary/10 text-[10px] font-medium text-primary"
          >
            {priceRules.length} reglas
          </Badge>
        </div>
        {!showForm && (
          <Button
            size="sm"
            variant="outline"
            onClick={() => setShowForm(true)}
            className="gap-1.5 border-border/60 text-xs text-foreground"
          >
            <Plus className="h-3 w-3" />
            Anadir
          </Button>
        )}
      </div>

      {/* Existing rules */}
      {priceRules.length > 0 && (
        <div className="flex flex-col divide-y divide-border/30">
          {priceRules.map((rule) => (
            <div
              key={rule.id}
              className="flex items-center justify-between px-5 py-3"
            >
              <div className="flex flex-wrap items-center gap-2">
                <Badge
                  variant="outline"
                  className="border-primary/20 bg-primary/5 text-xs text-primary"
                >
                  {dayTypeLabel(rule.dayType)}
                </Badge>
                <span className="text-xs text-muted-foreground">
                  {rule.startTime} - {rule.endTime}
                </span>
              </div>
              <div className="flex items-center gap-3">
                <span className="text-sm font-bold text-foreground">
                  {rule.price}
                  {rule.currency === "EUR" ? "\u20AC" : ` ${rule.currency}`}
                </span>
                <Button
                  variant="ghost"
                  size="icon"
                  className="h-7 w-7 text-muted-foreground hover:text-destructive"
                  onClick={() => onDelete(rule.id)}
                >
                  <Trash2 className="h-3.5 w-3.5" />
                </Button>
              </div>
            </div>
          ))}
        </div>
      )}

      {priceRules.length === 0 && !showForm && (
        <div className="px-5 py-8 text-center">
          <p className="text-sm text-muted-foreground">
            No hay reglas de precio configuradas.
          </p>
          <p className="mt-1 text-xs text-muted-foreground/70">
            Anade reglas para definir precios por tramo horario y dia.
          </p>
        </div>
      )}

      {/* Add form */}
      {showForm && (
        <div className="border-t border-border/50 bg-secondary/10 p-5">
          <h4 className="mb-3 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            Nueva regla
          </h4>
          <div className="flex flex-col gap-3">
            <div className="flex flex-col gap-1.5">
              <Label className="text-xs text-muted-foreground">Tipo de dia</Label>
              <Select
                value={form.dayType}
                onValueChange={(val) =>
                  setForm((prev) => ({
                    ...prev,
                    dayType: val as PriceRule["dayType"],
                  }))
                }
              >
                <SelectTrigger className="h-9 border-border/50 bg-secondary/30 text-sm text-foreground">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent className="border-border bg-card text-card-foreground">
                  {dayTypeOptions.map((o) => (
                    <SelectItem key={o.value} value={o.value}>
                      {o.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div className="flex flex-col gap-1.5">
                <Label className="text-xs text-muted-foreground">Hora inicio</Label>
                <Input
                  type="time"
                  value={form.startTime}
                  onChange={(e) =>
                    setForm((prev) => ({ ...prev, startTime: e.target.value }))
                  }
                  className="h-9 border-border/50 bg-secondary/30 text-sm text-foreground"
                />
              </div>
              <div className="flex flex-col gap-1.5">
                <Label className="text-xs text-muted-foreground">Hora fin</Label>
                <Input
                  type="time"
                  value={form.endTime}
                  onChange={(e) =>
                    setForm((prev) => ({ ...prev, endTime: e.target.value }))
                  }
                  className="h-9 border-border/50 bg-secondary/30 text-sm text-foreground"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div className="flex flex-col gap-1.5">
                <Label className="text-xs text-muted-foreground">Precio</Label>
                <Input
                  type="number"
                  min={0}
                  step={0.5}
                  value={form.price}
                  onChange={(e) =>
                    setForm((prev) => ({
                      ...prev,
                      price: parseFloat(e.target.value) || 0,
                    }))
                  }
                  className="h-9 border-border/50 bg-secondary/30 text-sm text-foreground"
                />
              </div>
              <div className="flex flex-col gap-1.5">
                <Label className="text-xs text-muted-foreground">Moneda</Label>
                <Select
                  value={form.currency}
                  onValueChange={(val) =>
                    setForm((prev) => ({ ...prev, currency: val }))
                  }
                >
                  <SelectTrigger className="h-9 border-border/50 bg-secondary/30 text-sm text-foreground">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent className="border-border bg-card text-card-foreground">
                    <SelectItem value="EUR">EUR</SelectItem>
                    <SelectItem value="USD">USD</SelectItem>
                    <SelectItem value="GBP">GBP</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>

            <div className="flex items-center justify-end gap-2 pt-1">
              <Button
                variant="ghost"
                size="sm"
                onClick={() => setShowForm(false)}
                className="text-xs text-muted-foreground"
              >
                Cancelar
              </Button>
              <Button
                size="sm"
                onClick={handleAdd}
                disabled={saving}
                className="gap-1.5 bg-primary text-xs font-medium text-primary-foreground hover:bg-primary/90"
              >
                {saving && <Loader2 className="h-3 w-3 animate-spin" />}
                Anadir regla
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
