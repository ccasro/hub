import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Panel Propietario - SportsHub",
  description: "Gestiona tus venues, pistas, horarios y reservas.",
};

export default function OwnerLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return <>{children}</>;
}
