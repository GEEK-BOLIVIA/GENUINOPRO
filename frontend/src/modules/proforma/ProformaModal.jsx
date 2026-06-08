import { Plus, X, Loader2 } from 'lucide-react';

export default function ProformaModal({
  show,
  onClose,
  onCreate,
  isCreating,
  proformaForm,
  setProformaForm,
}) {
  if (!show) return null;

  return (
    <div className="fixed inset-0 z-[70] flex items-center justify-center bg-slate-900/80 p-4 backdrop-blur-md">
      <div className="w-full max-w-md overflow-hidden rounded-[40px] border border-slate-200 bg-white shadow-2xl">
        <div className="flex items-center justify-between border-b border-slate-50 bg-slate-50/30 p-8">
          <div>
            <h4 className="text-xl font-black tracking-tight text-slate-900">Nueva Proforma</h4>
            <p className="mt-1 text-[10px] font-black uppercase tracking-widest text-indigo-500">
              Borrador
            </p>
          </div>

          <button
            onClick={onClose}
            className="text-slate-400 transition-colors hover:text-rose-500"
          >
            <X size={20} />
          </button>
        </div>

        <div className="space-y-8 p-10">
          <div className="space-y-3">
            <label className="ml-1 text-[10px] font-black uppercase tracking-widest text-slate-400">
              Moneda
            </label>

            <div className="grid grid-cols-2 gap-3">
              {['USD', 'BOB'].map((curr) => (
                <button
                  key={curr}
                  type="button"
                  onClick={() => setProformaForm((prev) => ({ ...prev, currency: curr }))}
                  className={`rounded-2xl border-2 py-3 text-sm font-bold transition-all ${
                    proformaForm.currency === curr
                      ? 'border-indigo-600 bg-indigo-50 text-indigo-600'
                      : 'border-slate-100 text-slate-400'
                  }`}
                >
                  {curr}
                </button>
              ))}
            </div>
          </div>

          <div className="space-y-3">
            <label className="ml-1 text-[10px] font-black uppercase tracking-widest text-slate-400">
              Valor total
            </label>

            <div className="group relative">
              <span className="absolute left-5 top-1/2 -translate-y-1/2 text-xl font-black text-slate-300 group-focus-within:text-indigo-500">
                {proformaForm.currency === 'USD' ? '$' : 'Bs'}
              </span>

              <input
                type="number"
                min="0"
                step="0.01"
                value={proformaForm.amount}
                onChange={(e) =>
                  setProformaForm((prev) => ({ ...prev, amount: e.target.value }))
                }
                className="w-full rounded-[24px] border-2 border-slate-100 bg-slate-50 py-5 pl-12 pr-6 text-2xl font-black outline-none transition-all focus:border-indigo-600 focus:bg-white"
              />
            </div>
          </div>

          <button
            type="button"
            onClick={onCreate}
            disabled={isCreating || !proformaForm.amount || Number(proformaForm.amount) <= 0}
            className="w-full rounded-[24px] bg-slate-900 py-5 text-xs font-black uppercase tracking-[0.25em] text-white transition-all hover:bg-black disabled:opacity-30"
          >
            {isCreating ? (
              <>
                <Loader2 size={18} className="mr-2 inline-block animate-spin" />
                Generando correlativo...
              </>
            ) : (
              <>
                <Plus size={18} className="mr-2 inline-block" />
                Crear proforma
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
}