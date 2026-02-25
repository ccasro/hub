import {auth0} from "@/lib/auth0"
import {redirect} from "next/navigation"
import {apiFetch} from "@/lib/api"
import {Resource, UserProfile, Venue} from "@/types"
import {OwnerVenuesClient} from "@/components/owner/owner-venues-client"

export default async function OwnerVenuesPage() {
  const session = await auth0.getSession()
  if (!session) redirect("/")

  const profile = await apiFetch<UserProfile>("/api/me")
  if (profile.role !== "OWNER") redirect("/dashboard")

  const [venues, resources] = await Promise.all([
    apiFetch<Venue[]>("/api/owner/venues"),
    apiFetch<Resource[]>("/api/owner/resources"),
  ])

  const resourceCountByVenue = resources.reduce<Record<string, number>>((acc, r) => {
    acc[r.venueId] = (acc[r.venueId] ?? 0) + 1
    return acc
  }, {})

  const venuesWithCount = venues
      .map((v) => ({ ...v, resourceCount: resourceCountByVenue[v.id] ?? 0 }))
      .sort((a, b) => (b.createdAt ?? "").localeCompare(a.createdAt ?? ""))

  return (
      <OwnerVenuesClient
          user={profile}
          venues={venuesWithCount}
      />
  )
}
