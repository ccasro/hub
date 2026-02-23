import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Dashboard - SportsHub",
  description:
    "Explora venues de padel cerca de ti, reserva pistas y encuentra jugadores.",
};

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return <>{children}</>;
}
