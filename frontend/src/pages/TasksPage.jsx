import {
  CalendarClock,
  Clock3,
  PhoneCall,
  Search,
} from 'lucide-react';

import { useEffect, useMemo, useState } from 'react';
import { getAllTasks } from '../services/tasksService';



export default function TasksPage() {

    const [tasks, setTasks] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
    async function loadTasks() {
        try {
        const data = await getAllTasks();
        setTasks(Array.isArray(data) ? data : []);
        } catch (error) {
        console.error(error);
        setTasks([]);
        } finally {
        setLoading(false);
        }
    }

    loadTasks();
    }, []);

    const stats = useMemo(() => {
    const now = new Date();

    return {
        pending: tasks.filter(
        (t) => (t.status || '').toUpperCase() === 'PENDING'
        ).length,

        overdue: tasks.filter((t) => {
        if (!t.dueAt) return false;

        return (
            (t.status || '').toUpperCase() === 'PENDING' &&
            new Date(t.dueAt) < now
        );
        }).length,

        today: tasks.filter((t) => {
        if (!t.dueAt) return false;

        const due = new Date(t.dueAt);

        return (
            due.getFullYear() === now.getFullYear() &&
            due.getMonth() === now.getMonth() &&
            due.getDate() === now.getDate()
        );
        }).length,
    };
    }, [tasks]);

  return (
    <div className="space-y-6">
      <section className="flex flex-col justify-between gap-4 xl:flex-row xl:items-end">
        <div>
          <p className="text-sm font-medium text-slate-500">
            Gestión comercial
          </p>

          <h1 className="mt-2 text-3xl font-black tracking-tight text-slate-900">
            Tareas
          </h1>

          <p className="mt-2 max-w-3xl text-sm leading-6 text-slate-500">
            Central de seguimientos, llamadas, recordatorios y acciones
            comerciales pendientes.
          </p>
        </div>

      </section>

      <section className="grid gap-5 md:grid-cols-3">
        <StatCard
          icon={Clock3}
          title="Pendientes"
          value={stats.pending}
          description="Tareas abiertas por resolver."
        />

        <StatCard
          icon={CalendarClock}
          title="Para hoy"
          value={stats.today}
          description="Acciones que requieren atención hoy."
        />

        <StatCard
          icon={PhoneCall}
          title="Vencidas"
          value={stats.overdue}
          description="Tareas fuera de plazo."
          danger
        />
      </section>

      <section className="rounded-3xl border border-slate-200 bg-white shadow-sm">
        <div className="flex flex-col gap-4 border-b border-slate-100 p-6 lg:flex-row lg:items-center lg:justify-between">
          <div>
            <h2 className="text-lg font-black text-slate-900">
              Bandeja de tareas
            </h2>

            <p className="mt-1 text-sm text-slate-500">
              Tareas comerciales registradas en BASE DE DATOS.
            </p>
          </div>

          <div className="flex items-center gap-3 rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 opacity-70">
            <Search size={18} className="text-slate-400" />
            <input
              disabled
              placeholder="Buscar tareas próximamente..."
              className="bg-transparent text-sm outline-none placeholder:text-slate-400"
            />
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full min-w-[900px] text-left">
            <thead className="bg-slate-50 text-xs uppercase tracking-wide text-slate-400">
              <tr>
                <th className="px-6 py-4">Tarea</th>
                <th className="px-6 py-4">Responsable</th>
                <th className="px-6 py-4">Tipo</th>
                <th className="px-6 py-4">Vencimiento</th>
                <th className="px-6 py-4">Estado</th>
                <th className="px-6 py-4 text-right">Acción</th>
              </tr>
            </thead>

            <tbody className="divide-y divide-slate-100">
              {tasks.map((task) => (
                <tr key={task.id} className="hover:bg-slate-50/70">
                  <td className="px-6 py-5 text-sm font-black text-slate-900">
                    {task.title}
                  </td>

                    <td className="px-6 py-5 text-sm text-slate-600">
                    {task.assignedTo || '-'}
                    </td>

                  <td className="px-6 py-5 text-sm text-slate-600">
                    {task.priority || 'MEDIA'}
                  </td>

                  <td className="px-6 py-5 text-sm font-bold text-slate-700">
                    {task.dueAt
                    ? new Date(task.dueAt).toLocaleDateString()
                    : '-'}
                  </td>

                  <td className="px-6 py-5">
                    <span
                    className={`rounded-full px-3 py-1 text-xs font-bold ${
                    (task.status || '').toUpperCase() === 'COMPLETED'
                        ? 'bg-emerald-50 text-emerald-700'
                        : 'bg-amber-50 text-amber-700'
                    }`}
                    >
                      {task.status}
                    </span>
                  </td>

                  <td className="px-6 py-5 text-right">
                    <button
                      disabled
                      className="rounded-xl bg-slate-200 px-4 py-2 text-xs font-bold text-slate-500"
                    >
                      Próximamente
                    </button>
                  </td>
                </tr>
              ))}
              {!loading && tasks.length === 0 && (
                <tr>
                    <td
                    colSpan="6"
                    className="px-6 py-12 text-center text-sm text-slate-500"
                    >
                    No existen tareas registradas.
                    </td>
                </tr>
                )}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}

function StatCard({ icon: Icon, title, value, description, danger = false }) {
  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
      <div
        className={`w-fit rounded-2xl p-3 ${
          danger
            ? 'bg-rose-50 text-rose-600'
            : 'bg-orange-50 text-orange-600'
        }`}
      >
        <Icon size={22} />
      </div>

      <p className="mt-5 text-sm font-bold text-slate-500">
        {title}
      </p>

      <h2 className="mt-2 text-3xl font-black text-slate-900">
        {value}
      </h2>

      <p className="mt-1 text-sm text-slate-500">
        {description}
      </p>
    </div>
  );
}