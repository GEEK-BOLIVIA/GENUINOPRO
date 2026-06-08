import { useEffect, useState } from 'react';
import { Lock, User } from 'lucide-react';
import { changeMyPassword, getMe } from '../../services/meApi';

export default function MyAccountPage() {
  const [me, setMe] = useState(null);
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');

  async function loadMe() {
    try {
      const data = await getMe();
      setMe(data);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadMe();
  }, []);

  async function handleChangePassword(event) {
    event.preventDefault();

    try {
      setSaving(true);
      setMessage('');

      await changeMyPassword(password);

      setPassword('');
      setMessage('Contraseña actualizada correctamente.');
    } catch (error) {
      console.error(error);
      setMessage('No se pudo actualizar la contraseña.');
    } finally {
      setSaving(false);
    }
  }

  if (loading) {
    return (
      <div className="p-8">
        <p>Cargando perfil...</p>
      </div>
    );
  }

  return (
    <div className="space-y-8">

      <section>
        <p className="text-xs font-bold uppercase tracking-[0.35em] text-slate-400">
          Cuenta
        </p>

        <h1 className="mt-2 text-4xl font-black text-slate-950">
          Mi cuenta
        </h1>
      </section>

      <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">

        <div className="mb-6 flex items-center gap-3">
          <User size={22} />
          <h2 className="text-xl font-black">
            Información de usuario
          </h2>
        </div>

        <div className="grid gap-4 md:grid-cols-2">

          <InfoField
            label="Usuario"
            value={me?.preferred_username || '-'}
          />

            <InfoField
            label="Rol"
            value={
                (me?.authorities || [])
                .find((role) =>
                    ['ROLE_ADMIN', 'ROLE_GERENCIA', 'ROLE_SUPERVISOR', 'ROLE_VENDEDOR', 'ROLE_CLIENTE'].includes(role)
                )
                ?.replace('ROLE_', '') || '-'
            }
            />

        </div>

      </section>

      <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">

        <div className="mb-6 flex items-center gap-3">
          <Lock size={22} />
          <h2 className="text-xl font-black">
            Cambiar contraseña
          </h2>
        </div>

        <form onSubmit={handleChangePassword} className="space-y-4">

          <div>
            <label className="mb-2 block text-xs font-black uppercase tracking-wide text-slate-400">
              Nueva contraseña
            </label>

            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full rounded-2xl border border-slate-200 px-4 py-3"
            />
          </div>

          <button
            type="submit"
            disabled={saving}
            className="rounded-2xl bg-orange-500 px-5 py-3 font-bold text-white"
          >
            Guardar contraseña
          </button>

        </form>

        {message && (
          <p className="mt-4 text-sm font-semibold text-slate-600">
            {message}
          </p>
        )}

      </section>

    </div>
  );
}

function InfoField({ label, value }) {
  return (
    <div>
      <p className="text-xs font-black uppercase tracking-wide text-slate-400">
        {label}
      </p>

      <p className="mt-2 text-sm font-semibold text-slate-700">
        {value}
      </p>
    </div>
  );
}