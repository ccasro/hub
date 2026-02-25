import {auth0} from "@/lib/auth0"
import {redirect} from "next/navigation"
import {apiFetch} from "@/lib/api"
import {AdminUserProfile, Booking, Resource, UserProfile, Venue} from "@/types"
import {AdminDashboardClient} from "@/components/admin/admin-dashboard-client"

interface AdminStats {
    totalUsers: number
    totalOwners: number
    totalPlayers: number
    totalVenues: number
    activeVenues: number
    pendingVenues: number
    totalResources: number
    activeResources: number
    pendingResources: number
    pendingOwnerRequests: number
    revenueThisMonth: number
    totalBookings: number
}

interface AdminPendingData {
    pendingVenues: Venue[]
    pendingResources: Resource[]
    pendingOwners: AdminUserProfile[]
    recentBookings: Booking[]
}

export default async function AdminDashboardPage() {
    const session = await auth0.getSession()
    if (!session) redirect("/")

    const profile = await apiFetch<UserProfile>("/api/me")
    if (profile.role !== "ADMIN") redirect("/dashboard")

    const [stats, pendingVenues, pendingResources, pendingOwners, recentBookings] =
        await Promise.all([
            apiFetch<AdminStats>("/api/admin/stats"),
            apiFetch<Venue[]>("/api/admin/venues/pending"),
            apiFetch<Resource[]>("/api/admin/resources/pending"),
            apiFetch<AdminUserProfile[]>("/api/admin/users/pending-owners"),
            apiFetch<Booking[]>("/api/admin/bookings?page=0&size=20"),
        ])

    return (
        <AdminDashboardClient
            stats={stats}
            pendingVenues={pendingVenues}
            pendingResources={pendingResources}
            pendingOwners={pendingOwners}
            recentBookings={recentBookings}
        />
    )
}