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
  SlidersHorizontal,
  TableProperties,
} from 'lucide-react';

import {
  getProformaRates,
  createProformaRate,
  updateProformaRate,
  deleteProformaRate,
  activateProformaRate,
  getCalculationParameters,
  createCalculationParameter,
  updateCalculationParameter,
  deleteCalculationParameter,
  activateCalculationParameter,
} from '../services/parametersApi';

const PROFORMA_TYPES = [
  { key: 'LCL', label: 'LCL', description: 'Carga consolidada por CBM o TON' },
  { key: 'FCL', label: 'FCL', description: "Contenedores 20', 40', 40HQ" },
  { key: 'HBL', label: 'HBL', description: 'Emisión, handling y documentación' },
  { key: 'AEREO', label: 'AÉREO', description: 'Peso real, volumétrico, AWB y handling' },
];

const CALCULATION_SCOPES = [
  { key: 'GENERAL', label: 'GENERAL', description: 'Parámetros comunes de liquidación' },
  { key: 'LCL', label: 'LCL', description: 'Parámetros específicos LCL' },
  { key: 'FCL', label: 'FCL', description: 'Parámetros específicos FCL' },
  { key: 'HBL', label: 'HBL', description: 'Parámetros específicos HBL' },
  { key: 'AEREO', label: 'AÉREO', description: 'Parámetros específicos Aéreo' },
];

const RATE_OPTIONS = {
  LCL: ['CBM', 'TON', 'GIRO_PERCENT', 'ALBO', 'COMISION_GENUINO', 'COMISION_TRANSFERENCIA'],
  FCL: [
    'FCL20',
    'FCL40',
    'FCL40HQ',
    'ALBO',
    'ADA',
    'DESPACHANTE',
    'GASTOS_EXTRA_NIT',
    'COMISION_GENUINO',
    'COMISION_TRANSFERENCIA',
    'GIRO_ALIBABA_PERCENT',
    'SWIFT_USD_YES_FIXED',
    'SWIFT_USD_YES_PERCENT',
    'SWIFT_USD_NO_FIXED',
    'SWIFT_USD_NO_PERCENT',
  ],
  HBL: ['EMISION_HBL', 'HANDLING', 'DOCUMENTACION'],
  AEREO: ['PESO_REAL', 'PESO_VOLUMETRICO', 'AWB', 'HANDLING'],
};

const RATE_LABELS = { CBM:'Tarifa por CBM', TON:'Tarifa por tonelada', GIRO_PERCENT:'Comisión de giro (%)', ALBO:'Gastos de despacho / ALBO', COMISION_GENUINO:'Comisión Genuino Importaciones', COMISION_TRANSFERENCIA:'Comisión por transferencia', FCL20:"Flete contenedor 20'", FCL40:"Flete contenedor 40'", FCL40HQ:'Flete contenedor 40HQ', ADA:'ADA', DESPACHANTE:'Comisión agencia despachante', GASTOS_EXTRA_NIT:'Gastos adicionales por NIT Genuino', GIRO_ALIBABA_PERCENT:'Comisión Alibaba (%)', SWIFT_USD_YES_FIXED:'SWIFT — cliente paga en USD (monto fijo)', SWIFT_USD_YES_PERCENT:'SWIFT — cliente paga en USD (%)', SWIFT_USD_NO_FIXED:'SWIFT — cliente no paga en USD (monto fijo)', SWIFT_USD_NO_PERCENT:'SWIFT — cliente no paga en USD (%)', EMISION_HBL:'Emisión HBL', HANDLING:'Manejo / Handling', DOCUMENTACION:'Documentación', PESO_REAL:'Peso real', PESO_VOLUMETRICO:'Peso volumétrico', AWB:'Guía aérea (AWB)' };
const PARAMETER_LABELS = { INSURANCE_PERCENT_DEFAULT:'Seguro por defecto', IVA_PERCENT:'IVA', CUSTOMS_FREIGHT_PERCENT:'Flete para efectos aduaneros', CUSTOMS_INSURANCE_PERCENT:'Seguro para efectos aduaneros', CUSTOMS_FREIGHT_RATE:'Tarifa de flete para Aduana', MARITIME_SELL_MARKUP_USD:'Margen comercial de transporte marítimo', INLAND_SELL_MARKUP_USD:'Margen comercial de transporte terrestre', CUSTOMS_MARITIME_ADJUSTMENT_USD:'Ajuste marítimo para Aduana', CUSTOMS_INLAND_ADJUSTMENT_USD:'Ajuste terrestre para Aduana', EXCHANGE_RATE:'Tipo de cambio comercial', TAX_EXCHANGE_RATE:'Tipo de cambio para impuestos', GA_PERCENT:'Gravamen arancelario (GA)', ICE_PERCENT:'ICE', CUSTOMS_FREIGHT_USD_PER_CBM: 'Flete aduanero por CBM', };
const UNIT_LABELS = { PERCENT:'Porcentaje (%)', RATE:'Tipo de cambio / tasa', USD:'Dólares (USD)', BOB:'Bolivianos (Bs)', BOOLEAN:'Sí / No', TEXT:'Texto' };
function humanizeCode(code){ if(!code) return '—'; return String(code).replaceAll('_',' ').toLowerCase().replace(/(^|\s)\S/g,c=>c.toUpperCase()); }
function getRateLabel(code){ return RATE_LABELS[code] || humanizeCode(code); }
function getParameterLabel(code){ return PARAMETER_LABELS[code] || humanizeCode(code); }
function getUnitLabel(unit){ return UNIT_LABELS[unit] || humanizeCode(unit); }

const UNIT_OPTIONS = [
  { key: 'PERCENT', label: 'Porcentaje (%)' },
  { key: 'RATE', label: 'Tipo de cambio / tasa' },
  { key: 'USD', label: 'USD' },
  { key: 'BOB', label: 'BOB' },
  { key: 'BOOLEAN', label: 'Sí / No' },
  { key: 'TEXT', label: 'Texto' },
];

const emptyRateForm = {
  id: null,
  proformaType: 'LCL',
  rateType: 'CBM',
  rangeFrom: '',
  rangeTo: '',
  price: '',
  currency: 'USD',
  active: true,
};

const emptyCalculationForm = {
  id: null,
  scope: 'GENERAL',
  code: '',
  numericValue: '',
  textValue: '',
  unit: 'PERCENT',
  version: 'LIQ_2026_09',
  active: true,
  description: '',
};

function formatCalculationValue(parameter) {
  if (parameter.unit === 'PERCENT') {
    const value = Number(parameter.numericValue ?? 0) * 100;
    return `${value.toLocaleString('es-BO', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 4,
    })} %`;
  }

  if (parameter.unit === 'BOOLEAN') {
    return String(parameter.textValue || '').toLowerCase() === 'true' ? 'Sí' : 'No';
  }

  if (parameter.unit === 'TEXT') return parameter.textValue || '—';
  if (parameter.numericValue === null || parameter.numericValue === undefined) return '—';

  return Number(parameter.numericValue).toLocaleString('es-BO', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 6,
  });
}

export default function ParametersPage() {
  const [section, setSection] = useState('RATES');

  const [activeType, setActiveType] = useState('LCL');
  const [rates, setRates] = useState([]);
  const [loadingRates, setLoadingRates] = useState(false);
  const [savingRate, setSavingRate] = useState(false);
  const [rateModalOpen, setRateModalOpen] = useState(false);
  const [rateForm, setRateForm] = useState(emptyRateForm);

  const [activeScope, setActiveScope] = useState('GENERAL');
  const [parameters, setParameters] = useState([]);
  const [loadingParameters, setLoadingParameters] = useState(false);
  const [savingParameter, setSavingParameter] = useState(false);
  const [parameterModalOpen, setParameterModalOpen] = useState(false);
  
  const [calculationPolicy, setCalculationPolicy] = useState(null);
  const [savingPolicy, setSavingPolicy] = useState(false);

  const [includeInactive, setIncludeInactive] = useState(false);

  const activeTypeInfo = useMemo(
    () => PROFORMA_TYPES.find((item) => item.key === activeType),
    [activeType]
  );

  const activeScopeInfo = useMemo(
    () => CALCULATION_SCOPES.find((item) => item.key === activeScope),
    [activeScope]
  );

  async function loadRates(type = activeType) {
    try {
      setLoadingRates(true);
      const data = await getProformaRates(type, includeInactive);
      setRates(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error(error);
      alert('No se pudieron cargar las tarifas.');
    } finally {
      setLoadingRates(false);
    }
  }

  async function loadParameters(scope = activeScope) {
    try {
      setLoadingParameters(true);
      const data = await getCalculationParameters(scope, includeInactive);
      setParameters(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error(error);
      alert('No se pudieron cargar los parámetros de cálculo.');
    } finally {
      setLoadingParameters(false);
    }
  }

  async function loadCalculationPolicy() {
    try {
      const data = await getCalculationParameters('GENERAL', false);

      const policy = Array.isArray(data)
        ? data.find(
            (item) =>
              item.code === 'CALCULATION_MODE' &&
              item.active
          )
        : null;

      setCalculationPolicy(policy);
    } catch (error) {
      console.error(
        'No se pudo cargar la política de cálculo',
        error
      );
    }
  }

  useEffect(() => {
    if (section === 'CALCULATION') {
      loadCalculationPolicy();
    }
  }, [section]);

  useEffect(() => {
    if (section === 'RATES') loadRates(activeType);
  }, [activeType, includeInactive, section]);

  useEffect(() => {
    if (section === 'CALCULATION') loadParameters(activeScope);
  }, [activeScope, includeInactive, section]);

  function openCreateRateModal() {
    setRateForm({
      ...emptyRateForm,
      proformaType: activeType,
      rateType: RATE_OPTIONS[activeType]?.[0] || '',
    });
    setRateModalOpen(true);
  }

  function openEditRateModal(rate) {
    setRateForm({
      id: rate.id,
      proformaType: rate.proformaType,
      rateType: rate.rateType,
      rangeFrom: rate.rangeFrom ?? '',
      rangeTo: rate.rangeTo ?? '',
      price: rate.price ?? '',
      currency: rate.currency || 'USD',
      active: rate.active ?? true,
    });
    setRateModalOpen(true);
  }

  function updateRateForm(field, value) {
    setRateForm((prev) => ({
      ...prev,
      [field]: value,
      ...(field === 'proformaType'
        ? { rateType: RATE_OPTIONS[value]?.[0] || '' }
        : {}),
    }));
  }

  function normalizeRatePayload() {
    return {
      proformaType: rateForm.proformaType,
      rateType: rateForm.rateType,
      rangeFrom: rateForm.rangeFrom === '' ? null : Number(rateForm.rangeFrom),
      rangeTo: rateForm.rangeTo === '' ? null : Number(rateForm.rangeTo),
      price: rateForm.price === '' ? null : Number(rateForm.price),
      currency: rateForm.currency || 'USD',
      active: Boolean(rateForm.active),
    };
  }

  async function handleRateSubmit(event) {
    event.preventDefault();

    if (!rateForm.proformaType || !rateForm.rateType) {
      alert('El tipo de proforma y el tipo de tarifa son obligatorios.');
      return;
    }

    if (rateForm.price === '' || Number(rateForm.price) < 0) {
      alert('El precio debe ser válido.');
      return;
    }

    try {
      setSavingRate(true);
      const payload = normalizeRatePayload();
      if (rateForm.id) await updateProformaRate(rateForm.id, payload);
      else await createProformaRate(payload);
      setRateModalOpen(false);
      await loadRates(activeType);
    } catch (error) {
      console.error(error);
      alert(error?.message || 'No se pudo guardar la tarifa.');
    } finally {
      setSavingRate(false);
    }
  }

  async function handleDeactivateRate(rate) {
    if (!confirm(`¿Desactivar la tarifa ${rate.rateType}?`)) return;
    try {
      await deleteProformaRate(rate.id);
      await loadRates(activeType);
    } catch (error) {
      console.error(error);
      alert('No se pudo desactivar la tarifa.');
    }
  }

  async function handleActivateRate(rate) {
    try {
      await activateProformaRate(rate.id);
      await loadRates(activeType);
    } catch (error) {
      console.error(error);
      alert('No se pudo reactivar la tarifa.');
    }
  }

async function handleCalculationModeChange(mode) {
  if (!calculationPolicy) {
    alert(
      'No se encontró la configuración global de cálculo.'
    );
    return;
  }

  if (calculationPolicy.textValue === mode) {
    return;
  }

  const label =
    mode === 'LIQUIDATION'
      ? 'Planilla de Liquidación'
      : 'Fórmula original de cada modalidad';

  const confirmed = window.confirm(
    `¿Deseas cambiar el método de cálculo vigente a "${label}"?\n\n` +
    'La configuración se aplicará a las nuevas proformas. ' +
    'Las proformas existentes no deben ser recalculadas.'
  );

  if (!confirmed) return;

  try {
    setSavingPolicy(true);

    const updated =
      await updateCalculationParameter(
        calculationPolicy.id,
        {
          ...calculationPolicy,
          textValue: mode,
          numericValue: null,
          unit: 'TEXT',
          active: true,
        }
      );

    setCalculationPolicy(updated);

    if (activeScope === 'GENERAL') {
      await loadParameters('GENERAL');
    }
  } catch (error) {
    console.error(error);

    alert(
      error?.message ||
      'No se pudo cambiar el método de cálculo.'
    );
  } finally {
    setSavingPolicy(false);
  }
}

  function openCreateParameterModal() {
    setParameterForm({ ...emptyCalculationForm, scope: activeScope });
    setParameterModalOpen(true);
  }

  function openEditParameterModal(parameter) {
    setParameterForm({
      id: parameter.id,
      scope: parameter.scope,
      code: parameter.code,
      numericValue:
        parameter.unit === 'PERCENT' && parameter.numericValue != null
          ? Number(parameter.numericValue) * 100
          : parameter.numericValue ?? '',
      textValue:
        parameter.unit === 'BOOLEAN'
          ? String(parameter.textValue || 'false').toLowerCase()
          : parameter.textValue ?? '',
      unit: parameter.unit || 'PERCENT',
      version: parameter.version || 'LIQ_2026_09',
      active: parameter.active ?? true,
      description: parameter.description || '',
    });
    setParameterModalOpen(true);
  }

  function updateParameterForm(field, value) {
    setParameterForm((prev) => ({
      ...prev,
      [field]: value,
      ...(field === 'unit'
        ? {
            numericValue: value === 'BOOLEAN' || value === 'TEXT' ? '' : prev.numericValue,
            textValue:
              value === 'BOOLEAN'
                ? prev.textValue || 'false'
                : value === 'TEXT'
                  ? prev.textValue
                  : '',
          }
        : {}),
    }));
  }

  function normalizeParameterPayload() {
    let numericValue = null;
    let textValue = null;

    if (parameterForm.unit === 'PERCENT') {
      numericValue = parameterForm.numericValue === '' ? null : Number(parameterForm.numericValue) / 100;
    } else if (['RATE', 'USD', 'BOB'].includes(parameterForm.unit)) {
      numericValue = parameterForm.numericValue === '' ? null : Number(parameterForm.numericValue);
    } else if (parameterForm.unit === 'BOOLEAN') {
      textValue = String(parameterForm.textValue || 'false');
    } else if (parameterForm.unit === 'TEXT') {
      textValue = parameterForm.textValue || '';
    }

    return {
      scope: parameterForm.scope,
      code: parameterForm.code.trim().toUpperCase(),
      numericValue,
      textValue,
      unit: parameterForm.unit,
      version: parameterForm.version || 'LIQ_2026_09',
      active: Boolean(parameterForm.active),
      description: parameterForm.description?.trim() || null,
    };
  }

  async function handleParameterSubmit(event) {
    event.preventDefault();

    if (!parameterForm.scope || !parameterForm.code || !parameterForm.unit) {
      alert('Alcance, código y unidad son obligatorios.');
      return;
    }

    if (
      ['PERCENT', 'RATE', 'USD', 'BOB'].includes(parameterForm.unit) &&
      (parameterForm.numericValue === '' || Number.isNaN(Number(parameterForm.numericValue)))
    ) {
      alert('Ingresa un valor numérico válido.');
      return;
    }

    if (
      parameterForm.unit === 'PERCENT' &&
      (Number(parameterForm.numericValue) < 0 || Number(parameterForm.numericValue) > 100)
    ) {
      alert('El porcentaje debe estar entre 0 y 100.');
      return;
    }

    try {
      setSavingParameter(true);
      const payload = normalizeParameterPayload();
      if (parameterForm.id) await updateCalculationParameter(parameterForm.id, payload);
      else await createCalculationParameter(payload);
      setParameterModalOpen(false);
      await loadParameters(activeScope);
    } catch (error) {
      console.error(error);
      alert(error?.message || 'No se pudo guardar el parámetro.');
    } finally {
      setSavingParameter(false);
    }
  }

  async function handleDeactivateParameter(parameter) {
    if (!confirm(`¿Desactivar el parámetro ${parameter.code}?`)) return;
    try {
      await deleteCalculationParameter(parameter.id);
      await loadParameters(activeScope);
    } catch (error) {
      console.error(error);
      alert('No se pudo desactivar el parámetro.');
    }
  }

  async function handleActivateParameter(parameter) {
    try {
      await activateCalculationParameter(parameter.id);
      await loadParameters(activeScope);
    } catch (error) {
      console.error(error);
      alert('No se pudo reactivar el parámetro.');
    }
  }

  function handleRefresh() {
    if (section === 'RATES') loadRates(activeType);
    else loadParameters(activeScope);
  }

  return (
    <div className="space-y-6">
      <section className="flex flex-col gap-4 rounded-3xl border border-slate-200 bg-white p-6 shadow-sm lg:flex-row lg:items-center lg:justify-between">
        <div>
          <p className="text-sm font-bold uppercase tracking-wide text-orange-600">
            Sistema / Parámetros Enterprise
          </p>
          <h1 className="mt-2 text-3xl font-black tracking-tight text-slate-900">
            Parámetros de cálculo
          </h1>
          <p className="mt-2 text-sm text-slate-500">
            Administración centralizada de tarifas y reglas de liquidación para LCL, FCL, HBL y Aéreo.
          </p>
        </div>

        <div className="flex flex-wrap gap-3">
          <button onClick={handleRefresh} className="inline-flex items-center gap-2 rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm font-bold text-slate-700 hover:bg-slate-50">
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
            onClick={section === 'RATES' ? openCreateRateModal : openCreateParameterModal}
            className="inline-flex items-center gap-2 rounded-2xl bg-slate-900 px-5 py-3 text-sm font-bold text-white shadow-lg shadow-slate-900/10 hover:bg-slate-700"
          >
            <Plus size={18} />
            {section === 'RATES' ? 'Nueva tarifa' : 'Nuevo parámetro'}
          </button>
        </div>
      </section>

      <section className="grid gap-4 md:grid-cols-2">
        <button
          onClick={() => setSection('RATES')}
          className={`rounded-3xl border p-5 text-left transition ${
            section === 'RATES'
              ? 'border-orange-300 bg-orange-50 shadow-sm'
              : 'border-slate-200 bg-white hover:border-orange-200'
          }`}
        >
          <div className="flex items-center gap-3">
            <TableProperties size={20} className={section === 'RATES' ? 'text-orange-600' : 'text-slate-400'} />
            <div>
              <h2 className="text-lg font-black text-slate-900">Tarifas por rango</h2>
              <p className="mt-1 text-sm text-slate-500">CBM, tonelaje, FOB, comisiones y otras tablas escalonadas.</p>
            </div>
          </div>
        </button>

        <button
          onClick={() => setSection('CALCULATION')}
          className={`rounded-3xl border p-5 text-left transition ${
            section === 'CALCULATION'
              ? 'border-orange-300 bg-orange-50 shadow-sm'
              : 'border-slate-200 bg-white hover:border-orange-200'
          }`}
        >
          <div className="flex items-center gap-3">
            <SlidersHorizontal size={20} className={section === 'CALCULATION' ? 'text-orange-600' : 'text-slate-400'} />
            <div>
              <h2 className="text-lg font-black text-slate-900">Planilla de Liquidación</h2>
              <p className="mt-1 text-sm text-slate-500">Porcentajes, tipos de cambio, valores escalares y reglas configurables.</p>
            </div>
          </div>
        </button>
      </section>

      {section === 'RATES' ? (
        <>
          <section className="grid gap-4 md:grid-cols-4">
            {PROFORMA_TYPES.map((item) => {
              const active = item.key === activeType;
              return (
                <button
                  key={item.key}
                  onClick={() => setActiveType(item.key)}
                  className={`rounded-3xl border p-5 text-left transition ${active ? 'border-orange-300 bg-orange-50 shadow-sm' : 'border-slate-200 bg-white hover:border-orange-200'}`}
                >
                  <div className="flex items-center justify-between">
                    <h2 className={`text-lg font-black ${active ? 'text-orange-700' : 'text-slate-900'}`}>{item.label}</h2>
                    <Settings size={18} className={active ? 'text-orange-600' : 'text-slate-400'} />
                  </div>
                  <p className={`mt-2 text-sm ${active ? 'text-orange-700/80' : 'text-slate-500'}`}>{item.description}</p>
                </button>
              );
            })}
          </section>

          <section className="overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm">
            <div className="flex items-center justify-between border-b border-slate-100 p-6">
              <div>
                <h2 className="text-lg font-black text-slate-900">Tarifas {activeTypeInfo?.label}</h2>
                <p className="mt-1 text-sm text-slate-500">Tipos configurables: {(RATE_OPTIONS[activeType] || []).map(getRateLabel).join(', ')}</p>
              </div>
            </div>

            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-slate-100">
                <thead className="bg-slate-50">
                  <tr>
                    {['Tipo tarifa', 'Desde', 'Hasta', 'Precio', 'Moneda', 'Estado'].map((label) => (
                      <th key={label} className="px-6 py-4 text-left text-xs font-black uppercase tracking-wide text-slate-500">{label}</th>
                    ))}
                    <th className="px-6 py-4 text-right text-xs font-black uppercase tracking-wide text-slate-500">Acciones</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 bg-white">
                  {loadingRates ? (
                    <tr><td colSpan="7" className="px-6 py-10 text-center text-sm font-medium text-slate-500">Cargando tarifas...</td></tr>
                  ) : rates.length === 0 ? (
                    <tr><td colSpan="7" className="px-6 py-10 text-center text-sm font-medium text-slate-500">No hay tarifas configuradas para {activeTypeInfo?.label}.</td></tr>
                  ) : (
                    rates.map((rate) => (
                      <tr key={rate.id} className="hover:bg-slate-50/70">
                        <td className="px-6 py-4 text-sm font-black text-slate-900">{getRateLabel(rate.rateType)}</td>
                        <td className="px-6 py-4 text-sm text-slate-600">{rate.rangeFrom ?? '—'}</td>
                        <td className="px-6 py-4 text-sm text-slate-600">{rate.rangeTo ?? '∞'}</td>
                        <td className="px-6 py-4 text-sm font-bold text-slate-900">{rate.price}</td>
                        <td className="px-6 py-4 text-sm text-slate-600">{rate.currency}</td>
                        <td className="px-6 py-4">
                          <span className={`rounded-full px-3 py-1 text-xs font-black ${rate.active ? 'bg-emerald-50 text-emerald-700' : 'bg-slate-100 text-slate-500'}`}>
                            {rate.active ? 'Activa' : 'Inactiva'}
                          </span>
                        </td>
                        <td className="px-6 py-4">
                          <div className="flex justify-end gap-2">
                            <button onClick={() => openEditRateModal(rate)} className="rounded-xl border border-slate-200 p-2 text-slate-600 hover:bg-slate-50" title="Editar"><Pencil size={16} /></button>
                            {rate.active ? (
                              <button onClick={() => handleDeactivateRate(rate)} className="rounded-xl border border-red-100 p-2 text-red-600 hover:bg-red-50" title="Desactivar"><Trash2 size={16} /></button>
                            ) : (
                              <button onClick={() => handleActivateRate(rate)} className="rounded-xl border border-emerald-100 p-2 text-emerald-600 hover:bg-emerald-50" title="Reactivar"><RotateCcw size={16} /></button>
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
        </>
      ) : (
        <>

        <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
          <div className="flex flex-col gap-2">
            <div>
              <p className="text-xs font-black uppercase tracking-widest text-orange-600">
                Política comercial
              </p>

              <h2 className="mt-1 text-xl font-black text-slate-900">
                Método de cálculo vigente
              </h2>

              <p className="mt-2 max-w-3xl text-sm leading-6 text-slate-500">
                Define cómo se calcularán las nuevas proformas.
                Cambiar esta política no debe modificar las
                proformas ya generadas.
              </p>
            </div>
          </div>

          <div className="mt-6 grid gap-4 md:grid-cols-2">
            <button
              type="button"
              disabled={true}
              onClick={() =>
                handleCalculationModeChange('MODALITY')
              }
              className={`rounded-3xl border p-5 text-left transition ${
                calculationPolicy?.textValue === 'MODALITY'
                  ? 'border-emerald-300 bg-emerald-50 shadow-sm'
                  : 'border-slate-200 bg-white hover:border-slate-300'
              } disabled:cursor-not-allowed disabled:opacity-60`}
            >
              <div className="flex items-start justify-between gap-4">
                <div>
                  <h3 className="text-base font-black text-slate-900">
                    Fórmula original de cada modalidad
                  </h3>

                  <p className="mt-2 text-sm leading-6 text-slate-500">
                    Mantiene las reglas propias ya validadas para
                    LCL, FCL, HBL y Aéreo.
                  </p>
                  <p className="mt-2 text-xs font-bold text-orange-600">
                    Disponible cuando finalice la integración con los motores de cálculo.
                  </p>
                </div>

                <div
                  className={`mt-1 h-5 w-5 rounded-full border-4 ${
                    calculationPolicy?.textValue === 'MODALITY'
                      ? 'border-emerald-500 bg-white'
                      : 'border-slate-300 bg-white'
                  }`}
                />
              </div>
            </button>

            <button
              type="button"
              disabled={savingPolicy || !calculationPolicy}
              onClick={() =>
                handleCalculationModeChange('LIQUIDATION')
              }
              className={`rounded-3xl border p-5 text-left transition ${
                calculationPolicy?.textValue === 'LIQUIDATION'
                  ? 'border-orange-300 bg-orange-50 shadow-sm'
                  : 'border-slate-200 bg-white hover:border-orange-200'
              } disabled:cursor-not-allowed disabled:opacity-60`}
            >
              <div className="flex items-start justify-between gap-4">
                <div>
                  <h3 className="text-base font-black text-slate-900">
                    Planilla de Liquidación
                  </h3>

                  <p className="mt-2 text-sm leading-6 text-slate-500">
                    Utiliza la configuración general vigente
                    definida por Genuino para liquidación,
                    normativa y contexto de importaciones.
                  </p>
                </div>

                <div
                  className={`mt-1 h-5 w-5 rounded-full border-4 ${
                    calculationPolicy?.textValue === 'LIQUIDATION'
                      ? 'border-orange-500 bg-white'
                      : 'border-slate-300 bg-white'
                  }`}
                />
              </div>
            </button>
          </div>

          <div className="mt-5 rounded-2xl bg-slate-50 px-4 py-3 text-sm text-slate-600">
            Política actual:{' '}
            <span className="font-black text-slate-900">
              {calculationPolicy?.textValue === 'LIQUIDATION'
                ? 'Planilla de Liquidación'
                : calculationPolicy?.textValue === 'MODALITY'
                  ? 'Fórmula original de cada modalidad'
                  : 'Cargando...'}
            </span>

            {savingPolicy && (
              <span className="ml-2 text-orange-600">
                Guardando...
              </span>
            )}
          </div>
        </section>

          <section className="grid gap-4 sm:grid-cols-2 lg:grid-cols-5">
            {CALCULATION_SCOPES.map((item) => {
              const active = item.key === activeScope;
              return (
                <button
                  key={item.key}
                  onClick={() => setActiveScope(item.key)}
                  className={`rounded-3xl border p-5 text-left transition ${active ? 'border-orange-300 bg-orange-50 shadow-sm' : 'border-slate-200 bg-white hover:border-orange-200'}`}
                >
                  <h2 className={`text-base font-black ${active ? 'text-orange-700' : 'text-slate-900'}`}>{item.label}</h2>
                  <p className="mt-2 text-xs leading-5 text-slate-500">{item.description}</p>
                </button>
              );
            })}
          </section>

          <section className="overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm">
            <div className="border-b border-slate-100 p-6">
              <h2 className="text-lg font-black text-slate-900">Planilla de Liquidación — {activeScopeInfo?.label}</h2>
              <p className="mt-1 text-sm text-slate-500">Los porcentajes se muestran en escala 0–100 y se almacenan internamente entre 0 y 1.</p>
            </div>
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-slate-100">
                <thead className="bg-slate-50">
                  <tr>
                    {['Parámetro', 'Descripción', 'Valor', 'Unidad', 'Versión', 'Estado'].map((label) => (
                      <th key={label} className="px-6 py-4 text-left text-xs font-black uppercase tracking-wide text-slate-500">{label}</th>
                    ))}
                    <th className="px-6 py-4 text-right text-xs font-black uppercase tracking-wide text-slate-500">Acciones</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 bg-white">
                  {loadingParameters ? (
                    <tr><td colSpan="7" className="px-6 py-10 text-center text-sm font-medium text-slate-500">Cargando parámetros...</td></tr>
                  ) : parameters.length === 0 ? (
                    <tr><td colSpan="7" className="px-6 py-10 text-center text-sm font-medium text-slate-500">No hay parámetros configurados para {activeScopeInfo?.label}.</td></tr>
                  ) : (
                    parameters
                      .filter(
                        (parameter) =>
                          parameter.code !== 'CALCULATION_MODE'
                      )
                      .map((parameter) => (
                      <tr key={parameter.id} className="hover:bg-slate-50/70">
                        <td className="px-6 py-4 text-sm font-black text-slate-900">{getParameterLabel(parameter.code)}</td>
                        <td className="max-w-md px-6 py-4 text-sm text-slate-600">{parameter.description || '—'}</td>
                        <td className="px-6 py-4 text-sm font-bold text-slate-900">{formatCalculationValue(parameter)}</td>
                        <td className="px-6 py-4 text-sm text-slate-600">{getUnitLabel(parameter.unit)}</td>
                        <td className="px-6 py-4 text-sm text-slate-600">{parameter.version}</td>
                        <td className="px-6 py-4">
                          <span className={`rounded-full px-3 py-1 text-xs font-black ${parameter.active ? 'bg-emerald-50 text-emerald-700' : 'bg-slate-100 text-slate-500'}`}>
                            {parameter.active ? 'Activo' : 'Inactivo'}
                          </span>
                        </td>
                        <td className="px-6 py-4">
                          <div className="flex justify-end gap-2">
                            <button onClick={() => openEditParameterModal(parameter)} className="rounded-xl border border-slate-200 p-2 text-slate-600 hover:bg-slate-50" title="Editar"><Pencil size={16} /></button>
                            {parameter.active ? (
                              <button onClick={() => handleDeactivateParameter(parameter)} className="rounded-xl border border-red-100 p-2 text-red-600 hover:bg-red-50" title="Desactivar"><Trash2 size={16} /></button>
                            ) : (
                              <button onClick={() => handleActivateParameter(parameter)} className="rounded-xl border border-emerald-100 p-2 text-emerald-600 hover:bg-emerald-50" title="Reactivar"><RotateCcw size={16} /></button>
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
        </>
      )}

      {rateModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 p-4">
          <form onSubmit={handleRateSubmit} className="w-full max-w-2xl rounded-3xl bg-white p-6 shadow-2xl">
            <div className="flex items-start justify-between">
              <div>
                <h2 className="text-xl font-black text-slate-900">{rateForm.id ? 'Editar tarifa' : 'Nueva tarifa'}</h2>
                <p className="mt-1 text-sm text-slate-500">Configura el tipo, rango, precio y moneda.</p>
              </div>
              <button type="button" onClick={() => setRateModalOpen(false)} className="rounded-xl p-2 text-slate-500 hover:bg-slate-100"><X size={20} /></button>
            </div>
            <div className="mt-6 grid gap-4 md:grid-cols-2">
              <label className="space-y-2"><span className="text-sm font-bold text-slate-700">Tipo de proforma</span><select value={rateForm.proformaType} onChange={(e) => updateRateForm('proformaType', e.target.value)} className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300">{PROFORMA_TYPES.map((item) => <option key={item.key} value={item.key}>{item.label}</option>)}</select></label>
              <label className="space-y-2"><span className="text-sm font-bold text-slate-700">Tipo de tarifa</span><select value={rateForm.rateType} onChange={(e) => updateRateForm('rateType', e.target.value)} className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300">{(RATE_OPTIONS[rateForm.proformaType] || []).map((item) => <option key={item} value={item}>{getRateLabel(item)}</option>)}</select></label>
              <label className="space-y-2"><span className="text-sm font-bold text-slate-700">Rango desde</span><input type="number" step="0.01" value={rateForm.rangeFrom} onChange={(e) => updateRateForm('rangeFrom', e.target.value)} className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300" placeholder="Ej. 0" /></label>
              <label className="space-y-2"><span className="text-sm font-bold text-slate-700">Rango hasta</span><input type="number" step="0.01" value={rateForm.rangeTo} onChange={(e) => updateRateForm('rangeTo', e.target.value)} className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300" placeholder="Vacío = infinito" /></label>
              <label className="space-y-2"><span className="text-sm font-bold text-slate-700">Precio</span><input type="number" step="0.01" value={rateForm.price} onChange={(e) => updateRateForm('price', e.target.value)} className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300" placeholder="Ej. 220" /></label>
              <label className="space-y-2"><span className="text-sm font-bold text-slate-700">Moneda</span><select value={rateForm.currency} onChange={(e) => updateRateForm('currency', e.target.value)} className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300"><option value="USD">USD</option><option value="BOB">BOB</option></select></label>
            </div>
            <div className="mt-6 flex justify-end gap-3">
              <button type="button" onClick={() => setRateModalOpen(false)} className="rounded-2xl border border-slate-200 px-5 py-3 text-sm font-bold text-slate-700 hover:bg-slate-50">Cancelar</button>
              <button type="submit" disabled={savingRate} className="inline-flex items-center gap-2 rounded-2xl bg-slate-900 px-5 py-3 text-sm font-bold text-white hover:bg-slate-700 disabled:opacity-60"><Save size={17} />{savingRate ? 'Guardando...' : 'Guardar tarifa'}</button>
            </div>
          </form>
        </div>
      )}

      {parameterModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 p-4">
          <form onSubmit={handleParameterSubmit} className="w-full max-w-3xl rounded-3xl bg-white p-6 shadow-2xl">
            <div className="flex items-start justify-between">
              <div>
                <h2 className="text-xl font-black text-slate-900">{parameterForm.id ? 'Editar parámetro' : 'Nuevo parámetro'}</h2>
                <p className="mt-1 text-sm text-slate-500">Configura un valor utilizado por la Planilla de Liquidación.</p>
              </div>
              <button type="button" onClick={() => setParameterModalOpen(false)} className="rounded-xl p-2 text-slate-500 hover:bg-slate-100"><X size={20} /></button>
            </div>
            <div className="mt-6 grid gap-4 md:grid-cols-2">
              <label className="space-y-2"><span className="text-sm font-bold text-slate-700">Alcance</span><select value={parameterForm.scope} onChange={(e) => updateParameterForm('scope', e.target.value)} className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300">{CALCULATION_SCOPES.map((item) => <option key={item.key} value={item.key}>{item.label}</option>)}</select></label>
              <label className="space-y-2"><span className="text-sm font-bold text-slate-700">Código interno</span><input type="text" value={parameterForm.code} onChange={(e) => updateParameterForm('code', e.target.value)} className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm uppercase outline-none focus:border-orange-300" placeholder="Ej. IVA_PERCENT" /></label>
              <label className="space-y-2"><span className="text-sm font-bold text-slate-700">Unidad</span><select value={parameterForm.unit} onChange={(e) => updateParameterForm('unit', e.target.value)} className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300">{UNIT_OPTIONS.map((item) => <option key={item.key} value={item.key}>{item.label}</option>)}</select></label>

              {['PERCENT', 'RATE', 'USD', 'BOB'].includes(parameterForm.unit) && (
                <label className="space-y-2"><span className="text-sm font-bold text-slate-700">{parameterForm.unit === 'PERCENT' ? 'Valor (%)' : 'Valor'}</span><input type="number" step="0.0001" value={parameterForm.numericValue} onChange={(e) => updateParameterForm('numericValue', e.target.value)} className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300" placeholder={parameterForm.unit === 'PERCENT' ? 'Ej. 14.94' : 'Ej. 11.58'} /></label>
              )}

              {parameterForm.unit === 'BOOLEAN' && (
                <label className="space-y-2"><span className="text-sm font-bold text-slate-700">Valor</span><select value={parameterForm.textValue || 'false'} onChange={(e) => updateParameterForm('textValue', e.target.value)} className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300"><option value="true">Sí</option><option value="false">No</option></select></label>
              )}

              {parameterForm.unit === 'TEXT' && (
                <label className="space-y-2"><span className="text-sm font-bold text-slate-700">Valor</span><input type="text" value={parameterForm.textValue} onChange={(e) => updateParameterForm('textValue', e.target.value)} className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300" placeholder="Valor de texto" /></label>
              )}

              <label className="space-y-2"><span className="text-sm font-bold text-slate-700">Versión</span><input type="text" value={parameterForm.version} onChange={(e) => updateParameterForm('version', e.target.value)} className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300" placeholder="LIQ_2026_09" /></label>
              <label className="space-y-2 md:col-span-2"><span className="text-sm font-bold text-slate-700">Descripción</span><textarea rows="3" value={parameterForm.description} onChange={(e) => updateParameterForm('description', e.target.value)} className="w-full resize-none rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300" placeholder="Explica para qué se utiliza este parámetro." /></label>
            </div>
            <div className="mt-6 flex justify-end gap-3">
              <button type="button" onClick={() => setParameterModalOpen(false)} className="rounded-2xl border border-slate-200 px-5 py-3 text-sm font-bold text-slate-700 hover:bg-slate-50">Cancelar</button>
              <button type="submit" disabled={savingParameter} className="inline-flex items-center gap-2 rounded-2xl bg-slate-900 px-5 py-3 text-sm font-bold text-white hover:bg-slate-700 disabled:opacity-60"><Save size={17} />{savingParameter ? 'Guardando...' : 'Guardar parámetro'}</button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}
