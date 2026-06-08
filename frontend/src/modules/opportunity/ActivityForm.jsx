import { Loader2 } from 'lucide-react';
import { getActivityTypeOptions } from '../../utils/crm';

export default function ActivityForm({
  selectedLead,
  activityForm,
  setActivityForm,
  activityError,
  isSavingActivity,
  saveActivity,
}) {
  return (
    <div className="mt-4 rounded-[26px] border border-slate-200 bg-white p-5 shadow-sm">
      <div className="mb-4 flex items-start justify-between gap-3">
        <div>
          <h5 className="text-lg font-semibold tracking-tight text-slate-900">
            Registrar actividad manual
          </h5>
          <p className="mt-1 text-xs uppercase tracking-[0.16em] text-slate-400">
            Seguimiento comercial del lead
          </p>
        </div>

        <span className="inline-flex items-center rounded-full bg-slate-100 px-3 py-1.5 text-[11px] font-medium text-slate-500">
          {selectedLead.id}
        </span>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        <div className="sm:col-span-2">
          <label className="mb-2 block text-xs font-semibold uppercase tracking-[0.14em] text-slate-400">
            Tipo
          </label>
          <div className="grid grid-cols-2 gap-2 sm:grid-cols-3">
            {getActivityTypeOptions().map((option) => (
              <button
                key={option.value}
                type="button"
                onClick={() => setActivityForm((prev) => ({ ...prev, type: option.value }))}
                className={`rounded-2xl px-3 py-3 text-sm font-medium transition ${
                  activityForm.type === option.value
                    ? 'bg-slate-900 text-white shadow-sm'
                    : 'border border-slate-200 bg-slate-50 text-slate-600 hover:bg-white'
                }`}
              >
                {option.label}
              </button>
            ))}
          </div>
        </div>

        <div className="sm:col-span-2">
          <label className="mb-2 block text-xs font-semibold uppercase tracking-[0.14em] text-slate-400">
            Fecha y hora
          </label>
          <input
            type="datetime-local"
            value={activityForm.activityDate}
            onChange={(e) => setActivityForm((prev) => ({ ...prev, activityDate: e.target.value }))}
            className="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 outline-none transition focus:border-slate-300 focus:bg-white"
          />
        </div>

        <div className="sm:col-span-2">
          <label className="mb-2 block text-xs font-semibold uppercase tracking-[0.14em] text-slate-400">
            Título <span className="text-rose-500">*</span>
          </label>
          <input
            type="text"
            value={activityForm.title}
            onChange={(e) => setActivityForm((prev) => ({ ...prev, title: e.target.value }))}
            placeholder="Ej. Llamada de seguimiento"
            className="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 outline-none transition placeholder:text-slate-400 focus:border-slate-300 focus:bg-white"
          />
        </div>

        <div className="sm:col-span-2">
          <label className="mb-2 block text-xs font-semibold uppercase tracking-[0.14em] text-slate-400">
            Descripción
          </label>
          <textarea
            value={activityForm.description}
            onChange={(e) => setActivityForm((prev) => ({ ...prev, description: e.target.value }))}
            placeholder="Detalle de la gestión realizada"
            rows={4}
            className="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 outline-none transition placeholder:text-slate-400 focus:border-slate-300 focus:bg-white"
          />
        </div>
      </div>

      {activityError ? (
        <div className="mt-4 rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
          {activityError}
        </div>
      ) : null}

      <div className="mt-5 flex items-center justify-between border-t border-slate-200 pt-4">
        <div className="text-xs text-slate-400">
          La actividad quedará registrada en el historial del lead.
        </div>

        <button
          type="button"
          onClick={saveActivity}
          disabled={isSavingActivity}
          className="inline-flex items-center justify-center gap-2 rounded-2xl bg-slate-900 px-5 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {isSavingActivity ? (
            <>
              <Loader2 size={16} className="animate-spin" />
              Guardando...
            </>
          ) : (
            'Guardar actividad'
          )}
        </button>
      </div>
    </div>
  );
}