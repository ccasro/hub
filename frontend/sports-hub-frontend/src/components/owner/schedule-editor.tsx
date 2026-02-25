"use client";

import {useState} from "react";
import {Button} from "@/components/ui/button";
import {Input} from "@/components/ui/input";
import {Badge} from "@/components/ui/badge";
import {Switch} from "@/components/ui/switch";
import {Clock, Loader2, Save} from "lucide-react";
import type {DaySchedule} from "@/types";

export interface DayScheduleUpdate {
  dayOfWeek: DaySchedule["dayOfWeek"];
  openingTime: string | null;
  closingTime: string | null;
}

interface ScheduleEditorProps {
  schedules: DaySchedule[];
  onSave: (updates: DayScheduleUpdate[]) => void;
}

const DAYS: { key: DaySchedule["dayOfWeek"]; label: string; short: string }[] = [
  { key: "MON", label: "Lunes",     short: "Lun" },
  { key: "TUE", label: "Martes",    short: "Mar" },
  { key: "WED", label: "Miercoles", short: "Mie" },
  { key: "THU", label: "Jueves",    short: "Jue" },
  { key: "FRI", label: "Viernes",   short: "Vie" },
  { key: "SAT", label: "Sabado",    short: "Sab" },
  { key: "SUN", label: "Domingo",   short: "Dom" },
];

interface DayState {
  enabled: boolean;
  openingTime: string;
  closingTime: string;
}

export function ScheduleEditor({ schedules, onSave }: ScheduleEditorProps) {
  const [saving, setSaving] = useState(false);

  const initialState: Record<string, DayState> = {};
  DAYS.forEach((d) => {
    const existing = schedules.find((s) => s.dayOfWeek === d.key);
    initialState[d.key] = existing
        ? { enabled: true, openingTime: existing.openingTime, closingTime: existing.closingTime }
        : { enabled: false, openingTime: "08:00", closingTime: "22:00" };
  });

  const [days, setDays] = useState(initialState);

  const toggleDay = (key: string) => {
    setDays((prev) => ({ ...prev, [key]: { ...prev[key], enabled: !prev[key].enabled } }));
  };

  const updateTime = (key: string, field: "openingTime" | "closingTime", value: string) => {
    setDays((prev) => ({ ...prev, [key]: { ...prev[key], [field]: value } }));
  };

  const handleSave = () => {
    setSaving(true);
    const updates: DayScheduleUpdate[] = DAYS.map(({ key }) => ({
      dayOfWeek: key as DaySchedule["dayOfWeek"],
      openingTime: days[key].enabled ? days[key].openingTime : null,
      closingTime: days[key].enabled ? days[key].closingTime : null,
    }));
    setTimeout(() => {
      onSave(updates);
      setSaving(false);
    }, 500);
  };

  const enabledCount = Object.values(days).filter((d) => d.enabled).length;

  return (
      <div className="rounded-xl border border-border/50 bg-card">
        <div className="flex items-center justify-between border-b border-border/50 px-5 py-4">
          <div className="flex items-center gap-2">
            <Clock className="h-4 w-4 text-primary" />
            <h3 className="font-[var(--font-space-grotesk)] text-sm font-bold text-foreground">
              Horario semanal
            </h3>
            <Badge variant="secondary" className="border-0 bg-primary/10 text-[10px] font-medium text-primary">
              {enabledCount}/7 dias
            </Badge>
          </div>
          <Button
              size="sm"
              onClick={handleSave}
              disabled={saving}
              className="gap-1.5 bg-primary text-xs font-medium text-primary-foreground hover:bg-primary/90"
          >
            {saving ? <Loader2 className="h-3 w-3 animate-spin" /> : <Save className="h-3 w-3" />}
            Guardar
          </Button>
        </div>

        <div className="flex flex-col divide-y divide-border/30">
          {DAYS.map((day) => {
            const state = days[day.key];
            return (
                <div
                    key={day.key}
                    className={`flex items-center gap-4 px-5 py-3 transition-colors ${
                        state.enabled ? "bg-transparent" : "bg-secondary/10 opacity-50"
                    }`}
                >
                  <Switch
                      checked={state.enabled}
                      onCheckedChange={() => toggleDay(day.key)}
                      aria-label={`Activar ${day.label}`}
                  />
                  <span className="w-20 text-sm font-medium text-foreground">{day.label}</span>
                  {state.enabled ? (
                      <div className="flex items-center gap-2">
                        <Input
                            type="time"
                            value={state.openingTime}
                            onChange={(e) => updateTime(day.key, "openingTime", e.target.value)}
                            className="h-8 w-28 border-border/50 bg-secondary/30 text-sm text-foreground"
                        />
                        <span className="text-xs text-muted-foreground">a</span>
                        <Input
                            type="time"
                            value={state.closingTime}
                            onChange={(e) => updateTime(day.key, "closingTime", e.target.value)}
                            className="h-8 w-28 border-border/50 bg-secondary/30 text-sm text-foreground"
                        />
                      </div>
                  ) : (
                      <span className="text-xs text-muted-foreground">Cerrado</span>
                  )}
                </div>
            );
          })}
        </div>
      </div>
  );
}