import {auth0} from "@/lib/auth0";
import {NextRequest, NextResponse} from "next/server";

export const runtime = "nodejs";

const API_URL = process.env.API_URL;
if (!API_URL) throw new Error("API_URL is not set");

const HOP_BY_HOP = new Set([
    "connection",
    "keep-alive",
    "proxy-authenticate",
    "proxy-authorization",
    "te",
    "trailer",
    "transfer-encoding",
    "upgrade",
    "host",
    "content-length",
]);

function responseHeaders(up: Response) {
    const h = new Headers();
    for (const [k, v] of up.headers) {
        if (HOP_BY_HOP.has(k.toLowerCase())) continue;
        h.set(k, v);
    }
    return h;
}

async function handler(request: NextRequest) {
    const session = await auth0.getSession();
    const token = session?.tokenSet?.accessToken;
    if (!token) return NextResponse.json({ message: "Unauthorized" }, { status: 401 });

    const path = request.nextUrl.pathname.replace(/^\/api\/proxy/, "") || "/";
    const url = new URL(path + request.nextUrl.search, API_URL);

    const headers = new Headers();
    headers.set("Authorization", `Bearer ${token}`);
    const ct = request.headers.get("content-type");
    if (ct) headers.set("content-type", ct);
    const accept = request.headers.get("accept");
    if (accept) headers.set("accept", accept);

    const method = request.method;
    const hasBody = !["GET", "HEAD"].includes(method);

    const upstream = await fetch(url, {
        method,
        headers,
        body: hasBody ? request.body : undefined,
        duplex: hasBody ? "half" : undefined,
        redirect: "manual",
    } as RequestInit);

    if (upstream.status === 204) return new NextResponse(null, { status: 204 });

    return new NextResponse(upstream.body, {
        status: upstream.status,
        headers: responseHeaders(upstream),
    });
}

export const GET = handler;
export const POST = handler;
export const PUT = handler;
export const PATCH = handler;
export const DELETE = handler;