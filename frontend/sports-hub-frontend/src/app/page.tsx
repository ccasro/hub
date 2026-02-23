import {SportsHubLogo} from "@/components/sports-hub-logo"
import {AuthForm} from "@/components/auth-form"
import {FeatureCards} from "@/components/feature-cards"
import {StatsBar} from "@/components/stats-bar"
import Image from "next/image"
import {auth0} from "@/lib/auth0";
import {redirect} from "next/navigation";


export default async function LoginPage() {

    const session = await auth0.getSession();

    if (session) redirect("/onboarding");

    return (
        <main className="flex min-h-svh bg-background">
            {/* Left Panel - Brand / Hero */}
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
                <div className="absolute bottom-0 left-0 right-0 h-1 bg-primary" aria-hidden="true"/>

                {/* Top - Logo */}
                <div className="relative z-10">
                    <SportsHubLogo/>
                </div>

                {/* Center - Main messaging */}
                <div className="relative z-10 max-w-lg">
                    <h1 className="font-[var(--font-space-grotesk)] text-5xl font-bold leading-tight tracking-tight text-foreground xl:text-6xl">
                        Reserva.{" "}
                        <span className="text-primary">Juega.</span>{" "}
                        <br/>
                        Compite.
                    </h1>
                    <p className="mt-5 max-w-md text-base leading-relaxed text-muted-foreground">
                        La plataforma que conecta jugadores, simplifica las reservas y lleva
                        tu juego al siguiente nivel.
                    </p>
                    <div className="mt-8">
                        <FeatureCards/>
                    </div>
                </div>

                {/* Bottom - Stats */}
                <div className="relative z-10">
                    <StatsBar/>
                </div>
            </div>

            {/* Right Panel - Auth Form */}
            <div className="flex w-full flex-col lg:w-1/2 xl:w-[45%]">
                {/* Mobile Logo */}
                <div className="flex items-center justify-between p-6 lg:hidden">
                    <SportsHubLogo/>
                </div>

                <div className="flex flex-1 items-center justify-center px-6 py-8 lg:px-12 xl:px-20">
                    <div className="w-full max-w-md">
                        <AuthForm/>
                    </div>
                </div>

                {/* Footer */}
                <footer className="p-6 text-center">
                    <p className="text-xs text-muted-foreground">
                        Al continuar, aceptas nuestros{" "}
                        <a href="#"
                           className="text-primary hover:text-primary/80 transition-colors underline-offset-4 hover:underline">
                            Terminos de Servicio
                        </a>{" "}
                        y{" "}
                        <a href="#"
                           className="text-primary hover:text-primary/80 transition-colors underline-offset-4 hover:underline">
                            Politica de Privacidad
                        </a>
                    </p>
                </footer>
            </div>
        </main>
    )
}
