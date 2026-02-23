import {auth0} from "@/lib/auth0";
import {NextRequest, NextResponse} from "next/server";

const API_URL = process.env.NEXT_PUBLIC_API_URL;
if (!API_URL) throw new Error("NEXT_PUBLIC_API_URL is not set");

async function handler(request: NextRequest) {
    const session = await auth0.getSession();
    if (!session?.tokenSet?.accessToken) {
        return NextResponse.json({message: "Unauthorized"}, {status: 401});
    }

    const path = request.nextUrl.pathname.replace(/^\/api\/proxy/, "");
    const url = `${API_URL}${path}${request.nextUrl.search}`;

    const headers = new Headers(request.headers);
    headers.set("Authorization", `Bearer ${session.tokenSet.accessToken}`);

    headers.delete("host");
    headers.delete("content-length");

    const hasBody = !["GET", "HEAD"].includes(request.method);
    const body = hasBody ? await request.arrayBuffer() : undefined;

    const upstream = await fetch(url, {
        method: request.method,
        headers,
        body: body ? Buffer.from(body) : undefined,
        redirect: "manual",
    });

    if (upstream.status === 204) {
        return new NextResponse(null, {status: 204});
    }

    const contentType = upstream.headers.get("content-type") || "";

    if (contentType.includes("application/json")) {
        const data = await upstream.json().catch(() => null);
        return NextResponse.json(data, {status: upstream.status});
    }

    const raw = await upstream.arrayBuffer();
    return new NextResponse(raw, {
        status: upstream.status,
        headers: {
            "content-type": contentType,
        },
    });
}

export const GET = handler;
export const POST = handler;
export const PUT = handler;
export const PATCH = handler;
export const DELETE = handler;