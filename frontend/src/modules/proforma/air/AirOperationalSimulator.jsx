import {
  useEffect,
  useMemo,
  useState,
} from 'react';

import {
  calculateAirProforma,
  createAirProforma,
  getAirProformaById,
  updateAirProforma,
} from '../../../services/airService';

import { getLeadById } from '../../../services/leadsService';

import {
  useNavigate,
  useSearchParams,
} from 'react-router-dom';

const initialForm = {
  opportunityId: '',
  customerId: '',

  issueDate: new Date().toISOString().slice(0, 10),
  validityDays: 1,

  sellerName: '',

  customerName: '',
  customerPhone: '',
  customerAddress: '',

  productName: '',
  quantity: 1,

  merchandiseValueUsd: 0,
  warehouseShippingUsd: 0,

  grossWeightKg: 0,
  airFreightUsd: 0,

  gaPercent: 10,
  ivaPercent: 14.94,
  icePercent: 0,

  exchangeRate: 10,
  taxExchangeRate: 9,

  supplierName: '',
  supplierPhone: '',

  paymentMethod: 'ALIBABA',

  genuinoCommissionBob: 0,

  commercialTerms: '',
};

export default function AirOperationalSimulator({
  mode = 'new',
  proformaId = null,
  onSaved,
}) {

const navigate = useNavigate();
const [searchParams] = useSearchParams();

const leadId =
  searchParams.get('leadId');

const opportunityId =
  searchParams.get('opportunityId');

 const [form, setForm] = useState({
  ...initialForm,

  opportunityId:
    opportunityId || '',

  customerId:
    leadId || '',
});

  const [result, setResult] =
    useState(null);

  const [loading, setLoading] =
    useState(false);

  const [saving, setSaving] =
    useState(false);

  const [error, setError] =
    useState('');

useEffect(() => {
  if (
    mode !== 'edit' ||
    !proformaId
  ) {
    return;
  }

  let active = true;

  async function loadExistingProforma() {
    setLoading(true);
    setError('');

    try {
      const data =
        await getAirProformaById(
          proformaId
        );

      if (!active) {
        return;
      }

      const input =
        data?.input || {};

      setForm((prev) => ({
        ...prev,

        opportunityId:
          data?.opportunityId ||
          input.opportunityId ||
          '',

        customerId:
          data?.customerId ||
          input.customerId ||
          '',

        issueDate:
          input.issueDate ||
          prev.issueDate,

        validityDays:
          input.validityDays ?? 1,

        sellerName:
          input.sellerName || '',

        customerName:
          input.customerName || '',

        customerPhone:
          input.customerPhone || '',

        customerAddress:
          input.customerAddress || '',

        productName:
          input.productName || '',

        quantity:
          input.quantity ?? 1,

        merchandiseValueUsd:
          input.merchandiseValueUsd ?? 0,

        warehouseShippingUsd:
          input.warehouseShippingUsd ?? 0,

        grossWeightKg:
          input.grossWeightKg ?? 0,

        airFreightUsd:
          input.airFreightUsd ?? 0,

        gaPercent:
          input.gaPercent ?? 10,

        ivaPercent:
          input.ivaPercent ?? 14.94,

        icePercent:
          input.icePercent ?? 0,

        exchangeRate:
          input.exchangeRate ?? 10,

        taxExchangeRate:
          input.taxExchangeRate ?? 9,

        supplierName:
          input.supplierName || '',

        supplierPhone:
          input.supplierPhone || '',

        paymentMethod:
          input.paymentMethod ||
          'ALIBABA',

        genuinoCommissionBob:
          input.genuinoCommissionBob ?? 0,

        commercialTerms:
          input.commercialTerms || '',
      }));

      setResult(
        data?.calculation || null
      );

    } catch (err) {
      console.error(
        'Error cargando proforma Aérea',
        err
      );

      setError(
        err?.message ||
        'No se pudo cargar la proforma Aérea.'
      );
    } finally {
      if (active) {
        setLoading(false);
      }
    }
  }

  loadExistingProforma();

  return () => {
    active = false;
  };
}, [
  mode,
  proformaId,
]);

  const update = (field, value) => {
    setForm((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const payload = useMemo(
    () => ({
      opportunityId:
        form.opportunityId,

      customerId:
        form.customerId || null,

      issueDate:
        form.issueDate,

      validityDays:
        Number(
          form.validityDays || 1
        ),

      sellerName:
        form.sellerName,

      customerName:
        form.customerName,

      customerPhone:
        form.customerPhone,

      customerAddress:
        form.customerAddress,

      productName:
        form.productName,

      quantity:
        Number(
          form.quantity || 1
        ),

      merchandiseValueUsd:
        Number(
          form.merchandiseValueUsd || 0
        ),

      warehouseShippingUsd:
        Number(
          form.warehouseShippingUsd || 0
        ),

      grossWeightKg:
        Number(
          form.grossWeightKg || 0
        ),

      airFreightUsd:
        Number(
          form.airFreightUsd || 0
        ),

      gaPercent:
        Number(
          form.gaPercent || 0
        ),

      ivaPercent:
        Number(
          form.ivaPercent || 0
        ),

      icePercent:
        Number(
          form.icePercent || 0
        ),

      exchangeRate:
        Number(
          form.exchangeRate || 0
        ),

      taxExchangeRate:
        Number(
          form.taxExchangeRate || 0
        ),

      supplierName:
        form.supplierName,

      supplierPhone:
        form.supplierPhone,

      paymentMethod:
        form.paymentMethod,

      genuinoCommissionBob:
        Number(
          form.genuinoCommissionBob || 0
        ),

      commercialTerms:
        form.commercialTerms,
    }),
    [form]
  );


async function handleCalculate() {
  setLoading(true);
  setError('');

  try {
    const data =
      await calculateAirProforma(
        payload
      );

    setResult(data);

  } catch (err) {
    console.error(
      'Error calculando proforma Aérea',
      err
    );

    setError(
      err?.message ||
      'No se pudo calcular la proforma Aérea.'
    );

  } finally {
    setLoading(false);
  }
}

async function handleSave() {
  setSaving(true);
  setError('');

  try {
    let data;

    if (
      mode === 'edit' &&
      proformaId
    ) {
      data =
        await updateAirProforma(
          proformaId,
          payload
        );

    } else {
      data =
        await createAirProforma({
          ...payload,

          notes:
            'Proforma Aérea creada desde CRM',

          createdBy:
            form.sellerName ||
            'system',
        });
    }

    if (onSaved) {
      onSaved(data);
    }

  } catch (err) {
    console.error(
      'Error guardando proforma Aérea',
      err
    );

    setError(
      err?.message ||
      (
        mode === 'edit'
          ? 'No se pudo actualizar la proforma Aérea.'
          : 'No se pudo guardar la proforma Aérea.'
      )
    );

  } finally {
    setSaving(false);
  }
}

useEffect(() => {
  if (!leadId) return;

  async function loadContext() {
    try {
      const lead =
        await getLeadById(leadId);

      setForm((prev) => ({
        ...prev,

        customerId:
          lead.id || leadId,

        opportunityId:
          opportunityId ||
          prev.opportunityId,

        customerName:
          lead.company ||
          lead.contact ||
          lead.fullName ||
          '',

        customerPhone:
          lead.phone || '',

        sellerName:
          lead.owner ||
          lead.assignedSellerName ||
          '',

        productName:
          lead.messagePreview ||
          prev.productName,
      }));

    } catch (err) {
      console.error(
        'Error cargando contexto Aéreo',
        err
      );
    }
  }

  loadContext();

}, [
  leadId,
  opportunityId,
]);

  return (
    <div className="grid gap-6 xl:grid-cols-[1.4fr_0.8fr]">

      <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">

        <div className="flex items-center justify-between gap-4">
          <div>
            <p className="text-xs font-black uppercase tracking-[0.2em] text-orange-500">
              Operación Aérea
            </p>

            <h2 className="mt-2 text-2xl font-black text-slate-900">
              Datos de la cotización
            </h2>
          </div>
        </div>

        <div className="mt-6 grid gap-4 md:grid-cols-2">

          <Field
            label="Cliente"
            value={form.customerName}
            onChange={(v) =>
              update(
                'customerName',
                v
              )
            }
          />

          <Field
            label="Teléfono"
            value={form.customerPhone}
            onChange={(v) =>
              update(
                'customerPhone',
                v
              )
            }
          />

          <Field
            label="Dirección"
            value={form.customerAddress}
            onChange={(v) =>
              update(
                'customerAddress',
                v
              )
            }
          />

          <Field
            label="Asesor"
            value={form.sellerName}
            onChange={(v) =>
              update(
                'sellerName',
                v
              )
            }
          />

          <Field
            label="Producto"
            value={form.productName}
            onChange={(v) =>
              update(
                'productName',
                v
              )
            }
          />

          <Field
            label="Cantidad"
            type="number"
            value={form.quantity}
            onChange={(v) =>
              update(
                'quantity',
                v
              )
            }
          />

          <Field
            label="Proveedor"
            value={form.supplierName}
            onChange={(v) =>
              update(
                'supplierName',
                v
              )
            }
          />

          <Field
            label="Teléfono proveedor"
            value={form.supplierPhone}
            onChange={(v) =>
              update(
                'supplierPhone',
                v
              )
            }
          />

        </div>

        <div className="my-7 border-t border-slate-200" />

        <p className="text-xs font-black uppercase tracking-[0.18em] text-slate-400">
          Mercadería y logística
        </p>

        <div className="mt-4 grid gap-4 md:grid-cols-2">

          <Field
            label="Valor mercadería USD"
            type="number"
            value={
              form.merchandiseValueUsd
            }
            onChange={(v) =>
              update(
                'merchandiseValueUsd',
                v
              )
            }
          />

          <Field
            label="Transporte fábrica → almacén USD"
            type="number"
            value={
              form.warehouseShippingUsd
            }
            onChange={(v) =>
              update(
                'warehouseShippingUsd',
                v
              )
            }
          />

          <Field
            label="Peso bruto kg"
            type="number"
            value={
              form.grossWeightKg
            }
            onChange={(v) =>
              update(
                'grossWeightKg',
                v
              )
            }
          />

          <Field
            label="Flete aéreo USD"
            type="number"
            value={
              form.airFreightUsd
            }
            onChange={(v) =>
              update(
                'airFreightUsd',
                v
              )
            }
          />

          <SelectField
            label="Método de pago"
            value={
              form.paymentMethod
            }
            onChange={(v) =>
              update(
                'paymentMethod',
                v
              )
            }
            options={[
              {
                value: 'ALIBABA',
                label: 'Alibaba',
              },
              {
                value: 'SWIFT',
                label: 'Swift',
              },
            ]}
          />

          <Field
            label="Comisión Genuino Bs"
            type="number"
            value={
              form.genuinoCommissionBob
            }
            onChange={(v) =>
              update(
                'genuinoCommissionBob',
                v
              )
            }
          />

        </div>

        <div className="my-7 border-t border-slate-200" />

        <p className="text-xs font-black uppercase tracking-[0.18em] text-slate-400">
          Aduana y tipo de cambio
        </p>

        <div className="mt-4 grid gap-4 md:grid-cols-3">

          <Field
            label="GA %"
            type="number"
            value={form.gaPercent}
            onChange={(v) =>
              update(
                'gaPercent',
                v
              )
            }
          />

          <Field
            label="IVA %"
            type="number"
            value={form.ivaPercent}
            onChange={(v) =>
              update(
                'ivaPercent',
                v
              )
            }
          />

          <Field
            label="ICE %"
            type="number"
            value={form.icePercent}
            onChange={(v) =>
              update(
                'icePercent',
                v
              )
            }
          />

          <Field
            label="T/C comercial"
            type="number"
            value={
              form.exchangeRate
            }
            onChange={(v) =>
              update(
                'exchangeRate',
                v
              )
            }
          />

          <Field
            label="T/C impuestos"
            type="number"
            value={
              form.taxExchangeRate
            }
            onChange={(v) =>
              update(
                'taxExchangeRate',
                v
              )
            }
          />

          <Field
            label="Validez días"
            type="number"
            value={
              form.validityDays
            }
            onChange={(v) =>
              update(
                'validityDays',
                v
              )
            }
          />

        </div>

        <div className="mt-6">

          <label className="text-xs font-black uppercase tracking-[0.14em] text-slate-400">
            Condiciones comerciales
          </label>

          <textarea
            rows={4}
            value={
              form.commercialTerms
            }
            onChange={(e) =>
              update(
                'commercialTerms',
                e.target.value
              )
            }
            className="mt-2 w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-orange-400 focus:bg-white"
          />

        </div>

        {error && (
          <div className="mt-5 rounded-2xl bg-red-50 px-4 py-3 text-sm font-semibold text-red-600">
            {error}
          </div>
        )}



      </section>

<AirResult
  result={result}
  loading={loading}
  saving={saving}
  onCalculate={handleCalculate}
  onSave={handleSave}
  mode={mode}
/>

    </div>
  );
}

function AirResult({
  result,
  loading,
  saving,
  onCalculate,
  onSave,
  mode,
}) {
  const moneyUsd = (value) =>
    `USD ${Number(
      value || 0
    ).toLocaleString(
      'es-BO',
      {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      }
    )}`;

  const moneyBob = (value) =>
    `Bs ${Number(
      value || 0
    ).toLocaleString(
      'es-BO',
      {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      }
    )}`;

  return (
    <aside className="rounded-3xl bg-slate-950 p-6 text-white shadow-xl">

      <p className="text-xs font-black uppercase tracking-[0.22em] text-orange-400">
        Resultado
      </p>

      <h2 className="mt-3 text-2xl font-black">
        Simulación Aérea
      </h2>

      {!result ? (
        <div className="mt-8 rounded-2xl bg-white/5 p-5 text-sm text-slate-400">
          Completa los datos y calcula la operación.
        </div>
      ) : (
        <div className="mt-6 space-y-3">

          <ResultItem
            label="FOB"
            value={
              moneyUsd(
                result.fobUsd
              )
            }
          />

          <ResultItem
            label="Transporte almacén"
            value={
              moneyUsd(
                result.warehouseShippingUsd
              )
            }
          />

          <ResultItem
            label="Comisión bancaria"
            value={
              moneyUsd(
                result.bankCommissionUsd
              )
            }
          />

          <ResultItem
            label="Flete aéreo"
            value={
              moneyUsd(
                result.airFreightUsd
              )
            }
          />

          <ResultItem
            label="Subtotal USD"
            value={
              moneyUsd(
                result.subtotalUsd
              )
            }
          />

          <div className="my-4 border-t border-white/10" />

          <ResultItem
            label="Impuestos Aduana"
            value={
              moneyBob(
                result.customsTaxesBob
              )
            }
          />

          <ResultItem
            label="Costos Bolivia"
            value={
              moneyBob(
                result.totalBoliviaBob
              )
            }
          />

          <div className="rounded-2xl bg-orange-500 p-5">
            <p className="text-xs font-black uppercase tracking-[0.14em]">
              Inversión total
            </p>

            <p className="mt-2 text-2xl font-black">
              {moneyBob(
                result.totalBob
              )}
            </p>
          </div>

          <ResultItem
            label="Precio unitario"
            value={
              moneyBob(
                result.unitPriceBob
              )
            }
          />

        </div>
      )}

      <button
        type="button"
        onClick={onCalculate}
        disabled={loading}
        className="mt-6 w-full rounded-2xl bg-white px-5 py-4 text-sm font-black text-slate-950 disabled:opacity-50"
        >
        {loading
            ? 'Calculando...'
            : 'Calcular'}
        </button>

        <button
        type="button"
        onClick={onSave}
        disabled={!result || saving}
        className="mt-3 w-full rounded-2xl bg-orange-500 px-5 py-4 text-sm font-black text-white hover:bg-orange-600 disabled:opacity-50"
        >
        {saving
        ? 'Guardando...'
        : mode === 'edit'
            ? 'Guardar correcciones'
            : 'Guardar proforma Aérea'}
        </button>

    </aside>
  );
}

function ResultItem({
  label,
  value,
}) {
  return (
    <div className="rounded-2xl bg-white/5 p-4">
      <p className="text-xs font-medium text-slate-400">
        {label}
      </p>

      <p className="mt-1 text-lg font-black">
        {value}
      </p>
    </div>
  );
}

function Field({
  label,
  value,
  onChange,
  type = 'text',
}) {
  return (
    <label className="block">
      <span className="text-xs font-black uppercase tracking-[0.12em] text-slate-400">
        {label}
      </span>

      <input
        type={type}
        value={value ?? ''}
        onChange={(e) =>
          onChange(e.target.value)
        }
        className="mt-2 w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm font-semibold text-slate-900 outline-none transition focus:border-orange-400 focus:bg-white"
      />
    </label>
  );
}

function SelectField({
  label,
  value,
  onChange,
  options,
}) {
  return (
    <label className="block">
      <span className="text-xs font-black uppercase tracking-[0.12em] text-slate-400">
        {label}
      </span>

      <select
        value={value}
        onChange={(e) =>
          onChange(e.target.value)
        }
        className="mt-2 w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm font-semibold text-slate-900 outline-none transition focus:border-orange-400 focus:bg-white"
      >
        {options.map((option) => (
          <option
            key={option.value}
            value={option.value}
          >
            {option.label}
          </option>
        ))}
      </select>
    </label>
  );
}