export function SportsHubLogo({ className = "" }: { className?: string }) {
  return (
    <div className={`flex items-center gap-2.5 ${className}`}>
      <div className="relative flex h-10 w-10 items-center justify-center rounded-xl bg-primary">
        <svg
          width="24"
          height="24"
          viewBox="0 0 24 24"
          fill="none"
          className="text-primary-foreground"
          aria-hidden="true"
        >
          <path
            d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z"
            fill="currentColor"
          />
          <path
            d="M17.5 6.5L14 10l3.5 3.5"
            stroke="currentColor"
            strokeWidth="1.5"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
        </svg>
      </div>
      <div className="flex flex-col leading-none">
        <span className="font-[var(--font-space-grotesk)] text-xl font-bold tracking-tight text-foreground">
          Sports<span className="text-primary">Hub</span>
        </span>
      </div>
    </div>
  )
}
