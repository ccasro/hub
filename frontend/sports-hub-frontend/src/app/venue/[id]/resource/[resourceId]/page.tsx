import {notFound} from "next/navigation"
import {apiFetch} from "@/lib/api"
import {Resource, SlotAvailability, Venue} from "@/types"
import {ResourceDetailClient} from "@/components/venue/resource-detail-client"

interface Props {
  params: Promise<{ id: string; resourceId: string }>
}

export default async function ResourceDetailPage({ params }: Props) {
  const { id, resourceId } = await params
  const today = new Date().toISOString().split("T")[0]

  const [venue, resources, slots] = await Promise.all([
    apiFetch<Venue>(`/api/venues/${id}`).catch(() => null),
    apiFetch<Resource[]>(`/api/venues/${id}/resources`).catch(() => []),
    apiFetch<SlotAvailability[]>(`/api/resources/${resourceId}/slots?date=${today}`).catch(() => []),
  ])

  if (!venue) notFound()

  const resource = resources.find(
      (r) => r.id === resourceId && r.status === "ACTIVE"
  )

  if (!resource) notFound()

  return (
      <ResourceDetailClient
          venue={venue}
          resource={resource}
          initialSlots={slots}
          initialDate={today}
      />
  )
}
