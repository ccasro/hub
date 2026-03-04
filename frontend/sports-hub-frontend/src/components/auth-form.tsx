import {Button} from "@/components/ui/button"
import {Separator} from "@/components/ui/separator"
import {ArrowRight, ShieldCheck, Users} from "lucide-react"

export function AuthForm() {
    return (
        <div className="w-full">
            {/* Header */}
            <div className="mb-8">
                <h2 className="font-[var(--font-space-grotesk)] text-2xl font-bold tracking-tight text-foreground">
                    Accede a SportsHub
                </h2>
                <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
                    Reserva pistas, encuentra jugadores y compite en rankings.
                </p>
            </div>

            {/* Primary CTA - Login with Auth0 */}
            <a href="/auth/login" className="block">
                <Button
                    className="h-13 w-full bg-primary font-semibold text-primary-foreground hover:bg-primary/90 text-base"
                >
                    Iniciar sesion
                    <ArrowRight className="ml-2 h-4 w-4"/>
                </Button>
            </a>

            <div className="my-5 flex items-center gap-4">
                <Separator className="flex-1 bg-border/50"/>
                <span className="text-xs font-medium uppercase tracking-wider text-muted-foreground">
          o continua con
        </span>
                <Separator className="flex-1 bg-border/50"/>
            </div>

            {/* Social Login Buttons */}
            <div className="flex flex-col gap-3">
                <a href="/auth/login?connection=google-oauth2" className="block">
                    <Button
                        variant="outline"
                        className="relative h-12 w-full border-border/60 bg-secondary/30 text-foreground hover:border-primary/40 hover:bg-secondary/50"
                    >
                        <svg className="absolute left-4 h-5 w-5" viewBox="0 0 24 24" aria-hidden="true">
                            <path
                                d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92a5.06 5.06 0 0 1-2.2 3.32v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.1z"
                                fill="#4285F4"
                            />
                            <path
                                d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                                fill="#34A853"
                            />
                            <path
                                d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                                fill="#FBBC05"
                            />
                            <path
                                d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                                fill="#EA4335"
                            />
                        </svg>
                        Continuar con Google
                    </Button>
                </a>

                <a href="/auth/login?connection=apple" className="block">
                    <Button
                        variant="outline"
                        className="relative h-12 w-full border-border/60 bg-secondary/30 text-foreground hover:border-primary/40 hover:bg-secondary/50"
                    >
                        <svg className="absolute left-4 h-5 w-5" viewBox="0 0 24 24" fill="currentColor"
                             aria-hidden="true">
                            <path
                                d="M17.05 20.28c-.98.95-2.05.88-3.08.4-1.09-.5-2.08-.48-3.24 0-1.44.62-2.2.44-3.06-.4C2.79 15.25 3.51 7.59 9.05 7.31c1.35.07 2.29.74 3.08.8 1.18-.24 2.31-.93 3.57-.84 1.51.12 2.65.72 3.4 1.8-3.12 1.87-2.38 5.98.48 7.13-.57 1.5-1.31 2.99-2.54 4.09zM12.03 7.25c-.15-2.23 1.66-4.07 3.74-4.25.29 2.58-2.34 4.5-3.74 4.25z"/>
                        </svg>
                        Continuar con Apple
                    </Button>
                </a>
            </div>

            {/* Divider */}
            <div className="my-5 flex items-center gap-4">
                <Separator className="flex-1 bg-border/50"/>
            </div>

            {/* Register CTA */}
            <a href="/auth/login?screen_hint=signup" className="block">
                <Button
                    variant="ghost"
                    className="h-12 w-full border border-dashed border-border/60 text-foreground hover:border-primary/40 hover:bg-secondary/30"
                >
                    <Users className="mr-2 h-4 w-4 text-primary"/>
                    Registrate gratis
                    <ArrowRight className="ml-auto h-4 w-4 text-muted-foreground"/>
                </Button>
            </a>

            {/* Trust badges */}
            <div className="mt-8 flex items-center justify-center gap-2 text-xs text-muted-foreground">
                <ShieldCheck className="h-3.5 w-3.5 text-primary/70"/>
                <span>Autenticación segura con Auth0</span>
            </div>
        </div>
    )
}
