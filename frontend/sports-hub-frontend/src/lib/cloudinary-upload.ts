export type UploadPurpose = "AVATAR" | "VENUE_IMAGE" | "RESOURCE_IMAGE"

export interface CloudinaryUploadResult {
    url: string
    publicId: string
}

export async function uploadToCloudinary(
    file: File,
    purpose: UploadPurpose,
    options?: { venueId?: string; resourceId?: string }
): Promise<CloudinaryUploadResult> {
    const sigRes = await fetch("/api/proxy/api/uploads/signature", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ purpose, ...options }),
    })
    if (!sigRes.ok) throw new Error(`Signature error ${sigRes.status}`)
    const sig = await sigRes.json()

    const fd = new FormData()
    fd.append("file", file)
    fd.append("api_key", sig.apiKey)
    fd.append("timestamp", String(sig.timestamp))
    fd.append("folder", sig.folder)
    fd.append("public_id", sig.publicId)
    fd.append("overwrite", String(sig.overwrite))
    fd.append("signature", sig.signature)

    const uploadRes = await fetch(
        `https://api.cloudinary.com/v1_1/${sig.cloudName}/image/upload`,
        { method: "POST", body: fd }
    )
    if (!uploadRes.ok) throw new Error(`Cloudinary upload failed ${uploadRes.status}`)
    const uploaded = await uploadRes.json()

    const url = uploaded.secure_url ?? uploaded.url
    if (!url) throw new Error("Upload OK pero falta URL")

    return { url, publicId: uploaded.public_id ?? sig.publicId }
}