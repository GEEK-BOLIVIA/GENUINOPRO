import { useEffect, useState } from 'react';
import { apiFetch } from '../../services/api';

export default function ApprovalPolicyPage() {
  const [data, setData] = useState(null);

  useEffect(() => {
    apiFetch('/config/approval-policies/LCL')
      .then(setData)
      .catch(console.error);
  }, []);

  const handleChange = (field, value) => {
    setData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleSave = async () => {
    await apiFetch('/config/approval-policies/LCL', {
      method: 'PUT',
      body: JSON.stringify({
        supervisorLimit: Number(data.supervisorLimit),
        commercialManagerLimit: Number(data.commercialManagerLimit)
      })
    });

    alert('Guardado correctamente');
  };

  if (!data) return null;

  return (
    <div className="mt-8 rounded-3xl bg-white p-6 shadow">
      <h2 className="mb-4 text-xl font-black">Parámetros de Aprobación</h2>

      <div className="space-y-4">
        <div>
          <label>Supervisor (USD)</label>
          <input
            type="number"
            value={data.supervisorLimit}
            onChange={e => handleChange('supervisorLimit', e.target.value)}
            className="w-full border p-2 rounded"
          />
        </div>

        <div>
          <label>Jefe Comercial (USD)</label>
          <input
            type="number"
            value={data.commercialManagerLimit}
            onChange={e => handleChange('commercialManagerLimit', e.target.value)}
            className="w-full border p-2 rounded"
          />
        </div>

        <button
          onClick={handleSave}
          className="bg-indigo-600 text-white px-4 py-2 rounded"
        >
          Guardar
        </button>
      </div>
    </div>
  );
}