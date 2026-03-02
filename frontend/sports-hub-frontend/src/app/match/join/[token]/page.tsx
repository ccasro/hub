import {auth0} from "@/lib/auth0"
import {redirect} from "next/navigation"
import {apiFetch} from "@/lib/api"
import type {MatchRequestResponse, UserProfile} from "@/types"
import {MatchJoinClient} from "@/components/match/match-join-client"

interface Props {
    params: Promise<{ token: string }>
}

export default async function MatchJoinPage({ params }: Props) {
    const session = await auth0.getSession()
    if (!session) redirect(`/auth/login?returnTo=/match/join/${(await params).token}`)

    const { token } = await params

    const [user, matchRequest] = await Promise.all([
        apiFetch<UserProfile>("/api/me"),
        apiFetch<MatchRequestResponse>(`/api/match/join/${token}`),
    ])

    return <MatchJoinClient user={user} matchRequest={matchRequest} token={token} />
}