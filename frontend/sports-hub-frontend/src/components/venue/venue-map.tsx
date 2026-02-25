"use client";

import {useEffect, useRef, useState} from "react";
import type * as Leaflet from "leaflet";

interface VenueMapProps {
  latitude: number;
  longitude: number;
  venueName: string;
}

type LeafletContainer = HTMLDivElement & { _leaflet_id?: number };

export function VenueMap({ latitude, longitude, venueName }: VenueMapProps) {
  const mapRef = useRef<HTMLDivElement>(null);
  const mapInstanceRef = useRef<Leaflet.Map | null>(null);
  const markerRef = useRef<Leaflet.Marker | null>(null);
  const [loaded, setLoaded] = useState(false);

  useEffect(() => {
    if (!mapRef.current || mapInstanceRef.current) return;

    const container = mapRef.current as LeafletContainer;

    const loadLeaflet = async () => {
      const L = (await import("leaflet")) as unknown as typeof Leaflet;

      if (!document.querySelector('link[href*="leaflet.css"]')) {
        const link = document.createElement("link");
        link.rel = "stylesheet";
        link.href = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.css";
        document.head.appendChild(link);
      }

      if (container._leaflet_id) delete container._leaflet_id;

      const DefaultIcon = L.icon({
        iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
        iconRetinaUrl:
            "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
        shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
      });

      const map = L.map(container, {
        center: [latitude, longitude],
        zoom: 15,
        zoomControl: true,
        scrollWheelZoom: false,
      });

      L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution:
            '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
      }).addTo(map);

      const marker = L.marker([latitude, longitude], { icon: DefaultIcon }).addTo(map);

      marker.bindPopup(
          `<div style="font-weight:600;font-size:14px;">${venueName}</div>`
      );

      marker.openPopup();

      mapInstanceRef.current = map;
      markerRef.current = marker;
      setLoaded(true);

      setTimeout(() => map.invalidateSize(), 150);
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

  // Si cambian coords / nombre, actualiza sin recrear mapa
  useEffect(() => {
    if (!mapInstanceRef.current || !markerRef.current) return;

    markerRef.current.setLatLng([latitude, longitude]);
    markerRef.current.setPopupContent(
        `<div style="font-weight:600;font-size:14px;">${venueName}</div>`
    );

    mapInstanceRef.current.setView(
        [latitude, longitude],
        mapInstanceRef.current.getZoom()
    );
  }, [latitude, longitude, venueName]);

  return (
      <div className="relative overflow-hidden rounded-xl border border-border/40">
        <div
            ref={mapRef}
            className="h-72 w-full bg-secondary/20 lg:h-80"
            style={{ zIndex: 0 }}
        />
        {!loaded && (
            <div className="absolute inset-0 flex items-center justify-center bg-secondary/30">
          <span className="animate-pulse text-sm text-muted-foreground">
            Cargando mapa...
          </span>
            </div>
        )}
      </div>
  );
}