import {
  CircleDollarSign,
  Clock3,
  FileText,
  Phone,
  Trophy,
  XCircle,
} from 'lucide-react';

const columns = [
  {
    id: 'NEW',
    title: 'Nuevos Leads',
    color: 'bg-sky-100 text-sky-700',
    icon: Phone,
    items: [
      {
        company: 'Importadora Atlas',
        contact: 'Carlos Mendoza',
        amount: '$ 12,500',
      },
      {
        company: 'Logística Orion',
        contact: 'María Pérez',
        amount: '$ 8,200',
      },
    ],
  },
  {
    id: 'CONTACTED',
    title: 'Contactados',
    color: 'bg-amber-100 text-amber-700',
    icon: Clock3,
    items: [
      {
        company: 'Global Freight',
        contact: 'Andrés Ruiz',
        amount: '$ 15,900',
      },
    ],
  },
  {
    id: 'PROPOSAL',
    title: 'Propuesta enviada',
    color: 'bg-violet-100 text-violet-700',
    icon: FileText,
    items: [
      {
        company: 'Bolivian Cargo',
        contact: 'Fernanda López',
        amount: '$ 21,000',
      },
    ],
  },
  {
    id: 'NEGOTIATION',
    title: 'Negociación',
    color: 'bg-orange-100 text-orange-700',
    icon: CircleDollarSign,
    items: [
      {
        company: 'Import Export SRL',
        contact: 'Luis Vargas',
        amount: '$ 31,400',
      },
    ],
  },
  {
    id: 'WON',
    title: 'Ganadas',
    color: 'bg-emerald-100 text-emerald-700',
    icon: Trophy,
    items: [
      {
        company: 'Grupo Industrial Nova',
        contact: 'Jorge Molina',
        amount: '$ 44,000',
      },
    ],
  },
  {
    id: 'LOST',
    title: 'Perdidas',
    color: 'bg-rose-100 text-rose-700',
    icon: XCircle,
    items: [],
  },
];

export default function OpportunitiesPage() {
  return (
    <div className="space-y-6">
      <section className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium text-slate-500">Comercial</p>

          <h1 className="mt-2 text-3xl font-black tracking-tight text-slate-900">
            Pipeline Comercial
          </h1>

          <p className="mt-2 text-sm text-slate-500">
            Seguimiento visual de oportunidades y estado comercial.
          </p>
        </div>

        <button className="rounded-2xl bg-orange-500 px-5 py-3 text-sm font-bold text-white shadow-lg shadow-orange-500/20 transition hover:bg-orange-600">
          Nueva oportunidad
        </button>
      </section>

      <section className="overflow-x-auto pb-4">
        <div className="flex min-w-[1400px] gap-5">
          {columns.map((column) => {
            const Icon = column.icon;

            return (
              <div
                key={column.id}
                className="w-[320px] shrink-0 rounded-3xl border border-slate-200 bg-slate-100/70 p-4"
              >
                <div className="mb-4 flex items-center justify-between">
                  <div
                    className={`flex items-center gap-2 rounded-xl px-3 py-2 text-sm font-bold ${column.color}`}
                  >
                    <Icon size={16} />
                    {column.title}
                  </div>

                  <span className="text-sm font-bold text-slate-400">
                    {column.items.length}
                  </span>
                </div>

                <div className="space-y-4">
                  {column.items.length === 0 ? (
                    <div className="rounded-2xl border border-dashed border-slate-300 bg-white p-6 text-center text-sm text-slate-400">
                      Sin oportunidades
                    </div>
                  ) : (
                    column.items.map((item) => (
                      <div
                        key={item.company}
                        className="rounded-3xl bg-white p-5 shadow-sm transition hover:-translate-y-1 hover:shadow-lg"
                      >
                        <div className="flex items-start justify-between">
                          <div>
                            <h2 className="text-sm font-black text-slate-900">
                              {item.company}
                            </h2>

                            <p className="mt-1 text-sm text-slate-500">
                              {item.contact}
                            </p>
                          </div>

                          <span className="rounded-full bg-emerald-50 px-3 py-1 text-xs font-bold text-emerald-700">
                            Activo
                          </span>
                        </div>

                        <div className="mt-5 flex items-center justify-between">
                          <span className="text-xs font-semibold uppercase tracking-wide text-slate-400">
                            Valor estimado
                          </span>

                          <span className="text-sm font-black text-slate-900">
                            {item.amount}
                          </span>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </section>
    </div>
  );
}