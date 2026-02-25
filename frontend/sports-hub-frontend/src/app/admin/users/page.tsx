import {auth0} from "@/lib/auth0"
import {redirect} from "next/navigation"
import {apiFetch} from "@/lib/api"
import {AdminUserProfile, UserProfile} from "@/types"
import {AdminUsersClient} from "@/components/admin/admin-users-client"

export default async function AdminUsersPage() {
  const session = await auth0.getSession()
  if (!session) redirect("/")

  const profile = await apiFetch<UserProfile>("/api/me")
  if (profile.role !== "ADMIN") redirect("/dashboard")

  const users = await apiFetch<AdminUserProfile[]>("/api/admin/users?page=0&size=200")

  return <AdminUsersClient users={users} />
}
