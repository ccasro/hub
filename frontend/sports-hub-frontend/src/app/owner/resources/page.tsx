import {auth0} from "@/lib/auth0"
import {redirect} from "next/navigation"
import {apiFetch} from "@/lib/api"
import {Resource, UserProfile, Venue} from "@/types"
import {OwnerResourcesClient} from "@/components/owner/owner-resources-client"

export default async function OwnerResourcesPage() {
  const session = await auth0.getSession()
  if (!session) redirect("/")

  const profile = await apiFetch<UserProfile>("/api/me")
  if (profile.role !== "OWNER") redirect("/dashboard")

  const [venues, resources] = await Promise.all([
    apiFetch<Venue[]>("/api/owner/venues"),
    apiFetch<Resource[]>("/api/owner/resources"),
  ])

  return (
      <OwnerResourcesClient
          user={profile}
          venues={venues}
          resources={resources}
      />
  )
}
