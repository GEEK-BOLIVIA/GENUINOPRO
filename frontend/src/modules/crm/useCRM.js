import { useEffect, useState } from 'react';
import { apiFetch } from '../../services/api';
import { normalizeTimelineResponse, mapStage } from '../../utils/crm';

export default function useCRM({ setToast }) {
  const [leads, setLeads] = useState([]);
  const [selectedLead, setSelectedLead] = useState(null);
  const [activities, setActivities] = useState([]);
  const [proformas, setProformas] = useState([]);

  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingTimeline, setIsLoadingTimeline] = useState(false);
  const [isSavingActivity, setIsSavingActivity] = useState(false);
  const [isSavingProforma, setIsSavingProforma] = useState(false);

  const [activityError, setActivityError] = useState('');

  const [activityForm, setActivityForm] = useState({
    type: 'NOTE',
    title: '',
    description: '',
    activityDate: new Date().toISOString().slice(0, 16),
  });

  async function loadLeads() {
    try {
      setIsLoading(true);

      const data = await apiFetch('/opportunities');

      const mapped = data.map((item) => ({
        id: item.id,
        cliente: (item.title || '').replace('Lead WhatsApp - ', '').trim() || 'Sin cliente',
        contacto: item.notes || 'Sin notas',
        telefono: item.phone || '-',
        etapa: mapStage(item.stage),
        monto: item.amount || 0,
        asesor:
          item.ownerUserId === 'seller1'
            ? 'Carlos'
            : item.ownerUserId === 'seller3'
            ? 'Luis'
            : item.ownerUserId || 'Sin asignar',
        prioridad: item.priority || 'Media',
        tituloOriginal: item.title || '',
      }));

      setLeads(mapped);
    } catch (err) {
      console.error(err);
      setToast?.({
        type: 'error',
        message: err.message || 'Error cargando oportunidades',
      });
    } finally {
      setIsLoading(false);
    }
  }

  async function openLead(lead) {
    setSelectedLead(lead);
    setActivities([]);
    setProformas([]);
    setActivityError('');
    setActivityForm({
      type: 'NOTE',
      title: '',
      description: '',
      activityDate: new Date().toISOString().slice(0, 16),
    });
    setIsLoadingTimeline(true);

    try {
      const [timelineData, proformasData] = await Promise.all([
        apiFetch(`/opportunities/${lead.id}/timeline`),
        apiFetch(`/opportunities/${lead.id}/proformas`),
      ]);

      setActivities(normalizeTimelineResponse(timelineData));
      setProformas(proformasData || []);
    } catch (err) {
      console.error(err);
      setToast?.({
        type: 'error',
        message: err.message || 'Error al cargar detalles',
      });
    } finally {
      setIsLoadingTimeline(false);
    }
  }

  async function saveActivity() {
    if (!selectedLead?.id) return;

    if (!activityForm.title.trim()) {
      setActivityError('El título es obligatorio');
      return;
    }

    setIsSavingActivity(true);

    try {
      const created = await apiFetch(`/opportunities/${selectedLead.id}/timeline`, {
        method: 'POST',
        body: JSON.stringify({
          ...activityForm,
          title: activityForm.title.trim(),
          description: activityForm.description.trim(),
        }),
      });

      const normalizedCreated = normalizeTimelineResponse({
        activities: [created],
        events: [],
      })[0];

      setActivities((prev) => [normalizedCreated, ...prev]);
      setActivityForm({
        type: 'NOTE',
        title: '',
        description: '',
        activityDate: new Date().toISOString().slice(0, 16),
      });
      setActivityError('');
      setToast?.({
        type: 'success',
        message: 'Actividad registrada correctamente',
      });
    } catch (err) {
      console.error(err);
      setActivityError(err.message || 'No se pudo guardar la actividad');
      setToast?.({
        type: 'error',
        message: err.message || 'No se pudo guardar la actividad',
      });
    } finally {
      setIsSavingActivity(false);
    }
  }

  async function handleCreateProforma({ proformaForm, onSuccess } = {}) {
    if (!selectedLead?.id) return;

    if (!proformaForm?.amount || Number(proformaForm.amount) <= 0) {
      setToast?.({
        type: 'error',
        message: 'Debes ingresar un monto válido para la proforma',
      });
      return;
    }

    setIsSavingProforma(true);

    try {
      const payload = {
        customerId: selectedLead.id,
        opportunityId: selectedLead.id,
        currency: proformaForm.currency,
        amount: parseFloat(proformaForm.amount),
      };

      const newProforma = await apiFetch('/proformas', {
        method: 'POST',
        body: JSON.stringify(payload),
      });

      setProformas((prev) => [newProforma, ...prev]);

      setToast?.({
        type: 'success',
        message: 'Proforma generada en borrador',
      });

      onSuccess?.();
      await loadLeads();
    } catch (err) {
      console.error(err);
      setToast?.({
        type: 'error',
        message: err.message || 'Error al crear proforma',
      });
    } finally {
      setIsSavingProforma(false);
    }
  }

  async function handleProformaDecision(id, action, reason) {
    try {
      const updated = await apiFetch(`/proformas/${id}/${action}`, {
        method: 'POST',
        body: JSON.stringify({ reason }),
      });

      setProformas((prev) => prev.map((p) => (p.id === id ? updated : p)));

      setToast?.({
        type: 'success',
        message: `Proforma ${action === 'approve' ? 'aprobada' : 'rechazada'}`,
      });

      if (action === 'approve') {
        await loadLeads();
      }
    } catch (err) {
      console.error(err);
      setToast?.({
        type: 'error',
        message: err.message || 'Error al procesar decisión',
      });
    }
  }

  useEffect(() => {
    loadLeads();
  }, []);

  return {
    leads,
    selectedLead,
    setSelectedLead,
    activities,
    proformas,
    isLoading,
    isLoadingTimeline,
    isSavingActivity,
    isSavingProforma,
    activityForm,
    setActivityForm,
    activityError,
    openLead,
    saveActivity,
    handleCreateProforma,
    handleProformaDecision,
    reloadLeads: loadLeads,
  };
}