import {
  useEffect,
  useState,
} from 'react';

import {
  useNavigate,
} from 'react-router-dom';

import {
  approveAirProforma,
  downloadAirProformaPdf,
  getAirProformaById,
  rejectAirProforma,
  submitAirForReview,
} from '../../../services/airService';

import { useAuth } from '../../../security/AuthProvider';

export default function AirProformaDetail({
  id,
}) {
  const navigate = useNavigate();
  const auth = useAuth();

  const [data, setData] =
    useState(null);

  const [loading, setLoading] =
    useState(true);

  const [actionLoading, setActionLoading] =
    useState(false);

  const [error, setError] =
    useState('');

  const roles =
    auth?.tokenParsed?.realm_access?.roles ||
    [];

  const isAdmin =
    roles.includes('ADMIN') ||
    roles.includes('OWNER');

  async function load() {
    setLoading(true);
    setError('');

    try {
      const response =
        await getAirProformaById(id);

      setData(response);
    } catch (err) {
      console.error(err);

      setError(
        err?.message ||
        'No se pudo cargar la proforma Aérea.'
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (!id) return;

    load();
  }, [id]);

  async function handleSubmitReview() {
    setActionLoading(true);
    setError('');

    try {
      const response =
        await submitAirForReview(id);

      setData(response);
    } catch (err) {
      console.error(err);

      setError(
        err?.message ||
        'No se pudo enviar la proforma a revisión.'
      );
    } finally {
      setActionLoading(false);
    }
  }

  async function handleApprove() {
    setActionLoading(true);
    setError('');

    try {
      const response =
        await approveAirProforma(id);

      setData(response);
    } catch (err) {
      console.error(err);

      setError(
        err?.message ||
        'No se pudo aprobar la proforma.'
      );
    } finally {
      setActionLoading(false);
    }
  }

  async function handleReject() {
    const reason =
      window.prompt(
        'Indique el motivo del rechazo:'
      );

    if (!reason?.trim()) {
      return;
    }

    setActionLoading(true);
    setError('');

    try {
      const response =
        await rejectAirProforma(
          id,
          reason.trim()
        );

      setData(response);
    } catch (err) {
      console.error(err);

      setError(
        err?.message ||
        'No se pudo rechazar la proforma.'
      );
    } finally {
      setActionLoading(false);
    }
  }

async function handlePdf() {
  setActionLoading(true);
  setError('');

  try {
    const blob =
      await downloadAirProformaPdf(id);

    const url =
      window.URL.createObjectURL(blob);

    window.open(
      url,
      '_blank',
      'noopener,noreferrer'
    );

    window.setTimeout(() => {
      window.URL.revokeObjectURL(url);
    }, 60000);
  } catch (err) {
    console.error(err);

    setError(
      err?.message ||
      'No se pudo generar el PDF de la proforma.'
    );
  } finally {
    setActionLoading(false);
  }
}

  if (loading) {
    return (
      <div className="rounded-3xl border border-slate-200 bg-white p-8 text-sm font-semibold text-slate-500">
        Cargando proforma Aérea...
      </div>
    );
  }

  if (error && !data) {
    return (
      <div className="rounded-3xl bg-red-50 p-6 text-sm font-bold text-red-600">
        {error}
      </div>
    );
  }

  if (!data) {
    return null;
  }

  const input =
    data.input || {};

  const calc =
    data.calculation || {};

  const status =
    data.status || 'DRAFT';

  const canEdit =
    status === 'DRAFT' ||
    status === 'REJECTED';

  const canSubmit =
    status === 'DRAFT' ||
    status === 'REJECTED';

  const canApprove =
    status === 'IN_REVIEW' &&
    isAdmin;

  const canReject =
    status === 'IN_REVIEW' &&
    isAdmin;

  return (
    <div className="space-y-5">

      {/* HEADER */}

      <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">

        <div className="flex flex-wrap items-start justify-between gap-4">

          <div>
            <p className="text-xs font-black uppercase tracking-[0.2em] text-orange-500">
              Proforma Aérea
            </p>

            <h1 className="mt-2 text-2xl font-black text-slate-950">
              {input.productName ||
                'Operación aérea'}
            </h1>

            <p className="mt-1 text-xs font-medium text-slate-400">
              Código: {data.id}
            </p>
          </div>

          <StatusBadge
            status={status}
          />

        </div>

        <div className="mt-6 grid gap-3 md:grid-cols-3 xl:grid-cols-6">

          <Info
            label="Cliente"
            value={
              input.customerName
            }
          />

          <Info
            label="Producto"
            value={
              input.productName
            }
          />

          <Info
            label="Asesor"
            value={
              input.sellerName
            }
          />

          <Info
            label="Fecha emisión"
            value={
              input.issueDate
            }
          />

          <Info
            label="Cantidad"
            value={
              input.quantity
            }
          />

          <Info
            label="Peso"
            value={
              input.grossWeightKg
                ? `${input.grossWeightKg} kg`
                : '-'
            }
          />

        </div>

      </section>

      {/* OPERACIÓN + USD */}

      <div className="grid gap-5 xl:grid-cols-2">

        <section className="rounded-3xl border border-slate-200 bg-white p-6">

          <SectionTitle
            eyebrow="Operación"
            title="Datos de la operación"
          />

          <div className="mt-5 grid gap-3 md:grid-cols-2">

            <Info
              label="Cliente"
              value={
                input.customerName
              }
            />

            <Info
              label="Teléfono"
              value={
                input.customerPhone
              }
            />

            <Info
              label="Dirección"
              value={
                input.customerAddress
              }
            />

            <Info
              label="Proveedor"
              value={
                input.supplierName
              }
            />

            <Info
              label="Teléfono proveedor"
              value={
                input.supplierPhone
              }
            />

            <Info
              label="Método de pago"
              value={
                input.paymentMethod
              }
            />

            <Info
              label="Peso bruto"
              value={
                input.grossWeightKg != null
                  ? `${input.grossWeightKg} kg`
                  : '-'
              }
            />

            <Info
              label="Validez"
              value={
                input.validityDays != null
                  ? `${input.validityDays} días`
                  : '-'
              }
            />

          </div>

        </section>

        <section className="rounded-3xl border border-slate-200 bg-white p-6">

          <SectionTitle
            eyebrow="Operación internacional"
            title="Resumen en dólares"
          />

          <div className="mt-5 overflow-hidden rounded-2xl border border-slate-200">

            <MoneyRow
              label="Valor FOB"
              value={
                moneyUsd(
                  calc.fobUsd
                )
              }
            />

            <MoneyRow
              label="Transporte fábrica → almacén"
              value={
                moneyUsd(
                  calc.warehouseShippingUsd
                )
              }
            />

            <MoneyRow
              label="Comisión giro bancario"
              value={
                moneyUsd(
                  calc.bankCommissionUsd
                )
              }
            />

            <MoneyRow
              label="Flete aéreo"
              value={
                moneyUsd(
                  calc.airFreightUsd
                )
              }
            />

            <MoneyRow
              label="Total USD"
              value={
                moneyUsd(
                  calc.subtotalUsd
                )
              }
              total
            />

          </div>

        </section>

      </div>

      {/* LIQUIDACIÓN ADUANERA */}

      <section className="rounded-3xl border border-slate-200 bg-white p-6">

        <SectionTitle
          eyebrow="Liquidación aduanera"
          title="Base tributaria e impuestos"
        />

        <div className="mt-5 grid gap-3 md:grid-cols-2 xl:grid-cols-5">

          <Info
            label="FOB efectos Aduana"
            value={
              moneyUsd(
                calc.customsFobUsd
              )
            }
          />

          <Info
            label="Flete efectos Aduana"
            value={
              moneyUsd(
                calc.customsFreightUsd
              )
            }
          />

          <Info
            label="Seguro"
            value={
              moneyUsd(
                calc.insuranceUsd
              )
            }
          />

          <Info
            label="Base imponible"
            value={
              moneyUsd(
                calc.taxableBaseUsd
              )
            }
          />

          <Info
            label="CIF frontera"
            value={
              moneyBob(
                calc.cifBorderBob
              )
            }
          />

        </div>

        <div className="mt-4 grid gap-3 md:grid-cols-4">

          <Highlight
            label="GA"
            value={
              moneyBob(
                calc.gaBob
              )
            }
          />

          <Highlight
            label="IVA"
            value={
              moneyBob(
                calc.ivaBob
              )
            }
          />

          <Highlight
            label="ICE"
            value={
              moneyBob(
                calc.iceBob
              )
            }
          />

          <Highlight
            label="Impuestos Aduana"
            value={
              moneyBob(
                calc.customsTaxesBob
              )
            }
          />

        </div>

      </section>

      {/* BOLIVIA */}

      <section className="rounded-3xl border border-slate-200 bg-white p-6">

        <SectionTitle
          eyebrow="Operación Bolivia"
          title="Desglose de costos"
        />

        <div className="mt-5 grid gap-3 md:grid-cols-2 xl:grid-cols-4">

          <Info
            label="Formulario ANB"
            value={
              moneyBob(
                calc.anbFormBob
              )
            }
          />

          <Info
            label="Almacenaje"
            value={
              moneyBob(
                calc.storageBob
              )
            }
          />

          <Info
            label="Carpeta"
            value={
              moneyBob(
                calc.folderBob
              )
            }
          />

          <Info
            label="Operación courier"
            value={
              moneyBob(
                calc.courierOperationalBob
              )
            }
          />

          <Info
            label="Impuestos nacionales"
            value={
              moneyBob(
                calc.nationalTaxesBob
              )
            }
          />

          <Info
            label="Agencia despachante"
            value={
              moneyBob(
                calc.dispatchAgencyCommissionBob
              )
            }
          />

          <Info
            label="Comisión Genuino"
            value={
              moneyBob(
                calc.genuinoCommissionBob
              )
            }
            orange
          />

          <Info
            label="Impuestos Aduana"
            value={
              moneyBob(
                calc.customsTaxesBob
              )
            }
          />

        </div>

        <div className="mt-5 grid gap-4 md:grid-cols-2 xl:grid-cols-4">

          <SummaryCard
            label="Total USD"
            value={
              moneyUsd(
                calc.subtotalUsd
              )
            }
          />

          <SummaryCard
            label="Costos Bolivia"
            value={
              moneyBob(
                calc.totalBoliviaBob
              )
            }
          />

          <SummaryCard
            label="Inversión total"
            value={
              moneyBob(
                calc.totalBob
              )
            }
            orange
          />

          <SummaryCard
            label="Precio unitario"
            value={
              moneyBob(
                calc.unitPriceBob
              )
            }
          />

        </div>

        <div className="mt-4 rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3">

          <div className="flex flex-wrap gap-x-6 gap-y-2 text-xs font-semibold text-slate-500">

            <span>
              T/C comercial:{' '}
              <strong className="text-slate-800">
                {input.exchangeRate ?? '-'}
              </strong>
            </span>

            <span>
              T/C impuestos:{' '}
              <strong className="text-slate-800">
                {input.taxExchangeRate ?? '-'}
              </strong>
            </span>

            <span>
              GA:{' '}
              <strong className="text-slate-800">
                {input.gaPercent ?? 0}%
              </strong>
            </span>

            <span>
              IVA:{' '}
              <strong className="text-slate-800">
                {input.ivaPercent ?? 0}%
              </strong>
            </span>

            <span>
              ICE:{' '}
              <strong className="text-slate-800">
                {input.icePercent ?? 0}%
              </strong>
            </span>

          </div>

        </div>

      </section>

      {/* RECHAZO */}

      {status === 'REJECTED' && (
        <section className="rounded-3xl border border-red-200 bg-red-50 p-5">

          <p className="text-xs font-black uppercase tracking-[0.16em] text-red-500">
            Motivo del rechazo
          </p>

          <p className="mt-2 font-semibold text-red-800">
            {data.rejectionReason ||
              'Sin motivo registrado.'}
          </p>

        </section>
      )}

      {error && (
        <div className="rounded-2xl bg-red-50 px-5 py-4 text-sm font-bold text-red-600">
          {error}
        </div>
      )}

      {/* ACTION BAR */}

      <section className="flex flex-wrap items-center justify-between gap-3 rounded-3xl border border-slate-200 bg-white p-4 shadow-sm">

        <button
          type="button"
          onClick={() =>
            navigate('/lcl')
          }
          className="rounded-xl border border-slate-200 px-5 py-3 text-sm font-black text-slate-600 hover:bg-slate-50"
        >
          ← Volver a proformas
        </button>

        <div className="flex flex-wrap justify-end gap-3">

        <button
        type="button"
        disabled={actionLoading}
        onClick={handlePdf}
        className="rounded-xl border border-orange-300 bg-orange-50 px-5 py-3 text-sm font-black text-orange-700 hover:bg-orange-100 disabled:opacity-50"
        >
        Descargar PDF
        </button>
          {canEdit && (
            <button
              type="button"
              onClick={() =>
                navigate(
                  `/air/${id}/editar`
                )
              }
              className="rounded-xl border border-slate-300 bg-white px-5 py-3 text-sm font-black text-slate-700 hover:bg-slate-50"
            >
              Editar
            </button>
          )}

          {canSubmit && (
            <button
              type="button"
              disabled={
                actionLoading
              }
              onClick={
                handleSubmitReview
              }
              className="rounded-xl bg-slate-950 px-5 py-3 text-sm font-black text-white hover:bg-slate-800 disabled:opacity-50"
            >
              Enviar a revisión
            </button>
          )}

          {canReject && (
            <button
              type="button"
              disabled={
                actionLoading
              }
              onClick={
                handleReject
              }
              className="rounded-xl bg-red-600 px-5 py-3 text-sm font-black text-white hover:bg-red-700 disabled:opacity-50"
            >
              Rechazar
            </button>
          )}

          {canApprove && (
            <button
              type="button"
              disabled={
                actionLoading
              }
              onClick={
                handleApprove
              }
              className="rounded-xl bg-emerald-600 px-5 py-3 text-sm font-black text-white hover:bg-emerald-700 disabled:opacity-50"
            >
              Aprobar
            </button>
          )}

        </div>

      </section>

    </div>
  );
}

function SectionTitle({
  eyebrow,
  title,
}) {
  return (
    <div>
      <p className="text-[11px] font-black uppercase tracking-[0.18em] text-orange-500">
        {eyebrow}
      </p>

      <h2 className="mt-1 text-xl font-black text-slate-950">
        {title}
      </h2>
    </div>
  );
}

function Info({
  label,
  value,
  orange = false,
}) {
  return (
    <div
      className={`rounded-2xl border p-4 ${
        orange
          ? 'border-orange-200 bg-orange-50'
          : 'border-slate-200 bg-slate-50'
      }`}
    >
      <p
        className={`text-[10px] font-black uppercase tracking-[0.12em] ${
          orange
            ? 'text-orange-500'
            : 'text-slate-400'
        }`}
      >
        {label}
      </p>

      <p
        className={`mt-2 text-sm font-black ${
          orange
            ? 'text-orange-700'
            : 'text-slate-950'
        }`}
      >
        {display(value)}
      </p>
    </div>
  );
}

function Highlight({
  label,
  value,
}) {
  return (
    <div className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
      <p className="text-xs font-semibold text-slate-500">
        {label}
      </p>

      <p className="mt-2 text-lg font-black text-slate-950">
        {value}
      </p>
    </div>
  );
}

function SummaryCard({
  label,
  value,
  orange = false,
}) {
  return (
    <div
      className={`rounded-2xl border p-5 ${
        orange
          ? 'border-orange-300 bg-orange-50'
          : 'border-slate-200 bg-white'
      }`}
    >
      <p
        className={`text-[10px] font-black uppercase tracking-[0.12em] ${
          orange
            ? 'text-orange-500'
            : 'text-slate-400'
        }`}
      >
        {label}
      </p>

      <p
        className={`mt-2 text-xl font-black ${
          orange
            ? 'text-orange-600'
            : 'text-slate-950'
        }`}
      >
        {value}
      </p>
    </div>
  );
}

function MoneyRow({
  label,
  value,
  total = false,
}) {
  return (
    <div
      className={`flex items-center justify-between gap-4 border-b border-slate-200 px-4 py-3 last:border-b-0 ${
        total
          ? 'bg-orange-50'
          : 'bg-white'
      }`}
    >
      <span
        className={`text-sm ${
          total
            ? 'font-black text-orange-700'
            : 'font-semibold text-slate-600'
        }`}
      >
        {label}
      </span>

      <span
        className={`text-sm font-black ${
          total
            ? 'text-orange-600'
            : 'text-slate-950'
        }`}
      >
        {value}
      </span>
    </div>
  );
}

function StatusBadge({
  status,
}) {
  const styles = {
    DRAFT:
      'bg-amber-50 text-amber-700',

    IN_REVIEW:
      'bg-blue-50 text-blue-700',

    APPROVED:
      'bg-emerald-50 text-emerald-700',

    REJECTED:
      'bg-red-50 text-red-700',
  };

  const labels = {
    DRAFT: 'Borrador',
    IN_REVIEW: 'En revisión',
    APPROVED: 'Aprobada',
    REJECTED: 'Rechazada',
  };

  return (
    <span
      className={`rounded-full px-4 py-2 text-xs font-black ${
        styles[status] ||
        'bg-slate-100 text-slate-600'
      }`}
    >
      {labels[status] ||
        status}
    </span>
  );
}

function moneyUsd(value) {
  return `USD ${formatMoney(
    value
  )}`;
}

function moneyBob(value) {
  return `Bs ${formatMoney(
    value
  )}`;
}

function formatMoney(value) {
  return Number(
    value || 0
  ).toLocaleString(
    'es-BO',
    {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }
  );
}

function display(value) {
  if (
    value === null ||
    value === undefined ||
    value === ''
  ) {
    return '-';
  }

  return value;
}