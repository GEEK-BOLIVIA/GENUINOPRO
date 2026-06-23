import { useEffect, useMemo, useState } from 'react';
import {
  Plus,
  RefreshCw,
  Save,
  X,
  Pencil,
  Trash2,
  Settings,
  RotateCcw,
} from 'lucide-react';

import {
  getProformaRates,
  createProformaRate,
  updateProformaRate,
  deleteProformaRate,
  activateProformaRate,
} from '../services/parametersApi';



const PROFORMA_TYPES = [
  { key: 'LCL', label: 'LCL', description: 'Carga consolidada por CBM o TON' },
  { key: 'FCL', label: 'FCL', description: "Contenedores 20', 40', 40HQ" },
  { key: 'HBL', label: 'HBL', description: 'Emisión, handling y documentación' },
  { key: 'AEREO', label: 'AÉREO', description: 'Peso real, volumétrico, AWB y handling' },
];

const RATE_OPTIONS = {
  LCL: [
    'CBM',
    'TON',
    'GIRO_PERCENT',
    'ALBO',
    'COMISION_GENUINO',
  ],

  FCL: [
    'FCL20',
    'FCL40',
    'FCL40HQ',
    'ALBO',
    'ADA',
    'DESPACHANTE',
    'GASTOS_EXTRA_NIT',
    'COMISION_GENUINO',
    'COMISION_GIRO_CHILE',
    'GIRO_ALIBABA_PERCENT',
  ],

  HBL: [
    'EMISION_HBL',
    'HANDLING',
    'DOCUMENTACION',
  ],

  AEREO: [
    'PESO_REAL',
    'PESO_VOLUMETRICO',
    'AWB',
    'HANDLING',
  ],
};

const emptyForm = {
  id: null,
  proformaType: 'LCL',
  rateType: 'CBM',
  rangeFrom: '',
  rangeTo: '',
  price: '',
  currency: 'USD',
  active: true,
};

export default function ParametersPage() {
  const [activeType, setActiveType] = useState('LCL');
  const [rates, setRates] = useState([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState(emptyForm);

  const activeTypeInfo = useMemo(
    () => PROFORMA_TYPES.find((item) => item.key === activeType),
    [activeType]
  );

  const [includeInactive, setIncludeInactive] = useState(false);

  async function loadRates(type = activeType) {
    try {
      setLoading(true);
      const data = await getProformaRates(type, includeInactive);
      setRates(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error(error);
      alert('No se pudieron cargar las tarifas.');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadRates(activeType);
  }, [activeType, includeInactive]);

  function openCreateModal() {
    setForm({
      ...emptyForm,
      proformaType: activeType,
      rateType: RATE_OPTIONS[activeType]?.[0] || '',
    });
    setModalOpen(true);
  }

  function openEditModal(rate) {
    setForm({
      id: rate.id,
      proformaType: rate.proformaType,
      rateType: rate.rateType,
      rangeFrom: rate.rangeFrom ?? '',
      rangeTo: rate.rangeTo ?? '',
      price: rate.price ?? '',
      currency: rate.currency || 'USD',
      active: rate.active ?? true,
    });
    setModalOpen(true);
  }

  function updateForm(field, value) {
    setForm((prev) => ({
      ...prev,
      [field]: value,
      ...(field === 'proformaType'
        ? { rateType: RATE_OPTIONS[value]?.[0] || '' }
        : {}),
    }));
  }

  function normalizePayload() {
    return {
      proformaType: form.proformaType,
      rateType: form.rateType,
      rangeFrom: form.rangeFrom === '' ? null : Number(form.rangeFrom),
      rangeTo: form.rangeTo === '' ? null : Number(form.rangeTo),
      price: form.price === '' ? null : Number(form.price),
      currency: form.currency || 'USD',
      active: Boolean(form.active),
    };
  }

  async function handleSubmit(event) {
    event.preventDefault();

    if (!form.proformaType || !form.rateType) {
      alert('El tipo de proforma y el tipo de tarifa son obligatorios.');
      return;
    }

    if (form.price === '' || Number(form.price) < 0) {
      alert('El precio debe ser válido.');
      return;
    }

    try {
      setSaving(true);
      const payload = normalizePayload();

      if (form.id) {
        await updateProformaRate(form.id, payload);
      } else {
        await createProformaRate(payload);
      }

      setModalOpen(false);
      await loadRates(activeType);
    } catch (error) {
      console.error(error);
      alert('No se pudo guardar la tarifa.');
    } finally {
      setSaving(false);
    }
  }

  async function handleDeactivate(rate) {
    const ok = confirm(`¿Desactivar la tarifa ${rate.rateType}?`);
    if (!ok) return;

    try {
      await deleteProformaRate(rate.id);
      await loadRates(activeType);
    } catch (error) {
      console.error(error);
      alert('No se pudo desactivar la tarifa.');
    }
  }

  async function handleActivate(rate) {
    try {
      await activateProformaRate(rate.id);
      await loadRates(activeType);
    } catch (error) {
      console.error(error);
      alert('No se pudo reactivar la tarifa.');
    }
  }

  return (
    <div className="space-y-6">
      <section className="flex flex-col gap-4 rounded-3xl border border-slate-200 bg-white p-6 shadow-sm lg:flex-row lg:items-center lg:justify-between">
        <div>
          <p className="text-sm font-bold uppercase tracking-wide text-orange-600">
            Sistema / Parámetros Enterprise
          </p>
          <h1 className="mt-2 text-3xl font-black tracking-tight text-slate-900">
            Tarifas de proformas
          </h1>
          <p className="mt-2 text-sm text-slate-500">
            Administración centralizada de tarifas LCL, FCL, HBL y Aéreo sin necesidad de SQL.
          </p>
        </div>

        <div className="flex flex-wrap gap-3">
          <button
            onClick={() => loadRates(activeType)}
            className="inline-flex items-center gap-2 rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm font-bold text-slate-700 hover:bg-slate-50"
          >
            <RefreshCw size={17} />
            Actualizar
          </button>

          <button
            onClick={() => setIncludeInactive((prev) => !prev)}
            className={`inline-flex items-center gap-2 rounded-2xl border px-4 py-3 text-sm font-bold ${
              includeInactive
                ? 'border-orange-200 bg-orange-50 text-orange-700'
                : 'border-slate-200 bg-white text-slate-700 hover:bg-slate-50'
            }`}
          >
            {includeInactive ? 'Mostrando inactivas' : 'Ver inactivas'}
          </button>

          <button
            onClick={openCreateModal}
            className="inline-flex items-center gap-2 rounded-2xl bg-slate-900 px-5 py-3 text-sm font-bold text-white shadow-lg shadow-slate-900/10 hover:bg-slate-700"
          >
            <Plus size={18} />
            Nueva tarifa
          </button>
        </div>
      </section>

      <section className="grid gap-4 md:grid-cols-4">
        {PROFORMA_TYPES.map((item) => {
          const active = item.key === activeType;

          return (
            <button
              key={item.key}
              onClick={() => setActiveType(item.key)}
              className={`rounded-3xl border p-5 text-left transition ${
                active
                  ? 'border-orange-300 bg-orange-50 shadow-sm'
                  : 'border-slate-200 bg-white hover:border-orange-200'
              }`}
            >
              <div className="flex items-center justify-between">
                <h2 className={`text-lg font-black ${active ? 'text-orange-700' : 'text-slate-900'}`}>
                  {item.label}
                </h2>
                <Settings size={18} className={active ? 'text-orange-600' : 'text-slate-400'} />
              </div>
              <p className={`mt-2 text-sm ${active ? 'text-orange-700/80' : 'text-slate-500'}`}>
                {item.description}
              </p>
            </button>
          );
        })}
      </section>

      <section className="overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm">
        <div className="flex items-center justify-between border-b border-slate-100 p-6">
          <div>
            <h2 className="text-lg font-black text-slate-900">
              Tarifas {activeTypeInfo?.label}
            </h2>
            <p className="mt-1 text-sm text-slate-500">
              Tipos configurables: {(RATE_OPTIONS[activeType] || []).join(', ')}
            </p>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-slate-100">
            <thead className="bg-slate-50">
              <tr>
                <th className="px-6 py-4 text-left text-xs font-black uppercase tracking-wide text-slate-500">
                  Tipo tarifa
                </th>
                <th className="px-6 py-4 text-left text-xs font-black uppercase tracking-wide text-slate-500">
                  Desde
                </th>
                <th className="px-6 py-4 text-left text-xs font-black uppercase tracking-wide text-slate-500">
                  Hasta
                </th>
                <th className="px-6 py-4 text-left text-xs font-black uppercase tracking-wide text-slate-500">
                  Precio
                </th>
                <th className="px-6 py-4 text-left text-xs font-black uppercase tracking-wide text-slate-500">
                  Moneda
                </th>
                <th className="px-6 py-4 text-left text-xs font-black uppercase tracking-wide text-slate-500">
                  Estado
                </th>
                <th className="px-6 py-4 text-right text-xs font-black uppercase tracking-wide text-slate-500">
                  Acciones
                </th>
              </tr>
            </thead>

            <tbody className="divide-y divide-slate-100 bg-white">
              {loading ? (
                <tr>
                  <td colSpan="7" className="px-6 py-10 text-center text-sm font-medium text-slate-500">
                    Cargando tarifas...
                  </td>
                </tr>
              ) : rates.length === 0 ? (
                <tr>
                  <td colSpan="7" className="px-6 py-10 text-center text-sm font-medium text-slate-500">
                    No hay tarifas configuradas para {activeTypeInfo?.label}.
                  </td>
                </tr>
              ) : (
                rates.map((rate) => (
                  <tr key={rate.id} className="hover:bg-slate-50/70">
                    <td className="px-6 py-4 text-sm font-black text-slate-900">
                      {rate.rateType}
                    </td>
                    <td className="px-6 py-4 text-sm text-slate-600">
                      {rate.rangeFrom ?? '—'}
                    </td>
                    <td className="px-6 py-4 text-sm text-slate-600">
                      {rate.rangeTo ?? '∞'}
                    </td>
                    <td className="px-6 py-4 text-sm font-bold text-slate-900">
                      {rate.price}
                    </td>
                    <td className="px-6 py-4 text-sm text-slate-600">
                      {rate.currency}
                    </td>
                    <td className="px-6 py-4">
                      {rate.active ? (
                        <span className="rounded-full bg-emerald-50 px-3 py-1 text-xs font-black text-emerald-700">
                          Activa
                        </span>
                      ) : (
                        <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-black text-slate-500">
                          Inactiva
                        </span>
                      )}
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex justify-end gap-2">
                        <button
                          onClick={() => openEditModal(rate)}
                          className="rounded-xl border border-slate-200 p-2 text-slate-600 hover:bg-slate-50"
                          title="Editar"
                        >
                          <Pencil size={16} />
                        </button>
                        {rate.active ? (
                          <button
                            onClick={() => handleDeactivate(rate)}
                            className="rounded-xl border border-red-100 p-2 text-red-600 hover:bg-red-50"
                            title="Desactivar"
                          >
                            <Trash2 size={16} />
                          </button>
                        ) : (
                          <button
                            onClick={() => handleActivate(rate)}
                            className="rounded-xl border border-emerald-100 p-2 text-emerald-600 hover:bg-emerald-50"
                            title="Reactivar"
                          >
                            <RotateCcw size={16} />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </section>

      {modalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 p-4">
          <form
            onSubmit={handleSubmit}
            className="w-full max-w-2xl rounded-3xl bg-white p-6 shadow-2xl"
          >
            <div className="flex items-start justify-between">
              <div>
                <h2 className="text-xl font-black text-slate-900">
                  {form.id ? 'Editar tarifa' : 'Nueva tarifa'}
                </h2>
                <p className="mt-1 text-sm text-slate-500">
                  Configura el tipo, rango, precio y moneda.
                </p>
              </div>

              <button
                type="button"
                onClick={() => setModalOpen(false)}
                className="rounded-xl p-2 text-slate-500 hover:bg-slate-100"
              >
                <X size={20} />
              </button>
            </div>

            <div className="mt-6 grid gap-4 md:grid-cols-2">
              <label className="space-y-2">
                <span className="text-sm font-bold text-slate-700">
                  Tipo de proforma
                </span>
                <select
                  value={form.proformaType}
                  onChange={(e) => updateForm('proformaType', e.target.value)}
                  className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300"
                >
                  {PROFORMA_TYPES.map((item) => (
                    <option key={item.key} value={item.key}>
                      {item.label}
                    </option>
                  ))}
                </select>
              </label>

              <label className="space-y-2">
                <span className="text-sm font-bold text-slate-700">
                  Tipo de tarifa
                </span>
                <select
                  value={form.rateType}
                  onChange={(e) => updateForm('rateType', e.target.value)}
                  className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300"
                >
                  {(RATE_OPTIONS[form.proformaType] || []).map((item) => (
                    <option key={item} value={item}>
                      {item}
                    </option>
                  ))}
                </select>
              </label>

              <label className="space-y-2">
                <span className="text-sm font-bold text-slate-700">
                  Rango desde
                </span>
                <input
                  type="number"
                  step="0.01"
                  value={form.rangeFrom}
                  onChange={(e) => updateForm('rangeFrom', e.target.value)}
                  className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300"
                  placeholder="Ej. 0"
                />
              </label>

              <label className="space-y-2">
                <span className="text-sm font-bold text-slate-700">
                  Rango hasta
                </span>
                <input
                  type="number"
                  step="0.01"
                  value={form.rangeTo}
                  onChange={(e) => updateForm('rangeTo', e.target.value)}
                  className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300"
                  placeholder="Vacío = infinito"
                />
              </label>

              <label className="space-y-2">
                <span className="text-sm font-bold text-slate-700">
                  Precio
                </span>
                <input
                  type="number"
                  step="0.01"
                  value={form.price}
                  onChange={(e) => updateForm('price', e.target.value)}
                  className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300"
                  placeholder="Ej. 220"
                />
              </label>

              <label className="space-y-2">
                <span className="text-sm font-bold text-slate-700">
                  Moneda
                </span>
                <select
                  value={form.currency}
                  onChange={(e) => updateForm('currency', e.target.value)}
                  className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300"
                >
                  <option value="USD">USD</option>
                  <option value="BOB">BOB</option>
                </select>
              </label>
            </div>

            <div className="mt-6 flex justify-end gap-3">
              <button
                type="button"
                onClick={() => setModalOpen(false)}
                className="rounded-2xl border border-slate-200 px-5 py-3 text-sm font-bold text-slate-700 hover:bg-slate-50"
              >
                Cancelar
              </button>

              <button
                type="submit"
                disabled={saving}
                className="inline-flex items-center gap-2 rounded-2xl bg-slate-900 px-5 py-3 text-sm font-bold text-white hover:bg-slate-700 disabled:opacity-60"
              >
                <Save size={17} />
                {saving ? 'Guardando...' : 'Guardar tarifa'}
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );


}