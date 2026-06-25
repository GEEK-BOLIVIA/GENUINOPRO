import { useEffect, useRef, useState } from 'react';
import { FileText, PackageCheck } from 'lucide-react';
import {
  getFclProformaById,
  downloadFclPdf,
  submitFclForReview,
  approveFclProforma,
  rejectFclProforma,
  approveFclByCustomer,
  updateFclProforma,
} from '../../../services/fclService';

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



export default function FclProformaDetail({ id }) {
  const [item, setItem] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
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
    file: null,
  });

  const imageFileRef = useRef(null);

  useEffect(() => {
    async function load() {
      try {
        const data = await getFclProformaById(id);
        setItem(data);

        if (data.customerId) {
          const exists = await clientExistsByLeadId(data.customerId);
          setClientExists(exists === true);
        } else {
          setClientExists(false);
        }
      } catch (error) {
        console.error(error);
        alert('No se pudo cargar la proforma FCL.');
      } finally {
        setLoading(false);
      }
    }

    load();
  }, [id]);

  useEffect(() => {
  loadAttachments();
}, [id]);

  if (loading) {
    return (
      <div className="rounded-3xl border border-slate-200 bg-white p-8 text-sm font-bold text-slate-500">
        Cargando detalle FCL...
      </div>
    );
  }

  if (!item) {
    return (
      <div className="rounded-3xl border border-slate-200 bg-white p-8 text-sm font-bold text-slate-500">
        No se encontró la proforma FCL.
      </div>
    );
  }

  async function handleWorkflow(action) {
  try {
    let updated;

    if (action === 'submit') {
      updated = await submitFclForReview(item.id);
    }

    if (action === 'approve') {
      updated = await approveFclProforma(item.id);
    }

    if (action === 'reject') {
      updated = await rejectFclProforma(item.id);
    }

    if (action === 'approveCustomer') {
    updated = await approveFclByCustomer(item.id);
    }

    setItem(updated);
  } catch (error) {
    console.error(error);
    alert('No se pudo actualizar el estado de la proforma FCL.');
  }
}

function updateField(field, value) {
  setItem((prev) => ({
    ...prev,
    [field]: value,
  }));
}

async function saveDraftChanges() {
  try {
    const updated = await updateFclProforma(item.id, item);
    setItem(updated);
    alert('Cambios guardados correctamente.');
  } catch (error) {
    console.error(error);
    alert(error.message || 'No se pudo guardar la proforma FCL.');
  }
}


const isDraft = item.status === 'DRAFT';

const fobAmount = Number(item.fobUsd || item.merchandiseValueUsd || 0);
const fobPayments = Math.max(1, Number(item.fobPaymentCount || 1));
const fobInstallment = fobAmount / fobPayments;

const installmentLabels = [
  'Primera cuota',
  'Segunda cuota',
  'Tercera cuota',
  'Cuarta cuota',
  'Quinta cuota',
];

async function loadAttachments() {
  if (!id) return;
  const data = await getProformaAttachments(id);
  setAttachments(Array.isArray(data) ? data : []);
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
    title: '',
    attachmentUrl: '',
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

  await uploadProformaAttachmentImage(
    id,
    formData
  );


  setImageForm({
    attachmentType: 'PRODUCT_IMAGE',
    title: '',
    description: '',
    file: null,
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
    <div className="space-y-6">
      <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
        <div className="flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
          <div>
            <p className="text-sm font-black uppercase tracking-[0.35em] text-orange-600">
              Proforma FCL
            </p>

            <h1 className="mt-3 text-3xl font-black text-slate-900">
              {item.code || 'Sin código'}
            </h1>

            <p className="mt-2 text-sm text-slate-500">
              Detalle comercial y operativo de la proforma FCL.
            </p>
          </div>

            <div className="flex flex-wrap items-center gap-3">
              <span className="w-fit rounded-full bg-slate-100 px-4 py-2 text-xs font-black text-slate-700">
                {item.status || 'DRAFT'}
              </span>


            </div>
        </div>

        <div className="mt-6 grid gap-4 md:grid-cols-4">
          <Info label="Cliente" value={item.customerName} editable={isDraft} onChange={(v) => updateField('customerName', v)} />
          <Info label="Teléfono" value={item.customerPhone} editable={isDraft} onChange={(v) => updateField('customerPhone', v)} />
          <Info label="Asesor" value={item.sellerName} editable={isDraft} onChange={(v) => updateField('sellerName', v)} />
          <Info label="Fecha" value={formatDate(item.createdAt)} />
        </div>
      </section>

      <section className="grid gap-6 xl:grid-cols-2">
        <Card title="Datos de la operación" icon={FileText}>
          <div className="grid gap-3 md:grid-cols-2">
            <Info label="Proveedor" value={item.supplierName} editable={isDraft} onChange={(v) => updateField('supplierName', v)} />
            <Info label="Teléfono proveedor" value={item.supplierPhone} editable={isDraft} onChange={(v) => updateField('supplierPhone', v)} />
            <Info label="Origen" value={item.originCity} editable={isDraft} onChange={(v) => updateField('originCity', v)} />
            <Info label="Puerto origen" value={item.originPort} editable={isDraft} onChange={(v) => updateField('originPort', v)} />
            <Info label="Destino" value={item.destinationCity} editable={isDraft} onChange={(v) => updateField('destinationCity', v)} />
            <Info label="Producto" value={item.product} editable={isDraft} onChange={(v) => updateField('product', v)} />
            <Info label="Contenedor" value={item.containerType} editable={isDraft} onChange={(v) => updateField('containerType', v)} />
            <Info label="Cantidad" value={item.containerCount} editable={isDraft} type="number" onChange={(v) => updateField('containerCount', v)} />
          </div>
        </Card>

        <Card title="Resumen USD" icon={PackageCheck}>
          <div className="space-y-3">
            <MoneyRow label="FOB USD" value={item.fobUsd || item.merchandiseValueUsd} prefix="USD" />
            <MoneyRow label="Comisión giro USD" value={item.bankTransferCommissionUsd || item.commissionUsd} prefix="USD" />
            <MoneyRow label="Transporte marítimo USD" value={item.maritimeFreightUsd || item.originFreightUsd} prefix="USD" />
            <MoneyRow label="Total inicial USD" value={item.totalUsdToStartOrder} prefix="USD" highlight />
          </div>
        </Card>
      </section>

        <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
        <h2 className="text-xl font-black text-slate-900">
            Resumen operación Bolivia
        </h2>

        <div className="mt-5 grid gap-4 md:grid-cols-3">
            <Info label="Seguro USD calculado" value={formatMoney(item.insuranceUsdCalculated, 'USD')} />
            <Info label="CIF Bs" value={formatMoney(item.cifBob)} />

            <Info label="Transporte terrestre Bs" value={item.inlandFreightBob} editable={isDraft} type="number" onChange={(v) => updateField('inlandFreightBob', v)} />
            <Info label="GA %" value={item.gaPercent} editable={isDraft} type="number" onChange={(v) => updateField('gaPercent', v)} />
            <Info label="IVA %" value={item.ivaPercent} editable={isDraft} type="number" onChange={(v) => updateField('ivaPercent', v)} />
            <Info label="ICE %" value={item.icePercent} editable={isDraft} type="number" onChange={(v) => updateField('icePercent', v)} />

            <Info label="Impuestos Aduana Bs" value={formatMoney(item.customsTaxesBob)} />
            <Info label="ALBO / despacho Bs" value={formatMoney(item.alboBob)} />
            <Info label="Despachante Bs" value={formatMoney(item.dispatchAgentCommissionBob)} />

            <Info label="Comisión Genuino Bs" value={formatMoney(item.genuinoCommissionBob)} />
            <Info label="Gastos extra NIT Bs" value={formatMoney(item.extraNitExpensesBob)} />
            <Info label="Importador" value={item.importerNitType} />
        </div>

        <div className="mt-6 rounded-3xl bg-orange-500 p-6 text-white">
            <p className="text-xs font-black uppercase tracking-widest text-orange-100">
            Total operación
            </p>

            <p className="mt-2 text-4xl font-black">
            Bs {formatNumber(item.totalOperationBob || item.totalBob)}
            </p>
        </div>
        </section>

        <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
          <h2 className="text-xl font-black text-slate-900">
            Formas de pago
          </h2>

          <div className="mt-5 grid gap-4 md:grid-cols-5">
            <Info label="Peso total TN" value={item.totalWeightTn} />
            <Info label="Nro. pagos FOB" value={item.fobPaymentCount} />
            <Info
              label="Cliente paga USD"
              value={item.customerPaysInUsd ? 'Sí' : 'No'}
            />
            <Info
              label="Cliente paga proveedor"
              value={item.customerPaysSupplier ? 'Sí' : 'No'}
            />
            <Info label="Método de pago" value={item.paymentMethod} />
          </div>

          <div className="mt-6 grid gap-6 lg:grid-cols-2">

            <div className="rounded-2xl border border-slate-200 p-5">
              <h3 className="text-sm font-black uppercase tracking-wide text-slate-500">
                Pago inicial
              </h3>

              <div className="mt-4 space-y-3">
                <MoneyRow
                  label="Valor FOB Mercadería"
                  value={item.fobUsd || item.merchandiseValueUsd}
                  prefix="USD"
                />

                <MoneyRow
                  label="Comisión Giro Bancario"
                  value={item.bankTransferCommissionUsd || item.commissionUsd}
                  prefix="USD"
                />

                <MoneyRow
                  label="Total inicial USD"
                  value={item.totalUsdToStartOrder}
                  prefix="USD"
                  highlight
                />
              </div>
              {fobPayments > 1 && (
                <div className="mt-5 rounded-2xl bg-slate-50 p-4">
                  <h4 className="text-xs font-black uppercase tracking-wide text-slate-400">
                    Valor FOB por cuotas
                  </h4>

                  <div className="mt-3 space-y-2">
                    {Array.from({ length: fobPayments }).map((_, index) => (
                      <MoneyRow
                        key={index}
                        label={installmentLabels[index] || `Cuota ${index + 1}`}
                        value={fobInstallment}
                        prefix="USD"
                      />
                    ))}
                  </div>
                </div>
              )}
            </div>

            <div className="rounded-2xl border border-slate-200 p-5">
              <h3 className="text-sm font-black uppercase tracking-wide text-slate-500">
                Pagos posteriores
              </h3>

              <div className="mt-4 space-y-3">

                <MoneyRow
                  label="Transporte marítimo"
                  value={item.maritimeFreightUsd}
                  prefix="USD"
                />

                <MoneyRow
                  label="Transporte terrestre"
                  value={item.inlandFreightBob}
                  prefix="Bs"
                />

                <MoneyRow
                  label="Impuestos Aduana"
                  value={item.customsTaxesBob}
                  prefix="Bs"
                />

                <MoneyRow
                  label="Comisión despachante"
                  value={item.dispatchAgentCommissionBob}
                  prefix="Bs"
                />

                <MoneyRow
                  label="Comisión Genuino"
                  value={item.genuinoCommissionBob}
                  prefix="Bs"
                />

              </div>
            </div>

          </div>
        </section>

        <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
          <h2 className="text-xl font-black text-slate-900">
            Producto y proveedor
          </h2>

          <div className="mt-5 grid gap-3">
            <input
              type="text"
              value={newAttachment.title}
              onChange={(e) =>
                setNewAttachment((prev) => ({
                  ...prev,
                  title: e.target.value,
                }))
              }
              placeholder="Título del enlace, ej: Producto principal Alibaba"
              className="w-full rounded-2xl border border-slate-300 px-4 py-3"
            />

            <input
              type="text"
              value={newAttachment.attachmentUrl}
              onChange={(e) =>
                setNewAttachment((prev) => ({
                  ...prev,
                  attachmentUrl: e.target.value,
                }))
              }
              placeholder="https://www.alibaba.com/..."
              className="w-full rounded-2xl border border-slate-300 px-4 py-3"
            />

            <textarea
              value={newAttachment.description}
              onChange={(e) =>
                setNewAttachment((prev) => ({
                  ...prev,
                  description: e.target.value,
                }))
              }
              placeholder="Descripción u observaciones del producto/proveedor"
              rows={3}
              className="w-full rounded-2xl border border-slate-300 px-4 py-3"
            />

            <button
              onClick={handleSaveAttachment}
              className="rounded-2xl bg-orange-500 px-5 py-3 font-black text-white hover:bg-orange-600"
            >
              Guardar link
            </button>
          </div>

          <div className="mt-6 rounded-2xl border border-dashed border-slate-300 bg-slate-50 p-4">
            <h3 className="text-sm font-black uppercase tracking-wide text-slate-500">
              Imágenes del producto/proveedor
            </h3>

            <div className="mt-4 grid gap-3">
              <select
                value={imageForm.attachmentType}
                onChange={(e) =>
                  setImageForm((prev) => ({
                    ...prev,
                    attachmentType: e.target.value,
                  }))
                }
                className="w-full rounded-2xl border border-slate-300 px-4 py-3"
              >
                <option value="PRODUCT_IMAGE">Imagen del producto</option>
                <option value="SUPPLIER_IMAGE">Imagen del proveedor</option>
              </select>

              <input
                type="text"
                value={imageForm.title}
                onChange={(e) =>
                  setImageForm((prev) => ({
                    ...prev,
                    title: e.target.value,
                  }))
                }
                placeholder="Título de la imagen"
                className="w-full rounded-2xl border border-slate-300 px-4 py-3"
              />

              <textarea
                value={imageForm.description}
                onChange={(e) =>
                  setImageForm((prev) => ({
                    ...prev,
                    description: e.target.value,
                  }))
                }
                placeholder="Descripción u observación de la imagen"
                rows={2}
                className="w-full rounded-2xl border border-slate-300 px-4 py-3"
              />

                <input
                  ref={imageFileRef}
                  type="file"
                  accept="image/*"
                  className="w-full rounded-2xl border border-slate-300 bg-white px-4 py-3"
                />

              <button
                type="button"
                onClick={handleUploadImage}
                className="rounded-2xl bg-slate-900 px-5 py-3 font-black text-white hover:bg-slate-800"
              >
                Subir imagen
              </button>
            </div>
          </div>
          <div className="mt-6 space-y-3">
            {attachments.length === 0 && (
              <div className="rounded-2xl bg-slate-50 p-4 text-sm text-slate-500">
                No existen enlaces asociados a esta proforma.
              </div>
            )}

            {attachments.map((attachment) => (
              <div
                key={attachment.id}
                className="flex items-center justify-between rounded-2xl border border-slate-200 p-4"
              >
                <div>
                  <p className="font-bold text-slate-900">
                    {attachment.title}
                  </p>

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
                  className="rounded-xl bg-rose-500 px-3 py-2 text-xs font-black text-white hover:bg-rose-600"
                >
                  Eliminar
                </button>
              </div>
            ))}
          </div>
        </section>

        <section className="rounded-3xl border border-slate-200 bg-slate-50 p-5">
        <div className="flex flex-wrap justify-end gap-3">
            <button
            onClick={() => downloadFclPdf(item.id)}
            className="rounded-2xl border border-slate-300 bg-white px-5 py-3 text-sm font-black text-slate-700 hover:bg-slate-100"
            >
            Descargar PDF
            </button>

              {item.status === 'DRAFT' && (
                <>
                  <button
                    onClick={saveDraftChanges}
                    className="rounded-2xl bg-slate-600 px-5 py-3 text-sm font-black text-white hover:bg-slate-700"
                  >
                    Guardar cambios
                  </button>

                  <button
                    onClick={() => handleWorkflow('submit')}
                    className="rounded-2xl bg-slate-900 px-5 py-3 text-sm font-black text-white hover:bg-slate-700"
                  >
                    Enviar a revisión
                  </button>
                </>
              )}

            {item.status === 'IN_REVIEW' && (
            <>
                <button
                onClick={() => handleWorkflow('reject')}
                className="rounded-2xl bg-rose-500 px-5 py-3 text-sm font-black text-white hover:bg-rose-600"
                >
                Rechazar
                </button>

                <button
                onClick={() => handleWorkflow('approve')}
                className="rounded-2xl bg-emerald-500 px-5 py-3 text-sm font-black text-white hover:bg-emerald-600"
                >
                Aprobar
                </button>
            </>
            )}

            {item.status === 'APPROVED' && (
            <button
                onClick={() => handleWorkflow('approveCustomer')}
                className="rounded-2xl bg-orange-500 px-5 py-3 text-sm font-black text-white hover:bg-orange-600"
            >
                Aprobado por cliente
            </button>
            )}

            {item.status === 'APPROVED_BY_CUSTOMER' && !clientExists && (
              <button
                onClick={() => setShowClientModal(true)}
                className="rounded-2xl bg-blue-600 px-5 py-3 text-sm font-black text-white hover:bg-blue-700"
              >
                Crear acceso cliente
              </button>
            )}

            {item.status === 'APPROVED_BY_CUSTOMER' && clientExists && (
              <span className="rounded-2xl bg-emerald-100 px-5 py-3 text-sm font-black text-emerald-700">
                Cliente registrado
              </span>
            )}

            {showClientModal && (
              <ClientAccessModal
                data={item}
                onClose={() => setShowClientModal(false)}
                onCreated={() => {
                  setClientExists(true);
                  setShowClientModal(false);
                }}
              />
            )}
        </div>
        </section>
    </div>

  );
}

function Card({ title, icon: Icon, children }) {
  return (
    <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="flex items-center gap-3">
        <div className="rounded-2xl bg-orange-50 p-3 text-orange-600">
          <Icon size={20} />
        </div>

        <h2 className="text-xl font-black text-slate-900">
          {title}
        </h2>
      </div>

      <div className="mt-5">
        {children}
      </div>
    </section>
  );
}

function Info({ label, value, editable = false, onChange, type = 'text' }) {
  return (
    <div className="rounded-2xl bg-slate-50 p-4">
      <p className="text-xs font-black uppercase tracking-wide text-slate-400">
        {label}
      </p>

      {editable ? (
        <input
          type={type}
          value={value ?? ''}
          onChange={(e) => onChange?.(e.target.value)}
          className="mt-2 w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm font-black text-slate-900 outline-none focus:border-orange-400"
        />
      ) : (
        <p className="mt-2 text-sm font-black text-slate-900">
          {value || '-'}
        </p>
      )}
    </div>
  );
}

function MoneyRow({ label, value, prefix = 'Bs', highlight = false }) {
  return (
    <div
      className={`flex items-center justify-between rounded-2xl px-4 py-3 ${
        highlight ? 'bg-orange-50' : 'bg-slate-50'
      }`}
    >
      <span className="text-sm font-bold text-slate-500">
        {label}
      </span>

      <span className={`text-sm font-black ${highlight ? 'text-orange-700' : 'text-slate-900'}`}>
        {prefix} {formatNumber(value)}
      </span>
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

function formatMoney(value, prefix = 'Bs') {
  if (value === null || value === undefined || value === '') return '-';

  return `${prefix} ${formatNumber(value)}`;
}

function formatDate(value) {
  if (!value) return '-';

  return new Date(value).toLocaleString('es-BO');
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
      alert('Esta proforma no está vinculada a un contacto. No se puede convertir a cliente automáticamente.');
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

        <div className="mt-6 grid gap-4 md:grid-cols-2">
          <ClientField label="Razón social" value={form.companyName} onChange={(v) => update('companyName', v)} />
          <ClientField label="Contacto" value={form.contactName} onChange={(v) => update('contactName', v)} />
          <ClientField label="Teléfono" value={form.phone} onChange={(v) => update('phone', v)} />
          <ClientField label="Correo" value={form.email} onChange={(v) => update('email', v)} />
          <ClientField label="Usuario" value={form.username} onChange={(v) => update('username', v)} />
        </div>

        <div className="mt-8 flex justify-end gap-3">
          <button type="button" onClick={onClose} className="rounded-2xl border border-slate-200 px-5 py-3 font-bold text-slate-600">
            Cancelar
          </button>

          <button type="button" onClick={handleSubmit} className="rounded-2xl bg-blue-600 px-5 py-3 font-bold text-white hover:bg-blue-700">
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

