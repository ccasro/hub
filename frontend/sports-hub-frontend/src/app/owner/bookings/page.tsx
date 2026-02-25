import {auth0} from "@/lib/auth0"
import {redirect} from "next/navigation"
import {apiFetch} from "@/lib/api"
import {Booking, Resource, UserProfile, Venue} from "@/types"
import {OwnerBookingsClient} from "@/components/owner/owner-bookings-client"

export default async function OwnerBookingsPage() {
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
      <OwnerBookingsClient
          user={profile}
          venues={venues}
          resources={resources}
          bookings={bookings}
      />
  )
}