import { useEffect, useState } from 'react';
import {
  BarChart3,
  ClipboardList,
  FileText,
  Target,
  Trophy,
  Users,
} from 'lucide-react';
import { getDashboardStats } from '../services/dashboardService';

export default function DashboardPage({ username = 'admin' }) {
  const [stats, setStats] = useState({
    totalLeads: 0,
    newLeads: 0,
    contacted: 0,
    quoted: 0,
    negotiation: 0,
    won: 0,
    conversionRate: 0,
  });

  useEffect(() => {
    let mounted = true;

    async function loadStats() {
      try {
        const data = await getDashboardStats();

        if (mounted) {
          setStats({
            totalLeads: data.totalLeads || 0,
            newLeads: data.newLeads || 0,
            contacted: data.contacted || 0,
            quoted: data.quoted || 0,
            negotiation: data.negotiation || 0,
            won: data.won || 0,
            conversionRate: data.conversionRate || 0,
          });
        }
      } catch (error) {
        console.error('Error cargando dashboard', error);
      }
    }

    loadStats();

    const interval = setInterval(loadStats, 8000);

    return () => {
      mounted = false;
      clearInterval(interval);
    };
  }, []);

  const openPipeline =
    stats.newLeads + stats.contacted + stats.quoted + stats.negotiation;

  const kpis = [
    {
      title: 'Contactos registrados',
      value: stats.totalLeads,
      icon: Users,
      description: 'Total de leads, prospectos y clientes registrados.',
    },
    {
      title: 'Leads nuevos',
      value: stats.newLeads,
      icon: Target,
      description: 'Contactos pendientes de primer seguimiento comercial.',
    },
    {
      title: 'Oportunidades abiertas',
      value: openPipeline,
      icon: ClipboardList,
      description: 'Contactos activos dentro del pipeline comercial.',
    },
    {
      title: 'Clientes ganados',
      value: stats.won,
      icon: Trophy,
      description: 'Contactos convertidos exitosamente en cliente.',
    },
    {
      title: 'Conversión comercial',
      value: `${stats.conversionRate}%`,
      icon: BarChart3,
      description: 'Clientes ganados sobre el total de contactos registrados.',
    },
  ];

  const pipeline = [
    { title: 'Nuevos', value: stats.newLeads },
    { title: 'Contactados', value: stats.contacted },
    { title: 'Cotizados', value: stats.quoted },
    { title: 'Negociación', value: stats.negotiation },
    { title: 'Ganados', value: stats.won },
  ];

  return (
    <div className="space-y-8">
      <section>
        <p className="text-sm font-medium text-slate-500">
          Genuino CRM PRO+
        </p>

        <h1 className="mt-2 text-3xl font-black tracking-tight text-slate-900">
          Bienvenido, {username}
        </h1>

        <p className="mt-2 max-w-3xl text-sm text-slate-500">
          Vista ejecutiva del flujo comercial: contactos registrados,
          oportunidades activas y clientes ganados.
        </p>
      </section>

      <section className="grid gap-5 md:grid-cols-2 xl:grid-cols-5">
        {kpis.map((item) => {
          const Icon = item.icon;

          return (
            <div
              key={item.title}
              className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm"
            >
              <div className="rounded-2xl bg-orange-50 p-3 text-orange-600 w-fit">
                <Icon size={22} />
              </div>

              <h2 className="mt-5 text-sm font-bold text-slate-500">
                {item.title}
              </h2>

              <p className="mt-2 text-3xl font-black text-slate-900">
                {item.value}
              </p>

              <p className="mt-2 text-sm leading-6 text-slate-500">
                {item.description}
              </p>
            </div>
          );
        })}
      </section>

      <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
        <div className="flex items-start justify-between gap-4">
          <div>
            <h2 className="text-lg font-black text-slate-900">
              Estado del pipeline comercial
            </h2>

            <p className="mt-2 text-sm text-slate-500">
              Distribución actual de contactos según su etapa comercial.
            </p>
          </div>

          <FileText className="text-slate-300" size={28} />
        </div>

        <div className="mt-6 grid gap-4 md:grid-cols-2 xl:grid-cols-5">
          {pipeline.map((item) => (
            <div
              key={item.title}
              className="rounded-2xl border border-slate-200 bg-slate-50 p-5"
            >
              <p className="text-sm font-bold text-slate-500">
                {item.title}
              </p>

              <p className="mt-2 text-3xl font-black text-slate-900">
                {item.value}
              </p>
            </div>
          ))}
        </div>
      </section>

      <section className="rounded-3xl border border-amber-200 bg-amber-50 p-6">
        <h2 className="text-lg font-black text-amber-900">
          Pendiente para el siguiente sprint
        </h2>

        <p className="mt-2 text-sm leading-6 text-amber-800">
          Las tareas, notificaciones, aprobaciones pendientes y actividades
          recientes se mostrarán aquí cuando conectemos esos módulos a datos
          reales. Por ahora fueron retiradas del Dashboard para evitar
          información decorativa o confusa.
        </p>
      </section>
    </div>
  );
}