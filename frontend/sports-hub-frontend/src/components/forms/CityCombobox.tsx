"use client"
import {City} from "country-state-city"
import {useEffect, useMemo, useRef, useState} from "react"
import {Input} from "@/components/ui/input"
import {ChevronDown, MapPin} from "lucide-react"

interface CityComboboxProps {
    countryCode: string
    value: string
    onChange: (city: string) => void
}

export function CityCombobox({countryCode, value, onChange}: CityComboboxProps) {
    const [query, setQuery] = useState(value)
    const [open, setOpen] = useState(false)
    const [prevCountry, setPrevCountry] = useState(countryCode)
    const ref = useRef<HTMLDivElement>(null)

    if (prevCountry !== countryCode) {
        setPrevCountry(countryCode)
        setQuery("")
    }

    useEffect(() => {
        onChange("")
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [countryCode])

    const cities = useMemo(() => {
        return City.getCitiesOfCountry(countryCode) ?? []
    }, [countryCode])

    const filtered = useMemo(() => {
        if (!query.trim()) return cities.slice(0, 50)
        return cities
            .filter((c) => c.name.toLowerCase().startsWith(query.toLowerCase()))
            .slice(0, 50)
    }, [cities, query])

    useEffect(() => {
        const handler = (e: MouseEvent) => {
            if (ref.current && !ref.current.contains(e.target as Node)) {
                setOpen(false)
            }
        }
        document.addEventListener("mousedown", handler)
        return () => document.removeEventListener("mousedown", handler)
    }, [])

    const handleSelect = (cityName: string) => {
        setQuery(cityName)
        onChange(cityName)
        setOpen(false)
    }

    return (
        <div ref={ref} className="relative">
            <div className="relative">
                <Input
                    id="citySearch"
                    value={query}
                    onChange={(e) => {
                        setQuery(e.target.value)
                        onChange("")
                        setOpen(true)
                    }}
                    onFocus={() => setOpen(true)}
                    placeholder="Busca tu ciudad..."
                    className="h-12 border-border/60 bg-secondary/30 text-foreground placeholder:text-muted-foreground focus-visible:border-primary/50 focus-visible:ring-primary/20 pr-8"
                    autoComplete="off"
                />
                <ChevronDown className="absolute right-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground pointer-events-none"/>
            </div>

            {open && filtered.length > 0 && (
                <ul className="absolute z-50 mt-1 max-h-56 w-full overflow-auto rounded-xl border border-border/60 bg-background shadow-lg">
                    {filtered.map((city) => (
                        <li
                            key={`${city.name}-${city.stateCode}`}
                            onMouseDown={() => handleSelect(city.name)}
                            className={`flex items-center gap-2 cursor-pointer px-3 py-2.5 text-sm transition-colors hover:bg-secondary/60 ${
                                value === city.name
                                    ? "bg-primary/10 text-primary font-medium"
                                    : "text-foreground"
                            }`}
                        >
                            <MapPin className="h-3.5 w-3.5 text-muted-foreground shrink-0"/>
                            {city.name}
                        </li>
                    ))}
                </ul>
            )}

            {open && query.length > 1 && filtered.length === 0 && (
                <div className="absolute z-50 mt-1 w-full rounded-xl border border-border/60 bg-background px-3 py-3 text-sm text-muted-foreground shadow-lg">
                    No se encontró ninguna ciudad
                </div>
            )}
        </div>
    )
}