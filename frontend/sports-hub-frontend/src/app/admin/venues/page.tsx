import {auth0} from "@/lib/auth0"
import {redirect} from "next/navigation"
import {apiFetch} from "@/lib/api"
import {UserProfile, Venue} from "@/types"
import {AdminVenuesClient} from "@/components/admin/admin-venues-client"

export default async function AdminVenuesPage() {
  const session = await auth0.getSession()
  if (!session) redirect("/")

  const profile = await apiFetch<UserProfile>("/api/me")
  if (profile.role !== "ADMIN") redirect("/dashboard")

  const venues = await apiFetch<Venue[]>("/api/admin/venues?page=0&size=100")

  return <AdminVenuesClient venues={venues} />
}
