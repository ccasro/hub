import {auth0} from "@/lib/auth0"
import {redirect} from "next/navigation"
import {apiFetch} from "@/lib/api"
import type {UserProfile} from "@/types"
import {MatchSearchClient} from "@/components/match/match-search-client"

export default async function MatchSearchPage() {
    const session = await auth0.getSession()
    if (!session) redirect("/")

    const user = await apiFetch<UserProfile>("/api/me")

    return <MatchSearchClient user={user} />
}