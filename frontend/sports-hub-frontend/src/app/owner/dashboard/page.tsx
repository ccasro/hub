import {auth0} from "@/lib/auth0"
import {redirect} from "next/navigation"
import {apiFetch} from "@/lib/api"
import {Booking, Resource, UserProfile, Venue} from "@/types"
import {OwnerDashboardClient} from "@/components/owner/owner-dashboard-client"

export default async function OwnerDashboardPage() {
  const session = await auth0.getSession()
  if (!session) redirect("/")

  const profile = await apiFetch<UserProfile>("/api/me")
  if (profile.role !== "OWNER") redirect("/dashboard")

  const [venues, resources, bookings] = await Promise.all([
    apiFetch<Venue[]>("/api/owner/venues"),
    apiFetch<Resource[]>("/api/owner/resources"),
    apiFetch<Booking[]>("/api/owner/bookings?page=0&size=200"),
  ])

  return (
      <OwnerDashboardClient
          user={profile}
          venues={venues}
          resources={resources}
          bookings={bookings}
      />
  )
}
