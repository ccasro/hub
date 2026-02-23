export function StatsBar() {
  const stats = [
    { value: "2.4K+", label: "Jugadores activos" },
    { value: "150+", label: "Pistas disponibles" },
    { value: "10K+", label: "Partidos jugados" },
  ]

  return (
    <div className="flex items-center gap-6">
      {stats.map((stat, i) => (
        <div key={stat.label} className="flex items-center gap-6">
          <div className="text-center">
            <p className="font-[var(--font-space-grotesk)] text-2xl font-bold text-primary">
              {stat.value}
            </p>
            <p className="text-xs text-muted-foreground">{stat.label}</p>
          </div>
          {i < stats.length - 1 && (
            <div className="h-8 w-px bg-border/50" aria-hidden="true" />
          )}
        </div>
      ))}
    </div>
  )
}
