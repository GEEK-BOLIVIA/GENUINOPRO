import EmptyState from '../../components/ui/EmptyState';

export default function ProformaList({ proformas, onDecision }) {
  return (
    <div className="space-y-3">
      {proformas.map((p) => (
        <div key={p.id} className="rounded-2xl border border-slate-100 bg-white p-4 shadow-sm">
          <div className="flex items-start justify-between">
            <div>
              <p className="text-[10px] font-black text-slate-400">
                #{p.series}-{p.number}
              </p>
              <p className="text-sm font-bold text-slate-800">
                {p.currency} {new Intl.NumberFormat().format(p.total)}
              </p>
            </div>

            <span
              className={`rounded-md px-2 py-0.5 text-[8px] font-black uppercase ${
                p.status === 'APPROVED'
                  ? 'bg-emerald-50 text-emerald-600'
                  : 'bg-amber-50 text-amber-600'
              }`}
            >
              {p.status}
            </span>
          </div>

          {p.status === 'IN_REVIEW' && (
            <div className="mt-3 flex gap-2">
              <button
                onClick={() => onDecision(p.id, 'approve', 'Validado')}
                className="flex-1 rounded-lg bg-emerald-600 py-1.5 text-[9px] font-bold text-white hover:bg-emerald-700"
              >
                APROBAR
              </button>
              <button
                onClick={() => onDecision(p.id, 'reject', 'No cumple')}
                className="flex-1 rounded-lg bg-rose-50 py-1.5 text-[9px] font-bold text-rose-600 hover:bg-rose-100"
              >
                RECHAZAR
              </button>
            </div>
          )}
        </div>
      ))}

      {!proformas.length && <EmptyState text="Todavía no hay proformas asociadas." />}
    </div>
  );
}