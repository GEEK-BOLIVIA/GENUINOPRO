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

  const [clientExists, setClientExists] = useState(false);
  const [showClientModal, setShowClientModal] = useState(false);
  const [activeTab, setActiveTab] = useState('Resumen');

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

  const usdSubtotal = usdLines.reduce(
    (acc, line) => acc + Number(line.total || 0),
    0
  );

  const bsSubtotal = bsLines.reduce(
    (acc, line) => acc + Number(line.total || 0),
    0
  );

  const convertedUsdToBs =
    Number(data.exchangeRate || 10) * usdSubtotal;

  const grandTotal =
    convertedUsdToBs + bsSubtotal;

  const unitPrice =
    Number(data.quantity || 1) > 0
      ? grandTotal / Number(data.quantity || 1)
      : 0;

            const isDraft = data?.status === 'DRAFT';
            const isReview = data?.status === 'IN_REVIEW';
            const isApproved = data?.status === 'APPROVED';
            const isRejected = data?.status === 'REJECTED';

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
      tabs={['Resumen', 'Cálculo', 'Producto y proveedor', 'Workflow']}
      activeTab={activeTab}
      onTabChange={setActiveTab}
      meta={[
        { label: 'Cliente', value: data.customerName },
        { label: 'Producto', value: data.cargoDescription },
        { label: 'Asesor', value: data.sellerName },
        { label: 'Origen', value: `${data.originCity || '-'} - ${data.originCountry || '-'}` },
        { label: 'Destino', value: `${data.destinationCity || '-'} - ${data.destinationCountry || '-'}` },
        {
          label: 'Fecha',
          value: data.createdAt
            ? new Date(data.createdAt).toLocaleString()
            : '-',
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

      {data.status === 'REJECTED' && (
        <div className="mb-4 rounded-2xl bg-rose-50 p-4 text-sm font-bold text-rose-700">
          Proforma rechazada. Debe ser corregida o regenerada.
        </div>
      )}

        

      <div className="mb-6 mt-6 grid gap-4 md:grid-cols-4">
        <div className="rounded-2xl bg-slate-100 p-4">
          <p className="text-xs text-slate-500">Total USD</p>
          <p className="text-2xl font-bold">
            USD {Number(data.totalUsd || 0).toLocaleString()}
          </p>
        </div>

        <div className="rounded-2xl bg-slate-100 p-4">
          <p className="text-xs text-slate-500">Total Bs</p>
          <p className="text-2xl font-bold">
            Bs {Number(data.total || 0).toLocaleString()}
          </p>
        </div>

        <div className="rounded-2xl bg-emerald-100 p-4">
          <p className="text-xs text-emerald-700">Utilidad</p>
          <p className="text-2xl font-bold text-emerald-700">
            Bs {Number(data.estimatedProfit || 0).toLocaleString()}
          </p>
        </div>

        <div className="rounded-2xl bg-orange-100 p-4">
          <p className="text-xs text-orange-700">Estado</p>
          <p className="text-xl font-bold text-orange-700">
            {statusLabel}
          </p>
        </div>
      </div>

      <div className="space-y-6">
        {activeTab === 'Cálculo' && (
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

                  {data.status === 'DRAFT' && line.editable ? (
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
        )}
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

                    {data.status === 'DRAFT' && ['ALBO', 'VAR', 'COM'].includes(line.code) ? (
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

        {activeTab === 'Workflow' && (
          <div className="space-y-4">

            <div className="rounded-2xl border border-slate-200 p-6">
              <h3 className="mb-4 text-lg font-black">
                Flujo de aprobación
              </h3>

              <div className="flex flex-wrap gap-3">

                <button
                  onClick={handleDownloadPdf}
                  className="rounded-xl bg-slate-950 px-5 py-3 font-bold text-white"
                >
                  Descargar PDF
                </button>

                {isDraft && (
                  <>
                    <button
                      onClick={saveChanges}
                      disabled={isSaving}
                      className="rounded-xl bg-slate-600 px-5 py-3 font-bold text-white"
                    >
                      Guardar cambios
                    </button>

                    <button
                      onClick={submitForReview}
                      disabled={isSaving}
                      className="rounded-xl bg-violet-600 px-5 py-3 font-bold text-white"
                    >
                      Enviar a revisión
                    </button>
                  </>
                )}

                {canApprove && isReview && (
                  <>
                    <button
                      onClick={approveProforma}
                      className="rounded-xl bg-emerald-600 px-5 py-3 font-bold text-white"
                    >
                      Aprobar
                    </button>

                    <button
                      onClick={rejectProforma}
                      className="rounded-xl bg-rose-600 px-5 py-3 font-bold text-white"
                    >
                      Rechazar
                    </button>
                  </>
                )}

              </div>
            </div>

          </div>
        )}

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

      {activeTab === 'Producto y proveedor' && (
        <section className="rounded-2xl border border-slate-200 p-5">
          <h3 className="mb-4 text-lg font-black">Producto y proveedor</h3>

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
      )}

      <div className="mt-5 flex items-center justify-between gap-4">
        <div className="text-xs text-slate-400">
          Las líneas editables se recalculan y guardan mientras la proforma esté en borrador.
        </div>

        <div className="flex gap-3">
          {isDraft && (
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
}