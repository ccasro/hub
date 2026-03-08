"use client"

import {useState} from "react"
import Link from "next/link"
import {SportsHubLogo} from "@/components/sports-hub-logo"
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar"
import {Badge} from "@/components/ui/badge"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"
import {Input} from "@/components/ui/input"
import {Button} from "@/components/ui/button"
import {
  Bell,
  CalendarDays,
  ChevronDown,
  Crown,
  LogOut,
  MapPin,
  Menu,
  Search,
  Settings,
  Swords,
  Trophy,
  User,
  X,
  Zap,
} from "lucide-react"
import {ThemeToggle} from "@/components/theme-toggle"
import type {UserProfile} from "@/types"

interface DashboardNavbarProps {
  user: UserProfile
  selectedCity: string
  onCityChange: (city: string) => void
  searchQuery: string
  onSearchChange: (query: string) => void
  upcomingBookingsCount?: number
  pendingInvitations?: number
  cities?: string[]
}

export function DashboardNavbar({
                                  user,
                                  selectedCity,
                                  onCityChange,
                                  searchQuery,
                                  onSearchChange,
                                  upcomingBookingsCount = 0,
                                  pendingInvitations = 0,
                                  cities = [],
                                }: DashboardNavbarProps) {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)

  const initials = user.displayName
      ? user.displayName.split(" ").map((n) => n[0]).join("").toUpperCase().slice(0, 2)
      : "U"

  const skillLabel =
      user.skillLevel === "BEGINNER"   ? "Principiante" :
          user.skillLevel === "INTERMEDIATE" ? "Intermedio"  :
              user.skillLevel === "ADVANCED"   ? "Avanzado"     : null

  return (
      <header className="sticky top-0 z-50 border-b border-border/50 bg-background/80 backdrop-blur-xl">
        <div className="mx-auto flex h-16 max-w-7xl items-center justify-between gap-4 px-4 lg:px-6">

          {/* Logo */}
          <Link href="/dashboard" className="shrink-0">
            <SportsHubLogo />
          </Link>

          {/* Desktop Filters */}
          <div className="hidden flex-1 items-center gap-3 md:flex">
            <div className="flex items-center gap-2 rounded-lg border border-border/50 bg-secondary/30 px-3">
              <MapPin className="h-4 w-4 text-primary" />
              <Select value={selectedCity} onValueChange={onCityChange}>
                <SelectTrigger className="h-9 w-[140px] border-0 bg-transparent px-0 text-sm text-foreground shadow-none focus:ring-0">
                  <SelectValue placeholder="Ciudad" />
                </SelectTrigger>
                <SelectContent className="border-border bg-card text-card-foreground">
                  {cities.map((city) => (
                      <SelectItem key={city} value={city}>{city}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="relative max-w-sm flex-1">
              <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                  value={searchQuery}
                  onChange={(e) => onSearchChange(e.target.value)}
                  placeholder="Buscar venues..."
                  className="h-9 border-border/50 bg-secondary/30 pl-9 text-sm text-foreground placeholder:text-muted-foreground focus-visible:border-primary/50 focus-visible:ring-primary/20"
              />
            </div>
          </div>

          {/* Right side */}
          <div className="flex items-center gap-1.5">
            {/* Mis Reservas — desktop */}
            <Link href="/dashboard/bookings" className="hidden md:block">
              <Button variant="ghost" size="sm" className="relative gap-2 text-muted-foreground hover:text-foreground">
                <CalendarDays className="h-4 w-4" />
                <span className="text-sm">Mis Reservas</span>
                {upcomingBookingsCount > 0 && (
                    <Badge className="h-5 min-w-5 justify-center rounded-full border-0 bg-primary px-1.5 text-[10px] font-bold text-primary-foreground">
                      {upcomingBookingsCount}
                    </Badge>
                )}
              </Button>
            </Link>

            {/* Buscar Partido — desktop */}
            <Link href="/match/search" className="hidden md:block">
              <Button variant="ghost" size="sm" className="gap-2 text-muted-foreground hover:text-foreground">
                <Swords className="h-4 w-4" />
                <span className="text-sm">Buscar Partido</span>
              </Button>
            </Link>

            {/* Notification bell — invitaciones */}
            <Link href="/match/invitations">
              <Button
                  variant="ghost"
                  size="icon"
                  className="relative h-9 w-9 text-muted-foreground hover:text-foreground"
                  aria-label="Invitaciones"
              >
                <Bell className="h-4 w-4" />
                {pendingInvitations > 0 && (
                    <Badge className="absolute -right-0.5 -top-0.5 flex h-4 min-w-4 items-center justify-center rounded-full border-0 bg-primary px-1 text-[9px] font-bold text-primary-foreground">
                      {pendingInvitations}
                    </Badge>
                )}
              </Button>
            </Link>

            <ThemeToggle />

            {/* Mobile menu toggle */}
            <Button
                variant="ghost"
                size="icon"
                className="h-9 w-9 text-muted-foreground hover:text-foreground md:hidden"
                onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
                aria-label="Abrir menu"
            >
              {mobileMenuOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
            </Button>

            {/* Profile Dropdown */}
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <button
                    className="flex items-center gap-2.5 rounded-full border border-border/50 bg-secondary/30 py-1.5 pl-3 pr-1.5 transition-colors hover:border-primary/30 hover:bg-secondary/50 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary/20"
                    aria-label="Menu de perfil"
                >
                  <div className="hidden flex-col items-end sm:flex">
                                    <span className="text-sm font-medium leading-tight text-foreground">
                                        {user.displayName ?? "Usuario"}
                                    </span>
                    {skillLabel && (
                        <span className="text-[10px] leading-tight text-muted-foreground">
                                            {skillLabel}
                                        </span>
                    )}
                  </div>
                  <Avatar className="h-8 w-8 border border-border/50">
                    <AvatarImage src={user.avatarUrl ?? undefined} alt={user.displayName ?? "Avatar"} />
                    <AvatarFallback className="bg-primary/10 text-xs font-semibold text-primary">
                      {initials}
                    </AvatarFallback>
                  </Avatar>
                  <ChevronDown className="hidden h-3 w-3 text-muted-foreground sm:block" />
                </button>
              </DropdownMenuTrigger>

              <DropdownMenuContent align="end" className="w-64 border-border bg-card text-card-foreground">

                {/* Profile header */}
                <DropdownMenuLabel className="font-normal">
                  <div className="flex items-center gap-3">
                    <Avatar className="h-10 w-10 border border-border/50">
                      <AvatarImage src={user.avatarUrl ?? undefined} alt={user.displayName ?? "Avatar"} />
                      <AvatarFallback className="bg-primary/10 text-sm font-semibold text-primary">
                        {initials}
                      </AvatarFallback>
                    </Avatar>
                    <div className="flex flex-col gap-0.5">
                      <p className="text-sm font-semibold text-foreground">{user.displayName}</p>
                      <p className="text-xs text-muted-foreground">{user.email}</p>
                      <div className="flex items-center gap-1.5">
                        <Badge variant="secondary" className="h-[18px] border-0 bg-primary/10 px-1.5 text-[10px] font-medium text-primary">
                          {user.role === "PLAYER" ? "Jugador" : user.role === "OWNER" ? "Propietario" : "Admin"}
                        </Badge>
                        {user.city && (
                            <span className="flex items-center gap-0.5 text-[10px] text-muted-foreground">
                                                    <MapPin className="h-2.5 w-2.5" />
                              {user.city}
                                                </span>
                        )}
                      </div>
                    </div>
                  </div>
                </DropdownMenuLabel>
                <DropdownMenuSeparator className="bg-border/50" />

                {/* Navigation */}
                <DropdownMenuGroup>
                  <DropdownMenuItem asChild>
                    <Link href="/dashboard/bookings" className="cursor-pointer gap-2 text-foreground focus:bg-secondary/50 focus:text-foreground">
                      <CalendarDays className="h-4 w-4 text-muted-foreground" />
                      Mis Reservas
                      {upcomingBookingsCount > 0 && (
                          <Badge className="ml-auto h-5 min-w-5 justify-center rounded-full border-0 bg-primary px-1.5 text-[10px] font-bold text-primary-foreground">
                            {upcomingBookingsCount}
                          </Badge>
                      )}
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuItem asChild>
                    <Link href="/match/my" className="cursor-pointer gap-2 text-foreground focus:bg-secondary/50 focus:text-foreground">
                      <Trophy className="h-4 w-4 text-muted-foreground" />
                      Mis Partidos
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuItem asChild>
                    <Link href="/match/invitations" className="cursor-pointer gap-2 text-foreground focus:bg-secondary/50 focus:text-foreground">
                      <Bell className="h-4 w-4 text-muted-foreground" />
                      Invitaciones
                      {pendingInvitations > 0 && (
                          <Badge className="ml-auto h-5 min-w-5 justify-center rounded-full border-0 bg-primary px-1.5 text-[10px] font-bold text-primary-foreground">
                            {pendingInvitations}
                          </Badge>
                      )}
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuItem asChild>
                    <Link href="/match/search" className="cursor-pointer gap-2 text-foreground focus:bg-secondary/50 focus:text-foreground">
                      <Swords className="h-4 w-4 text-muted-foreground" />
                      Buscar Partido
                    </Link>
                  </DropdownMenuItem>
                </DropdownMenuGroup>
                <DropdownMenuSeparator className="bg-border/50" />

                {/* Profile actions */}
                <DropdownMenuGroup>
                  <DropdownMenuItem asChild>
                    <Link href="/dashboard/profile" className="cursor-pointer gap-2 text-foreground focus:bg-secondary/50 focus:text-foreground">
                      <User className="h-4 w-4 text-muted-foreground" />
                      Editar Perfil
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuItem asChild>
                    <Link href="/dashboard/settings" className="cursor-pointer gap-2 text-foreground focus:bg-secondary/50 focus:text-foreground">
                      <Settings className="h-4 w-4 text-muted-foreground" />
                      Configuracion
                    </Link>
                  </DropdownMenuItem>
                </DropdownMenuGroup>

                {/* Matching notifications — todos los roles excepto ADMIN */}
                {user.role !== "ADMIN" && (
                    <>
                      <DropdownMenuSeparator className="bg-border/50" />
                      <DropdownMenuGroup>
                        <DropdownMenuItem asChild>
                          <Link href="/dashboard/settings?tab=matching" className="cursor-pointer gap-2 text-foreground focus:bg-secondary/50 focus:text-foreground">
                            <Zap className="h-4 w-4 text-muted-foreground" />
                            Propuestas de partido
                            {user.matchNotificationsEnabled ? (
                                <Badge className="ml-auto h-5 border-0 bg-emerald-500/10 px-1.5 text-[10px] font-medium text-emerald-400">
                                  Activo
                                </Badge>
                            ) : (
                                <Badge className="ml-auto h-5 border-0 bg-secondary px-1.5 text-[10px] font-medium text-muted-foreground">
                                  Inactivo
                                </Badge>
                            )}
                          </Link>
                        </DropdownMenuItem>
                      </DropdownMenuGroup>
                    </>
                )}

                {/* Owner request — solo PLAYER */}
                {user.role === "PLAYER" && (
                    <>
                      <DropdownMenuSeparator className="bg-border/50" />
                      <DropdownMenuGroup>
                        <DropdownMenuItem asChild>
                          <Link href="/dashboard/settings" className="cursor-pointer gap-2 text-primary focus:bg-primary/10 focus:text-primary">
                            <Crown className="h-4 w-4" />
                            Ser propietario
                            {user.ownerRequestStatus === "PENDING" && (
                                <Badge className="ml-auto h-5 border-0 bg-amber-500/10 px-1.5 text-[10px] font-bold text-amber-400">
                                  Pendiente
                                </Badge>
                            )}
                          </Link>
                        </DropdownMenuItem>
                      </DropdownMenuGroup>
                    </>
                )}

                {/* Owner panel — solo OWNER */}
                {user.role === "OWNER" && (
                    <>
                      <DropdownMenuSeparator className="bg-border/50" />
                      <DropdownMenuGroup>
                        <DropdownMenuItem asChild>
                          <Link href="/owner/dashboard" className="cursor-pointer gap-2 text-primary focus:bg-primary/10 focus:text-primary">
                            <Crown className="h-4 w-4" />
                            Panel de Owner
                          </Link>
                        </DropdownMenuItem>
                      </DropdownMenuGroup>
                    </>
                )}

                <DropdownMenuSeparator className="bg-border/50" />
                <DropdownMenuItem asChild>
                  <a href="/auth/logout" className="cursor-pointer gap-2 text-destructive focus:bg-destructive/10 focus:text-destructive">
                    <LogOut className="h-4 w-4" />
                    Cerrar sesion
                  </a>
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </div>

        {/* Mobile menu */}
        {mobileMenuOpen && (
            <div className="flex flex-col gap-3 border-t border-border/50 bg-background/95 px-4 py-3 backdrop-blur-xl md:hidden">
              <Link
                  href="/dashboard/bookings"
                  className="flex items-center justify-between rounded-lg border border-border/50 bg-secondary/30 px-3 py-2.5"
                  onClick={() => setMobileMenuOpen(false)}
              >
                <div className="flex items-center gap-2">
                  <CalendarDays className="h-4 w-4 text-primary" />
                  <span className="text-sm font-medium text-foreground">Mis Reservas</span>
                </div>
                {upcomingBookingsCount > 0 && (
                    <Badge className="h-5 min-w-5 justify-center rounded-full border-0 bg-primary px-1.5 text-[10px] font-bold text-primary-foreground">
                      {upcomingBookingsCount}
                    </Badge>
                )}
              </Link>

              <Link
                  href="/match/my"
                  className="flex items-center gap-2 rounded-lg border border-border/50 bg-secondary/30 px-3 py-2.5"
                  onClick={() => setMobileMenuOpen(false)}
              >
                <Trophy className="h-4 w-4 text-primary" />
                <span className="text-sm font-medium text-foreground">Mis Partidos</span>
              </Link>

              <Link
                  href="/match/invitations"
                  className="flex items-center justify-between rounded-lg border border-border/50 bg-secondary/30 px-3 py-2.5"
                  onClick={() => setMobileMenuOpen(false)}
              >
                <div className="flex items-center gap-2">
                  <Bell className="h-4 w-4 text-primary" />
                  <span className="text-sm font-medium text-foreground">Invitaciones</span>
                </div>
                {pendingInvitations > 0 && (
                    <Badge className="h-5 min-w-5 justify-center rounded-full border-0 bg-primary px-1.5 text-[10px] font-bold text-primary-foreground">
                      {pendingInvitations}
                    </Badge>
                )}
              </Link>

              <Link
                  href="/match/search"
                  className="flex items-center gap-2 rounded-lg border border-border/50 bg-secondary/30 px-3 py-2.5"
                  onClick={() => setMobileMenuOpen(false)}
              >
                <Swords className="h-4 w-4 text-primary" />
                <span className="text-sm font-medium text-foreground">Buscar Partido</span>
              </Link>

              <div className="flex items-center gap-2 rounded-lg border border-border/50 bg-secondary/30 px-3">
                <MapPin className="h-4 w-4 text-primary" />
                <Select value={selectedCity} onValueChange={onCityChange}>
                  <SelectTrigger className="h-9 border-0 bg-transparent px-0 text-sm text-foreground shadow-none focus:ring-0">
                    <SelectValue placeholder="Ciudad" />
                  </SelectTrigger>
                  <SelectContent className="border-border bg-card text-card-foreground">
                    {cities.map((city) => (
                        <SelectItem key={city} value={city}>{city}</SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div className="relative">
                <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                    value={searchQuery}
                    onChange={(e) => onSearchChange(e.target.value)}
                    placeholder="Buscar venues..."
                    className="h-9 border-border/50 bg-secondary/30 pl-9 text-sm text-foreground placeholder:text-muted-foreground"
                />
              </div>
            </div>
        )}
      </header>
  )
}
