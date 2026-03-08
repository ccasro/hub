import {auth0} from "@/lib/auth0"
import {redirect} from "next/navigation"
import {apiFetch} from "@/lib/api"
import type {MatchRequestResponse, UserProfile} from "@/types"
import {MyMatchesClient} from "@/components/match/my-matches-client"

export default async function MyMatchesPage() {
    const session = await auth0.getSession()
    if (!session) redirect("/")

    const [user, matches] = await Promise.all([
        apiFetch<UserProfile>("/api/me"),
        apiFetch<MatchRequestResponse[]>("/api/match/requests/my").catch(() => [] as MatchRequestResponse[]),
    ])

    return <MyMatchesClient user={user} matches={matches}/>
}
