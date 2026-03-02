import {auth0} from "@/lib/auth0"
import {redirect} from "next/navigation"
import {apiFetch} from "@/lib/api"
import type {UserProfile} from "@/types"
import {MatchCreateClient} from "@/components/match/match-create-client"

export default async function MatchCreatePage() {
    const session = await auth0.getSession()
    if (!session) redirect("/")

    const user = await apiFetch<UserProfile>("/api/me")

    return <MatchCreateClient user={user} />
}