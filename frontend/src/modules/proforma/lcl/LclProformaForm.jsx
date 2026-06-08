import { CheckCircle2, Loader2, Ship } from 'lucide-react';
import useLclProforma from './useLclProforma';

function Field({ label, value, onChange, type = 'text', placeholder = '' }) {
  return (
    <div>
      <label className="mb-1 block text-[10px] font-black uppercase tracking-widest text-slate-400">
        {label}
      </label>
      <input
        type={type}
        value={value ?? ''}
        placeholder={placeholder}
        onChange={(e) => onChange(e.target.value)}
        className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm font-semibold text-slate-700 outline-none transition focus:border-orange-500 focus:ring-4 focus:ring-orange-500/10"
      />
    </div>
  );
}

function TextArea({ label, value, onChange, placeholder = '' }) {
  return (
    <div className="md:col-span-3">
      <label className="mb-1 block text-[10px] font-black uppercase tracking-widest text-slate-400">
        {label}
      </label>
      <textarea
        value={value ?? ''}
        placeholder={placeholder}
        onChange={(e) => onChange(e.target.value)}
        rows={3}
        className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm font-semibold text-slate-700 outline-none transition focus:border-orange-500 focus:ring-4 focus:ring-orange-500/10"
      />
    </div>
  );
}

export default function LclProformaForm({ onCreated }) {
  const { form, result, isSaving, error, updateField, resetForm, submit } =
    useLclProforma();

  async function handleSubmit() {
    const created = await submit();

    if (created?.id && onCreated) {
      onCreated(created);
    }
  }

  return (
    <div className="space-y-6">
      <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
        <div className="flex items-start gap-4">
          <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-orange-50 text-orange-600">
            <Ship size={28} />
          </div>

          <div>
            <p className="text-xs font-black uppercase tracking-[0.3em] text-slate-400">
              Nueva cotización
            </p>
            <h1 className="mt-1 text-2xl font-black text-slate-900">
              Proforma LCL
            </h1>
            <p className="mt-1 text-sm text-slate-500">
              Registro real de cotización marítima consolidada.
            </p>
          </div>
        </div>
      </section>

      <div className="grid gap-6 xl:grid-cols-[1fr_360px]">
        <div className="space-y-6">
          <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="mb-5 text-sm font-black uppercase tracking-widest text-slate-400">
              Datos comerciales
            </h2>

            <div className="grid gap-4 md:grid-cols-3">
              <Field label="Opportunity ID" value={form.opportunityId} onChange={(v) => updateField('opportunityId', v)} />
              <Field label="Customer ID" value={form.customerId} onChange={(v) => updateField('customerId', v)} />
              <Field label="Moneda" value={form.currency} onChange={(v) => updateField('currency', v)} />
              <Field label="Vendedor" value={form.sellerName} onChange={(v) => updateField('sellerName', v)} />
              <Field label="Cliente" value={form.customerName} onChange={(v) => updateField('customerName', v)} />
              <Field label="Teléfono" value={form.customerPhone} onChange={(v) => updateField('customerPhone', v)} />
              <Field label="Fecha emisión" type="date" value={form.issueDate} onChange={(v) => updateField('issueDate', v)} />
              <Field label="Validez días" type="number" value={form.validityDays} onChange={(v) => updateField('validityDays', v)} />
              <Field label="Dirección cliente" value={form.customerAddress} onChange={(v) => updateField('customerAddress', v)} />
            </div>
          </section>

          <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="mb-5 text-sm font-black uppercase tracking-widest text-slate-400">
              Operación logística
            </h2>

            <div className="grid gap-4 md:grid-cols-3">
              <Field label="País origen" value={form.originCountry} onChange={(v) => updateField('originCountry', v)} />
              <Field label="Ciudad origen" value={form.originCity} onChange={(v) => updateField('originCity', v)} />
              <Field label="Puerto origen" value={form.portOrigin} onChange={(v) => updateField('portOrigin', v)} />
              <Field label="País destino" value={form.destinationCountry} onChange={(v) => updateField('destinationCountry', v)} />
              <Field label="Ciudad destino" value={form.destinationCity} onChange={(v) => updateField('destinationCity', v)} />
              <Field label="Puerto destino" value={form.portDestination} onChange={(v) => updateField('portDestination', v)} />
              <Field label="Incoterm" value={form.incoterm} onChange={(v) => updateField('incoterm', v)} />
              <Field label="Transit time" value={form.transitTime} onChange={(v) => updateField('transitTime', v)} />
              <Field label="Naviera" value={form.carrierName} onChange={(v) => updateField('carrierName', v)} />
              <Field label="Agente" value={form.agentName} onChange={(v) => updateField('agentName', v)} />
              <Field label="Tipo de carga" value={form.cargoType} onChange={(v) => updateField('cargoType', v)} />
              <Field label="Bultos" type="number" value={form.packageCount} onChange={(v) => updateField('packageCount', v)} />
              <Field label="Peso kg" type="number" value={form.grossWeightKg} onChange={(v) => updateField('grossWeightKg', v)} />
              <Field label="Volumen CBM" type="number" value={form.volumeCbm} onChange={(v) => updateField('volumeCbm', v)} />
              <TextArea label="Descripción de carga" value={form.cargoDescription} onChange={(v) => updateField('cargoDescription', v)} />
            </div>
          </section>

          <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="mb-5 text-sm font-black uppercase tracking-widest text-slate-400">
              Costos y margen
            </h2>

            <div className="grid gap-4 md:grid-cols-3">
              <Field label="Freight rate" type="number" value={form.freightRate} onChange={(v) => updateField('freightRate', v)} />
              <Field label="Origin charges" type="number" value={form.originCharges} onChange={(v) => updateField('originCharges', v)} />
              <Field label="Destination charges" type="number" value={form.destinationCharges} onChange={(v) => updateField('destinationCharges', v)} />
              <Field label="Handling" type="number" value={form.handlingCharges} onChange={(v) => updateField('handlingCharges', v)} />
              <Field label="Documentation" type="number" value={form.documentationCharges} onChange={(v) => updateField('documentationCharges', v)} />
              <Field label="Customs" type="number" value={form.customsCharges} onChange={(v) => updateField('customsCharges', v)} />
              <Field label="Insurance" type="number" value={form.insuranceCharges} onChange={(v) => updateField('insuranceCharges', v)} />
              <Field label="Other" type="number" value={form.otherCharges} onChange={(v) => updateField('otherCharges', v)} />
              <Field label="Commission" type="number" value={form.commissionAmount} onChange={(v) => updateField('commissionAmount', v)} />
              <Field label="Margin" type="number" value={form.marginAmount} onChange={(v) => updateField('marginAmount', v)} />
              <TextArea label="Términos comerciales" value={form.commercialTerms} onChange={(v) => updateField('commercialTerms', v)} />
            </div>
          </section>
        </div>

        <aside className="h-fit rounded-3xl bg-slate-950 p-6 text-white shadow-xl">
          <h3 className="text-xl font-black">Resumen LCL</h3>
          <p className="mt-2 text-sm text-slate-400">
            Crea una proforma borrador con cálculo automático.
          </p>

          {error && (
            <div className="mt-5 rounded-2xl bg-rose-500/20 p-4 text-sm font-bold text-rose-100">
              {error}
            </div>
          )}

          {result && (
            <div className="mt-5 rounded-2xl bg-emerald-500/20 p-4 text-sm text-emerald-100">
              <div className="flex items-center gap-2 font-bold">
                <CheckCircle2 size={18} />
                Proforma creada
              </div>
              <div className="mt-3 text-xs text-emerald-100/80">
                {result.id}
              </div>
              <div className="mt-3 text-2xl font-black">
                {result.currency} {Number(result.total || 0).toLocaleString()}
              </div>
              <div className="mt-1 text-xs">
                Utilidad estimada: {result.currency}{' '}
                {Number(result.estimatedProfit || 0).toLocaleString()}
              </div>
            </div>
          )}

          <button
            onClick={handleSubmit}
            disabled={isSaving}
            className="mt-6 flex w-full items-center justify-center gap-2 rounded-2xl bg-orange-500 py-4 text-sm font-black uppercase tracking-widest text-white shadow-lg shadow-orange-500/20 hover:bg-orange-600 disabled:opacity-60"
          >
            {isSaving ? (
              <>
                <Loader2 size={18} className="animate-spin" />
                Guardando...
              </>
            ) : (
              'Crear proforma'
            )}
          </button>

          <button
            onClick={resetForm}
            disabled={isSaving}
            className="mt-3 w-full rounded-2xl border border-white/10 py-3 text-sm font-bold text-slate-300 hover:bg-white/5"
          >
            Limpiar formulario
          </button>
        </aside>
      </div>
    </div>
  );
}