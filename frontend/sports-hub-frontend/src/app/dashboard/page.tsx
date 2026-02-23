import {auth0} from "@/lib/auth0"
import {redirect} from "next/navigation"
import {apiFetch} from "@/lib/api"
import {Booking, UserProfile, Venue} from "@/types"
import {DashboardClient} from "@/components/dashboard/dashboard-client"

export default async function DashboardPage() {
    const session = await auth0.getSession()
    if (!session) redirect("/")

    const [profile, venues, bookings] = await Promise.all([
        apiFetch<UserProfile>("/api/me"),
        apiFetch<Venue[]>("/api/venues"),
        apiFetch<Booking[]>("/api/bookings/my"),
    ])

    if (!profile.onboardingCompleted) redirect("/onboarding")

    switch (profile.role) {
        case "ADMIN":
            redirect("/admin/dashboard")
        case "OWNER":
            redirect("/owner/dashboard")
    }

    return (
        <DashboardClient
            user={profile}
            venues={venues}
            bookings={bookings}
        />
    )
}