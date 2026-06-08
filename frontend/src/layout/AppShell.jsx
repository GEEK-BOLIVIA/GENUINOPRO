import {
  LayoutDashboard,
  FileText,
  Settings,
  LogOut,
  UserCircle,
  Users,
  MessageSquare,
  KanbanSquare,
  ShieldCheck,
  ClipboardList,
  User,
} from 'lucide-react';

import { useAuth } from '../security/AuthProvider';

export default function AppShell({ currentPage, setCurrentPage, children }) {
  const auth = useAuth();
  const roles = auth?.roles || [];

  const hasRole = (...allowedRoles) =>
    allowedRoles.some((role) => roles.includes(role));

  const menu = [
    { key: 'DASHBOARD', label: 'Dashboard', icon: LayoutDashboard },

    ...(hasRole('ADMIN', 'GERENCIA', 'SUPERVISOR', 'VENDEDOR')
      ? [{ key: 'LEADS', label: 'Contactos', icon: Users }]
      : []),

    ...(hasRole('ADMIN', 'GERENCIA', 'SUPERVISOR', 'VENDEDOR')
      ? [{ key: 'INBOX', label: 'Inbox', icon: MessageSquare }]
      : []),

    ...(hasRole('ADMIN', 'GERENCIA', 'SUPERVISOR', 'VENDEDOR')
      ? [{ key: 'PIPELINE', label: 'Pipeline', icon: KanbanSquare }]
      : []),

    ...(hasRole('ADMIN', 'GERENCIA', 'SUPERVISOR', 'VENDEDOR')
      ? [{ key: 'TASKS', label: 'Tareas', icon: ClipboardList }]
      : []),

    ...(hasRole('ADMIN', 'GERENCIA', 'SUPERVISOR', 'VENDEDOR')
      ? [{ key: 'LCL', label: 'Proformas', icon: FileText }]
      : []),

    ...(hasRole('ADMIN', 'GERENCIA')
      ? [{ key: 'PARAMETERS', label: 'Parámetros', icon: Settings }]
      : []),

    ...(hasRole('ADMIN')
      ? [{ key: 'ADMIN_USERS', label: 'Administración', icon: ShieldCheck }]
      : []),

    {
      key: 'ACCOUNT',
      label: 'Mi Cuenta',
      icon: User,
    },
  ];

  const currentLabel =
    menu.find((item) => item.key === currentPage)?.label || 'Dashboard';

  return (
    <div className="flex h-screen bg-slate-50 text-slate-900">
      <aside className="flex w-72 flex-col bg-slate-950 text-white">
        <div className="p-8">
          <div className="text-xl font-black">
            Genuino <span className="text-orange-500">PRO+</span>
          </div>
          <div className="mt-1 text-xs text-slate-400">
            CRM Logístico
          </div>
        </div>

        <nav className="flex-1 space-y-2 px-4">
          {menu.map((item) => {
            const Icon = item.icon;
            const active = currentPage === item.key;

            return (
              <button
                key={item.key}
                onClick={() => setCurrentPage(item.key)}
                className={`flex w-full items-center gap-3 rounded-2xl px-4 py-3 text-sm font-bold transition ${
                  active
                    ? 'bg-orange-600 text-white'
                    : 'text-slate-300 hover:bg-slate-800'
                }`}
              >
                <Icon size={18} />
                {item.label}
              </button>
            );
          })}
        </nav>

        <div className="border-t border-slate-800 p-4">
          <div className="mb-4 flex items-center gap-3">
            <UserCircle size={34} className="text-slate-400" />
            <div className="min-w-0">
              <div className="truncate text-sm font-bold">
                {auth?.username || 'Usuario'}
              </div>
              <div className="truncate text-xs text-slate-400">
                {(auth?.roles || []).join(', ')}
              </div>
            </div>
          </div>

          <button
            onClick={auth?.logout}
            className="flex w-full items-center justify-center gap-2 rounded-2xl bg-slate-800 px-4 py-3 text-sm font-bold hover:bg-slate-700"
          >
            <LogOut size={16} />
            Salir
          </button>
        </div>
      </aside>

      <main className="flex min-w-0 flex-1 flex-col">
        <header className="sticky top-0 z-20 flex h-20 items-center justify-between border-b border-slate-200 bg-white/90 px-8 backdrop-blur">
          <div>
            <p className="text-xs font-bold uppercase tracking-[0.25em] text-slate-400">
              Genuino CRM Enterprise
            </p>

            <h1 className="mt-1 text-2xl font-black tracking-tight text-slate-900">
              {currentLabel}
            </h1>
          </div>
        </header>

        <section className="flex-1 overflow-auto p-8">
          {children}
        </section>
      </main>
    </div>
  );
}