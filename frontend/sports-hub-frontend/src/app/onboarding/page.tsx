import {SportsHubLogo} from "@/components/sports-hub-logo"
import {OnboardingForm} from "@/components/onboarding-form"
import Image from "next/image"
import type {Metadata} from "next"
import {redirect} from "next/navigation";
import {auth0} from "@/lib/auth0";
import {apiFetch} from "@/lib/api";
import {UserProfile} from "@/types";

export const metadata: Metadata = {
    title: "SportsHub - Completa tu perfil",
    description:
        "Configura tu perfil en SportsHub para reservar pistas, encontrar jugadores y competir.",
}

export default async function OnboardingPage() {
    const session = await auth0.getSession();
    if (!session) redirect("/");

    const profile = await apiFetch<UserProfile>("/api/me");

    if (profile.onboardingCompleted) redirect("/dashboard");
    
    return (
        <main className="force-dark flex min-h-svh bg-background">
            {/* Left Panel - Motivational */}
            <div className="relative hidden w-1/2 flex-col justify-between overflow-hidden p-10 lg:flex xl:w-[55%]">
                {/* Background Image */}
                <Image
                    src="/images/padel-hero.jpg"
                    alt="Pista de padel iluminada de noche"
                    fill
                    className="object-cover"
                    priority
                />
                {/* Dark overlay */}
                <div className="absolute inset-0 bg-background/80" aria-hidden="true"/>
                {/* Accent line */}
                <div
                    className="absolute bottom-0 left-0 right-0 h-1 bg-primary"
                    aria-hidden="true"
                />

                {/* Top - Logo */}
                <div className="relative z-10">
                    <SportsHubLogo/>
                </div>

                {/* Center - Messaging */}
                <div className="relative z-10 max-w-lg">
                    <h1 className="font-[var(--font-space-grotesk)] text-5xl font-bold leading-tight tracking-tight text-foreground xl:text-6xl">
                        Ya casi{" "}
                        <span className="text-primary">estas.</span>
                    </h1>
                    <p className="mt-5 max-w-md text-base leading-relaxed text-muted-foreground">
                        Completa tu perfil para que otros jugadores te encuentren, conectes
                        con rivales de tu nivel y reserves tus pistas favoritas.
                    </p>

                    {/* Benefits list */}
                    <div className="mt-8 flex flex-col gap-4">
                        {[
                            {
                                title: "Perfil personalizado",
                                desc: "Los demas jugadores veran tu nivel y deporte favorito.",
                            },
                            {
                                title: "Mejor matching",
                                desc: "Te conectamos con jugadores que encajan con tu perfil.",
                            },
                            {
                                title: "Reservas mas rapidas",
                                desc: "Tu ubicacion nos ayuda a mostrarte las pistas cercanas.",
                            },
                        ].map((item) => (
                            <div
                                key={item.title}
                                className="flex items-start gap-3 rounded-xl border border-border/50 bg-secondary/30 p-4"
                            >
                                <div
                                    className="mt-0.5 flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-primary/20">
                                    <div className="h-2 w-2 rounded-full bg-primary"/>
                                </div>
                                <div>
                                    <p className="font-[var(--font-space-grotesk)] text-sm font-semibold text-foreground">
                                        {item.title}
                                    </p>
                                    <p className="mt-0.5 text-sm leading-relaxed text-muted-foreground">
                                        {item.desc}
                                    </p>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Bottom spacer */}
                <div className="relative z-10"/>
            </div>

            {/* Right Panel - Onboarding Form */}
            <div className="flex w-full flex-col lg:w-1/2 xl:w-[45%]">
                {/* Mobile Logo */}
                <div className="flex items-center justify-between p-6 lg:hidden">
                    <SportsHubLogo/>
                    <span className="text-xs text-muted-foreground">Configurar perfil</span>
                </div>

                <div className="flex flex-1 items-center justify-center px-6 py-8 lg:px-12 xl:px-20">
                    <div className="w-full max-w-md">
                        <OnboardingForm/>
                    </div>
                </div>

                {/* Footer */}
                <footer className="p-6 text-center">
                    <p className="text-xs text-muted-foreground">
                        Podras editar tu perfil en cualquier momento desde la configuracion
                        de tu cuenta.
                    </p>
                </footer>
            </div>
        </main>
    )
}
