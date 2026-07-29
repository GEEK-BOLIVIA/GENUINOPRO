import { useEffect, useState } from 'react';
import { Calculator, Save } from 'lucide-react';
import { useNavigate, useSearchParams } from 'react-router-dom';

import {
  createFclProforma,
  calculateFclProforma,
  getFclProformaById,
  updateFclProforma,
} from '../../../services/fclService';

import { getLeadById } from '../../../services/leadsService';


const initialForm = {
  customerName: '',
  customerPhone: '',
  sellerName: '',
  destinationCity: '',
  
  supplierName: '',
  supplierPhone: '',
  originCity: '',
  originPort: '',

  containerType: 'FCL20',
  containerCount: '1',
  product: '',

  fobUsd: '',

  // Tipo de cambio comercial
  exchangeRate: '6.96',

  // Tipo de cambio exclusivo para impuestos
  taxExchangeRate: '',

  maritimeFreightUsd: '',
  containerReleaseUsd: '',

  inlandFreightBob: '',
  miscellaneousExpensesBob: '',

  calculationRuleVersion: 'FCL_GOV_2026_07',

  gaPercent: '10',
  ivaPercent: '14.94',
  icePercent: '0',

  paymentMethod: 'ALIBABA',
  importerNitType: 'NIT_CLIENTE',

  totalWeightTn: '',
  fobPaymentCount: '2',
  customerPaysInUsd: false,
  customerPaysSupplier: false,

  originFreightUsd: '',
  alboBob: '',
  adaBob: '',
  commissionUsd: '',

  taxExchangeRate: '',

  containerReleaseUsd: '',

  miscellaneousExpensesBob: '',

  customerAddress: '',

  calculationRuleVersion: 'FCL_GOV_2026_07',
};

export default function FclOperationalSimulator({
  mode = 'new',
  proformaId = null,
}) {
  const [form, setForm] = useState(initialForm);
  const [saving, setSaving] = useState(false);
  const [calculating, setCalculating] = useState(false);
  const [calculation, setCalculation] = useState(null);
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    async function loadProforma() {
      if (mode !== 'edit' || !proformaId) return;

      try {
        const item = await getFclProformaById(proformaId);

        setForm({

          customerId: item.customerId || '',
          opportunityId: item.opportunityId || '',
          code: item.code || '',
          customerName: item.customerName || '',
          customerPhone: item.customerPhone || '',
          sellerName: item.sellerName || '',
          destinationCity: item.destinationCity || '',
          supplierName: item.supplierName || '',
          supplierPhone: item.supplierPhone || '',
          originCity: item.originCity || '',
          originPort: item.originPort || '',
          containerType: item.containerType || 'FCL20',
          containerCount: item.containerCount || '1',
          product: item.product || '',
          fobUsd: item.fobUsd || '',
          exchangeRate: item.exchangeRate || '6.96',
          taxExchangeRate: item.taxExchangeRate ?? '',
          maritimeFreightUsd: item.maritimeFreightUsd || '',
          containerReleaseUsd: item.containerReleaseUsd ?? '',
          inlandFreightBob: item.inlandFreightBob || '',
          miscellaneousExpensesBob: item.miscellaneousExpensesBob ?? '',
          gaPercent: item.gaPercent || '10',
          ivaPercent: item.ivaPercent || '14.94',
          icePercent: item.icePercent || '0',
          paymentMethod: item.paymentMethod || 'ALIBABA',
          importerNitType: item.importerNitType || 'NIT_CLIENTE',
          totalWeightTn: item.totalWeightTn || '',
          fobPaymentCount: item.fobPaymentCount || '2',
          customerPaysInUsd: item.customerPaysInUsd || false,
          customerPaysSupplier: item.customerPaysSupplier || false,

          taxExchangeRate:
              item.taxExchangeRate || '',

          containerReleaseUsd:
              item.containerReleaseUsd || '',

          miscellaneousExpensesBob:
              item.miscellaneousExpensesBob || '',

          customerAddress:
              item.customerAddress || '',

          calculationRuleVersion:
              item.calculationRuleVersion || 'FCL_GOV_2026_07',

          calculationRuleVersion:
              item.calculationRuleVersion ||
              'FCL_GOV_2026_07',
        });

        setCalculation(item);
      } catch (error) {
        console.error(error);
      }
    }

    loadProforma();
  }, [mode, proformaId]);


    useEffect(() => {
    async function loadLeadData() {
      if (mode !== 'new') return;

      const leadId = searchParams.get('leadId');

      if (!leadId) return;

      try {
        const lead = await getLeadById(leadId);

        setForm((prev) => ({
          ...prev,
          customerId: lead.id || leadId,
          customerName:
            lead.company ||
            lead.fullName ||
            lead.contact ||
            prev.customerName ||
            '',
          customerPhone:
            lead.phone ||
            lead.customerPhone ||
            prev.customerPhone ||
            '',
          sellerName:
            lead.assignedSellerName ||
            lead.assignedSellerId ||
            lead.ownerUserName ||
            lead.ownerUserId ||
            lead.owner ||
            lead.sellerName ||
            lead.advisorName ||
            prev.sellerName ||
            '',
          product:
            lead.messagePreview ||
            lead.requirement ||
            prev.product ||
            '',
          destinationCity:
            lead.destinationCity ||
            lead.city ||
            prev.destinationCity ||
            '',
        }));
      } catch (error) {
        console.error('No se pudo cargar datos del lead para FCL', error);
      }
    }

    loadLeadData();
  }, [mode, searchParams]);


  function update(field, value) {
    setForm((prev) => ({ ...prev, [field]: value }));
    setCalculation(null);
  }

  function numberOrNull(value) {
    return value === '' || value === null || value === undefined
      ? null
      : Number(value);
  }

  function resolveOpportunityId() {
    const opportunityId = searchParams.get('opportunityId');

    if (opportunityId && opportunityId.startsWith('opp_')) {
      return opportunityId;
    }

    return null;
  }

  function buildPayload(source = form) {
    const leadId = searchParams.get('leadId');
    const opportunityId = resolveOpportunityId();

    return {
      ...source,
      customerId: leadId || source.customerId || null,
      opportunityId: opportunityId || source.opportunityId || null,
      containerCount: Number(source.containerCount || 1),
      fobUsd: numberOrNull(source.fobUsd),
      exchangeRate: numberOrNull(source.exchangeRate),
      taxExchangeRate: numberOrNull(source.taxExchangeRate),
      maritimeFreightUsd: numberOrNull(source.maritimeFreightUsd),
      containerReleaseUsd: numberOrNull(source.containerReleaseUsd),
      inlandFreightBob: numberOrNull(source.inlandFreightBob),
      miscellaneousExpensesBob:
      numberOrNull(
        source.miscellaneousExpensesBob
      ),
      gaPercent: numberOrNull(source.gaPercent),
      ivaPercent: numberOrNull(source.ivaPercent),
      icePercent: numberOrNull(source.icePercent),
      totalWeightTn: numberOrNull(source.totalWeightTn),
      fobPaymentCount: Number(source.fobPaymentCount || 1),
      customerPaysInUsd: Boolean(source.customerPaysInUsd),
      customerPaysSupplier: Boolean(source.customerPaysSupplier),

      taxExchangeRate:
          numberOrNull(source.taxExchangeRate),

      containerReleaseUsd:
          numberOrNull(source.containerReleaseUsd),

      miscellaneousExpensesBob:
          numberOrNull(source.miscellaneousExpensesBob),

      calculationRuleVersion:
          source.calculationRuleVersion ||
          'FCL_GOV_2026_07',
    };
  }

  async function handleCalculate() {
    try {
      setCalculating(true);

      const result = await calculateFclProforma(buildPayload());

      setCalculation(result);

      setForm((prev) => ({
        ...prev,

      taxExchangeRate:
        result.taxExchangeRate ??
        prev.taxExchangeRate,

      containerReleaseUsd:
        result.containerReleaseUsd ??
        prev.containerReleaseUsd,

      miscellaneousExpensesBob:
        result.miscellaneousExpensesBob ??
        prev.miscellaneousExpensesBob,

      calculationRuleVersion:
        result.calculationRuleVersion ||
        prev.calculationRuleVersion,

        originFreightUsd: result.originFreightUsd ?? '',
        maritimeFreightUsd: result.maritimeFreightUsd ?? prev.maritimeFreightUsd,
        alboBob: result.alboBob ?? '',
        adaBob: result.adaBob ?? '',
        commissionUsd: result.commissionUsd ?? '',
        exchangeRate: result.exchangeRate ?? prev.exchangeRate,
      }));
      } catch (error) {
        console.error(
          'Error guardando proforma FCL:',
          error
        );

        alert(
          error?.message ||
            'No se pudo guardar la proforma FCL.'
        );
      }
  }

  async function handleSave() {
  if (!calculation) {
    alert('Primero debes calcular la proforma FCL.');
    return;
  }

  try {
    setSaving(true);

    const payload = buildPayload({
      ...form,
      ...calculation,
    });

    let saved;

    if (mode === 'edit') {
      saved = await updateFclProforma(proformaId, payload);
      alert('Proforma FCL actualizada correctamente.');
    } else {
      const opportunityId = resolveOpportunityId();

      if (!opportunityId) {
        alert('No se encontró la oportunidad comercial. Abre la FCL desde Contacto 360 seleccionando un requerimiento válido.');
        return;
      }

      saved = await createFclProforma(payload);
      alert('Proforma FCL guardada correctamente.');
    }

    navigate(`/fcl/${saved.id}`);
  } catch (error) {
    console.error(error);
    alert('No se pudo guardar la proforma FCL.');
  } finally {
    setSaving(false);
  }

}
  const fullInvestmentBob = calculation
  ? Number(calculation.subtotalUsd || 0) *
      Number(
        calculation.exchangeRateUsed ||
          calculation.exchangeRate ||
          form.exchangeRate ||
          0
      ) +
    Number(calculation.totalOperationBob || 0)
  : 0;


  return (
    <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
        <div>
          <p className="text-xs font-black uppercase tracking-[0.45em] text-orange-600">
            Simulador de importación FCL
          </p>

          <h2 className="mt-3 text-2xl font-black text-slate-900">
            Cotización rápida antes de proforma formal
          </h2>

          <p className="mt-2 max-w-3xl text-sm leading-6 text-slate-500">
            Basado en el Excel operativo de Genuino para proformas FCL.
          </p>
        </div>

        <button
          onClick={handleCalculate}
          disabled={calculating}
          className="inline-flex items-center justify-center gap-2 rounded-2xl bg-slate-950 px-5 py-3 text-sm font-black text-white hover:bg-slate-800 disabled:opacity-60"
        >
          <Calculator size={18} />
          {calculating ? 'Calculando...' : 'Calcular'}
        </button>
      </div>

      <div className="mt-6 grid gap-6 xl:grid-cols-[1fr_380px]">
        <div className="space-y-5">
          <StepCard step="Paso 1" title="Cliente y atención comercial">
            <div className="grid gap-4 md:grid-cols-4">
              <Field label="Cliente" value={form.customerName} onChange={(v) => update('customerName', v)} />
              <Field label="Teléfono" value={form.customerPhone} onChange={(v) => update('customerPhone', v)} />
              <Field label="Asesor comercial" value={form.sellerName} onChange={(v) => update('sellerName', v)} />
              <Field label="Dirección / destino" value={form.destinationCity} onChange={(v) => update('destinationCity', v)} />
            </div>
          </StepCard>

          <StepCard step="Paso 2" title="Proveedor">
            <div className="grid gap-4 md:grid-cols-4">
              <Field label="Proveedor" value={form.supplierName} onChange={(v) => update('supplierName', v)} />
              <Field label="Teléfono proveedor" value={form.supplierPhone} onChange={(v) => update('supplierPhone', v)} />
              <Field label="Ciudad / país origen" value={form.originCity} onChange={(v) => update('originCity', v)} />
              <Field label="Puerto origen" value={form.originPort} onChange={(v) => update('originPort', v)} />
            </div>
          </StepCard>

          <StepCard step="Paso 3" title="Datos logísticos y comerciales">
            <div className="grid gap-4 md:grid-cols-4">
              <SelectField
                label="Tipo contenedor"
                value={form.containerType}
                onChange={(v) => update('containerType', v)}
                options={[
                  ['FCL20', "20'"],
                  ['FCL40', "40'"],
                  ['FCL40HQ', '40HQ'],
                ]}
              />

              <Field type="number" label="Cantidad contenedores" value={form.containerCount} onChange={(v) => update('containerCount', v)} />
              <Field label="Producto" value={form.product} onChange={(v) => update('product', v)} />
              <Field type="number" label="FOB USD" value={form.fobUsd} onChange={(v) => update('fobUsd', v)} />
              <Field type="number" label="Transporte marítimo USD" value={form.maritimeFreightUsd} onChange={(v) => update('maritimeFreightUsd', v)} />
              <Field
                type="number"
                label="Liberación contenedor USD"
                value={form.containerReleaseUsd}
                onChange={(value) =>
                  update(
                    'containerReleaseUsd',
                    value
                  )
                }
              />
              <Field type="number" label="Transporte terrestre Bs" value={form.inlandFreightBob} onChange={(v) => update('inlandFreightBob', v)} />
              <Field
                type="number"
                label="Otros gastos Bs"
                value={
                  form.miscellaneousExpensesBob
                }
                onChange={(value) =>
                  update(
                    'miscellaneousExpensesBob',
                    value
                  )
                }
              />
              <Field type="number" label="T/C comercial" value={form.exchangeRate} onChange={(v) => update('exchangeRate', v)} />
              <Field
                label="Peso total TN"
                value={form.totalWeightTn}
                onChange={(value) => update('totalWeightTn', value)}
              />

              <Field
                type="number"
                label="T/C para impuestos"
                value={form.taxExchangeRate}
                onChange={(value) =>
                  update('taxExchangeRate', value)
                }
              />
              <Field
                label="Número pagos FOB"
                type="number"
                value={form.fobPaymentCount}
                onChange={(value) => update('fobPaymentCount', value)}
              />
            </div>
          </StepCard>

          <StepCard step="Paso 4" title="Tributación y pago">
            <div className="grid gap-4 md:grid-cols-4">
              <Field type="number" label="GA %" value={form.gaPercent} onChange={(v) => update('gaPercent', v)} />
              <Field type="number" label="IVA %" value={form.ivaPercent} onChange={(v) => update('ivaPercent', v)} />
              <Field type="number" label="ICE %" value={form.icePercent} onChange={(v) => update('icePercent', v)} />

              <SelectField
                label="Método de pago"
                value={form.paymentMethod}
                onChange={(v) => update('paymentMethod', v)}
                options={[
                  ['ALIBABA', 'Alibaba'],
                  ['CHILE', 'Chile'],
                ]}
              />

              <SelectField
                label="Cliente paga en USD"
                value={String(form.customerPaysInUsd)}
                onChange={(value) => update('customerPaysInUsd', value === 'true')}
                options={[
                  ['false', 'No'],
                  ['true', 'Sí'],
                ]}
              />

              <SelectField
                label="Cliente paga proveedor"
                value={String(form.customerPaysSupplier)}
                onChange={(value) => update('customerPaysSupplier', value === 'true')}
                options={[
                  ['false', 'No'],
                  ['true', 'Sí'],
                ]}
              />

              <SelectField
                label="Importador"
                value={form.importerNitType}
                onChange={(v) => update('importerNitType', v)}
                options={[
                  ['NIT_CLIENTE', 'NIT Cliente'],
                  ['GENUINO', 'NIT Genuino'],
                ]}
              />
            </div>
          </StepCard>

          <StepCard step="Paso 5" title="Tarifas calculadas">
            <div className="grid gap-4 md:grid-cols-4">
              <ReadOnlyField label="Flete USD" value={form.originFreightUsd} />
              <ReadOnlyField label="ALBO Bs" value={form.alboBob} />
              <ReadOnlyField label="ADA Bs" value={form.adaBob} />
              <ReadOnlyField label="Comisión giro USD" value={form.commissionUsd} />
            </div>
          </StepCard>
        </div>

        <aside className="rounded-3xl bg-slate-950 p-6 text-white">
          <h3 className="text-xl font-black">Resultado FCL</h3>

          {!calculation ? (
            <div className="mt-8 rounded-2xl bg-white/5 p-5 text-sm text-slate-300">
              Completa los datos y presiona calcular.
            </div>
          ) : (
            <div className="mt-6 space-y-3">
              <ResultRow label="Seguro USD" value={calculation.insuranceUsdCalculated} />
              <ResultRow label="CIF Bs" value={calculation.cifBob} />
              <ResultRow
                label="T/C impuestos"
                value={
                  calculation.taxExchangeRate
                }
              />
              <ResultRow label="GA Bs" value={calculation.gaBob} />
              <ResultRow label="IVA Bs" value={calculation.ivaBob} />
              <ResultRow label="ICE Bs" value={calculation.iceBob} />
              <ResultRow label="Impuestos aduana Bs" value={calculation.customsTaxesBob} />
              <ResultRow label="ALBO Bs" value={calculation.alboBob} />
              <ResultRow label="ADA Bs" value={calculation.adaBob} />
              <ResultRow label="Despachante Bs" value={calculation.dispatchAgentCommissionBob} />
              <ResultRow label="Comisión Genuino Bs" value={calculation.genuinoCommissionBob} />
              <ResultRow label="Gastos extra NIT Bs" value={calculation.extraNitExpensesBob} />
              <ResultRow label="Comisión giro USD" value={calculation.bankTransferCommissionUsd} />
              <ResultRow
                label="Liberación contenedor USD"
                value={
                  calculation.containerReleaseUsd
                }
              />

              <ResultRow
                label="Otros gastos Bs"
                value={
                  calculation.miscellaneousExpensesBob
                }
              />
              <div className="rounded-2xl border border-white/10 px-4 py-3">
                <p className="text-xs text-slate-400">
                  Versión de reglas
                </p>

                <p className="mt-1 text-sm font-black text-white">
                  {calculation.calculationRuleVersion ||
                    form.calculationRuleVersion}
                </p>
              </div>
              <div className="mt-5 rounded-2xl bg-orange-500 p-5">
                <p className="text-xs font-black uppercase tracking-widest text-orange-100">
                  Costos operación Bolivia
                </p>
                <p className="mt-2 text-3xl font-black">
                  {formatNumber(calculation.totalOperationBob || calculation.totalBob)}
                </p>
              </div>

              <div className="mt-3 rounded-2xl bg-emerald-500 p-5">
                <p className="text-xs font-black uppercase tracking-widest text-emerald-50">
                  Inversión referencial total
                </p>

                <p className="mt-2 text-3xl font-black text-white">
                  Bs {formatNumber(fullInvestmentBob)}
                </p>
              </div>
            </div>
          )}

          <button
            onClick={handleSave}
            disabled={saving || !calculation}
            className="mt-6 inline-flex w-full items-center justify-center gap-2 rounded-2xl bg-white px-5 py-3 text-sm font-black text-slate-950 hover:bg-slate-100 disabled:opacity-40"
          >
            <Save size={18} />
            {saving ? 'Guardando...' : 'Guardar proforma FCL'}
          </button>
        </aside>
      </div>
    </section>
  );
}

function StepCard({ step, title, children }) {
  return (
    <div className="rounded-3xl border border-slate-200 bg-slate-50/60 p-5">
      <p className="text-xs font-black uppercase tracking-[0.35em] text-orange-600">{step}</p>
      <h3 className="mt-2 text-lg font-black text-slate-900">{title}</h3>
      <div className="mt-5">{children}</div>
    </div>
  );
}

function Field({ label, value, onChange, type = 'text' }) {
  return (
    <label className="space-y-2">
      <span className="text-xs font-black uppercase tracking-widest text-slate-400">{label}</span>
      <input
        type={type}
        step={type === 'number' ? '0.01' : undefined}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm font-bold text-slate-700 outline-none focus:border-orange-300"
      />
    </label>
  );
}

function SelectField({ label, value, onChange, options }) {
  return (
    <label className="space-y-2">
      <span className="text-xs font-black uppercase tracking-widest text-slate-400">{label}</span>
      <select
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm font-bold text-slate-700 outline-none focus:border-orange-300"
      >
        {options.map(([value, label]) => (
          <option key={value} value={value}>{label}</option>
        ))}
      </select>
    </label>
  );
}

function ReadOnlyField({ label, value }) {
  return (
    <label className="space-y-2">
      <span className="text-xs font-black uppercase tracking-widest text-slate-400">{label}</span>
      <input
        value={value || ''}
        readOnly
        className="w-full rounded-2xl border border-slate-200 bg-slate-100 px-4 py-3 text-sm font-bold text-slate-500 outline-none"
      />
    </label>
  );
}

function ResultRow({ label, value }) {
  return (
    <div className="flex items-center justify-between rounded-2xl bg-white/5 px-4 py-3">
      <span className="text-sm text-slate-300">{label}</span>
      <span className="text-sm font-black text-white">{formatNumber(value)}</span>
    </div>
  );
}

function formatNumber(value) {
  if (value === null || value === undefined || value === '') return '-';

  return Number(value).toLocaleString('es-BO', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
}

