import {auth0} from "@/lib/auth0"
import {redirect} from "next/navigation"
import {apiFetch} from "@/lib/api"
import type {MatchInvitation} from "@/types"
import {MatchInvitationsClient} from "@/components/match/match-invitations-client"

export default async function MatchInvitationsPage() {
    const session = await auth0.getSession()
    if (!session) redirect("/")

    const invitations = await apiFetch<MatchInvitation[]>("/api/match/invitations")

    return <MatchInvitationsClient invitations={invitations}/>
}
