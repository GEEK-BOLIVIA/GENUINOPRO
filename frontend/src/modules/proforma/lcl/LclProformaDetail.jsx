import { useEffect, useRef, useState } from 'react';
import { Loader2, Save } from 'lucide-react';
import { apiFetch } from '../../../services/api';

import { canApproveProforma, canCreateProforma } from '../../../security/roles';

import { downloadLclPdf } from '../../../services/api';
import { useAuth } from '../../../security/AuthProvider';

import { recalculateLclProforma } from '../../../services/lclService';

import { submitLclForReview, clientAcceptLclProforma, clientRejectLclProforma,} from '../../../services/lclService';

import { useNavigate } from 'react-router-dom';

import {
  clientExistsByLeadId,
  createClientAccount,
  markLeadAsClient,
  markOpportunityAsClient,
} from '../../../services/clientService';

import {
  getProformaAttachments,
  createProformaAttachment,
  deleteProformaAttachment,
  uploadProformaAttachmentImage,
} from '../../../services/proformaAttachmentService';

import EnterprisePageHeader from '../../../components/enterprise/EnterprisePageHeader';

export default function LclProformaDetail({ id }) {
  const [data, setData] = useState(null);
  const [editableLines, setEditableLines] = useState([]);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);
  const navigate = useNavigate();

  const [showAttachmentForm, setShowAttachmentForm] = useState(false);

  const [clientExists, setClientExists] = useState(false);
  const [showClientModal, setShowClientModal] = useState(false);


  const [attachments, setAttachments] = useState([]);

  const [newAttachment, setNewAttachment] = useState({
    attachmentType: 'ALIBABA_LINK',
    attachmentUrl: '',
    title: '',
    description: '',
  });

  const [imageForm, setImageForm] = useState({
    attachmentType: 'PRODUCT_IMAGE',
    title: '',
    description: '',
  });


  const imageFileRef = useRef(null);

    const auth = useAuth();
    const userRoles = auth?.roles || [];
    const canEditDraft = canCreateProforma(userRoles);
    const canApprove = canApproveProforma(userRoles);
    const { token } = useAuth();

  async function loadDetail() {
    if (!id) return;

    try {
      setError('');

      const response = await apiFetch(`/typed-proformas/lcl/${id}`);

      console.log('LCL DETAIL RESPONSE', response);

      setData(response);
      setEditableLines(response.chargeLines || []);

      if (response.customerId) {
        const exists = await clientExistsByLeadId(response.customerId);
        setClientExists(exists === true);
      } else {
        setClientExists(false);
      }
    } catch (err) {
      setError(err.message || 'No se pudo cargar el detalle LCL');
    }
  }

  useEffect(() => {
    loadDetail();
  }, [id]);

  useEffect(() => {
    loadAttachments();
  }, [id]);

  function handleLineChange(index, field, value) {
    const updated = [...editableLines];

    updated[index] = {
      ...updated[index],
      [field]: value === '' ? '' : Number(value),
    };

    const quantity = Number(updated[index].quantity || 0);
    const unitPrice = Number(updated[index].unitPrice || 0);
    updated[index].total = quantity * unitPrice;

    setEditableLines(updated);
  }

  async function saveChanges() {
    if (!data?.id) return;

    try {
      setIsSaving(true);
      setError('');

      const payload = {
        updatedBy: 'hugo',
        chargeLines: editableLines
          .filter((line) => ['ALBO', 'VAR', 'COM'].includes(line.code))
          .map((line) => ({
            code: line.code,
            quantity: Number(line.quantity || 0),
            unitPrice: Number(line.unitPrice || 0),
          })),
      };

      const response = await recalculateLclProforma(data.id, payload);

      setData(response);
      setEditableLines(response.chargeLines || []);
    } catch (err) {
      setError(err.message || 'No se pudo guardar el recálculo');
    } finally {
      setIsSaving(false);
    }
  }

  async function submitForReview() {
    if (!data?.id) return;

    try {
      setIsSaving(true);
      setError('');

      const response = await apiFetch(`/typed-proformas/lcl/${data.id}/submit-review`, {
        method: 'POST',
        body: JSON.stringify({ actor: 'hugo' }),
      });

      setData(response);
      setEditableLines(response.chargeLines || []);
    } catch (err) {
      setError(err.message || 'No se pudo enviar a revisión');
    } finally {
      setIsSaving(false);
    }
  }

  async function approveProforma() {
    if (!data?.id) return;

    try {
      setIsSaving(true);
      setError('');

      const response = await apiFetch(`/typed-proformas/lcl/${data.id}/approve`, {
        method: 'POST',
        body: JSON.stringify({}),
      });

      setData(response);
      setEditableLines(response.chargeLines || []);
    } catch (err) {
      setError(err.message || 'No se pudo aprobar la proforma');
    } finally {
      setIsSaving(false);
    }
  }

  const handleDownloadPdf = async () => {
    try {
      const blob = await downloadLclPdf(id, token);

      const url = window.URL.createObjectURL(blob);

      const a = document.createElement('a');

      a.href = url;
      a.download = `proforma-${id}.pdf`;

      document.body.appendChild(a);

      a.click();

      a.remove();

      window.URL.revokeObjectURL(url);

    } catch (error) {
      console.error(error);
      alert('No se pudo descargar PDF');
    }
  };

  async function rejectProforma() {
    if (!data?.id) return;

    const reason = window.prompt('Motivo del rechazo');
    if (!reason) return;

    try {
      setIsSaving(true);
      setError('');

      const response = await apiFetch(`/typed-proformas/lcl/${data.id}/reject`, {
        method: 'POST',
        body: JSON.stringify({ reason }),
      });

      setData(response);
      setEditableLines(response.chargeLines || []);
    } catch (err) {
      setError(err.message || 'No se pudo rechazar la proforma');
    } finally {
      setIsSaving(false);
    }
  }

  if (error) {
    return (
      <div className="mt-4 rounded-2xl bg-rose-50 p-4 text-sm text-rose-700">
        {error}
      </div>
    );
  }

  if (!data) {
    return (
      <div className="mt-4 rounded-2xl bg-slate-100 p-4 text-sm text-slate-500">
        Cargando detalle LCL...
      </div>
    );
  }

  
  const usdLines = editableLines.filter((line) =>
    ['FOB', 'GIRO', 'MAR'].includes(line.code)
  );

  const bsLines = editableLines.filter((line) =>
    ['ADU', 'ALBO', 'COM', 'VAR'].includes(line.code)
  );

  const lineByCode = (code) =>
    editableLines.find((line) => line.code === code);

  const fobLine = lineByCode('FOB');
  const giroLine = lineByCode('GIRO');
  const maritimeLine = lineByCode('MAR');

  const customsLine = lineByCode('ADU');
  const alboLine = lineByCode('ALBO');
  const miscellaneousLine = lineByCode('VAR');
  const genuinoCommissionLine = lineByCode('COM');

  const usdSubtotal = usdLines.reduce(
    (acc, line) => acc + Number(line.total || 0),
    0
  );

  const bsSubtotal = bsLines.reduce(
    (acc, line) => acc + Number(line.total || 0),
    0
  );

const commercialExchangeRate =
  Number(data.exchangeRate ?? 10);

const taxExchangeRate =
  Number(data.taxExchangeRate ?? 8);

const convertedUsdToBs =
  commercialExchangeRate * usdSubtotal;

  const grandTotal =
    convertedUsdToBs + bsSubtotal;

const quantity =
  Number(data.packageCount || 1);

const unitPrice =
  quantity > 0
    ? grandTotal / quantity
    : 0;

            const isDraft = data?.status === 'DRAFT';
            const isReview = data?.status === 'IN_REVIEW';
            const isApproved = data?.status === 'APPROVED';
            const isRejected = data?.status === 'REJECTED';

            const canEditProforma = isDraft || isRejected;

            const STATUS_LABELS = {
              DRAFT: 'Borrador',
              IN_REVIEW: 'En revisión',
              APPROVED: 'Aprobada',
              REJECTED: 'Rechazada',
            };

            const statusLabel = STATUS_LABELS[data.status] || data.status;   
  
  async function handleClientAccept() {
    const confirmed = window.confirm(
      '¿Confirmas que el cliente aprobó esta proforma?'
    );

    if (!confirmed) return;

    try {
      const response = await clientAcceptLclProforma(data.id);
      setData(response);
      alert('Proforma aprobada por cliente.');
    } catch (error) {
      alert(error.message || 'No se pudo aprobar la proforma por cliente.');
    }
  }

  async function handleClientReject() {
    const reason = window.prompt('Motivo de rechazo del cliente:');

    if (!reason || !reason.trim()) {
      alert('El motivo de rechazo es obligatorio.');
      return;
    }

    try {
      const response = await clientRejectLclProforma(data.id, reason.trim());
      setData(response);
      alert('Proforma rechazada por cliente.');
    } catch (error) {
      alert(error.message || 'No se pudo rechazar la proforma por cliente.');
    }
  }

  async function loadAttachments() {
    if (!id) return;
    const response = await getProformaAttachments(id);
    setAttachments(Array.isArray(response) ? response : []);
  }

  async function handleSaveAttachment() {
    if (!newAttachment.attachmentUrl.trim()) {
      alert('El link es obligatorio.');
      return;
    }

    await createProformaAttachment(id, {
      attachmentType: 'ALIBABA_LINK',
      title: newAttachment.title || 'Link Alibaba',
      attachmentUrl: newAttachment.attachmentUrl,
      description: newAttachment.description || '',
    });

    setNewAttachment({
      attachmentType: 'ALIBABA_LINK',
      attachmentUrl: '',
      title: '',
      description: '',
    });

    await loadAttachments();
  }

  async function handleUploadImage() {
    const file = imageFileRef.current?.files?.[0];

    if (!file) {
      alert('Debes seleccionar una imagen.');
      return;
    }

    const formData = new FormData();
    formData.append('file', file);
    formData.append('attachmentType', imageForm.attachmentType);
    formData.append('title', imageForm.title || 'Imagen de producto');
    formData.append('description', imageForm.description || '');

    await uploadProformaAttachmentImage(id, formData);

    setImageForm({
      attachmentType: 'PRODUCT_IMAGE',
      title: '',
      description: '',
    });

    if (imageFileRef.current) {
      imageFileRef.current.value = '';
    }

    await loadAttachments();
  }

  async function handleDeleteAttachment(attachmentId) {
    await deleteProformaAttachment(id, attachmentId);
    await loadAttachments();
  }

  return (
    <div className="mt-8 rounded-3xl bg-white p-6 text-slate-900 shadow">
    <EnterprisePageHeader
      title="Proforma LCL"
      subtitle={`Código: ${data.code || data.id}`}
      statusLabel={statusLabel}
      meta={[
        { label: 'Cliente', value: data.customerName },
        { label: 'Producto', value: data.cargoDescription },
        { label: 'Asesor', value: data.sellerName },
        { label: 'Origen', value: `${data.originCity || '-'} - ${data.originCountry || '-'}` },
        { label: 'Destino', value: `${data.destinationCity || '-'} - ${data.destinationCountry || '-'}` },
        {
          label: 'Fecha de emisión',
          value: data.issueDate
            ? new Date(`${data.issueDate}T00:00:00`).toLocaleDateString('es-BO')
            : '-',
        },
        {
          label: 'Cantidad',
          value: data.packageCount ?? '-',
        },
      ]}
    />

      {data.status === 'APPROVED' && (
        <div className="mb-4 rounded-2xl bg-emerald-50 p-4 text-sm font-bold text-emerald-700">
          Proforma aprobada. Este documento está bloqueado y listo para generación de PDF.
        </div>
      )}

      {data.status === 'IN_REVIEW' && (
        <div className="mb-4 rounded-2xl bg-amber-50 p-4 text-sm font-bold text-amber-700">
          Proforma enviada a revisión. Solo un usuario autorizado puede aprobar o rechazar.
        </div>
      )}

      {isRejected && (
        <div className="mb-4 rounded-2xl border border-rose-200 bg-rose-50 p-5">
          <p className="font-black text-rose-700">
            Proforma rechazada
          </p>

          <p className="mt-2 text-sm text-rose-700">
            <strong>Motivo:</strong>{' '}
            {data.rejectionReason || 'Sin motivo registrado'}
          </p>

          <p className="mt-2 text-sm text-rose-600">
            Realiza las correcciones necesarias y vuelve a enviarla a revisión.
          </p>

          <button
            type="button"
            onClick={() => navigate(`/lcl/${data.id}/editar`)}
            className="mt-4 rounded-xl bg-rose-600 px-5 py-2.5 text-sm font-black text-white hover:bg-rose-700"
          >
            Editar proforma
          </button>
        </div>
      )}

        
      <section className="mt-7 rounded-3xl border border-slate-200 bg-white p-6">
        <div>
          <p className="text-xs font-black uppercase tracking-[0.2em] text-orange-500">
            Estructura de costos
          </p>

          <h2 className="mt-1 text-xl font-black text-slate-950">
            Desglose de la operación
          </h2>

          <p className="mt-1 text-sm text-slate-500">
            Distribución referencial de los principales componentes de la importación.
          </p>
        </div>

        <div className="mt-6">
          <p className="mb-3 text-xs font-black uppercase tracking-wider text-slate-400">
            Pagos expresados en dólares
          </p>

          <div className="grid gap-3 md:grid-cols-3">
            <CostCard
              label="Valor FOB de la mercadería"
              value={fobLine?.total}
              currency="USD"
            />

            <CostCard
              label="Comisión giro bancario"
              value={giroLine?.total}
              currency="USD"
            />

            <CostCard
              label="Transporte internacional"
              value={maritimeLine?.total}
              currency="USD"
            />
          </div>
        </div>

        <div className="mt-6">
          <p className="mb-3 text-xs font-black uppercase tracking-wider text-slate-400">
            Costos de operación en Bolivia
          </p>

          <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-4">
            <CostCard
              label="Impuestos aduaneros"
              value={customsLine?.total}
              currency="Bs"
            />

            <CostCard
              label="Despacho / ALBO"
              value={alboLine?.total}
              currency="Bs"
            />

            <CostCard
              label="Gastos varios"
              value={miscellaneousLine?.total}
              currency="Bs"
            />

            <CostCard
              label="Comisión Genuino"
              value={genuinoCommissionLine?.total}
              currency="Bs"
              highlighted
            />
          </div>
        </div>
      </section>

        <section className="mt-6">
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">

            <SummaryCard
              label="Total en dólares"
              value={`USD ${usdSubtotal.toLocaleString('es-BO', {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2,
              })}`}
              helper="FOB + giro + transporte"
            />

            <SummaryCard
              label="Costos en Bolivia"
              value={`Bs ${bsSubtotal.toLocaleString('es-BO', {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2,
              })}`}
              helper="Aduana + ALBO + gastos + comisión"
            />

            <SummaryCard
              label="Inversión total"
              value={`Bs ${grandTotal.toLocaleString('es-BO', {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2,
              })}`}
              helper="Inversión referencial"
              featured
            />

            <SummaryCard
              label="Precio unitario"
              value={`Bs ${unitPrice.toLocaleString('es-BO', {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2,
              })}`}
              helper={`${quantity} unidad${quantity === 1 ? '' : 'es'}`}
            />

          </div>

          <div className="mt-3 flex flex-wrap gap-x-6 gap-y-2 rounded-2xl border border-slate-200 bg-slate-50 px-5 py-3 text-xs text-slate-500">

            <span>
              <b className="text-slate-700">T/C comercial:</b>{' '}
              {commercialExchangeRate.toLocaleString('es-BO', {
                minimumFractionDigits: 2,
                maximumFractionDigits: 4,
              })}
            </span>

            <span>
              <b className="text-slate-700">T/C impuestos:</b>{' '}
              {taxExchangeRate.toLocaleString('es-BO', {
                minimumFractionDigits: 2,
                maximumFractionDigits: 4,
              })}
            </span>

            <span>
              <b className="text-slate-700">Comisión Genuino:</b>{' '}
              Bs{' '}
              {Number(data.estimatedProfit || 0).toLocaleString('es-BO', {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2,
              })}
            </span>

            <span>
              <b className="text-slate-700">Reglas:</b>{' '}
              {data.calculationRuleVersion || 'Versión histórica LCL'}
            </span>

          </div>
        </section>

      <div className="space-y-6">
        
        <section className="overflow-hidden rounded-2xl border border-slate-200">
          <div className="bg-orange-50 px-4 py-3 text-center text-sm font-black uppercase tracking-widest text-orange-600">
            Expresado en dólares americanos
          </div>

          <table className="w-full border-collapse text-sm">
            <thead className="bg-slate-100 text-slate-600">
              <tr>
                <th className="px-4 py-3 text-left">Descripción</th>
                <th className="px-4 py-3 text-right">Total</th>
              </tr>
            </thead>
            <tbody>
              {usdLines.map((line, index) => (
                <tr key={line.code} className="border-t border-slate-100">
                  <td className="px-4 py-3 font-bold">{line.description}</td>

                  <td className="px-4 py-3 text-right font-black">

                  {canEditProforma && line.editable ? (
                    <input
                      type="number"
                      value={line.unitPrice || 0}
                      onChange={(e) =>
                        handleLineChange(index, 'unitPrice', e.target.value)
                      }
                      className="w-32 rounded-lg border border-slate-300 px-2 py-1 text-right"
                    />
                  ) : (
                    <>USD {Number(line.total || 0).toLocaleString()}</>
                  )}
                  </td>


                </tr>
              ))}
              <tr className="border-t border-slate-200 bg-yellow-50">
                <td className="px-4 py-3 text-right font-black">TOTAL EN USD</td>
                <td className="px-4 py-3 text-right font-black text-slate-950">
                  USD {usdSubtotal.toLocaleString()}
                </td>
              </tr>
            </tbody>
          </table>
        </section>
        
        <section className="overflow-hidden rounded-2xl border border-slate-200">
          <div className="bg-orange-50 px-4 py-3 text-center text-sm font-black uppercase tracking-widest text-orange-600">
            Expresado en bolivianos
          </div>

          <table className="w-full border-collapse text-sm">
            <thead className="bg-slate-100 text-slate-600">
              <tr>
                <th className="px-4 py-3 text-left">Descripción</th>
                <th className="px-4 py-3 text-right">Total</th>
              </tr>
            </thead>
            <tbody>
              {bsLines.map((line, index) => (
                <tr key={line.code} className="border-t border-slate-100">
                  <td className="px-4 py-3 font-bold">{line.description}</td>

                  <td className="px-4 py-3 text-right font-black">

                    {canEditProforma && ['ALBO', 'VAR', 'COM'].includes(line.code) ? (
                      <input
                        type="number"
                        value={line.unitPrice || 0}
                        onChange={(e) =>
                          handleLineChange(
                            usdLines.length + index,
                            'unitPrice',
                            e.target.value
                          )
                        }
                        className="w-32 rounded-lg border border-slate-300 px-2 py-1 text-right"
                      />
                    ) : (
                      <>Bs {Number(line.total || 0).toLocaleString()}</>
                    )}
                  </td>
                </tr>
              ))}
              <tr className="border-t border-slate-200 bg-yellow-50">
                <td className="px-4 py-3 text-right font-black">TOTAL EN BS</td>
                <td className="px-4 py-3 text-right font-black text-slate-950">
                  Bs {bsSubtotal.toLocaleString()}
                </td>
              </tr>
            </tbody>
          </table>
        </section>


        <section className="overflow-hidden rounded-2xl border border-slate-200">
          <div className="bg-slate-950 px-4 py-3 text-center text-sm font-black uppercase tracking-widest text-white">
            Resumen final en bolivianos
          </div>

          <table className="w-full border-collapse text-sm">
            <tbody>
              <tr className="border-t border-slate-100">
                <td className="px-4 py-3 font-bold">Valor FOB + Comisión + Transporte</td>
                <td className="px-4 py-3 text-right font-black">
                  USD {usdSubtotal.toLocaleString()}
                </td>
              </tr>
              <tr className="border-t border-slate-100">
                <td className="px-4 py-3 font-bold">Conversión en Bs</td>
                <td className="px-4 py-3 text-right font-black">
                  Bs {convertedUsdToBs.toLocaleString()}
                </td>
              </tr>
              <tr className="border-t border-slate-100">
                <td className="px-4 py-3 font-bold">Saldo contraentrega</td>
                <td className="px-4 py-3 text-right font-black">
                  Bs {bsSubtotal.toLocaleString()}
                </td>
              </tr>
              <tr className="border-t border-slate-200 bg-yellow-300">
                <td className="px-4 py-4 text-right text-lg font-black">TOTAL</td>
                <td className="px-4 py-4 text-right text-2xl font-black">
                  Bs {grandTotal.toLocaleString()}
                </td>
              </tr>
              <tr className="border-t border-slate-200">
                <td className="px-4 py-4 text-right text-lg font-black italic">
                  Precio unitario
                </td>
                <td className="px-4 py-4 text-right text-2xl font-black">
                  Bs {unitPrice.toLocaleString(undefined, { maximumFractionDigits: 2 })}
                </td>
              </tr>
            </tbody>
          </table>
        </section>
      </div>


<section className="mt-6 rounded-2xl border border-slate-200 bg-white">
  <div className="flex items-center justify-between gap-4 p-5">
    <div>
      <h3 className="text-lg font-black text-slate-900">
        Producto y proveedor
      </h3>

      <p className="mt-1 text-sm text-slate-500">
        Links de referencia, fotografías y documentación del producto.
      </p>
    </div>

    <button
      type="button"
      onClick={() => setShowAttachmentForm((prev) => !prev)}
      className="rounded-xl border border-slate-200 px-4 py-2 text-sm font-black text-slate-700 hover:bg-slate-50"
    >
      {showAttachmentForm ? 'Cerrar' : '+ Agregar'}
    </button>
  </div>

  {showAttachmentForm && (
    <div className="border-t border-slate-100 p-5">

          <div className="grid gap-3">
            <input
              value={newAttachment.title}
              onChange={(e) =>
                setNewAttachment((prev) => ({ ...prev, title: e.target.value }))
              }
              placeholder="Título del enlace"
              className="rounded-2xl border border-slate-300 px-4 py-3"
            />

            <input
              value={newAttachment.attachmentUrl}
              onChange={(e) =>
                setNewAttachment((prev) => ({ ...prev, attachmentUrl: e.target.value }))
              }
              placeholder="https://www.alibaba.com/..."
              className="rounded-2xl border border-slate-300 px-4 py-3"
            />

            <textarea
              value={newAttachment.description}
              onChange={(e) =>
                setNewAttachment((prev) => ({ ...prev, description: e.target.value }))
              }
              placeholder="Descripción u observaciones"
              rows={3}
              className="rounded-2xl border border-slate-300 px-4 py-3"
            />

            <button
              onClick={handleSaveAttachment}
              className="rounded-2xl bg-orange-500 px-5 py-3 font-black text-white"
            >
              Guardar link
            </button>
          </div>

          <div className="mt-6 rounded-2xl border border-dashed border-slate-300 bg-slate-50 p-4">
            <h4 className="text-sm font-black uppercase text-slate-500">
              Imágenes del producto/proveedor
            </h4>

            <div className="mt-4 grid gap-3">
              <select
                value={imageForm.attachmentType}
                onChange={(e) =>
                  setImageForm((prev) => ({ ...prev, attachmentType: e.target.value }))
                }
                className="rounded-2xl border border-slate-300 px-4 py-3"
              >
                <option value="PRODUCT_IMAGE">Imagen del producto</option>
                <option value="SUPPLIER_IMAGE">Imagen del proveedor</option>
              </select>

              <input
                value={imageForm.title}
                onChange={(e) =>
                  setImageForm((prev) => ({ ...prev, title: e.target.value }))
                }
                placeholder="Título de la imagen"
                className="rounded-2xl border border-slate-300 px-4 py-3"
              />

              <textarea
                value={imageForm.description}
                onChange={(e) =>
                  setImageForm((prev) => ({ ...prev, description: e.target.value }))
                }
                placeholder="Descripción de la imagen"
                rows={2}
                className="rounded-2xl border border-slate-300 px-4 py-3"
              />

              <input
                ref={imageFileRef}
                type="file"
                accept="image/*"
                className="rounded-2xl border border-slate-300 bg-white px-4 py-3"
              />

              <button
                type="button"
                onClick={handleUploadImage}
                className="rounded-2xl bg-slate-900 px-5 py-3 font-black text-white"
              >
                Subir imagen
              </button>
            </div>
          </div>
    </div>
  )}
          <div className="mt-6 space-y-3">
            {attachments.length === 0 && (
              <div className="rounded-2xl bg-slate-50 p-4 text-sm text-slate-500">
                No existen adjuntos asociados a esta proforma.
              </div>
            )}

            {attachments.map((attachment) => (
              <div
                key={attachment.id}
                className="flex items-center justify-between rounded-2xl border border-slate-200 p-4"
              >
                <div>
                  <p className="font-bold text-slate-900">{attachment.title}</p>

                  {attachment.attachmentType === 'PRODUCT_IMAGE' ||
                  attachment.attachmentType === 'SUPPLIER_IMAGE' ? (
                    <img
                      src={`http://localhost:8081${attachment.attachmentUrl}`}
                      alt={attachment.title || 'Imagen adjunta'}
                      className="mt-3 h-40 w-40 rounded-2xl border border-slate-200 object-cover"
                    />
                  ) : (
                    <a
                      href={attachment.attachmentUrl}
                      target="_blank"
                      rel="noreferrer"
                      className="text-sm text-blue-600 underline"
                    >
                      {attachment.attachmentUrl}
                    </a>
                  )}

                  {attachment.description && (
                    <p className="mt-2 text-sm text-slate-500">
                      {attachment.description}
                    </p>
                  )}
                </div>

                <button
                  onClick={() => handleDeleteAttachment(attachment.id)}
                  className="rounded-xl bg-rose-500 px-3 py-2 text-xs font-black text-white"
                >
                  Eliminar
                </button>
              </div>
            ))}
          </div>
        </section>
    

      <div className="mt-5 flex items-center justify-between gap-4">
        <div className="text-xs text-slate-400">
          Las líneas editables se recalculan y guardan mientras la proforma esté en borrador.
        </div>

        <div className="flex gap-3">
          {canEditProforma && (
            <>
              <button
                onClick={handleDownloadPdf}
                className="rounded-2xl bg-slate-900 px-6 py-3 text-sm font-black text-white hover:bg-slate-800"
              >
                Descargar PDF
              </button>

              <button
                type="button"
                onClick={saveChanges}
                disabled={saving}
                className="rounded-2xl bg-slate-500 px-6 py-3 text-sm font-black text-white hover:bg-slate-600 disabled:opacity-60"
              >
                {saving ? 'Guardando...' : 'Guardar cambios'}
              </button>

              <button
                onClick={async () => {
                  try {
                    await submitLclForReview(id);
                    window.location.reload();
                  } catch (error) {
                    alert(error.message);
                  }
                }}
                className="rounded-2xl bg-violet-600 px-6 py-3 font-bold text-white"
              >
                Enviar a revisión
              </button>
            </>
          )}

          {isReview && canApprove && (
            <>
              <button
                type="button"
                onClick={approveProforma}
                disabled={isSaving}
                className="rounded-2xl bg-emerald-600 px-6 py-3 text-sm font-black text-white shadow-lg shadow-emerald-600/20 hover:bg-emerald-700 disabled:opacity-50"
              >
                Aprobar
              </button>

              <button
                type="button"
                onClick={rejectProforma}
                disabled={isSaving}
                className="rounded-2xl bg-rose-600 px-6 py-3 text-sm font-black text-white shadow-lg shadow-rose-600/20 hover:bg-rose-700 disabled:opacity-50"
              >
                Rechazar
              </button>
            </>
          )}

          {data.status === 'APPROVED' && (
            <>
              <button
                onClick={handleClientAccept}
                className="rounded-2xl bg-emerald-600 px-5 py-3 text-sm font-bold text-white hover:bg-emerald-700"
              >
                Proforma aprobada por cliente
              </button>

              <button
                onClick={handleClientReject}
                className="rounded-2xl bg-rose-600 px-5 py-3 text-sm font-bold text-white hover:bg-rose-700"
              >
                Rechazada por cliente
              </button>


            </>
          )}

            {data.status === 'CLIENT_ACCEPTED' && !clientExists && (
              <button
                onClick={() => setShowClientModal(true)}
                className="rounded-2xl bg-blue-600 px-5 py-3 text-sm font-bold text-white hover:bg-blue-700"
              >
                Crear acceso cliente
              </button>
            )}

            {data.status === 'CLIENT_ACCEPTED' && clientExists && (
              <span className="rounded-2xl bg-emerald-100 px-5 py-3 text-sm font-bold text-emerald-700">
                Cliente registrado
              </span>
            )}

          {(isApproved || isRejected || data.status === 'CLIENT_ACCEPTED' || data.status === 'CLIENT_REJECTED') && (
            <button
              onClick={handleDownloadPdf}
              className="rounded-2xl bg-slate-900 px-6 py-3 text-sm font-black text-white hover:bg-slate-800"
            >
              Descargar PDF
            </button>
          )}
        </div>
      </div>

      <div className="mt-6 rounded-2xl bg-slate-900 p-5 text-right text-white">
        <div className="text-sm text-slate-300">Total proforma</div>
        <div className="text-3xl font-black">
          {data.currency} {Number(data.total).toLocaleString()}
        </div>
        <div className="mt-1 text-sm text-emerald-300">
          Utilidad estimada: {data.currency}{' '}
          {Number(data.estimatedProfit).toLocaleString()}
        </div>
      </div>

      {showClientModal && (
        <ClientAccessModal
          data={data}
          onClose={() => setShowClientModal(false)}
          onCreated={() => {
            setClientExists(true);
            setShowClientModal(false);
          }}
        />
      )}
    </div>
  );

  function SummaryCard({
    label,
    value,
    helper,
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

        <p className="mt-1 text-xs text-slate-400">
          {helper}
        </p>
      </div>
    );
  }

function ClientAccessModal({ data, onClose, onCreated }) {
  const [form, setForm] = useState({
    companyName: data.customerName || '',
    contactName: data.customerName || '',
    email: '',
    phone: data.customerPhone || '',
    username: '',
  });

  function update(field, value) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  async function handleSubmit() {
    if (!form.email.trim()) {
      alert('El correo es obligatorio.');
      return;
    }

    const leadId = data.customerId;

    if (!leadId) {
      alert(
        'Esta proforma no está vinculada a un contacto. No se puede convertir a cliente automáticamente.'
      );
      return;
    }

    try {
      await createClientAccount({
        leadId,
        acceptedProformaId: data.id,
        companyName: form.companyName,
        contactName: form.contactName,
        email: form.email,
        phone: form.phone,
        username: form.username || form.email,
      });

      await markLeadAsClient(leadId);
      console.log('OPPORTUNITY ID PARA MOVER PIPELINE:', data.opportunityId);

      if (data.opportunityId) {
        await markOpportunityAsClient(data.opportunityId);
      } else {
        alert('No se encontró opportunityId en esta proforma. No se puede mover el Pipeline.');
      }

      alert('Acceso cliente creado correctamente.');
      onCreated?.();
    } catch (error) {
      alert(error.message || 'No se pudo crear el acceso cliente.');
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/50 p-6">
      <div className="w-full max-w-2xl rounded-3xl bg-white p-8 shadow-2xl">
        <h2 className="text-2xl font-black text-slate-900">
          Crear acceso cliente
        </h2>

        <p className="mt-2 text-sm text-slate-500">
          Este cliente podrá ingresar posteriormente al portal para ver sus proformas.
        </p>

        <div className="mt-6 grid gap-4 md:grid-cols-2">
          <ClientField
            label="Razón social"
            value={form.companyName}
            onChange={(v) => update('companyName', v)}
          />

          <ClientField
            label="Contacto"
            value={form.contactName}
            onChange={(v) => update('contactName', v)}
          />

          <ClientField
            label="Teléfono"
            value={form.phone}
            onChange={(v) => update('phone', v)}
          />

          <ClientField
            label="Correo"
            value={form.email}
            onChange={(v) => update('email', v)}
          />

          <ClientField
            label="Usuario"
            value={form.username}
            onChange={(v) => update('username', v)}
          />
        </div>

        <div className="mt-8 flex justify-end gap-3">
          <button
            type="button"
            onClick={onClose}
            className="rounded-2xl border border-slate-200 px-5 py-3 font-bold text-slate-600"
          >
            Cancelar
          </button>

          <button
            type="button"
            onClick={handleSubmit}
            className="rounded-2xl bg-blue-600 px-5 py-3 font-bold text-white hover:bg-blue-700"
          >
            Crear acceso
          </button>
        </div>
      </div>
    </div>
  );
}

function ClientField({ label, value, onChange }) {
  return (
    <label className="block">
      <span className="text-xs font-bold uppercase tracking-wide text-slate-400">
        {label}
      </span>

      <input
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="mt-2 w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-blue-400"
      />
    </label>
  );
}

function CostCard({
  label,
  value,
  currency,
  highlighted = false,
}) {
  const amount = Number(value || 0);

  return (
    <div
      className={`rounded-2xl border p-4 ${
        highlighted
          ? 'border-orange-200 bg-orange-50'
          : 'border-slate-200 bg-slate-50/60'
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
        {currency}{' '}
        {amount.toLocaleString('es-BO', {
          minimumFractionDigits: 2,
          maximumFractionDigits: 2,
        })}
      </p>
    </div>
  );
}

}