import {auth0} from "@/lib/auth0"
import {redirect} from "next/navigation"
import {apiFetch} from "@/lib/api"
import {Booking, UserProfile} from "@/types"
import {SettingsClient} from "@/components/my/settings-client"

export default async function SettingsPage() {
  const session = await auth0.getSession()
  if (!session) redirect("/")

  const [user, bookings] = await Promise.all([
    apiFetch<UserProfile>("/api/me"),
    apiFetch<Booking[]>("/api/bookings/my"),
  ])

  const today = new Date().toISOString().split("T")[0]
  const upcomingCount = bookings.filter(
      (b) => b.status === "CONFIRMED" && b.bookingDate >= today
  ).length

  return <SettingsClient user={user} upcomingCount={upcomingCount} />
}
