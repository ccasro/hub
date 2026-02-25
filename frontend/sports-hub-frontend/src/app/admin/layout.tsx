import {AdminSidebar} from "@/components/admin/admin-sidebar";
import {UserProfile} from "@/types";
import {auth0} from "@/lib/auth0";
import {redirect} from "next/navigation";
import {apiFetch} from "@/lib/api";


export default async function AdminLayout({
  children,
}: {
  children: React.ReactNode;
}) {
    const session = await auth0.getSession();
    if (!session) redirect("/");

    const profile = await apiFetch<UserProfile>("/api/me");
    if (profile.role !== "ADMIN") redirect("/dashboard");
  return (
    <div className="flex min-h-screen bg-background">
      <AdminSidebar user={ profile } />
      <main className="flex-1 overflow-y-auto">{children}</main>
    </div>
  );
}
