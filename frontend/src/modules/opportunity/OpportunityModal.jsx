import {
  Building2,
  CalendarClock,
  X,
  Plus,
  Phone,
  User2,
 
} from 'lucide-react';
import InfoRow from '../../components/ui/InfoRow';
import EmptyState from '../../components/ui/EmptyState';
import ActivityTimeline from './ActivityTimeline';
import ActivityForm from './ActivityForm';
import {
  formatCurrency,

} from '../../utils/crm';

import ProformaList from '../proforma/ProformaList';

export default function OpportunityModal({
  selectedLead,
  setSelectedLead,
  proformas,
  activities,
  isLoadingTimeline,
  showProformaModal,
  setShowProformaModal,
  handleProformaDecision,
  activityForm,
  setActivityForm,
  activityError,
  isSavingActivity,
  saveActivity,
}) {
  if (!selectedLead) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/60 p-4 backdrop-blur-sm">
      <div className="relative flex h-[92vh] w-full max-w-5xl flex-col overflow-hidden rounded-[40px] bg-white shadow-2xl">
        <div className="flex shrink-0 items-center justify-between border-b border-slate-100 bg-slate-50/50 px-10 py-8">
          <div>
            <div className="text-[10px] font-black uppercase tracking-[0.25em] text-indigo-500">
              Expediente CRM
            </div>
            <h3 className="mt-1 text-3xl font-black text-slate-900">{selectedLead.cliente}</h3>
          </div>
          <button
            onClick={() => setSelectedLead(null)}
            className="rounded-2xl border border-slate-200 p-3 transition-all hover:text-rose-500"
          >
            <X size={24} />
          </button>
        </div>

        <div className="flex-1 overflow-y-auto">
          <div className="grid min-h-full lg:grid-cols-[380px_1fr]">
            <div className="space-y-8 border-r border-slate-100 bg-slate-50/30 p-10">
              <div className="space-y-4">
                <InfoRow icon={Building2} label="Cliente" value={selectedLead.cliente} />
                <InfoRow icon={User2} label="Contacto" value={selectedLead.contacto} />
                <InfoRow icon={Phone} label="Teléfono" value={selectedLead.telefono} />
                <InfoRow icon={CalendarClock} label="Fase" value={selectedLead.etapa} />
              </div>

              <div className="rounded-[32px] bg-indigo-600 p-8 text-white shadow-xl">
                <p className="text-[10px] font-bold uppercase tracking-widest text-indigo-200">
                  Monto estimado
                </p>
                <h4 className="mt-2 text-4xl font-black">{formatCurrency(selectedLead.monto)}</h4>
              </div>

              <div className="pt-6">
                <div className="mb-4 flex items-center justify-between">
                  <h5 className="text-[11px] font-black uppercase tracking-widest text-slate-400">
                    Proformas
                  </h5>
                  <button
                    onClick={() => setShowProformaModal(true)}
                    className="rounded-lg bg-slate-900 p-1.5 text-white transition-colors hover:bg-black"
                  >
                    <Plus size={14} />
                  </button>
                </div>

                <ProformaList
                proformas={proformas}
                onDecision={handleProformaDecision}
                />
              </div>
            </div>

            <div className="bg-white p-10">
              <div className="mb-3 flex items-center justify-between">
                <div>
                  <h4 className="text-[22px] font-semibold tracking-tight text-slate-900">Actividades</h4>
                  <p className="text-xs uppercase tracking-[0.18em] text-slate-400">
                    Historial comercial del lead
                  </p>
                </div>
                <span className="inline-flex items-center rounded-full bg-slate-100 px-3 py-1.5 text-xs font-medium text-slate-500">
                  {activities.length} registro{activities.length === 1 ? '' : 's'}
                </span>
              </div>

              <ActivityTimeline
                activities={activities}
                isLoadingTimeline={isLoadingTimeline}
                />

              <ActivityForm
                selectedLead={selectedLead}
                activityForm={activityForm}
                setActivityForm={setActivityForm}
                activityError={activityError}
                isSavingActivity={isSavingActivity}
                saveActivity={saveActivity}
                />
            </div>
          </div>
        </div>     
      </div>
    </div>
  );
}