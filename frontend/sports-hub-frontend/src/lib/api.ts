import "server-only";
import {auth0} from "@/lib/auth0";

const API_URL = process.env.NEXT_PUBLIC_API_URL;
if (!API_URL) throw new Error("NEXT_PUBLIC_API_URL is not set");

export async function apiFetch<T>(
    endpoint: string,
    options: RequestInit = {}
): Promise<T> {
    const session = await auth0.getSession();

    const headers = new Headers(options.headers);

    if (session?.tokenSet?.accessToken) {
        headers.set("Authorization", `Bearer ${session.tokenSet.accessToken}`);
    }

    if (options.body && !(options.body instanceof FormData)) {
        headers.set("Content-Type", "application/json");
    }

    const response = await fetch(`${API_URL}${endpoint}`, {
        ...options,
        headers,
    });

    if (!response.ok) {
        const error = await response.json().catch(() => ({}));
        throw new ApiError(response.status, error.message ?? "Unknown error");
    }

    if (response.status === 204) return null as T;

    return response.json();
}

export async function apiFetchClient<T>(
    endpoint: string,
    options?: RequestInit
): Promise<T> {
    const response = await fetch(`/api/proxy${endpoint}`, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...options?.headers,
        },
    });

    if (!response.ok) {
        const error = await response.json().catch(() => ({}));
        throw new ApiError(response.status, error.message ?? "Unknown error");
    }

    if (response.status === 204) return null as T;

    return response.json();
}

export class ApiError extends Error {
    constructor(public status: number, message: string) {
        super(message);
    }
}