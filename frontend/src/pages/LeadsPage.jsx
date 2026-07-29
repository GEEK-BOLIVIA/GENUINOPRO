import {
  BadgeCheck,
  Filter,
  Phone,
  Plus,
  Search,
  Users,
  UserCheck,
  UserPlus,
} from 'lucide-react';

import { useEffect, useMemo, useState } from 'react';
import LeadDetailsModal from '../components/leads/LeadDetailsModal';
import CreateLeadModal from '../components/leads/CreateLeadModal';
import { createLead, getLeads } from '../services/leadsService';
import {
  saveLeadCustomerProfile,
} from '../services/customerProfileService';

const statusStyles = {
  NEW: 'bg-sky-50 text-sky-700',
  CONTACTED: 'bg-indigo-50 text-indigo-700',
  NEGOTIATION: 'bg-amber-50 text-amber-700',
  QUOTED: 'bg-violet-50 text-violet-700',
  WON: 'bg-emerald-50 text-emerald-700',
  LOST: 'bg-rose-50 text-rose-700',
};

const statusLabels = {
  NEW: 'Lead nuevo',
  CONTACTED: 'Contactado',
  NEGOTIATION: 'Negociación',
  QUOTED: 'Cotizado',
  WON: 'Cliente',
  LOST: 'Descartado',
};

export default function LeadsPage() {
  const [leads, setLeads] = useState([]);
  const [selectedLead, setSelectedLead] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

  useEffect(() => {
    async function loadLeads() {
      const data = await getLeads();
      setLeads(Array.isArray(data) ? data : []);
    }

    loadLeads();
  }, []);

  const summary = useMemo(() => {
    const total = leads.length;
    const newLeads = leads.filter((lead) => lead.status === 'NEW').length;
    const customers = leads.filter((lead) => lead.status === 'WON').length;

    return { total, newLeads, customers };
  }, [leads]);

  function openLead(lead) {
    setSelectedLead(lead);
    setIsModalOpen(true);
  }

  function closeLead() {
    setSelectedLead(null);
    setIsModalOpen(false);
  }

  async function handleCreateLead(payload) {
    const {
      customerProfile,
      ...leadPayload
    } = payload;

    const created = await createLead(leadPayload);

    if (!created?.id) {
      throw new Error(
        'El backend creó el lead, pero no devolvió un identificador válido.'
      );
    }

    await saveLeadCustomerProfile(
      created.id,
      customerProfile
    );

    setLeads((prev) => [created, ...prev]);

    return created;
  }

  return (
    <div className="space-y-6">
      <section className="flex items-center justify-between gap-4">
        <div>
          <p className="text-sm font-medium text-slate-500">
            Base comercial
          </p>

          <h1 className="mt-2 text-3xl font-black tracking-tight text-slate-900">
            Contactos
          </h1>

          <p className="mt-2 max-w-3xl text-sm leading-6 text-slate-500">
            Directorio central de leads, prospectos y clientes. Desde aquí se
            accede al Contacto 360, historial, responsable comercial y estado
            actual.
          </p>
        </div>

        <button
          onClick={() => setIsCreateModalOpen(true)}
          className="flex items-center gap-2 rounded-2xl bg-orange-500 px-5 py-3 text-sm font-bold text-white shadow-lg shadow-orange-500/20 transition hover:bg-orange-600"
        >
          <Plus size={18} />
          Nuevo contacto
        </button>
      </section>

      <section className="grid gap-4 md:grid-cols-3">
        <SummaryCard
          icon={Users}
          title="Contactos registrados"
          value={summary.total}
          description="Total de registros comerciales."
        />

        <SummaryCard
          icon={UserPlus}
          title="Leads nuevos"
          value={summary.newLeads}
          description="Pendientes de primer seguimiento."
        />

        <SummaryCard
          icon={UserCheck}
          title="Clientes"
          value={summary.customers}
          description="Contactos convertidos a cliente."
        />
      </section>

      <section className="rounded-3xl border border-slate-200 bg-white shadow-sm">
        <div className="flex flex-col gap-4 border-b border-slate-100 p-6 lg:flex-row lg:items-center lg:justify-between">
          <div className="flex items-center gap-3 rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 opacity-70">
            <Search size={18} className="text-slate-400" />

            <input
              type="text"
              disabled
              placeholder="Búsqueda avanzada pendiente..."
              className="bg-transparent text-sm outline-none placeholder:text-slate-400"
            />
          </div>

          <button
            disabled
            className="flex cursor-not-allowed items-center gap-2 rounded-2xl border border-slate-200 px-4 py-3 text-sm font-bold text-slate-400"
          >
            <Filter size={16} />
            Filtros próximamente
          </button>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full min-w-[1100px] text-left">
            <thead className="bg-slate-50 text-xs uppercase tracking-wide text-slate-400">
              <tr>
                <th className="px-6 py-4">Contacto</th>
                <th className="px-6 py-4">Teléfono</th>
                <th className="px-6 py-4">Canal</th>
                <th className="px-6 py-4">Estado comercial</th>
                <th className="px-6 py-4">Responsable</th>
                <th className="px-6 py-4">Último mensaje / nota</th>
                <th className="px-6 py-4 text-right">Acción</th>
              </tr>
            </thead>

            <tbody className="divide-y divide-slate-100">
              {leads.map((lead) => (
                <tr key={lead.id} className="hover:bg-slate-50/70">
                  <td className="px-6 py-5">
                    <div className="flex items-center gap-3">
                      <div className="rounded-2xl bg-orange-50 p-3 text-orange-600">
                        <Phone size={18} />
                      </div>

                      <div>
                        <h2 className="text-sm font-black text-slate-900">
                          {lead.company || lead.contact || 'Sin nombre'}
                        </h2>

                        <p className="text-xs text-slate-400">
                          {lead.contact || 'Contacto comercial'}
                        </p>
                      </div>
                    </div>
                  </td>

                  <td className="px-6 py-5 text-sm font-bold text-slate-700">
                    {lead.phone || '-'}
                  </td>

                  <td className="px-6 py-5 text-sm text-slate-600">
                    {lead.channel || lead.source || '-'}
                  </td>

                  <td className="px-6 py-5">
                    <span
                      className={`rounded-full px-3 py-1 text-xs font-bold ${
                        statusStyles[lead.status] || 'bg-slate-100 text-slate-600'
                      }`}
                    >
                      {statusLabels[lead.status] || lead.status || 'Sin estado'}
                    </span>
                  </td>

                  <td className="px-6 py-5">
                    <div className="flex items-center gap-2 text-sm font-bold text-slate-700">
                      <BadgeCheck size={16} className="text-emerald-500" />
                      {lead.owner || 'Sin asignar'}
                    </div>
                  </td>

                  <td className="max-w-xs truncate px-6 py-5 text-sm text-slate-500">
                    {lead.messagePreview || 'Sin actividad registrada'}
                  </td>

                  <td className="px-6 py-5 text-right">
                    <button
                      onClick={() => openLead(lead)}
                      className="rounded-xl bg-slate-900 px-4 py-2 text-xs font-bold text-white hover:bg-slate-700"
                    >
                      Ver Contacto 360
                    </button>
                  </td>
                </tr>
              ))}

              {leads.length === 0 && (
                <tr>
                  <td
                    colSpan={7}
                    className="px-6 py-12 text-center text-sm text-slate-500"
                  >
                    Todavía no hay contactos registrados.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </section>

      <LeadDetailsModal
        lead={selectedLead}
        open={isModalOpen}
        onClose={closeLead}
      />

      <CreateLeadModal
        open={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        onCreate={handleCreateLead}
      />
    </div>
  );
}

function SummaryCard({ icon: Icon, title, value, description }) {
  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="rounded-2xl bg-orange-50 p-3 text-orange-600 w-fit">
        <Icon size={22} />
      </div>

      <h2 className="mt-4 text-sm font-bold text-slate-500">
        {title}
      </h2>

      <p className="mt-2 text-3xl font-black text-slate-900">
        {value}
      </p>

      <p className="mt-1 text-sm text-slate-500">
        {description}
      </p>
    </div>
  );
}