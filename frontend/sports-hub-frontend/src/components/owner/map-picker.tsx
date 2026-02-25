"use client";

import {useEffect, useRef, useState} from "react";
import {Input} from "@/components/ui/input";
import {Label} from "@/components/ui/label";
import {MapPin} from "lucide-react";
import type * as Leaflet from "leaflet";

interface MapPickerProps {
  latitude: number;
  longitude: number;
  onLocationChange: (lat: number, lng: number) => void;
}

type LeafletContainer = HTMLDivElement & { _leaflet_id?: number };

export function MapPicker({ latitude, longitude, onLocationChange }: MapPickerProps) {
  const mapRef = useRef<HTMLDivElement>(null);
  const mapInstanceRef = useRef<Leaflet.Map | null>(null);
  const markerRef = useRef<Leaflet.Marker | null>(null);
  const [loaded, setLoaded] = useState(false);

  useEffect(() => {
    if (!mapRef.current || mapInstanceRef.current) return;

    const container = mapRef.current as LeafletContainer;

    const loadLeaflet = async () => {
      const L = (await import("leaflet")) as unknown as typeof Leaflet;

      // Load CSS
      if (!document.querySelector('link[href*="leaflet.css"]')) {
        const link = document.createElement("link");
        link.rel = "stylesheet";
        link.href = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.css";
        document.head.appendChild(link);
      }

      if (container._leaflet_id) {
        delete container._leaflet_id;
      }

      const DefaultIcon = L.icon({
        iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
        iconRetinaUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
        shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
        iconSize: [25, 41],
        iconAnchor: [12, 41],
      });

      const map = L.map(container, {
        center: [latitude, longitude],
        zoom: 14,
        zoomControl: true,
      });

      L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution:
            '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
      }).addTo(map);

      const marker = L.marker([latitude, longitude], {
        icon: DefaultIcon,
        draggable: true,
      }).addTo(map);

      marker.on("dragend", () => {
        const pos = marker.getLatLng();
        const clampedLat = Math.max(-90, Math.min(90, pos.lat));
        const clampedLng = Math.max(-180, Math.min(180, pos.lng));
        onLocationChange(
            parseFloat(clampedLat.toFixed(6)),
            parseFloat(clampedLng.toFixed(6))
        );
      });

      map.on("click", (e: Leaflet.LeafletMouseEvent) => {
        const clampedLat = Math.max(-90, Math.min(90, e.latlng.lat));
        const clampedLng = Math.max(-180, Math.min(180, e.latlng.lng));
        marker.setLatLng([clampedLat, clampedLng]);
        onLocationChange(
            parseFloat(clampedLat.toFixed(6)),
            parseFloat(clampedLng.toFixed(6))
        );
      });

      mapInstanceRef.current = map;
      markerRef.current = marker;
      setLoaded(true);

      setTimeout(() => map.invalidateSize(), 100);
    };

    loadLeaflet();

    return () => {
      mapInstanceRef.current?.remove();
      mapInstanceRef.current = null;
      markerRef.current = null;

      if (container._leaflet_id) delete container._leaflet_id;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    if (markerRef.current && mapInstanceRef.current) {
      markerRef.current.setLatLng([latitude, longitude]);
      mapInstanceRef.current.setView([latitude, longitude], mapInstanceRef.current.getZoom());
    }
  }, [latitude, longitude]);

  const handleLatChange = (val: string) => {
    const lat = parseFloat(val);
    if (!isNaN(lat) && lat >= -90 && lat <= 90) {
      onLocationChange(lat, longitude);
    }
  };

  const handleLngChange = (val: string) => {
    const lng = parseFloat(val);
    if (!isNaN(lng) && lng >= -180 && lng <= 180) {
      onLocationChange(latitude, lng);
    }
  };

  return (
      <div className="flex flex-col gap-3">
        <div className="flex items-center gap-2">
          <MapPin className="h-4 w-4 text-primary" />
          <span className="text-sm font-medium text-foreground">Ubicacion en el mapa</span>
        </div>

        <div className="relative overflow-hidden rounded-lg border border-border/50">
          <div ref={mapRef} className="h-64 w-full bg-secondary/30" style={{ zIndex: 0 }} />
          {!loaded && (
              <div className="absolute inset-0 flex items-center justify-center bg-secondary/30">
                <span className="text-sm text-muted-foreground">Cargando mapa...</span>
              </div>
          )}
        </div>

        <p className="text-xs text-muted-foreground">
          Haz clic en el mapa o arrastra el marcador para ubicar el venue.
        </p>

        <div className="grid grid-cols-2 gap-3">
          <div className="flex flex-col gap-1.5">
            <Label className="text-xs text-muted-foreground">Latitud (-90 a 90)</Label>
            <Input
                type="number"
                step="0.000001"
                min={-90}
                max={90}
                value={latitude}
                onChange={(e) => handleLatChange(e.target.value)}
                className="h-9 border-border/50 bg-secondary/30 text-sm text-foreground"
            />
          </div>
          <div className="flex flex-col gap-1.5">
            <Label className="text-xs text-muted-foreground">Longitud (-180 a 180)</Label>
            <Input
                type="number"
                step="0.000001"
                min={-180}
                max={180}
                value={longitude}
                onChange={(e) => handleLngChange(e.target.value)}
                className="h-9 border-border/50 bg-secondary/30 text-sm text-foreground"
            />
          </div>
        </div>
      </div>
  );
}