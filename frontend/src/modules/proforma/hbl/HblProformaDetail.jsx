import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import {
  getHblProformaById,
  submitHblForReview,
  approveHblProforma,
  rejectHblProforma,
  downloadHblPdf,
} from '../../../services/hblService';

import EnterprisePageHeader
  from '../../../components/enterprise/EnterprisePageHeader';

export default function HblProformaDetail({ id }) {
  const navigate = useNavigate();

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [working, setWorking] = useState(false);
  const [error, setError] = useState('');

  async function loadDetail() {
    try {
      setError('');

      const response =
        await getHblProformaById(id);

      setData(response);
    } catch (err) {
      console.error(err);

      setError(
        err.message ||
          'No se pudo cargar la proforma HBL.'
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (id) {
      loadDetail();
    }
  }, [id]);

  if (loading) {
    return (
      <div className="rounded-3xl border border-slate-200 bg-white p-8 text-sm font-bold text-slate-500">
        Cargando detalle HBL...
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-2xl border border-rose-200 bg-rose-50 p-5 text-sm font-bold text-rose-700">
        {error}
      </div>
    );
  }

  if (!data) {
    return null;
  }

  const input = data.input || {};
  const calc = data.calculation || {};

  const isDraft =
    data.status === 'DRAFT';

  const isReview =
    data.status === 'IN_REVIEW';

  const isApproved =
    data.status === 'APPROVED';

  const isRejected =
    data.status === 'REJECTED';

  const STATUS_LABELS = {
    DRAFT: 'Borrador',
    IN_REVIEW: 'En revisión',
    APPROVED: 'Aprobada',
    REJECTED: 'Rechazada',
  };

  const statusLabel =
    STATUS_LABELS[data.status] ||
    data.status ||
    '-';

  async function handleSubmitReview() {
    try {
      setWorking(true);

      await submitHblForReview(data.id);

      await loadDetail();
    } catch (err) {
      alert(
        err.message ||
          'No se pudo enviar la proforma a revisión.'
      );
    } finally {
      setWorking(false);
    }
  }

  async function handleApprove() {
    try {
      setWorking(true);

      await approveHblProforma(data.id);

      await loadDetail();
    } catch (err) {
      alert(
        err.message ||
          'No se pudo aprobar la proforma.'
      );
    } finally {
      setWorking(false);
    }
  }

  async function handleReject() {
    const reason = window.prompt(
      'Motivo del rechazo de la proforma:'
    );

    if (!reason || !reason.trim()) {
      return;
    }

    try {
      setWorking(true);

      await rejectHblProforma(
        data.id,
        reason.trim()
      );

      await loadDetail();
    } catch (err) {
      alert(
        err.message ||
          'No se pudo rechazar la proforma.'
      );
    } finally {
      setWorking(false);
    }
  }

  return (
    <div className="rounded-3xl bg-white p-6 text-slate-900 shadow-sm">

      <EnterprisePageHeader
        title="Proforma HBL"
        subtitle={`Código: ${data.id}`}
        statusLabel={statusLabel}
        meta={[
          {
            label: 'Cliente',
            value: input.customerName,
          },
          {
            label: 'Producto',
            value: input.productName,
          },
          {
            label: 'Asesor',
            value: input.sellerName,
          },
          {
            label: 'Fecha de emisión',
            value: input.issueDate
              ? new Date(
                  `${input.issueDate}T00:00:00`
                ).toLocaleDateString('es-BO')
              : '-',
          },
          {
            label: 'Cantidad',
            value: input.quantity,
          },
          {
            label: 'Peso',
            value: input.grossWeightKg
              ? `${input.grossWeightKg} kg`
              : '-',
          },
          {
            label: 'Volumen',
            value: input.volumeCbm
              ? `${input.volumeCbm} CBM`
              : '-',
          },
        ]}
      />

      {isReview && (
        <div className="mb-6 rounded-2xl border border-amber-200 bg-amber-50 p-4 text-sm font-bold text-amber-700">
          Proforma enviada a revisión interna.
        </div>
      )}

      {isApproved && (
        <div className="mb-6 rounded-2xl border border-emerald-200 bg-emerald-50 p-4 text-sm font-bold text-emerald-700">
          Proforma aprobada internamente.
        </div>
      )}

      {isRejected && (
        <div className="mb-6 rounded-2xl border border-rose-200 bg-rose-50 p-5">
          <p className="font-black text-rose-700">
            Proforma rechazada
          </p>

          <p className="mt-2 text-sm text-rose-700">
            <strong>Motivo:</strong>{' '}
            {data.rejectionReason ||
              'Sin motivo registrado'}
          </p>

          <p className="mt-2 text-sm text-rose-600">
            Corrige los datos observados y vuelve a enviarla a revisión.
          </p>

          <button
            type="button"
            onClick={() =>
              navigate(
                `/hbl/${data.id}/editar`
              )
            }
            className="mt-4 rounded-xl bg-rose-600 px-5 py-2.5 text-sm font-black text-white hover:bg-rose-700"
          >
            Editar proforma
          </button>
        </div>
      )}

      {/* =========================================
          DATOS DE LA OPERACIÓN
      ========================================== */}

      <section className="grid gap-6 xl:grid-cols-2">

        <DetailCard title="Datos de la operación">
          <div className="grid gap-3 md:grid-cols-2">

            <Info
              label="Cliente"
              value={input.customerName}
            />

            <Info
              label="Teléfono"
              value={input.customerPhone}
            />

            <Info
              label="Dirección"
              value={input.customerAddress}
            />

            <Info
              label="Proveedor"
              value={input.supplierName}
            />

            <Info
              label="Teléfono proveedor"
              value={input.supplierPhone}
            />

            <Info
              label="Producto"
              value={input.productName}
            />

            <Info
              label="Cantidad"
              value={input.quantity}
            />

            <Info
              label="Producto sensible"
              value={
                input.sensitiveProduct
                  ? 'Sí'
                  : 'No'
              }
            />

          </div>
        </DetailCard>

        <DetailCard title="Resumen en dólares">
          <div className="space-y-2">

            <MoneyRow
              label="Valor FOB"
              value={calc.fobUsd}
              currency="USD"
            />

            <MoneyRow
              label="Comisión giro bancario"
              value={
                calc.bankTransferCommissionUsd
              }
              currency="USD"
            />

            <MoneyRow
              label="Flete marítimo / terrestre"
              value={
                calc.maritimeLandFreightUsd
              }
              currency="USD"
            />

            <MoneyRow
              label="Recargo producto sensible"
              value={
                calc.sensitiveProductSurchargeUsd
              }
              currency="USD"
            />

            <MoneyRow
              label="Subtotal USD"
              value={calc.subtotalUsd}
              currency="USD"
              highlight
            />

          </div>
        </DetailCard>

      </section>

      {/* =========================================
          OPERACIÓN BOLIVIA
      ========================================== */}

      <section className="mt-6 rounded-3xl border border-slate-200 bg-white p-6">

        <div>
          <p className="text-xs font-black uppercase tracking-[0.2em] text-orange-500">
            Operación Bolivia
          </p>

          <h2 className="mt-1 text-xl font-black text-slate-950">
            Desglose de costos e impuestos
          </h2>
        </div>

<div className="mt-6 rounded-2xl border border-slate-200 bg-slate-50 p-5">
  <p className="text-xs font-black uppercase tracking-[0.2em] text-orange-500">
    Liquidación aduanera
  </p>

  <div className="mt-4 grid gap-3 md:grid-cols-2 xl:grid-cols-5">
    <Info
      label="FOB efectos Aduana"
      value={`USD ${formatNumber(calc.customsFobUsd)}`}
    />

    <Info
      label="Flete efectos Aduana"
      value={`USD ${formatNumber(calc.customsFreightUsd)}`}
    />

    <Info
      label="Seguro"
      value={`USD ${formatNumber(calc.insuranceUsd)}`}
    />

    <Info
      label="Base imponible"
      value={`USD ${formatNumber(calc.taxableBaseUsd)}`}
    />

    <Info
      label="CIF frontera"
      value={`Bs ${formatNumber(calc.cifBorderBob)}`}
    />
  </div>
</div>

        <div className="mt-6 grid gap-3 md:grid-cols-2 xl:grid-cols-4">

          <CostCard
            label="GA"
            value={calc.gaBob}
          />

          <CostCard
            label="IVA"
            value={calc.ivaBob}
          />

          <CostCard
            label="ICE"
            value={calc.iceBob}
          />

          <CostCard
            label="Impuestos Aduana"
            value={calc.customsTaxesBob}
          />

            <CostCard
            label="ALBO / despacho"
            value={calc.alboCustomsClearanceBob}
            />

          <CostCard
            label="Despachante"
            value={
              calc.dispatchAgentCommissionBob
            }
          />

          <CostCard
            label="Gastos extra NIT"
            value={calc.extraNitExpensesBob}
          />

          <CostCard
            label="Comisión Genuino"
            value={calc.genuinoCommissionBob}
            highlighted
          />

        </div>

        <div className="mt-6 grid gap-4 md:grid-cols-2 xl:grid-cols-4">

          <SummaryCard
            label="Total en dólares"
            value={`USD ${formatNumber(
              calc.subtotalUsd
            )}`}
          />

          <SummaryCard
            label="Costos Bolivia"
            value={`Bs ${formatNumber(
              calc.totalBoliviaBob ||
                calc.totalOperationBob
            )}`}
          />

          <SummaryCard
            label="Inversión total"
            value={`Bs ${formatNumber(
              calc.totalBob
            )}`}
            featured
          />

          <SummaryCard
            label="Precio unitario"
            value={`Bs ${formatNumber(
              calc.unitPriceBob
            )}`}
          />

        </div>

        <div className="mt-3 flex flex-wrap gap-x-6 gap-y-2 rounded-2xl border border-slate-200 bg-slate-50 px-5 py-3 text-xs text-slate-500">

          <span>
            <b className="text-slate-700">
              T/C comercial:
            </b>{' '}
            {formatNumber(
              input.exchangeRate
            )}
          </span>

          <span>
            <b className="text-slate-700">
              T/C impuestos:
            </b>{' '}
            {formatNumber(
              input.taxExchangeRate
            )}
          </span>

          <span>
            <b className="text-slate-700">
              GA:
            </b>{' '}
            {input.gaPercent ?? 0}%
          </span>

          <span>
            <b className="text-slate-700">
              IVA:
            </b>{' '}
            {input.ivaPercent ?? 0}%
          </span>

          <span>
            <b className="text-slate-700">
              ICE:
            </b>{' '}
            {input.icePercent ?? 0}%
          </span>

        </div>

      </section>

      {/* =========================================
          PAGO / IMPORTADOR
      ========================================== */}

      <section className="mt-6 rounded-3xl border border-slate-200 bg-white p-6">

        <h2 className="text-xl font-black text-slate-900">
          Condiciones de la operación
        </h2>

        <div className="mt-5 grid gap-4 md:grid-cols-2 xl:grid-cols-4">

          <Info
            label="Método de pago"
            value={input.paymentMethod}
          />

          <Info
            label="NIT importador"
            value={input.importerNitType}
          />

          <Info
            label="Cliente paga en USD"
            value={
              input.customerPaysInUsd
                ? 'Sí'
                : 'No'
            }
          />

          <Info
            label="Validez"
            value={
              input.validityDays
                ? `${input.validityDays} días`
                : '-'
            }
          />

        </div>

        {input.commercialTerms && (
          <div className="mt-5 rounded-2xl bg-slate-50 p-5">
            <p className="text-xs font-black uppercase tracking-wide text-slate-400">
              Términos comerciales
            </p>

            <p className="mt-2 text-sm text-slate-700">
              {input.commercialTerms}
            </p>
          </div>
        )}

      </section>

      {/* =========================================
          WORKFLOW
      ========================================== */}

      <section className="mt-6 rounded-3xl border border-slate-200 bg-slate-50 p-5">

        <div className="flex flex-wrap justify-end gap-3">

          {(isDraft || isRejected) && (
            <>
              {isRejected && (
                <button
                  type="button"
                  onClick={() =>
                    navigate(
                      `/hbl/${data.id}/editar`
                    )
                  }
                  className="rounded-2xl border border-slate-300 bg-white px-5 py-3 text-sm font-black text-slate-700"
                >
                  Editar
                </button>
              )}

              <button
                type="button"
                onClick={handleSubmitReview}
                disabled={working}
                className="rounded-2xl bg-slate-950 px-5 py-3 text-sm font-black text-white hover:bg-slate-800 disabled:opacity-50"
              >
                Enviar a revisión
              </button>
            </>
          )}

          {isReview && (
            <>
              <button
                type="button"
                onClick={handleReject}
                disabled={working}
                className="rounded-2xl bg-rose-600 px-5 py-3 text-sm font-black text-white hover:bg-rose-700 disabled:opacity-50"
              >
                Rechazar
              </button>

              <button
                type="button"
                onClick={handleApprove}
                disabled={working}
                className="rounded-2xl bg-emerald-600 px-5 py-3 text-sm font-black text-white hover:bg-emerald-700 disabled:opacity-50"
              >
                Aprobar
              </button>

                <button
                type="button"
                onClick={() =>
                    downloadHblPdf(data.id)
                }
                className="rounded-2xl bg-slate-950 px-5 py-3 text-sm font-black text-white hover:bg-slate-800"
                >
                Descargar PDF
                </button>
            </>
          )}

        </div>

      </section>

    </div>
  );
}

function DetailCard({
  title,
  children,
}) {
  return (
    <section className="rounded-3xl border border-slate-200 bg-white p-6">
      <h2 className="text-xl font-black text-slate-900">
        {title}
      </h2>

      <div className="mt-5">
        {children}
      </div>
    </section>
  );
}

function Info({
  label,
  value,
}) {
  return (
    <div className="rounded-2xl bg-slate-50 p-4">
      <p className="text-[10px] font-black uppercase tracking-wider text-slate-400">
        {label}
      </p>

      <p className="mt-1 text-sm font-black text-slate-900">
        {value ?? '-'}
      </p>
    </div>
  );
}

function MoneyRow({
  label,
  value,
  currency = 'Bs',
  highlight = false,
}) {
  return (
    <div
      className={`flex items-center justify-between rounded-2xl px-4 py-3 ${
        highlight
          ? 'bg-orange-50'
          : 'bg-slate-50'
      }`}
    >
      <span className="text-sm font-bold text-slate-500">
        {label}
      </span>

      <span
        className={`text-sm font-black ${
          highlight
            ? 'text-orange-700'
            : 'text-slate-900'
        }`}
      >
        {currency}{' '}
        {formatNumber(value)}
      </span>
    </div>
  );
}

function CostCard({
  label,
  value,
  highlighted = false,
}) {
  return (
    <div
      className={`rounded-2xl border p-4 ${
        highlighted
          ? 'border-orange-200 bg-orange-50'
          : 'border-slate-200 bg-slate-50'
      }`}
    >
      <p
        className={`text-xs font-bold ${
          highlighted
            ? 'text-orange-600'
            : 'text-slate-500'
        }`}
      >
        {label}
      </p>

      <p
        className={`mt-2 text-xl font-black ${
          highlighted
            ? 'text-orange-700'
            : 'text-slate-950'
        }`}
      >
        Bs {formatNumber(value)}
      </p>
    </div>
  );
}

function SummaryCard({
  label,
  value,
  featured = false,
}) {
  return (
    <div
      className={`rounded-2xl border p-5 ${
        featured
          ? 'border-orange-200 bg-orange-50'
          : 'border-slate-200 bg-white'
      }`}
    >
      <p
        className={`text-xs font-black uppercase tracking-wider ${
          featured
            ? 'text-orange-600'
            : 'text-slate-400'
        }`}
      >
        {label}
      </p>

      <p
        className={`mt-2 text-2xl font-black ${
          featured
            ? 'text-orange-700'
            : 'text-slate-950'
        }`}
      >
        {value}
      </p>
    </div>
  );
}

function formatNumber(value) {
  const number = Number(value || 0);

  return number.toLocaleString(
    'es-BO',
    {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }
  );
}