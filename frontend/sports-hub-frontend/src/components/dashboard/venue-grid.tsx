"use client";

import {VenueCard} from "@/components/dashboard/venue-card";
import {MapPin, SearchX} from "lucide-react";
import type {Venue} from "@/types";

interface VenueGridProps {
  venues: Venue[];
  selectedCity: string;
  searchQuery: string;
}

export function VenueGrid({
  venues,
  selectedCity,
  searchQuery,
}: VenueGridProps) {
  // Filter: only ACTIVE venues, then by city and search
  const filtered = venues
    .filter((v) => v.status === "ACTIVE")
    .filter((v) => selectedCity === "Todas" || v.city === selectedCity)
    .filter(
      (v) =>
        !searchQuery ||
        v.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        v.city?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        v.description?.toLowerCase().includes(searchQuery.toLowerCase())
    );

  const activeCount = venues.filter((v) => v.status === "ACTIVE").length;

  return (
    <section>
      {/* Results header */}
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="font-[var(--font-space-grotesk)] text-2xl font-bold text-foreground">
            Venues disponibles
          </h1>
          <p className="mt-1 text-sm text-muted-foreground">
            {filtered.length} de {activeCount} venues
            {selectedCity !== "Todas" && (
              <span>
                {" "}
                en <span className="font-medium text-primary">{selectedCity}</span>
              </span>
            )}
          </p>
        </div>
      </div>

      {/* Grid or empty state */}
      {filtered.length > 0 ? (
        <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
          {filtered.map((venue) => (
            <VenueCard key={venue.id} venue={venue} />
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center rounded-xl border border-dashed border-border/50 bg-card/50 py-20">
          {searchQuery ? (
            <>
              <SearchX className="h-12 w-12 text-muted-foreground/30" />
              <h3 className="mt-4 font-[var(--font-space-grotesk)] text-lg font-semibold text-foreground">
                Sin resultados
              </h3>
              <p className="mt-1 max-w-xs text-center text-sm text-muted-foreground">
                No encontramos venues que coincidan con{" "}
                <span className="font-medium text-foreground">
                  {'"'}{searchQuery}{'"'}
                </span>
              </p>
            </>
          ) : (
            <>
              <MapPin className="h-12 w-12 text-muted-foreground/30" />
              <h3 className="mt-4 font-[var(--font-space-grotesk)] text-lg font-semibold text-foreground">
                No hay venues en {selectedCity}
              </h3>
              <p className="mt-1 max-w-xs text-center text-sm text-muted-foreground">
                Prueba seleccionando otra ciudad o eligiendo{" "}
                <span className="font-medium text-primary">Todas</span> para
                ver todos los venues disponibles.
              </p>
            </>
          )}
        </div>
      )}
    </section>
  );
}
