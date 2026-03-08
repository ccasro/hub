import {auth0} from "@/lib/auth0"
import {redirect} from "next/navigation"
import {apiFetch} from "@/lib/api"
import {Booking, MatchInvitation, MatchRequestResponse, UserProfile, Venue} from "@/types"
import {DashboardClient} from "@/components/dashboard/dashboard-client"

export default async function DashboardPage() {
    const session = await auth0.getSession()
    if (!session) redirect("/")

    const profile = await apiFetch<UserProfile>("/api/me")

    if (!profile.onboardingCompleted) redirect("/onboarding")

    switch (profile.role) {
        case "ADMIN": redirect("/admin/dashboard")
        case "OWNER": redirect("/owner/dashboard")
    }

    const [venues, bookings, invitations, matches] = await Promise.all([
        apiFetch<Venue[]>("/api/venues"),
        apiFetch<Booking[]>("/api/bookings/my"),
        apiFetch<MatchInvitation[]>("/api/match/invitations").catch(() => [] as MatchInvitation[]),
        apiFetch<MatchRequestResponse[]>("/api/match/requests/my").catch(() => [] as MatchRequestResponse[]),
    ])

    const pendingInvitations = invitations.filter(
        i => i.status === "PENDING" && i.matchStatus === "OPEN"
    ).length

    return (
        <DashboardClient
            user={profile}
            venues={venues}
            bookings={bookings}
            pendingInvitations={pendingInvitations}
            matches={matches}
        />
    )
}