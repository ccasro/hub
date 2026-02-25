"use client"

import {useState} from "react"
import Link from "next/link"
import {usePathname} from "next/navigation"
import {SportsHubLogo} from "@/components/sports-hub-logo"
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar"
import {Badge} from "@/components/ui/badge"
import {Button} from "@/components/ui/button"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {
  Building2,
  CalendarDays,
  ChevronDown,
  LayoutDashboard,
  LayoutGrid,
  LogOut,
  Menu,
  Settings,
  ShieldCheck,
  Users,
  X,
} from "lucide-react"
import {ThemeToggle} from "@/components/theme-toggle"
import type {UserProfile} from "@/types"

interface AdminSidebarProps {
  user: UserProfile
  pendingVenues?: number
  pendingResources?: number
  pendingOwnerRequests?: number
}

export function AdminSidebar({
                               user,
                               pendingVenues = 0,
                               pendingResources = 0,
                               pendingOwnerRequests = 0,
                             }: AdminSidebarProps) {
  const pathname = usePathname()
  const [mobileOpen, setMobileOpen] = useState(false)

  const initials = user.displayName
      ? user.displayName.split(" ").map((n) => n[0]).join("").toUpperCase().slice(0, 2)
      : "A"

  const totalPending = pendingVenues + pendingResources + pendingOwnerRequests

  const navItems = [
    { href: "/admin/dashboard", label: "Dashboard", icon: LayoutDashboard, badge: null },
    { href: "/admin/venues",    label: "Venues",    icon: Building2,        badge: pendingVenues },
    { href: "/admin/resources", label: "Pistas",    icon: LayoutGrid,       badge: pendingResources },
    { href: "/admin/users",     label: "Usuarios",  icon: Users,            badge: pendingOwnerRequests },
    { href: "/admin/bookings",  label: "Reservas",  icon: CalendarDays,     badge: null },
  ]

  return (
      <>
        {/* Mobile header bar */}
        <header className="sticky top-0 z-50 flex h-14 items-center justify-between border-b border-border/50 bg-background/80 px-4 backdrop-blur-xl lg:hidden">
          <div className="flex items-center gap-2">
            <Link href="/admin/dashboard">
              <SportsHubLogo />
            </Link>
            {totalPending > 0 && (
                <Badge className="h-5 border-0 bg-amber-500/20 px-1.5 text-[10px] font-bold text-amber-400">
                  {totalPending}
                </Badge>
            )}
          </div>
          <Button
              variant="ghost"
              size="icon"
              className="h-9 w-9 text-muted-foreground"
              onClick={() => setMobileOpen(!mobileOpen)}
              aria-label="Menu"
          >
            {mobileOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
          </Button>
        </header>

        {/* Mobile overlay */}
        {mobileOpen && (
            <div
                className="fixed inset-0 z-40 bg-black/60 lg:hidden"
                onClick={() => setMobileOpen(false)}
            />
        )}

        {/* Sidebar */}
        <aside
            className={`fixed inset-y-0 left-0 z-50 flex w-64 flex-col border-r border-border/50 bg-card transition-transform duration-300 lg:static lg:translate-x-0 ${
                mobileOpen ? "translate-x-0" : "-translate-x-full"
            }`}
        >
          {/* Logo */}
          <div className="flex h-16 shrink-0 items-center gap-3 border-b border-border/50 px-5">
            <Link href="/admin/dashboard" onClick={() => setMobileOpen(false)}>
              <SportsHubLogo />
            </Link>
          </div>

          {/* Role badge */}
          <div className="px-5 pb-2 pt-4">
            <Badge variant="secondary" className="border-0 bg-amber-500/10 text-xs font-medium text-amber-400">
              <ShieldCheck className="mr-1 h-3 w-3" />
              Panel Administrador
            </Badge>
          </div>

          {/* Nav */}
          <nav className="flex-1 overflow-y-auto px-3 py-2">
            <ul className="flex flex-col gap-1">
              {navItems.map((item) => {
                const isActive =
                    pathname === item.href ||
                    (item.href !== "/admin/dashboard" && pathname.startsWith(item.href))
                return (
                    <li key={item.href}>
                      <Link
                          href={item.href}
                          onClick={() => setMobileOpen(false)}
                          className={`flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors ${
                              isActive
                                  ? "bg-amber-500/10 text-amber-400"
                                  : "text-muted-foreground hover:bg-secondary/50 hover:text-foreground"
                          }`}
                      >
                        <item.icon className="h-4 w-4 shrink-0" />
                        <span className="flex-1">{item.label}</span>
                        {item.badge !== null && item.badge > 0 && (
                            <Badge className="h-5 border-0 bg-amber-500/20 px-1.5 text-[10px] font-bold text-amber-400">
                              {item.badge}
                            </Badge>
                        )}
                      </Link>
                    </li>
                )
              })}
            </ul>
          </nav>

          {/* Theme toggle */}
          <div className="flex items-center justify-between border-t border-border/50 px-5 py-2">
            <span className="text-xs font-medium text-muted-foreground">Tema</span>
            <ThemeToggle />
          </div>

          {/* User footer */}
          <div className="border-t border-border/50 p-3">
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <button className="flex w-full items-center gap-3 rounded-lg px-2 py-2 text-left transition-colors hover:bg-secondary/50 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary/20">
                  <Avatar className="h-9 w-9 border border-amber-500/30">
                    <AvatarImage src={user.avatarUrl ?? undefined} alt={user.displayName ?? "Avatar"} />
                    <AvatarFallback className="bg-amber-500/10 text-xs font-semibold text-amber-400">
                      {initials}
                    </AvatarFallback>
                  </Avatar>
                  <div className="min-w-0 flex-1">
                    <p className="truncate text-sm font-medium text-foreground">{user.displayName}</p>
                    <p className="truncate text-xs text-muted-foreground">{user.email}</p>
                  </div>
                  <ChevronDown className="h-3.5 w-3.5 shrink-0 text-muted-foreground" />
                </button>
              </DropdownMenuTrigger>
              <DropdownMenuContent side="top" align="start" className="w-56 border-border bg-card text-card-foreground">
                <DropdownMenuLabel className="font-normal">
                  <Badge variant="secondary" className="h-[18px] border-0 bg-amber-500/10 px-1.5 text-[10px] font-medium text-amber-400">
                    Admin
                  </Badge>
                </DropdownMenuLabel>
                <DropdownMenuSeparator className="bg-border/50" />
                <DropdownMenuGroup>
                  <DropdownMenuItem asChild>
                    <Link href="/admin/settings" className="cursor-pointer gap-2 text-foreground focus:bg-secondary/50 focus:text-foreground">
                      <Settings className="h-4 w-4 text-muted-foreground" />
                      Configuracion
                    </Link>
                  </DropdownMenuItem>
                </DropdownMenuGroup>
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
        </aside>
      </>
  )
}
