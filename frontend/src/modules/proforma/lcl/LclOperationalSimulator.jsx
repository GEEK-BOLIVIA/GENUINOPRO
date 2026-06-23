import { useEffect, useState } from 'react';
import { Calculator, Loader2 } from 'lucide-react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import {
  calculateOperationalLcl,
  createOperationalLclProforma,
} from '../../../services/lclService';

import {
  getLeads,
  getLeadById,
} from '../../../services/leadsService';
import { getOpportunity } from '../../../services/opportunityApi';

const initialForm = {
  customerId: '',
  customerName: '',
  advisorName: '',
  shippingAddress: '',
  customerPhone: '',
  productName: '',
  quantity: 1,
  merchandiseValueUsd: 0,
  weightKg: 0,
  warehouseShippingUsd: 0,
  gaPercentage: 10,
  ivaPercentage: 14.94,
  miscellaneousExpensesBs: 0,
  cbm: 0,
  exchangeRate: 10,
  supplierName: '',
  supplierPhone: '',
  iceAmountBs: 0,
  needsHbl: false,
  customerPaysUsdCash: false,
};

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
        className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm font-bold text-slate-700 outline-none transition focus:border-orange-500 focus:ring-4 focus:ring-orange-500/10"
      />
    </div>
  );
}

function SectionCard({ eyebrow, title, description, children }) {
  return (
    <div className="rounded-[28px] border border-slate-100 bg-slate-50/80 p-5">
      <div className="mb-5">
        <p className="text-[10px] font-black uppercase tracking-[0.28em] text-orange-500">
          {eyebrow}
        </p>
        <h3 className="mt-1 text-lg font-black text-slate-950">
          {title}
        </h3>
        {description && (
          <p className="mt-1 text-sm text-slate-500">
            {description}
          </p>
        )}
      </div>
      {children}
    </div>
  );
}

export default function LclOperationalSimulator() {

  const navigate = useNavigate();  
  const [searchParams] = useSearchParams();
  const leadId = searchParams.get('leadId');
  const opportunityId = searchParams.get('opportunityId');

  const [form, setForm] = useState({
    ...initialForm,
    leadId: leadId || '',
    opportunityId: opportunityId || '',
  });
  const [result, setResult] = useState(null);
  const [isCalculating, setIsCalculating] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState('');
  

  function update(field, value) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }
    function validateForm() {
    if (!form.customerName.trim()) {
        return 'El nombre del cliente es obligatorio';
    }

    if (!form.productName.trim()) {
        return 'El producto es obligatorio';
    }

    if (Number(form.quantity || 0) <= 0) {
        return 'La cantidad debe ser mayor a cero';
    }

    if (Number(form.exchangeRate || 0) <= 0) {
        return 'El tipo de cambio debe ser mayor a cero';
    }

    return '';
    }

    useEffect(() => {
    async function loadContext() {

        try {

          if (leadId) {
            const lead = await getLeadById(leadId);

            setForm((prev) => ({
              ...prev,
              leadId: lead.id,
              customerId: lead.id,
              opportunityId,

              customerName:
                lead.company ||
                lead.contact ||
                lead.fullName ||
                '',

              customerPhone:
                lead.phone ||
                '',

              advisorName:
                lead.owner ||
                lead.assignedSellerName ||
                '',

              productName:
                lead.messagePreview ||
                prev.productName ||
                '',
            }));

            return;
          }

        if (leadId) {

            const leads = await getLeads();

            const lead = leads.find(
            (item) => item.id === leadId
            );

            if (!lead) return;

            setForm((prev) => ({
            ...prev,
            leadId: lead.id,
            customerId: lead.id,
            customerName: lead.company || lead.contact || '',
            customerPhone: lead.phone || '',
            advisorName: lead.owner || '',
            }));
        }

        } catch (error) {
        console.error(
            'Error cargando contexto de proforma',
            error
        );
        }
    }

    loadContext();

    }, [leadId, opportunityId]);
  
  async function calculate() {
    try {
      setIsCalculating(true);
      setError('');

        const validationError = validateForm();

        if (validationError) {
        setError(validationError);
        return;
        }      

        if (!form.customerName.trim()) {
        setError('El nombre del cliente es obligatorio');
        return;
        }

        if (!form.productName.trim()) {
        setError('El producto es obligatorio');
        return;
        }

        if (Number(form.quantity || 0) <= 0) {
        setError('La cantidad debe ser mayor a cero');
        return;
        }

        if (Number(form.exchangeRate || 0) <= 0) {
        setError('El tipo de cambio debe ser mayor a cero');
        return;
        }

      const payload = {
        ...form,
        quantity: Number(form.quantity || 0),
        merchandiseValueUsd: Number(form.merchandiseValueUsd || 0),
        weightKg: Number(form.weightKg || 0),
        warehouseShippingUsd: Number(form.warehouseShippingUsd || 0),
        gaPercentage: Number(form.gaPercentage || 0),
        ivaPercentage: Number(form.ivaPercentage || 0),
        miscellaneousExpensesBs: Number(form.miscellaneousExpensesBs || 0),
        cbm: Number(form.cbm || 0),
        exchangeRate: Number(form.exchangeRate || 0),
        iceAmountBs: Number(form.iceAmountBs || 0),
      };

      const response = await calculateOperationalLcl(payload);
      setResult(response);
    } catch (err) {
      console.error(err);
      setError(err.message || 'No se pudo calcular la proforma');
    } finally {
      setIsCalculating(false);
    }
  }

    async function saveAsProforma() {
    try {
        setIsSaving(true);
        setError('');

        const validationError = validateForm();

        if (validationError) {
        setError(validationError);
        return;
        }  

        if (!form.customerName.trim()) {
        setError('El nombre del cliente es obligatorio');
        return;
        }

        if (!form.productName.trim()) {
        setError('El producto es obligatorio');
        return;
        }

        if (Number(form.quantity || 0) <= 0) {
        setError('La cantidad debe ser mayor a cero');
        return;
        }

        if (Number(form.exchangeRate || 0) <= 0) {
        setError('El tipo de cambio debe ser mayor a cero');
        return;
        }

        const payload = {
        ...form,
        quantity: Number(form.quantity || 0),
        merchandiseValueUsd: Number(form.merchandiseValueUsd || 0),
        weightKg: Number(form.weightKg || 0),
        warehouseShippingUsd: Number(form.warehouseShippingUsd || 0),
        gaPercentage: Number(form.gaPercentage || 0),
        ivaPercentage: Number(form.ivaPercentage || 0),
        miscellaneousExpensesBs: Number(form.miscellaneousExpensesBs || 0),
        cbm: Number(form.cbm || 0),
        exchangeRate: Number(form.exchangeRate || 0),
        iceAmountBs: Number(form.iceAmountBs || 0),
        };

        const created = await createOperationalLclProforma(payload);

        console.log('PROFORMA OPERATIVA CREADA', created);

        navigate(`/lcl/${created.id}`);
    } catch (err) {
        console.error(err);
        setError(err.message || 'No se pudo guardar la proforma');
    } finally {
        setIsSaving(false);
    }
    }

  return (
    <section className="mt-6 rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="mb-6 flex items-start justify-between gap-4">
        <div>
          <p className="text-xs font-black uppercase tracking-[0.3em] text-orange-500">
            Simulador de Importación LCL
          </p>
          <h2 className="mt-1 text-2xl font-black text-slate-900">
            Cotización rápida antes de proforma formal
          </h2>
          <p className="mt-1 max-w-3xl text-sm text-slate-500">
            Modifica cliente, mercancía, peso, CBM y costos para consultar escenarios en tiempo real. Cuando el resultado sea aprobado por el cliente, guarda la simulación como proforma.
          </p>
        </div>

        <button
          onClick={calculate}
          disabled={isCalculating}
          className="flex shrink-0 items-center gap-2 rounded-2xl bg-slate-950 px-6 py-4 text-sm font-black text-white hover:bg-slate-800 disabled:opacity-60"
        >
          {isCalculating ? (
            <>
              <Loader2 size={18} className="animate-spin" />
              Calculando...
            </>
          ) : (
            <>
              <Calculator size={18} />
              Calcular
            </>
          )}
        </button>
      </div>

      {error && (
        <div className="mb-5 rounded-2xl bg-rose-50 p-4 text-sm font-bold text-rose-700">
          {error}
        </div>
      )}

      <div className="grid gap-6 xl:grid-cols-[1fr_420px]">
        <div className="space-y-5">
          <SectionCard
            eyebrow="Paso 1"
            title="Cliente y atención comercial"
            description="Datos mínimos para identificar al contacto y preparar la cotización."
          >
            <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
              <Field label="Cliente" value={form.customerName} onChange={(v) => update('customerName', v)} placeholder="Ej. Importbol" />
              <Field label="Teléfono" value={form.customerPhone} onChange={(v) => update('customerPhone', v)} placeholder="+591..." />
              <Field label="Asesor comercial" value={form.advisorName} onChange={(v) => update('advisorName', v)} />
              <Field label="Dirección / ciudad destino" value={form.shippingAddress} onChange={(v) => update('shippingAddress', v)} placeholder="Ej. La Paz" />
            </div>
          </SectionCard>

          <SectionCard
            eyebrow="Paso 2"
            title="Proveedor"
            description="Información referencial del proveedor para futuras validaciones y seguimiento."
          >
            <div className="grid gap-4 md:grid-cols-2">
              <Field label="Proveedor" value={form.supplierName} onChange={(v) => update('supplierName', v)} placeholder="Nombre del proveedor" />
              <Field label="Teléfono proveedor" value={form.supplierPhone} onChange={(v) => update('supplierPhone', v)} />
            </div>
          </SectionCard>

          <SectionCard
            eyebrow="Paso 3"
            title="Mercancía"
            description="Datos principales para calcular volumen, peso y valor base de la operación."
          >
            <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-5">
              <div className="xl:col-span-2">
                <Field label="Producto" value={form.productName} onChange={(v) => update('productName', v)} placeholder="Ej. Batería VW89" />
              </div>
              <Field label="Cantidad" type="number" value={form.quantity} onChange={(v) => update('quantity', v)} />
              <Field label="Valor FOB / mercadería USD" type="number" value={form.merchandiseValueUsd} onChange={(v) => update('merchandiseValueUsd', v)} />
              <Field label="Peso kg" type="number" value={form.weightKg} onChange={(v) => update('weightKg', v)} />
              <Field label="CBM" type="number" value={form.cbm} onChange={(v) => update('cbm', v)} />
            </div>
          </SectionCard>

          <SectionCard
            eyebrow="Paso 4"
            title="Costos, tributos y tipo de cambio"
            description="Variables ajustables para simular distintos escenarios antes de generar la proforma."
          >
            <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
              <Field label="Envío almacén USD" type="number" value={form.warehouseShippingUsd} onChange={(v) => update('warehouseShippingUsd', v)} />
              <Field label="GA %" type="number" value={form.gaPercentage} onChange={(v) => update('gaPercentage', v)} />
              <Field label="IVA %" type="number" value={form.ivaPercentage} onChange={(v) => update('ivaPercentage', v)} />
              <Field label="Tipo cambio" type="number" value={form.exchangeRate} onChange={(v) => update('exchangeRate', v)} />
              <Field label="ICE Bs" type="number" value={form.iceAmountBs} onChange={(v) => update('iceAmountBs', v)} />
              <Field label="Gastos varios Bs" type="number" value={form.miscellaneousExpensesBs} onChange={(v) => update('miscellaneousExpensesBs', v)} />
            </div>
          </SectionCard>
        </div>

        <aside className="rounded-3xl bg-slate-950 p-6 text-white">
          <h3 className="text-xl font-black">Resultado de simulación</h3>
          <p className="mt-1 text-sm text-slate-400">
            Consulta rápida antes de generar la proforma formal.
          </p>

          {!result ? (
            <div className="mt-8 rounded-2xl bg-white/5 p-5 text-sm text-slate-400">
              Completa los datos y presiona calcular para ver la simulación.
            </div>
          ) : (
            <div className="mt-6 space-y-4">
              <div className="rounded-2xl bg-white/5 p-4">
                <p className="text-xs text-slate-400">Total USD</p>
                <p className="text-3xl font-black">USD {Number(result.totalUsd || 0).toLocaleString()}</p>
              </div>

              <div className="rounded-2xl bg-white/5 p-4">
                <p className="text-xs text-slate-400">Conversión USD a Bs</p>
                <p className="text-2xl font-black">Bs {Number(result.usdConvertedToBs || 0).toLocaleString()}</p>
              </div>

              <div className="rounded-2xl bg-orange-500 p-4">
                <p className="text-xs text-orange-100">Saldo contraentrega</p>
                <p className="text-3xl font-black">Bs {Number(result.totalBs || 0).toLocaleString()}</p>
              </div>

              <div className="rounded-2xl bg-emerald-500 p-4">
                <p className="text-xs text-emerald-100">Total general</p>
                <p className="text-3xl font-black">Bs {Number(result.grandTotalBs || 0).toLocaleString()}</p>
              </div>

              <div className="rounded-2xl bg-white p-4 text-slate-950">
                <p className="text-xs text-slate-500">Precio unitario</p>
                <p className="text-3xl font-black">Bs {Number(result.unitPriceBs || 0).toLocaleString()}</p>
              </div>

              <div className="rounded-2xl border border-white/10 p-4">
                <p className="mb-3 text-xs font-black uppercase tracking-widest text-slate-400">
                  Pagos
                </p>
                <div className="rounded-2xl border border-white/10 p-4">
                <p className="mb-3 text-xs font-black uppercase tracking-widest text-slate-400">
                    Líneas generadas
                </p>

                <button
                type="button"
                onClick={saveAsProforma}
                disabled={!result || isSaving}
                className="w-full rounded-2xl bg-white px-6 py-4 text-sm font-black text-slate-950 hover:bg-slate-100 disabled:cursor-not-allowed disabled:opacity-50"
                >
                {isSaving ? 'Guardando...' : 'Generar proforma formal'}
                </button>

                <div className="space-y-2">
                    {(result.generatedLines || []).map((line) => (
                    <div
                        key={line.code}
                        className="flex items-center justify-between gap-3 rounded-xl bg-white/5 px-3 py-2 text-sm"
                    >
                        <div>
                        <p className="font-black text-white">{line.code}</p>
                        <p className="text-xs text-slate-400">{line.description}</p>
                        </div>

                        <b>
                        {line.currency === 'BOB' ? 'Bs' : 'USD'}{' '}
                        {Number(line.amount || 0).toLocaleString()}
                        </b>
                    </div>
                    ))}
                </div>
                </div>
                <div className="space-y-2 text-sm">
                  <div className="flex justify-between"><span>Pago 1</span><b>USD {result.firstPaymentUsd}</b></div>
                  <div className="flex justify-between"><span>Pago 2</span><b>USD {result.secondPaymentUsd}</b></div>
                  <div className="flex justify-between"><span>Pago 3</span><b>Bs {result.thirdPaymentBs}</b></div>
                </div>
              </div>
            </div>
          )}
        </aside>
      </div>
    </section>
  );
}