import {auth0} from "@/lib/auth0"
import {redirect} from "next/navigation"
import {apiFetch} from "@/lib/api"
import type {MatchRequestResponse, UserProfile} from "@/types"
import {MatchDetailClient} from "@/components/match/match-detail-client"

interface Props {
    params: Promise<{ id: string }>
}

export default async function MatchDetailPage({ params }: Props) {
    const session = await auth0.getSession()
    if (!session) redirect("/")

    const { id } = await params

    const [user, matchRequest] = await Promise.all([
        apiFetch<UserProfile>("/api/me"),
        apiFetch<MatchRequestResponse>(`/api/match/requests/${id}`),
    ])

    return <MatchDetailClient user={user} matchRequest={matchRequest} />
}