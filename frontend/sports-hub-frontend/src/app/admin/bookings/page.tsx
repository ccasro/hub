// src/app/admin/bookings/page.tsx
import {auth0} from "@/lib/auth0"
import {redirect} from "next/navigation"
import {apiFetch} from "@/lib/api"
import {Booking, UserProfile} from "@/types"
import {AdminBookingsClient} from "@/components/admin/admin-bookings-client"

export default async function AdminBookingsPage() {
  const session = await auth0.getSession()
  if (!session) redirect("/")

  const profile = await apiFetch<UserProfile>("/api/me")
  if (profile.role !== "ADMIN") redirect("/dashboard")

  const bookings = await apiFetch<Booking[]>("/api/admin/bookings?page=0&size=100")

  return <AdminBookingsClient bookings={bookings} />
}
