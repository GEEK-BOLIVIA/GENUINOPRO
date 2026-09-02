import { useEffect, useState } from 'react';
import { Calculator, Loader2 } from 'lucide-react';
import { useNavigate, useSearchParams } from 'react-router-dom';

import {
  calculateHblProforma,
  createHblProforma,
  getHblProformaById,
  updateHblProforma,
} from '../../../services/hblService';

import { getLeadById } from '../../../services/leadsService';

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

  customsFobUsd: 0,
  merchandiseValueUsd: 0,
  warehouseShippingUsd: 0,

  grossWeightKg: 0,
  volumeCbm: 0,

  gaPercent: 10,
  ivaPercent: 14.94,
  icePercent: 0,

  sensitiveProduct: false,

  exchangeRate: 10,
  taxExchangeRate: 9,

  supplierName: '',
  supplierPhone: '',

  paymentMethod: 'TRANSFERENCIA',
  importerNitType: 'GENUINO',
  customerPaysInUsd: false,

  commercialTerms: '',
};



function Field({
  label,
  value,
  onChange,
  type = 'text',
  placeholder = '',
}) {
  return (
    <label className="block">
      <span className="mb-1 block text-[10px] font-black uppercase tracking-widest text-slate-400">
        {label}
      </span>

      <input
        type={type}
        value={value ?? ''}
        placeholder={placeholder}
        onChange={(e) => onChange(e.target.value)}
        className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm font-bold text-slate-700 outline-none focus:border-orange-500 focus:ring-4 focus:ring-orange-500/10"
      />
    </label>
  );
}

function SectionCard({
  eyebrow,
  title,
  description,
  children,
}) {
  return (
    <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
      <p className="text-[10px] font-black uppercase tracking-[0.28em] text-orange-500">
        {eyebrow}
      </p>

      <h2 className="mt-1 text-lg font-black text-slate-950">
        {title}
      </h2>

      {description && (
        <p className="mt-1 text-sm text-slate-500">
          {description}
        </p>
      )}

      <div className="mt-5">{children}</div>
    </section>
  );
}

function ResultCard({ label, value, featured = false }) {
  return (
    <div
      className={`rounded-2xl p-4 ${
        featured
          ? 'bg-orange-500 text-white'
          : 'bg-white/5 text-white'
      }`}
    >
      <p
        className={`text-xs ${
          featured ? 'text-orange-100' : 'text-slate-400'
        }`}
      >
        {label}
      </p>

      <p className="mt-1 text-2xl font-black">
        {value}
      </p>
    </div>
  );
}

export default function HblOperationalSimulator({
  mode = 'new',
  proformaId = null,
}) {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const isEditMode =
    mode === 'edit' && Boolean(proformaId);

    const [isLoadingEdit, setIsLoadingEdit] =
    useState(false);

  const leadId = searchParams.get('leadId');
  const opportunityId =
    searchParams.get('opportunityId');

  const [form, setForm] = useState({
    ...initialForm,
    opportunityId: opportunityId || '',
    customerId: leadId || '',
  });

  const [result, setResult] = useState(null);
  const [isCalculating, setIsCalculating] =
    useState(false);
  const [isSaving, setIsSaving] =
    useState(false);
  const [error, setError] = useState('');

  function update(field, value) {
    setForm((prev) => ({
      ...prev,
      [field]: value,
    }));
  }

  useEffect(() => {
    if (!leadId) return;

    if (isEditMode) return;

    async function loadContext() {
      try {
        const lead = await getLeadById(leadId);

        setForm((prev) => ({
          ...prev,

          customerId: lead.id || leadId,

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
          'Error cargando contexto HBL',
          err
        );
      }
    }

    loadContext();
        }, [
        leadId,
        opportunityId,
        isEditMode,
        ]);

  useEffect(() => {
  if (!isEditMode || !proformaId) return;

  async function loadExisting() {
    try {
      setIsLoadingEdit(true);
      setError('');

      const existing =
        await getHblProformaById(
          proformaId
        );

      const input =
        existing?.input || {};

      setForm((prev) => ({
        ...prev,
        ...input,

        opportunityId:
          existing.opportunityId ||
          input.opportunityId ||
          '',

        customerId:
          existing.customerId ||
          input.customerId ||
          '',
      }));

      setResult(
        existing?.calculation || null
      );

    } catch (err) {
      console.error(
        'Error cargando HBL para edición',
        err
      );

      setError(
        err.message ||
          'No se pudo cargar la proforma HBL.'
      );
    } finally {
      setIsLoadingEdit(false);
    }
  }

  loadExisting();

}, [isEditMode, proformaId]);

  function validate() {
    if (!form.customerName.trim()) {
      return 'El cliente es obligatorio.';
    }

    if (!form.productName.trim()) {
      return 'El producto es obligatorio.';
    }

    if (Number(form.quantity || 0) <= 0) {
      return 'La cantidad debe ser mayor a cero.';
    }

    if (Number(form.exchangeRate || 0) <= 0) {
      return 'El tipo de cambio comercial debe ser mayor a cero.';
    }

    if (Number(form.taxExchangeRate || 0) <= 0) {
      return 'El tipo de cambio para impuestos debe ser mayor a cero.';
    }

    return '';
  }

  function buildPayload() {
    return {
      ...form,

      validityDays:
        Number(form.validityDays || 1),

      quantity:
        Number(form.quantity || 0),

      merchandiseValueUsd:
        Number(form.merchandiseValueUsd || 0),

      warehouseShippingUsd:
        Number(form.warehouseShippingUsd || 0),

      grossWeightKg:
        Number(form.grossWeightKg || 0),

        customsFobUsd:
        Number(
            form.customsFobUsd ||
            form.merchandiseValueUsd ||
            0
        ),

      volumeCbm:
        Number(form.volumeCbm || 0),

      gaPercent:
        Number(form.gaPercent || 0),

      ivaPercent:
        Number(form.ivaPercent || 0),

      icePercent:
        Number(form.icePercent || 0),

      exchangeRate:
        Number(form.exchangeRate || 0),

      taxExchangeRate:
        Number(form.taxExchangeRate || 0),
    };
  }

  async function calculate() {
    try {
      setIsCalculating(true);
      setError('');

      const validationError = validate();

      if (validationError) {
        setError(validationError);
        return;
      }

      const response =
        await calculateHblProforma(
          buildPayload()
        );

      setResult(response);
    } catch (err) {
      console.error(err);

      setError(
        err.message ||
          'No se pudo calcular la proforma HBL.'
      );
    } finally {
      setIsCalculating(false);
    }
  }

  async function save() {
    try {
      setIsSaving(true);
      setError('');

      const validationError = validate();

      if (validationError) {
        setError(validationError);
        return;
      }

        const payload = buildPayload();

        if (isEditMode) {

        await updateHblProforma(
            proformaId,
            payload
        );

        navigate(
            `/hbl/${proformaId}`
        );

        return;
        }

        const created =
        await createHblProforma(
            payload
        );

        navigate(
        `/hbl/${created.id}`
        );

      
    } catch (err) {
      console.error(err);

      setError(
        err.message ||
          'No se pudo guardar la proforma HBL.'
      );
    } finally {
      setIsSaving(false);
    }
  }

        if (isLoadingEdit) {
        return (
            <div className="rounded-3xl border border-slate-200 bg-white p-8 text-center">
            <Loader2
                className="mx-auto animate-spin"
                size={24}
            />

            <p className="mt-3 text-sm font-bold text-slate-500">
                Cargando proforma HBL para edición...
            </p>
            </div>
        );
        }

  return (
    <div className="grid gap-6 xl:grid-cols-[1fr_420px]">

      <div className="space-y-6">

        <SectionCard
          eyebrow="Paso 1"
          title="Cliente y atención comercial"
          description="Información comercial asociada a la cotización."
        >
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">

            <Field
              label="Cliente"
              value={form.customerName}
              onChange={(v) =>
                update('customerName', v)
              }
            />

            <Field
              label="Teléfono"
              value={form.customerPhone}
              onChange={(v) =>
                update('customerPhone', v)
              }
            />

            <Field
              label="Asesor"
              value={form.sellerName}
              onChange={(v) =>
                update('sellerName', v)
              }
            />

            <Field
              label="Dirección"
              value={form.customerAddress}
              onChange={(v) =>
                update('customerAddress', v)
              }
            />

            <Field
              label="Fecha emisión"
              type="date"
              value={form.issueDate}
              onChange={(v) =>
                update('issueDate', v)
              }
            />

            <Field
              label="Validez días"
              type="number"
              value={form.validityDays}
              onChange={(v) =>
                update('validityDays', v)
              }
            />

          </div>
        </SectionCard>

        <SectionCard
          eyebrow="Paso 2"
          title="Producto y proveedor"
        >
          <div className="grid gap-4 md:grid-cols-2">

            <Field
              label="Producto"
              value={form.productName}
              onChange={(v) =>
                update('productName', v)
              }
            />

            <Field
              label="Cantidad"
              type="number"
              value={form.quantity}
              onChange={(v) =>
                update('quantity', v)
              }
            />

            <Field
              label="Proveedor"
              value={form.supplierName}
              onChange={(v) =>
                update('supplierName', v)
              }
            />

            <Field
              label="Teléfono proveedor"
              value={form.supplierPhone}
              onChange={(v) =>
                update('supplierPhone', v)
              }
            />

          </div>
        </SectionCard>

        <SectionCard
          eyebrow="Paso 3"
          title="Mercadería y logística"
        >
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">

            <Field
              label="Valor mercadería USD"
              type="number"
              value={form.merchandiseValueUsd}
              onChange={(v) =>
                update(
                  'merchandiseValueUsd',
                  v
                )
              }
            />

            <Field
            label="FOB efectos Aduana USD"
            type="number"
            value={form.customsFobUsd}
            onChange={(v) =>
                update(
                'customsFobUsd',
                v
                )
            }
            />

            <Field
              label="Envío almacén USD"
              type="number"
              value={form.warehouseShippingUsd}
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
              value={form.grossWeightKg}
              onChange={(v) =>
                update('grossWeightKg', v)
              }
            />

            <Field
              label="CBM"
              type="number"
              value={form.volumeCbm}
              onChange={(v) =>
                update('volumeCbm', v)
              }
            />

          </div>

          <label className="mt-5 flex items-center gap-3 rounded-2xl border border-slate-200 bg-slate-50 p-4">
            <input
              type="checkbox"
              checked={form.sensitiveProduct}
              onChange={(e) =>
                update(
                  'sensitiveProduct',
                  e.target.checked
                )
              }
            />

            <span className="text-sm font-bold text-slate-700">
              Producto sensible
            </span>
          </label>
        </SectionCard>

        <SectionCard
          eyebrow="Paso 4"
          title="Tributos y tipo de cambio"
        >
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-5">

            <Field
              label="GA %"
              type="number"
              value={form.gaPercent}
              onChange={(v) =>
                update('gaPercent', v)
              }
            />

            <Field
              label="IVA %"
              type="number"
              value={form.ivaPercent}
              onChange={(v) =>
                update('ivaPercent', v)
              }
            />

            <Field
              label="ICE %"
              type="number"
              value={form.icePercent}
              onChange={(v) =>
                update('icePercent', v)
              }
            />

            <Field
              label="T/C comercial"
              type="number"
              value={form.exchangeRate}
              onChange={(v) =>
                update('exchangeRate', v)
              }
            />

            <Field
              label="T/C impuestos"
              type="number"
              value={form.taxExchangeRate}
              onChange={(v) =>
                update('taxExchangeRate', v)
              }
            />

          </div>
        </SectionCard>

        <SectionCard
          eyebrow="Paso 5"
          title="Pago e importación"
        >
          <div className="grid gap-4 md:grid-cols-2">

            <label>
              <span className="mb-1 block text-[10px] font-black uppercase tracking-widest text-slate-400">
                Método de pago
              </span>

              <select
                value={form.paymentMethod}
                onChange={(e) =>
                  update(
                    'paymentMethod',
                    e.target.value
                  )
                }
                className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm font-bold"
              >
                <option value="TRANSFERENCIA">
                  Transferencia bancaria
                </option>
                <option value="ALIBABA">
                  Alibaba
                </option>
                <option value="ALIPAY">
                  Alipay
                </option>
              </select>
            </label>

            <label>
              <span className="mb-1 block text-[10px] font-black uppercase tracking-widest text-slate-400">
                NIT importador
              </span>

              <select
                value={form.importerNitType}
                onChange={(e) =>
                  update(
                    'importerNitType',
                    e.target.value
                  )
                }
                className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm font-bold"
              >
                <option value="GENUINO">
                  Genuino
                </option>
                <option value="HOLDING_COMEX">
                  Holding Comex
                </option>
                <option value="SILCEXBOL">
                  Silcexbol
                </option>
              </select>
            </label>

          </div>

          <label className="mt-4 flex items-center gap-3 rounded-2xl bg-slate-50 p-4">
            <input
              type="checkbox"
              checked={form.customerPaysInUsd}
              onChange={(e) =>
                update(
                  'customerPaysInUsd',
                  e.target.checked
                )
              }
            />

            <span className="text-sm font-bold text-slate-700">
              Cliente paga en dólares
            </span>
          </label>

          <textarea
            value={form.commercialTerms}
            onChange={(e) =>
              update(
                'commercialTerms',
                e.target.value
              )
            }
            placeholder="Términos comerciales u observaciones"
            rows={3}
            className="mt-4 w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm"
          />
        </SectionCard>

      </div>

      <aside className="h-fit rounded-3xl bg-slate-950 p-6 text-white xl:sticky xl:top-6">

        <p className="text-xs font-black uppercase tracking-[0.25em] text-orange-400">
          Resultado
        </p>

        <h2 className="mt-2 text-xl font-black">
        {isEditMode
            ? 'Corrección HBL'
            : 'Simulación HBL'}
        </h2>

        {!result ? (
          <div className="mt-6 rounded-2xl bg-white/5 p-5 text-sm text-slate-400">
            Completa los datos y calcula la operación.
          </div>
        ) : (
          <div className="mt-6 space-y-3">

            <ResultCard
              label="FOB"
              value={`USD ${Number(
                result.fobUsd || 0
              ).toLocaleString('es-BO')}`}
            />

            <ResultCard
              label="Comisión bancaria"
              value={`USD ${Number(
                result.bankTransferCommissionUsd ||
                  0
              ).toLocaleString('es-BO')}`}
            />

            <ResultCard
              label="Flete marítimo / terrestre"
              value={`USD ${Number(
                result.maritimeLandFreightUsd ||
                  0
              ).toLocaleString('es-BO')}`}
            />

            <ResultCard
              label="Impuestos"
              value={`Bs ${Number(
                result.customsTaxesBob || 0
              ).toLocaleString('es-BO')}`}
            />

            <ResultCard
              label="Comisión Genuino"
              value={`Bs ${Number(
                result.genuinoCommissionBob ||
                  0
              ).toLocaleString('es-BO')}`}
            />

            <ResultCard
              label="TOTAL"
              value={`Bs ${Number(
                result.totalBob || 0
              ).toLocaleString('es-BO')}`}
              featured
            />

            <ResultCard
              label="Precio unitario"
              value={`Bs ${Number(
                result.unitPriceBob || 0
              ).toLocaleString('es-BO')}`}
            />

          </div>
        )}

        {error && (
          <div className="mt-5 rounded-2xl bg-rose-500/10 p-4 text-sm font-bold text-rose-300">
            {error}
          </div>
        )}

        <button
          type="button"
          onClick={calculate}
          disabled={isCalculating}
          className="mt-6 flex w-full items-center justify-center gap-2 rounded-2xl bg-white px-5 py-4 text-sm font-black text-slate-950 disabled:opacity-50"
        >
          {isCalculating ? (
            <>
              <Loader2
                size={18}
                className="animate-spin"
              />
              Calculando...
            </>
          ) : (
            <>
              <Calculator size={18} />
              Calcular
            </>
          )}
        </button>

        <button
          type="button"
          onClick={save}
          disabled={!result || isSaving}
          className="mt-3 w-full rounded-2xl bg-orange-500 px-5 py-4 text-sm font-black text-white hover:bg-orange-600 disabled:opacity-50"
        >
            {isSaving
            ? 'Guardando...'
            : isEditMode
                ? 'Guardar correcciones'
                : 'Guardar proforma HBL'}
        </button>

      </aside>
    </div>
  );
}