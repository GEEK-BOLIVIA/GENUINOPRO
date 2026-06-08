import { useEffect, useMemo, useState } from 'react';
import { ShieldCheck, UserPlus, Search, X, Loader2 } from 'lucide-react';
import {
  createAdminUser,
  disableAdminUser,
  enableAdminUser,
  getAdminUsers,
  resetAdminUserPassword,
  updateAdminUser,
} from '../../services/adminUsersApi';


const ROLES = ['ADMIN', 'GERENCIA', 'SUPERVISOR', 'VENDEDOR', 'CLIENTE'];

const emptyForm = {
  firstName: '',
  lastName: '',
  email: '',
  username: '',
  password: '',
  role: 'VENDEDOR',
};

export default function UsersAdminPage() {
  const [users, setUsers] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [manageModalOpen, setManageModalOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [manageForm, setManageForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    role: 'VENDEDOR',
    password: '',
  });

  async function loadUsers() {
    try {
      setLoading(true);
      setError('');
      const data = await getAdminUsers();
      setUsers(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error(err);
      setError('No se pudieron cargar los usuarios desde Keycloak.');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadUsers();
  }, []);

  const filteredUsers = useMemo(() => {
    const value = search.trim().toLowerCase();

    if (!value) return users;

    return users.filter((user) => {
      const fullName = `${user.firstName || ''} ${user.lastName || ''}`.toLowerCase();

      return (
        fullName.includes(value) ||
        (user.username || '').toLowerCase().includes(value) ||
        (user.email || '').toLowerCase().includes(value) ||
        (user.role || '').toLowerCase().includes(value)
      );
    });
  }, [users, search]);

  function updateField(field, value) {
    setForm((prev) => ({
      ...prev,
      [field]: value,
    }));
  }

  function closeModal() {
    setModalOpen(false);
    setForm(emptyForm);
  }

    function openManageModal(user) {
    setSelectedUser(user);
    setManageForm({
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        email: user.email || '',
        role: user.role || 'VENDEDOR',
        password: '',
    });
    setManageModalOpen(true);
    }

    function closeManageModal() {
    setManageModalOpen(false);
    setSelectedUser(null);
    }

    function updateManageField(field, value) {
    setManageForm((prev) => ({
        ...prev,
        [field]: value,
    }));
    }

    async function handleUpdateUser(event) {
    event.preventDefault();

    if (!selectedUser) return;

    try {
        setSaving(true);
        setError('');

        await updateAdminUser(selectedUser.id, {
        firstName: manageForm.firstName,
        lastName: manageForm.lastName,
        email: manageForm.email,
        role: manageForm.role,
        });

        if (manageForm.password && manageForm.password.trim().length > 0) {
        await resetAdminUserPassword(selectedUser.id, manageForm.password.trim());
        }

        await loadUsers();
        closeManageModal();
    } catch (err) {
        console.error(err);
        setError('No se pudo actualizar el usuario.');
    } finally {
        setSaving(false);
    }
    }

    async function handleToggleUserStatus() {
    if (!selectedUser) return;

    try {
        setSaving(true);
        setError('');

        if (selectedUser.enabled) {
        await disableAdminUser(selectedUser.id);
        } else {
        await enableAdminUser(selectedUser.id);
        }

        await loadUsers();
        closeManageModal();
    } catch (err) {
        console.error(err);
        setError('No se pudo cambiar el estado del usuario.');
    } finally {
        setSaving(false);
    }
    }

    async function handleResetPassword() {
    if (!selectedUser || !manageForm.password) return;

    try {
        setSaving(true);
        setError('');

        await resetAdminUserPassword(selectedUser.id, manageForm.password);

        setManageForm((prev) => ({
        ...prev,
        password: '',
        }));

        await loadUsers();
    } catch (err) {
        console.error(err);
        setError('No se pudo resetear la contraseña.');
    } finally {
        setSaving(false);
    }
    }

  async function handleCreateUser(event) {
    event.preventDefault();

    try {
      setSaving(true);
      setError('');

      await createAdminUser(form);
      await loadUsers();

      closeModal();
    } catch (err) {
      console.error(err);
      setError('No se pudo crear el usuario. Revisa que el usuario/correo no exista y que el rol esté creado en Keycloak.');
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="space-y-8">
      <section className="flex items-start justify-between gap-4">
        <div>
          <p className="text-xs font-bold uppercase tracking-[0.35em] text-slate-400">
            Administración
          </p>
          <h1 className="mt-2 text-4xl font-black text-slate-950">
            Usuarios y roles
          </h1>
          <p className="mt-2 text-slate-500">
            Gestión real de accesos internos y usuarios cliente desde Keycloak.
          </p>
        </div>

        <button
          type="button"
          onClick={() => setModalOpen(true)}
          className="flex items-center gap-2 rounded-2xl bg-orange-500 px-5 py-3 font-bold text-white shadow-lg shadow-orange-500/20 hover:bg-orange-600"
        >
          <UserPlus size={18} />
          Nuevo usuario
        </button>
      </section>

      {error && (
        <div className="rounded-2xl border border-red-200 bg-red-50 px-5 py-4 text-sm font-semibold text-red-700">
          {error}
        </div>
      )}

      <section className="grid gap-4 md:grid-cols-4">
        <StatCard title="Usuarios" value={users.length} />
        <StatCard
          title="Vendedores"
          value={users.filter((u) => u.role === 'VENDEDOR').length}
        />
        <StatCard
          title="Admins"
          value={users.filter((u) => u.role === 'ADMIN').length}
        />
        <StatCard
          title="Clientes"
          value={users.filter((u) => u.role === 'CLIENTE').length}
        />
      </section>

      <section className="rounded-3xl border border-slate-200 bg-white shadow-sm">
        <div className="flex items-center justify-between border-b border-slate-100 p-6">
          <div>
            <h2 className="text-xl font-black text-slate-900">
              Usuarios del sistema
            </h2>
            <p className="mt-1 text-sm text-slate-500">
              Usuarios reales registrados en Keycloak.
            </p>
          </div>

          <div className="flex items-center gap-2 rounded-2xl border border-slate-200 px-4 py-3 text-slate-400">
            <Search size={18} />
            <input
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              placeholder="Buscar usuario..."
              className="w-56 bg-transparent text-sm outline-none"
            />
          </div>
        </div>

        <div className="grid grid-cols-5 bg-slate-50 px-6 py-4 text-xs font-black uppercase tracking-wide text-slate-400">
          <div>Nombre</div>
          <div>Correo</div>
          <div>Rol</div>
          <div>Estado</div>
          <div className="text-right">Acción</div>
        </div>

        {loading && (
          <div className="flex items-center gap-3 border-t border-slate-100 px-6 py-8 text-sm font-bold text-slate-500">
            <Loader2 className="animate-spin" size={18} />
            Cargando usuarios...
          </div>
        )}

        {!loading && filteredUsers.length === 0 && (
          <div className="border-t border-slate-100 px-6 py-8 text-sm font-bold text-slate-400">
            No hay usuarios para mostrar.
          </div>
        )}

        {!loading &&
          filteredUsers.map((user) => (
            <div
              key={user.id}
              className="grid grid-cols-5 items-center border-t border-slate-100 px-6 py-5"
            >
              <div>
                <p className="font-black text-slate-900">
                  {user.firstName || user.lastName
                    ? `${user.firstName || ''} ${user.lastName || ''}`.trim()
                    : user.username}
                </p>
                <p className="mt-1 text-xs font-semibold text-slate-400">
                  @{user.username}
                </p>
              </div>

              <div className="text-sm text-slate-500">
                {user.email || 'Sin correo'}
              </div>

              <div>
                <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-bold text-slate-700">
                  {user.role || 'SIN_ROL'}
                </span>
              </div>

              <div>
                <span
                  className={`rounded-full px-3 py-1 text-xs font-bold ${
                    user.enabled
                      ? 'bg-emerald-100 text-emerald-700'
                      : 'bg-red-100 text-red-700'
                  }`}
                >
                  {user.enabled ? 'Activo' : 'Inactivo'}
                </span>
              </div>

              <div className="text-right">
                <button
                type="button"
                onClick={() => openManageModal(user)}
                className="rounded-xl bg-slate-950 px-4 py-2 text-sm font-bold text-white hover:bg-slate-800"
                >
                Gestionar
                </button>
              </div>
            </div>
          ))}
      </section>

      {modalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/40 p-4">
          <div className="w-full max-w-2xl rounded-3xl bg-white shadow-2xl">
            <div className="flex items-center justify-between border-b border-slate-100 p-6">
              <div>
                <h2 className="text-2xl font-black text-slate-950">
                  Nuevo usuario
                </h2>
                <p className="mt-1 text-sm text-slate-500">
                  Se creará un usuario real en Keycloak.
                </p>
              </div>

              <button
                type="button"
                onClick={closeModal}
                className="rounded-2xl bg-slate-100 p-3 text-slate-500 hover:bg-slate-200"
              >
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleCreateUser} className="space-y-5 p-6">
              <div className="grid gap-4 md:grid-cols-2">
                <Field
                  label="Nombre"
                  value={form.firstName}
                  onChange={(value) => updateField('firstName', value)}
                  required
                />

                <Field
                  label="Apellido"
                  value={form.lastName}
                  onChange={(value) => updateField('lastName', value)}
                  required
                />

                <Field
                  label="Correo"
                  type="email"
                  value={form.email}
                  onChange={(value) => updateField('email', value)}
                  required
                />

                <Field
                  label="Usuario"
                  value={form.username}
                  onChange={(value) => updateField('username', value)}
                  required
                />

                <Field
                  label="Contraseña"
                  type="password"
                  value={form.password}
                  onChange={(value) => updateField('password', value)}
                  required
                />

                <div>
                  <label className="mb-2 block text-xs font-black uppercase tracking-wide text-slate-400">
                    Rol
                  </label>
                  <select
                    value={form.role}
                    onChange={(event) => updateField('role', event.target.value)}
                    className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm font-semibold outline-none focus:border-orange-400"
                  >
                    {ROLES.map((role) => (
                      <option key={role} value={role}>
                        {role}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="flex justify-end gap-3 pt-4">
                <button
                  type="button"
                  onClick={closeModal}
                  className="rounded-2xl border border-slate-200 px-5 py-3 text-sm font-black text-slate-600 hover:bg-slate-50"
                >
                  Cancelar
                </button>

                <button
                  type="submit"
                  disabled={saving}
                  className="flex items-center gap-2 rounded-2xl bg-orange-500 px-5 py-3 text-sm font-black text-white shadow-lg shadow-orange-500/20 hover:bg-orange-600 disabled:cursor-not-allowed disabled:opacity-60"
                >
                  {saving && <Loader2 className="animate-spin" size={16} />}
                  Crear usuario
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
      {manageModalOpen && selectedUser && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/40 p-4">
          <div className="w-full max-w-2xl rounded-3xl bg-white shadow-2xl">
            <div className="flex items-center justify-between border-b border-slate-100 p-6">
              <div>
                <h2 className="text-2xl font-black text-slate-950">
                  Gestionar usuario
                </h2>

                <p className="mt-1 text-sm text-slate-500">
                  Editar datos, rol, estado y contraseña.
                </p>
              </div>

              <button
                type="button"
                onClick={closeManageModal}
                className="rounded-2xl bg-slate-100 p-3"
              >
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleUpdateUser} className="space-y-5 p-6">

              <div className="grid gap-4 md:grid-cols-2">

                <Field
                  label="Nombre"
                  value={manageForm.firstName}
                  onChange={(value) =>
                    updateManageField('firstName', value)
                  }
                />

                <Field
                  label="Apellido"
                  value={manageForm.lastName}
                  onChange={(value) =>
                    updateManageField('lastName', value)
                  }
                />

                <Field
                  label="Correo"
                  type="email"
                  value={manageForm.email}
                  onChange={(value) =>
                    updateManageField('email', value)
                  }
                />

                <div>
                  <label className="mb-2 block text-xs font-black uppercase tracking-wide text-slate-400">
                    Rol
                  </label>

                  <select
                    value={manageForm.role}
                    onChange={(e) =>
                      updateManageField('role', e.target.value)
                    }
                    className="w-full rounded-2xl border border-slate-200 px-4 py-3"
                  >
                    {ROLES.map((role) => (
                      <option key={role} value={role}>
                        {role}
                      </option>
                    ))}
                  </select>
                </div>

              </div>

              <div className="rounded-2xl border border-slate-200 p-4">

                <p className="mb-3 text-sm font-bold text-slate-700">
                  Cambiar contraseña
                </p>

                <Field
                  label="Nueva contraseña"
                  type="password"
                  value={manageForm.password}
                  onChange={(value) =>
                    updateManageField('password', value)
                  }
                />

                <p className="mt-2 text-xs font-semibold text-slate-400">
                Si dejas este campo vacío, la contraseña actual no se modificará.
                </p>

              </div>

              <div className="flex justify-between">

                <button
                  type="button"
                  onClick={handleToggleUserStatus}
                  className={`rounded-xl px-4 py-2 text-sm font-bold text-white ${
                    selectedUser.enabled
                      ? 'bg-red-600'
                      : 'bg-emerald-600'
                  }`}
                >
                  {selectedUser.enabled
                    ? 'Desactivar usuario'
                    : 'Activar usuario'}
                </button>

                <button
                  type="submit"
                  disabled={saving}
                  className="rounded-xl bg-orange-500 px-5 py-3 text-sm font-bold text-white"
                >
                  Guardar cambios
                </button>

              </div>

            </form>
          </div>
        </div>
      )}
    </div>
  );
}

function StatCard({ title, value }) {
  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="mb-4 inline-flex rounded-2xl bg-orange-50 p-3 text-orange-600">
        <ShieldCheck size={20} />
      </div>
      <p className="text-sm font-bold uppercase tracking-wide text-slate-400">
        {title}
      </p>
      <p className="mt-2 text-3xl font-black text-slate-950">{value}</p>
    </div>
  );
}

function Field({ label, value, onChange, type = 'text', required = false }) {
  return (
    <div>
      <label className="mb-2 block text-xs font-black uppercase tracking-wide text-slate-400">
        {label}
      </label>
      <input
        type={type}
        value={value}
        required={required}
        onChange={(event) => onChange(event.target.value)}
        className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm font-semibold outline-none focus:border-orange-400"
      />
    </div>
  );
}