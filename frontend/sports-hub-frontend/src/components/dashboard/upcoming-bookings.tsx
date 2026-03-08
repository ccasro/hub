"use client";

import Link from "next/link";
import {Badge} from "@/components/ui/badge";
import {Button} from "@/components/ui/button";
import {ArrowRight, Clock, MapPin, Ticket,} from "lucide-react";
import type {Booking} from "@/types";

interface UpcomingBookingsProps {
  bookings: Booking[];
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr + "T00:00:00");
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const tomorrow = new Date(today);
  tomorrow.setDate(tomorrow.getDate() + 1);

  if (date.getTime() === today.getTime()) return "Hoy";
  if (date.getTime() === tomorrow.getTime()) return "Manana";

  return date.toLocaleDateString("es-ES", {
    weekday: "short",
    day: "numeric",
    month: "short",
  });
}

function formatTime(timeStr: string): string {
  return timeStr.slice(0, 5);
}

export function UpcomingBookings({ bookings }: UpcomingBookingsProps) {
  const now = new Date();
  const upcoming = bookings
    .filter((b) => b.status === "CONFIRMED" && new Date(`${b.bookingDate}T${b.endTime}`) > now)
    .sort((a, b) => a.bookingDate.localeCompare(b.bookingDate) || a.startTime.localeCompare(b.startTime))
    .slice(0, 3);

  if (upcoming.length === 0) {
    return (
      <section className="rounded-xl border border-border/50 bg-card p-5">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="font-[var(--font-space-grotesk)] text-lg font-bold text-foreground">
            Proximas reservas
          </h2>
        </div>
        <div className="flex flex-col items-center py-8">
          <div className="flex h-12 w-12 items-center justify-center rounded-full bg-secondary/50">
            <Ticket className="h-6 w-6 text-muted-foreground/50" />
          </div>
          <p className="mt-3 text-sm font-medium text-foreground">
            Sin reservas pendientes
          </p>
          <p className="mt-1 text-xs text-muted-foreground">
            Explora los venues y reserva tu proxima pista
          </p>
        </div>
      </section>
    );
  }

  return (
    <section className="rounded-xl border border-border/50 bg-card p-5">
      <div className="mb-4 flex items-center justify-between">
        <h2 className="font-[var(--font-space-grotesk)] text-lg font-bold text-foreground">
          Proximas reservas
        </h2>
        <Link href="/dashboard/bookings">
          <Button
            variant="ghost"
            size="sm"
            className="gap-1.5 text-xs text-muted-foreground hover:text-foreground"
          >
            Ver todas
            <ArrowRight className="h-3 w-3" />
          </Button>
        </Link>
      </div>

      <div className="flex flex-col gap-3">
        {upcoming.map((booking) => (
          <div
            key={booking.id}
            className="group flex items-center gap-4 rounded-lg border border-border/30 bg-secondary/20 p-3.5 transition-colors hover:border-primary/20 hover:bg-secondary/30"
          >
            {/* Date block */}
            <div className="flex h-12 w-14 shrink-0 flex-col items-center justify-center rounded-lg bg-primary/10">
              <span className="text-[10px] font-semibold uppercase leading-none text-primary">
                {formatDate(booking.bookingDate).split(" ")[0]}
              </span>
              <span className="mt-0.5 text-lg font-bold leading-none text-foreground">
                {new Date(booking.bookingDate + "T00:00:00").getDate()}
              </span>
            </div>

            {/* Info */}
            <div className="flex-1 min-w-0">
              <p className="truncate text-sm font-semibold text-foreground">
                {booking.venueName}
              </p>
              <div className="mt-1 flex flex-wrap items-center gap-x-3 gap-y-1">
                <span className="flex items-center gap-1 text-xs text-muted-foreground">
                  <Clock className="h-3 w-3 text-primary/70" />
                  {formatTime(booking.startTime)} - {formatTime(booking.endTime)}
                </span>
                {booking.venueCity && (
                  <span className="flex items-center gap-1 text-xs text-muted-foreground">
                    <MapPin className="h-3 w-3 text-primary/70" />
                    {booking.venueCity}
                  </span>
                )}
              </div>
              {booking.resourceName && (
                <Badge
                  variant="secondary"
                  className="mt-1.5 h-5 border-0 bg-secondary/50 text-[10px] font-medium text-muted-foreground"
                >
                  {booking.resourceName}
                </Badge>
              )}
            </div>

            {/* Price */}
            <div className="shrink-0 text-right">
              <p className="text-sm font-bold text-foreground">
                {booking.pricePaid}{booking.currency === "EUR" ? "\u20AC" : booking.currency}
              </p>
              <Badge className="mt-1 h-5 border-0 bg-green-500/10 text-[10px] font-medium text-green-400">
                Confirmada
              </Badge>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}
