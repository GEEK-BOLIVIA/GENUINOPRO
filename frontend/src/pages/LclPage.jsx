import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  CheckCircle2,
  Clock3,
  FileText,
  Loader2,
  PackageCheck,
  Plus,
  RefreshCw,
  Ship,
} from 'lucide-react';

import { getLclProformas } from '../services/lclService';
import LclProformaDetail from '../modules/proforma/lcl/LclProformaDetail';
import LclOperationalSimulator from '../modules/proforma/lcl/LclOperationalSimulator';

export default function LclPage({ mode = 'list' }) {
  const { id } = useParams();
  const navigate = useNavigate();

  const [proformas, setProformas] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  async function loadProformas() {
    try {
      setIsLoading(true);
      setError('');

      const response = await getLclProformas();
      setProformas(Array.isArray(response) ? response : []);
    } catch (err) {
      console.error(err);
      setError(err.message || 'No se pudieron cargar las proformas');
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    if (mode === 'list') {
      loadProformas();
    }
  }, [mode]);

  const stats = useMemo(() => {
    return {
      total: proformas.length,
      approved: proformas.filter((item) =>
        ['APPROVED', 'APPROVED_BY_CUSTOMER'].includes(item.rawStatus)
      ).length,
      pending: proformas.filter((item) =>
        ['DRAFT', 'IN_REVIEW'].includes(item.rawStatus)
      ).length,
      customerApproved: proformas.filter(
        (item) => item.rawStatus === 'APPROVED_BY_CUSTOMER'
      ).length,
    };
  }, [proformas]);

  if (mode === 'new') {
    return (
      <div className="space-y-5">
        <button
          onClick={() => navigate('/lcl')}
          className="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm font-bold text-slate-600 hover:bg-slate-50"
        >
          ← Volver a proformas
        </button>

        <section>
          <p className="text-sm font-medium text-slate-500">
            Nueva cotización
          </p>

          <h1 className="mt-2 text-3xl font-black tracking-tight text-slate-900">
            Proforma LCL
          </h1>

          <p className="mt-2 max-w-3xl text-sm leading-6 text-slate-500">
            Simulador operativo para carga consolidada LCL. Los demás tipos de
            proforma se incorporarán sobre este mismo módulo.
          </p>
        </section>

        <LclOperationalSimulator />
      </div>
    );
  }

  if (mode === 'detail' && id) {
    return (
      <div className="space-y-5">
        <button
          onClick={() => navigate('/lcl')}
          className="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm font-bold text-slate-600 hover:bg-slate-50"
        >
          ← Volver a proformas
        </button>

        <LclProformaDetail id={id} />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <section className="flex flex-col justify-between gap-4 xl:flex-row xl:items-end">
        <div>
          <p className="text-sm font-medium text-slate-500">
            Cotizaciones comerciales
          </p>

          <h1 className="mt-2 text-3xl font-black tracking-tight text-slate-900">
            Proformas
          </h1>

          <p className="mt-2 max-w-3xl text-sm leading-6 text-slate-500">
            Gestión central de proformas comerciales. Actualmente está operativo
            el flujo LCL; esta vista queda preparada para FCL, Aéreo y
            Personalizada.
          </p>
        </div>

        <div className="flex flex-wrap gap-3">
          <button
            onClick={loadProformas}
            className="flex items-center gap-2 rounded-2xl border border-slate-200 bg-white px-5 py-3 text-sm font-bold text-slate-600 hover:bg-slate-50"
          >
            <RefreshCw size={18} />
            Actualizar
          </button>

          <button
            onClick={() => navigate('/lcl/nueva')}
            className="flex items-center gap-2 rounded-2xl bg-orange-500 px-5 py-3 text-sm font-bold text-white shadow-lg shadow-orange-500/20 transition hover:bg-orange-600"
          >
            <Plus size={18} />
            Nueva proforma LCL
          </button>
        </div>
      </section>

      <section className="grid gap-5 md:grid-cols-4">
        <StatCard
          icon={FileText}
          title="Total proformas"
          value={stats.total}
          description="Cotizaciones registradas."
        />

        <StatCard
          icon={Clock3}
          title="Pendientes"
          value={stats.pending}
          description="Borrador o en revisión."
        />

        <StatCard
          icon={CheckCircle2}
          title="Aprobadas internas"
          value={stats.approved}
          description="Aprobadas por el equipo."
        />

        <StatCard
          icon={PackageCheck}
          title="Aprobadas por cliente"
          value={stats.customerApproved}
          description="Listas para operación."
        />
      </section>

      <section className="grid gap-4 md:grid-cols-4">
        <TypeTab active label="LCL" description="Operativo" />
        <TypeTab label="FCL" description="Próximamente" />
        <TypeTab label="Aéreo" description="Próximamente" />
        <TypeTab label="Personalizada" description="Próximamente" />
      </section>

      <section className="rounded-3xl border border-slate-200 bg-white shadow-sm">
        <div className="flex flex-col gap-3 border-b border-slate-100 p-6 md:flex-row md:items-center md:justify-between">
          <div>
            <h2 className="text-lg font-black text-slate-900">
              Bandeja de proformas LCL
            </h2>

            <p className="mt-1 text-sm text-slate-500">
              Cotizaciones reales registradas en PostgreSQL.
            </p>
          </div>

          <span className="w-fit rounded-full bg-orange-50 px-3 py-1 text-xs font-bold text-orange-700">
            Tipo operativo actual: LCL
          </span>
        </div>

        {error && (
          <div className="m-6 rounded-2xl bg-rose-50 p-4 text-sm font-bold text-rose-700">
            {error}
          </div>
        )}

        {isLoading ? (
          <div className="flex items-center justify-center gap-3 p-10 text-sm font-bold text-slate-500">
            <Loader2 className="animate-spin" size={18} />
            Cargando proformas...
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full min-w-[950px] text-left">
              <thead className="bg-slate-50 text-xs uppercase tracking-wide text-slate-400">
                <tr>
                  <th className="px-6 py-4">Código</th>
                  <th className="px-6 py-4">Fecha</th>
                  <th className="px-6 py-4">Cliente</th>
                  <th className="px-6 py-4">Tipo</th>
                  <th className="px-6 py-4">Origen</th>
                  <th className="px-6 py-4">Destino</th>
                  <th className="px-6 py-4">Monto</th>
                  <th className="px-6 py-4">Estado</th>
                  <th className="px-6 py-4 text-right">Acción</th>
                </tr>
              </thead>

              <tbody className="divide-y divide-slate-100">
                {proformas.map((item) => (
                  <tr
                    key={item.id || item.code}
                    className="hover:bg-slate-50/60"
                  >
                    <td className="px-6 py-5 text-sm font-black text-slate-900">
                      {item.code || '-'}
                    </td>

                    <td className="px-6 py-5 text-sm text-slate-500">
                      {item.createdAt
                        ? new Date(item.createdAt).toLocaleString()
                        : '-'}
                    </td>

                    <td className="px-6 py-5 text-sm font-bold text-slate-700">
                      {item.client || 'Sin cliente'}
                    </td>

                    <td className="px-6 py-5">
                      <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-bold text-slate-600">
                        LCL
                      </span>
                    </td>

                    <td className="px-6 py-5 text-sm text-slate-500">
                      <div className="flex items-center gap-2">
                        <Ship size={16} />
                        {item.origin || '-'}
                      </div>
                    </td>

                    <td className="px-6 py-5 text-sm text-slate-500">
                      {item.destination || '-'}
                    </td>

                    <td className="px-6 py-5 text-sm font-black text-slate-900">
                      {item.amount || '-'}
                    </td>

                    <td className="px-6 py-5">
                      <span
                        className={`rounded-full px-3 py-1 text-xs font-bold ${
                          item.statusColor || 'bg-slate-100 text-slate-600'
                        }`}
                      >
                        {item.status || item.rawStatus || 'Sin estado'}
                      </span>
                    </td>

                    <td className="px-6 py-5 text-right">
                      <button
                        onClick={() => navigate(`/lcl/${item.id}`)}
                        className="rounded-xl bg-slate-900 px-4 py-2 text-xs font-bold text-white hover:bg-slate-700"
                      >
                        Ver detalle
                      </button>
                    </td>
                  </tr>
                ))}

                {proformas.length === 0 && (
                  <tr>
                    <td
                      colSpan="9"
                      className="px-6 py-10 text-center text-sm text-slate-500"
                    >
                      No hay proformas registradas todavía.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </div>
  );
}

function StatCard({ icon: Icon, title, value, description }) {
  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="w-fit rounded-2xl bg-orange-50 p-3 text-orange-600">
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

function TypeTab({ active = false, label, description }) {
  return (
    <div
      className={`rounded-3xl border p-5 ${
        active
          ? 'border-orange-200 bg-orange-50'
          : 'border-slate-200 bg-white opacity-70'
      }`}
    >
      <p
        className={`text-lg font-black ${
          active ? 'text-orange-700' : 'text-slate-600'
        }`}
      >
        {label}
      </p>

      <p className="mt-1 text-sm text-slate-500">
        {description}
      </p>
    </div>
  );
}