"use client";

import Image from "next/image";
import Link from "next/link";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { MapPin, LayoutGrid, ArrowRight } from "lucide-react";
import type { Venue } from "@/lib/types";

interface VenueCardProps {
  venue: Venue;
}

export function VenueCard({ venue }: VenueCardProps) {
  const mainImage = [...venue.images].sort((a, b) => a.displayOrder - b.displayOrder)[0];

  return (
    <div className="group relative flex flex-col overflow-hidden rounded-xl border border-border/50 bg-card transition-all duration-300 hover:border-primary/30 hover:shadow-lg hover:shadow-primary/5">
      {/* Image */}
      <div className="relative aspect-[16/10] overflow-hidden bg-secondary/30">
        {mainImage ? (
          <Image
            src={mainImage.url}
            alt={mainImage.alt || venue.name}
            fill
            className="object-cover transition-transform duration-500 group-hover:scale-105"
            sizes="(max-width: 640px) 100vw, (max-width: 1024px) 50vw, 33vw"
          />
        ) : (
          <div className="flex h-full items-center justify-center">
            <LayoutGrid className="h-10 w-10 text-muted-foreground/30" />
          </div>
        )}

        {/* Overlay gradient */}
        <div className="absolute inset-0 bg-gradient-to-t from-card/80 via-transparent to-transparent" />

        {/* Resource count badge */}
        <div className="absolute right-3 top-3">
          <Badge
            variant="secondary"
            className="border-0 bg-background/80 text-foreground backdrop-blur-sm"
          >
            <LayoutGrid className="mr-1 h-3 w-3 text-primary" />
            {venue.resourceCount}{" "}
            {venue.resourceCount === 1 ? "pista" : "pistas"}
          </Badge>
        </div>
      </div>

      {/* Content */}
      <div className="flex flex-1 flex-col gap-3 p-4">
        <div className="flex-1">
          <h3 className="font-[var(--font-space-grotesk)] text-lg font-bold leading-tight text-foreground transition-colors group-hover:text-primary">
            {venue.name}
          </h3>

          {venue.city && (
            <div className="mt-1.5 flex items-center gap-1 text-sm text-muted-foreground">
              <MapPin className="h-3.5 w-3.5 text-primary/70" />
              <span>
                {venue.street ? `${venue.street}, ` : ""}
                {venue.city}
              </span>
            </div>
          )}

          {venue.description && (
            <p className="mt-2 line-clamp-2 text-sm leading-relaxed text-muted-foreground">
              {venue.description}
            </p>
          )}
        </div>

        <Link href={`/venue/${venue.id}`} className="block">
          <Button className="h-10 w-full bg-primary font-medium text-primary-foreground hover:bg-primary/90">
            Reservar
            <ArrowRight className="ml-2 h-4 w-4" />
          </Button>
        </Link>
      </div>
    </div>
  );
}
