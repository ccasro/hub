import {notFound} from "next/navigation"
import {apiFetch} from "@/lib/api"
import {Resource, Venue} from "@/types"
import {VenueDetailClient} from "@/components/venue/venue-detail-client"

interface Props {
  params: Promise<{ id: string }>
}

export default async function VenueDetailPage({ params }: Props) {
  const { id } = await params

  const [venue, resources] = await Promise.all([
    apiFetch<Venue>(`/api/venues/${id}`).catch(() => null),
    apiFetch<Resource[]>(`/api/venues/${id}/resources`).catch(() => []),
  ])

  if (!venue) notFound()

  const activeResources = resources.filter((r) => r.status === "ACTIVE")

  return <VenueDetailClient venue={venue} resources={activeResources} />
}
