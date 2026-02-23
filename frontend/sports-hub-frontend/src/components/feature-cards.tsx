import { Calendar, Users, Trophy } from "lucide-react"

const features = [
  {
    icon: Calendar,
    title: "Reserva Pistas",
    description: "Encuentra y reserva pistas de padel en tu zona al instante.",
  },
  {
    icon: Users,
    title: "Matching",
    description: "Conecta con jugadores de tu nivel y organiza partidos.",
  },
  {
    icon: Trophy,
    title: "Rankings",
    description: "Compite, sube posiciones y demuestra tu nivel.",
  },
]

export function FeatureCards() {
  return (
    <div className="flex flex-col gap-4">
      {features.map((feature) => (
        <div
          key={feature.title}
          className="group flex items-start gap-4 rounded-xl border border-border/50 bg-secondary/30 p-4 transition-colors hover:border-primary/30 hover:bg-secondary/50"
        >
          <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-primary/10 text-primary transition-colors group-hover:bg-primary/20">
            <feature.icon className="h-5 w-5" />
          </div>
          <div>
            <h3 className="font-[var(--font-space-grotesk)] text-sm font-semibold text-foreground">
              {feature.title}
            </h3>
            <p className="mt-0.5 text-sm leading-relaxed text-muted-foreground">
              {feature.description}
            </p>
          </div>
        </div>
      ))}
    </div>
  )
}
