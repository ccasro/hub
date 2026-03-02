"use client"

import {useEffect, useRef, useState} from "react"
import type * as Leaflet from "leaflet"

type LeafletContainer = HTMLDivElement & { _leaflet_id?: number }

export interface VenueMarker {
    venueId: string
    venueName: string
    venueCity: string
    venueLatitude: number
    venueLongitude: number
    distanceKm: number
    slotCount: number
}

interface Props {
    center: [number, number]
    radiusKm: number
    venues: VenueMarker[]
    selectedVenueId: string | null
    onVenueClick: (venueId: string) => void
}

export function MatchMap({ center, radiusKm, venues, selectedVenueId, onVenueClick }: Props) {
    const mapRef         = useRef<HTMLDivElement>(null)
    const mapInstanceRef = useRef<Leaflet.Map | null>(null)
    const circleRef      = useRef<Leaflet.Circle | null>(null)
    const markersRef     = useRef<Map<string, Leaflet.Marker>>(new Map())
    const LRef           = useRef<typeof Leaflet | null>(null)
    const [loaded, setLoaded] = useState(false)

    // ── Init map once ─────────────────────────────────────────────
    useEffect(() => {
        if (!mapRef.current || mapInstanceRef.current) return
        const container = mapRef.current as LeafletContainer

        const init = async () => {
            const L = (await import("leaflet")) as unknown as typeof Leaflet
            LRef.current = L

            if (!document.querySelector('link[href*="leaflet.css"]')) {
                const link = document.createElement("link")
                link.rel = "stylesheet"
                link.href = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
                document.head.appendChild(link)
            }

            if (container._leaflet_id) delete container._leaflet_id

            const map = L.map(container, {
                center,
                zoom: 12,
                zoomControl: true,
                scrollWheelZoom: true,
            })

            L.tileLayer("https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png", {
                attribution: '&copy; <a href="https://www.openstreetmap.org">OpenStreetMap</a> &copy; <a href="https://carto.com">CARTO</a>',
            }).addTo(map)

            circleRef.current = L.circle(center, {
                radius: radiusKm * 1000,
                color: "#6366f1",
                fillColor: "#6366f1",
                fillOpacity: 0.06,
                weight: 1.5,
                dashArray: "5 5",
            }).addTo(map)

            mapInstanceRef.current = map
            setLoaded(true)
            setTimeout(() => map.invalidateSize(), 150)
        }

        init()

        return () => {
            mapInstanceRef.current?.remove()
            mapInstanceRef.current = null
            circleRef.current = null
            markersRef.current.clear()
            if ((mapRef.current as LeafletContainer)?._leaflet_id) {
                delete (mapRef.current as LeafletContainer)._leaflet_id
            }
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    // ── Update center + circle when city/radius changes ───────────
    useEffect(() => {
        if (!mapInstanceRef.current || !circleRef.current) return
        mapInstanceRef.current.setView(center, mapInstanceRef.current.getZoom())
        circleRef.current.setLatLng(center)
        circleRef.current.setRadius(radiusKm * 1000)
    }, [center, radiusKm])

    // ── Update markers when venues change ─────────────────────────
    useEffect(() => {
        const map = mapInstanceRef.current
        const L = LRef.current
        if (!map || !L) return

        const DefaultIcon = L.icon({
            iconUrl:       "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
            iconRetinaUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
            shadowUrl:     "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
            iconSize:    [25, 41],
            iconAnchor:  [12, 41],
            popupAnchor: [1, -34],
        })

        markersRef.current.forEach(m => m.remove())
        markersRef.current.clear()

        venues.forEach(venue => {
            const marker = L.marker(
                [venue.venueLatitude, venue.venueLongitude],
                { icon: DefaultIcon }
            ).addTo(map)

            marker.bindPopup(`
                <div style="min-width:150px;font-family:system-ui">
                    <p style="font-weight:600;font-size:13px;margin:0 0 2px 0">${venue.venueName}</p>
                    <p style="font-size:11px;color:#888;margin:0 0 4px 0">${venue.venueCity} · ${venue.distanceKm} km</p>
                    <p style="font-size:11px;color:#6366f1;margin:0;font-weight:500">
                        ${venue.slotCount} slot${venue.slotCount !== 1 ? "s" : ""} disponible${venue.slotCount !== 1 ? "s" : ""}
                    </p>
                </div>
            `)

            marker.on("click", () => onVenueClick(venue.venueId))
            markersRef.current.set(venue.venueId, marker)
        })
    }, [venues, onVenueClick])

    useEffect(() => {
        if (!selectedVenueId) return
        const marker = markersRef.current.get(selectedVenueId)
        if (marker) marker.openPopup()
    }, [selectedVenueId])

    return (
        <div className="relative h-full w-full">
            <div
                ref={mapRef}
                className="h-full w-full"
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
    )
}