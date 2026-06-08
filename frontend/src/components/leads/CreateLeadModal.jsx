import { useEffect, useState } from 'react';
import { getSellerUsers } from '../../services/adminUsersApi';

export default function CreateLeadModal({
  open,
  onClose,
  onCreate,
}) {

  const [form, setForm] = useState({
    fullName: '',
    phone: '',
    messagePreview: '',
    assignedSellerId: '',
  });

  const [sellerUsers, setSellerUsers] = useState([]);

  useEffect(() => {
    async function loadSellers() {
      try {
        const data = await getSellerUsers();
        setSellerUsers(Array.isArray(data) ? data : []);
      } catch (error) {
        console.error('Error cargando vendedores', error);
        setSellerUsers([]);
      }
    }

    if (open) {
      loadSellers();
    }
  }, [open]);

  if (!open) return null;

  function updateField(field, value) {
    setForm((prev) => ({
      ...prev,
      [field]: value,
    }));
  }

async function handleSubmit(e) {
  e.preventDefault();

  try {
    await onCreate(form);

    setForm({
      fullName: '',
      phone: '',
      messagePreview: '',
      assignedSellerId: '',
    });

    onClose();
  } catch (error) {
    console.error('Error creando lead:', error);
    alert('No se pudo crear el lead. Revisa consola o backend.');
  }
}

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm">
      <div className="w-full max-w-xl rounded-3xl bg-white p-8 shadow-2xl">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm text-slate-500">
              Comercial
            </p>

            <h2 className="mt-1 text-2xl font-black text-slate-900">
              Nuevo Lead
            </h2>
          </div>

          <button
            onClick={onClose}
            className="rounded-2xl border border-slate-200 px-4 py-2 text-sm font-bold text-slate-600 hover:bg-slate-50"
          >
            Cerrar
          </button>
        </div>

        <form
          onSubmit={handleSubmit}
          className="mt-8 space-y-5"
        >
          <div>
            <label className="text-sm font-bold text-slate-700">
              Nombre / Empresa
            </label>

            <input
              type="text"
              value={form.fullName}
              onChange={(e) => updateField('fullName', e.target.value)}
              className="mt-2 w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none focus:border-orange-300"
            />
          </div>

          <div>
            <label className="text-sm font-bold text-slate-700">
              Teléfono
            </label>

            <input
              type="text"
              value={form.phone}
              onChange={(e) => updateField('phone', e.target.value)}
              className="mt-2 w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none focus:border-orange-300"
            />
          </div>

          <div>
            <label className="text-sm font-bold text-slate-700">
              Observación
            </label>

            <textarea
              rows={4}
              value={form.messagePreview}
              onChange={(e) => updateField('messagePreview', e.target.value)}
              className="mt-2 w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none focus:border-orange-300"
            />
          </div>

          <div>
            <label className="text-sm font-bold text-slate-700">
              Responsable
            </label>

            <select
              value={form.assignedSellerId}
              onChange={(e) => updateField('assignedSellerId', e.target.value)}
              className="mt-2 w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none focus:border-orange-300"
            >
              <option value="">Asignación automática</option>

              {sellerUsers.map((user) => (
                <option key={user.id} value={user.username}>
                  {user.firstName || user.username} {user.lastName || ''}
                </option>
              ))}
            </select>
          </div>

          <button
            type="submit"
            className="w-full rounded-2xl bg-orange-500 px-5 py-4 text-sm font-bold text-white hover:bg-orange-600"
          >
            Crear lead
          </button>
        </form>
      </div>
    </div>
  );
}