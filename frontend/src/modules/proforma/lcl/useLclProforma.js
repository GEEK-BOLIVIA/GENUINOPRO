import { useState } from 'react';
import { createLclProforma } from '../../../services/lclService';

const initialForm = {
  opportunityId: '',
  customerId: '',
  currency: 'USD',
  notes: '',
  createdBy: 'admin',

  issueDate: new Date().toISOString().slice(0, 10),
  validityDays: 15,

  sellerName: 'admin',
  customerName: '',
  customerPhone: '',
  customerAddress: '',

  originCountry: 'China',
  originCity: '',
  destinationCountry: 'Bolivia',
  destinationCity: 'Cochabamba',
  portOrigin: '',
  portDestination: 'Arica',

  incoterm: 'FOB',
  cargoType: 'Mercadería general',
  transitTime: '',
  carrierName: '',
  agentName: '',

  packageCount: '',
  grossWeightKg: '',
  volumeCbm: '',
  cargoDescription: '',

  freightRate: '',
  originCharges: '',
  destinationCharges: '',
  handlingCharges: '',
  documentationCharges: '',
  customsCharges: '',
  insuranceCharges: '',
  otherCharges: '',
  commissionAmount: '',
  marginAmount: '',

  commercialTerms: 'Validez 15 días. Sujeto a disponibilidad de espacio y variación de tarifas.',
};

export default function useLclProforma() {
  const [form, setForm] = useState(initialForm);
  const [result, setResult] = useState(null);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState('');

  function updateField(field, value) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  function preloadContact(contact) {
    if (!contact) return;

    setForm((prev) => ({
      ...prev,
      customerId: contact.id || '',
      customerName: contact.company || contact.contact || '',
      customerPhone: contact.phone || '',
      customerAddress: contact.address || '',
    }));
  }

  function resetForm() {
    setForm(initialForm);
    setResult(null);
    setError('');
  }

  function normalizePayload() {
    const numericFields = [
      'validityDays',
      'packageCount',
      'grossWeightKg',
      'volumeCbm',
      'freightRate',
      'originCharges',
      'destinationCharges',
      'handlingCharges',
      'documentationCharges',
      'customsCharges',
      'insuranceCharges',
      'otherCharges',
      'commissionAmount',
      'marginAmount',
    ];

    const payload = { ...form };

    numericFields.forEach((field) => {
      payload[field] =
        payload[field] === '' || payload[field] === null
          ? null
          : Number(payload[field]);
    });

    return payload;
  }

  async function submit() {
    if (!form.opportunityId.trim()) {
      setError('La oportunidad es obligatoria');
      return null;
    }

    if (!form.customerName.trim()) {
      setError('El cliente es obligatorio');
      return null;
    }

    try {
      setIsSaving(true);
      setError('');

      const response = await createLclProforma(normalizePayload());
      setResult(response);

      return response;
    } catch (err) {
      setError(err.message || 'No se pudo crear la proforma LCL');
      return null;
    } finally {
      setIsSaving(false);
    }
  }

  return {
    form,
    result,
    isSaving,
    error,
    updateField,
    preloadContact,
    resetForm,
    submit,
  };
} 