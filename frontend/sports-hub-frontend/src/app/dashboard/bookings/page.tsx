import {auth0} from "@/lib/auth0"
import {redirect} from "next/navigation"
import {apiFetch} from "@/lib/api"
import {Booking, UserProfile} from "@/types"
import {BookingsClient} from "@/components/my/bookings-client"

export default async function BookingsPage() {
  const session = await auth0.getSession()
  if (!session) redirect("/")

  const [user, bookings] = await Promise.all([
    apiFetch<UserProfile>("/api/me"),
    apiFetch<Booking[]>("/api/bookings/my"),
  ])

  return <BookingsClient user={user} bookings={bookings} />
}