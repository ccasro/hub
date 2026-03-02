import "server-only";
import {auth0} from "@/lib/auth0";

const API_URL = process.env.API_URL;
if (!API_URL) throw new Error("API_URL is not set");

export async function apiFetch<T>(
    endpoint: string,
    options: RequestInit = {}
): Promise<T> {
    const session = await auth0.getSession();


    const headers = new Headers(options.headers);

    if (!session?.tokenSet?.accessToken) {
        throw new ApiError(401, "Missing access token - please re-login");
    }
    headers.set("Authorization", `Bearer ${session.tokenSet.accessToken}`);

    if (session?.tokenSet?.accessToken) {
        headers.set("Authorization", `Bearer ${session.tokenSet.accessToken}`);
    }

    if (options.body && !(options.body instanceof FormData)) {
        headers.set("Content-Type", "application/json");
    }

    const requestUrl = `${API_URL}${endpoint}`;

    const response = await fetch(`${API_URL}${endpoint}`, {
        ...options,
        headers,
    });

    type ErrorBody = {
        message?: string;
        error?: string;
        detail?: string;
    };

    function isErrorBody(value: unknown): value is ErrorBody {
        return typeof value === "object" && value !== null;
    }

    if (!response.ok) {
        const contentType = response.headers.get("content-type") || "";

        let body: unknown;

        if (contentType.includes("application/json")) {
            body = await response.json().catch(() => null);
        } else {
            body = await response.text().catch(() => null);
        }

        console.error("apiFetch error:", {
            requestUrl,
            status: response.status,
            contentType,
            body,
        });

        let message = "Unknown error";

        if (isErrorBody(body)) {
            message = body.message ?? body.error ?? body.detail ?? message;
        } else if (typeof body === "string") {
            message = body.slice(0, 500);
        } else if (response.statusText) {
            message = response.statusText;
        }

        throw new ApiError(response.status, message);
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