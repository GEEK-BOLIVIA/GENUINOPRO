import { useEffect, useState } from 'react';
import { getSellerUsers } from '../../services/adminUsersApi';
import { getBoliviaCities } from '../../services/customerProfileService';

const INITIAL_FORM = {
  customerType: 'NATURAL_PERSON',

  // Datos generales del lead
  messagePreview: '',
  assignedSellerId: '',

  // Persona natural
  fullName: '',
  cityCode: '',
  mobilePhone: '',

  // Empresa
  legalName: '',
  taxId: '',
  companyPhone: '',
  addressText: '',
  mapsUrl: '',
  legalRepresentativeName: '',
};

export default function CreateLeadModal({
  open,
  onClose,
  onCreate,
}) {
  const [form, setForm] = useState(INITIAL_FORM);
  const [sellerUsers, setSellerUsers] = useState([]);
  const [cities, setCities] = useState([]);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!open) return;

async function loadInitialData() {
  const [sellersResult, citiesResult] =
    await Promise.allSettled([
      getSellerUsers(),
      getBoliviaCities(),
    ]);

  if (sellersResult.status === 'fulfilled') {
    setSellerUsers(
      Array.isArray(sellersResult.value)
        ? sellersResult.value
        : []
    );
  } else {
    console.warn(
      'No se pudo cargar el listado de vendedores:',
      sellersResult.reason
    );

    setSellerUsers([]);
  }

  if (citiesResult.status === 'fulfilled') {
    setCities(
      Array.isArray(citiesResult.value)
        ? citiesResult.value
        : []
    );
  } else {
    console.error(
      'No se pudo cargar el catálogo de ciudades:',
      citiesResult.reason
    );

    setCities([]);
  }
}

    loadInitialData();
  }, [open]);

  if (!open) return null;

  function updateField(field, value) {
    setForm((prev) => ({
      ...prev,
      [field]: value,
    }));
  }

  function validate() {
    if (form.customerType === 'NATURAL_PERSON') {
      if (!form.fullName.trim()) {
        throw new Error(
          'El nombre completo es obligatorio.'
        );
      }


      if (!form.mobilePhone.trim()) {
        throw new Error(
          'El número de celular es obligatorio.'
        );
      }
    }

    if (form.customerType === 'COMPANY') {
      if (!form.legalName.trim()) {
        throw new Error(
          'La razón social es obligatoria.'
        );
      }

      if (!form.taxId.trim()) {
        throw new Error(
          'El NIT es obligatorio.'
        );
      }

      if (!form.companyPhone.trim()) {
        throw new Error(
          'El teléfono es obligatorio.'
        );
      }

      if (!form.addressText.trim()) {
        throw new Error(
          'La dirección es obligatoria.'
        );
      }

      if (!form.legalRepresentativeName.trim()) {
        throw new Error(
          'El representante legal es obligatorio.'
        );
      }
      if (!form.cityCode) {
        throw new Error('La ciudad es obligatoria.');
      }
    }
  }

  function buildPayload() {
    if (form.customerType === 'NATURAL_PERSON') {
      return {
        fullName: form.fullName.trim(),
        phone: form.mobilePhone.trim(),
        messagePreview: form.messagePreview.trim(),
        assignedSellerId: form.assignedSellerId,

        customerProfile: {
          customerType: 'NATURAL_PERSON',
          fullName: form.fullName.trim(),
          cityCode: form.cityCode,
          mobilePhone: form.mobilePhone.trim(),
        },
      };
    }

    return {
      fullName: form.legalName.trim(),
      phone: form.companyPhone.trim(),
      messagePreview: form.messagePreview.trim(),
      assignedSellerId: form.assignedSellerId,

      customerProfile: {
        customerType: 'COMPANY',
        legalName: form.legalName.trim(),
        taxId: form.taxId.trim(),
        companyPhone: form.companyPhone.trim(),
        cityCode: form.cityCode,
        addressText: form.addressText.trim(),
        mapsUrl: form.mapsUrl.trim() || null,
        legalRepresentativeName:
          form.legalRepresentativeName.trim(),
      },
    };
  }

  async function handleSubmit(event) {
    event.preventDefault();

    try {
      validate();
      setSaving(true);

      await onCreate(buildPayload());

      setForm(INITIAL_FORM);
      onClose();
    } catch (error) {
      console.error(
        'Error creando contacto:',
        error
      );

      alert(
        error.message ||
          'No se pudo crear el contacto.'
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center overflow-y-auto bg-black/40 p-6 backdrop-blur-sm">
      <div className="my-8 w-full max-w-3xl rounded-3xl bg-white p-8 shadow-2xl">
        <div className="flex items-center justify-between gap-4">
          <div>
            <p className="text-sm text-slate-500">
              Comercial
            </p>

            <h2 className="mt-1 text-2xl font-black text-slate-900">
              Nuevo contacto
            </h2>

            <p className="mt-2 text-sm text-slate-500">
              Registra los datos que serán utilizados
              posteriormente en las proformas.
            </p>
          </div>

          <button
            type="button"
            onClick={onClose}
            className="rounded-2xl border border-slate-200 px-4 py-2 text-sm font-bold text-slate-600 hover:bg-slate-50"
          >
            Cerrar
          </button>
        </div>

        <form
          onSubmit={handleSubmit}
          className="mt-8 space-y-6"
        >
          <div>
            <label className="text-sm font-bold text-slate-700">
              Tipo de cliente
            </label>

            <div className="mt-3 grid gap-3 md:grid-cols-2">
              <CustomerTypeButton
                active={
                  form.customerType ===
                  'NATURAL_PERSON'
                }
                title="Persona natural"
                description="Cliente individual"
                onClick={() =>
                  updateField(
                    'customerType',
                    'NATURAL_PERSON'
                  )
                }
              />

              <CustomerTypeButton
                active={
                  form.customerType === 'COMPANY'
                }
                title="Empresa"
                description="Persona jurídica"
                onClick={() =>
                  updateField(
                    'customerType',
                    'COMPANY'
                  )
                }
              />
            </div>
          </div>

          {form.customerType ===
            'NATURAL_PERSON' && (
            <section className="rounded-3xl border border-slate-200 bg-slate-50 p-5">
              <h3 className="font-black text-slate-900">
                Datos de la persona natural
              </h3>

              <div className="mt-5 grid gap-4 md:grid-cols-2">
                <Field
                  label="Nombre completo"
                  value={form.fullName}
                  onChange={(value) =>
                    updateField(
                      'fullName',
                      value
                    )
                  }
                />

                <label className="block">
                  <span className="text-sm font-bold text-slate-700">
                    Ciudad
                  </span>

                  <select
                    value={form.cityCode}
                    onChange={(event) =>
                      updateField(
                        'cityCode',
                        event.target.value
                      )
                    }
                    className="mt-2 w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 outline-none focus:border-orange-300"
                  >
                    <option value="">
                      Seleccionar ciudad
                    </option>

                    {cities.map((city) => (
                      <option
                        key={city.code}
                        value={city.code}
                      >
                        {city.name} —{' '}
                        {city.department}
                      </option>
                    ))}
                  </select>
                </label>

                <Field
                  label="Número de celular"
                  value={form.mobilePhone}
                  onChange={(value) =>
                    updateField(
                      'mobilePhone',
                      value
                    )
                  }
                />
              </div>
            </section>
          )}

          {form.customerType === 'COMPANY' && (
            <section className="rounded-3xl border border-slate-200 bg-slate-50 p-5">
              <h3 className="font-black text-slate-900">
                Datos de la empresa
              </h3>

              <div className="mt-5 grid gap-4 md:grid-cols-2">
                <Field
                  label="Razón social"
                  value={form.legalName}
                  onChange={(value) =>
                    updateField(
                      'legalName',
                      value
                    )
                  }
                />

                <Field
                  label="NIT"
                  value={form.taxId}
                  onChange={(value) =>
                    updateField('taxId', value)
                  }
                />

                <Field
                  label="Teléfono"
                  value={form.companyPhone}
                  onChange={(value) =>
                    updateField(
                      'companyPhone',
                      value
                    )
                  }
                />

                <label className="block">
                  <span className="text-sm font-bold text-slate-700">
                    Ciudad
                  </span>

                  <select
                    value={form.cityCode}
                    onChange={(event) =>
                      updateField(
                        'cityCode',
                        event.target.value
                      )
                    }
                    className="mt-2 w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 outline-none focus:border-orange-300"
                  >
                    <option value="">
                      Seleccionar ciudad
                    </option>

                    {cities.map((city) => (
                      <option
                        key={city.code}
                        value={city.code}
                      >
                        {city.name} — {city.department}
                      </option>
                    ))}
                  </select>
                </label>

                <Field
                  label="Representante legal"
                  value={
                    form.legalRepresentativeName
                  }
                  onChange={(value) =>
                    updateField(
                      'legalRepresentativeName',
                      value
                    )
                  }
                />

                <div className="md:col-span-2">
                  <Field
                    label="Dirección"
                    value={form.addressText}
                    onChange={(value) =>
                      updateField(
                        'addressText',
                        value
                      )
                    }
                    placeholder="Dirección completa de la empresa"
                  />
                </div>

                <div className="md:col-span-2">
                  <Field
                    label="Enlace de Google Maps"
                    value={form.mapsUrl}
                    onChange={(value) =>
                      updateField(
                        'mapsUrl',
                        value
                      )
                    }
                    placeholder="https://maps.google.com/..."
                    required={false}
                  />
                </div>
              </div>
            </section>
          )}

          <section className="grid gap-4 md:grid-cols-2">
            <label className="block">
              <span className="text-sm font-bold text-slate-700">
                Responsable
              </span>

              <select
                value={form.assignedSellerId}
                onChange={(event) =>
                  updateField(
                    'assignedSellerId',
                    event.target.value
                  )
                }
                className="mt-2 w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none focus:border-orange-300"
              >
                <option value="">
                  Asignación automática
                </option>

                {sellerUsers.map((user) => (
                  <option
                    key={user.id}
                    value={user.username}
                  >
                    {user.firstName ||
                      user.username}{' '}
                    {user.lastName || ''}
                  </option>
                ))}
              </select>
            </label>

            <label className="block">
              <span className="text-sm font-bold text-slate-700">
                Observación
              </span>

              <textarea
                rows={3}
                value={form.messagePreview}
                onChange={(event) =>
                  updateField(
                    'messagePreview',
                    event.target.value
                  )
                }
                className="mt-2 w-full rounded-2xl border border-slate-200 px-4 py-3 outline-none focus:border-orange-300"
              />
            </label>
          </section>

          <button
            type="submit"
            disabled={saving}
            className="w-full rounded-2xl bg-orange-500 px-5 py-4 text-sm font-bold text-white hover:bg-orange-600 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {saving
              ? 'Guardando contacto...'
              : 'Crear contacto'}
          </button>
        </form>
      </div>
    </div>
  );
}

function CustomerTypeButton({
  active,
  title,
  description,
  onClick,
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`rounded-2xl border p-4 text-left transition ${
        active
          ? 'border-orange-500 bg-orange-50'
          : 'border-slate-200 bg-white hover:border-slate-300'
      }`}
    >
      <p
        className={`font-black ${
          active
            ? 'text-orange-700'
            : 'text-slate-900'
        }`}
      >
        {title}
      </p>

      <p className="mt-1 text-sm text-slate-500">
        {description}
      </p>
    </button>
  );
}

function Field({
  label,
  value,
  onChange,
  placeholder = '',
}) {
  return (
    <label className="block">
      <span className="text-sm font-bold text-slate-700">
        {label}
      </span>

      <input
        type="text"
        value={value}
        placeholder={placeholder}
        onChange={(event) =>
          onChange(event.target.value)
        }
        className="mt-2 w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 outline-none focus:border-orange-300"
      />
    </label>
  );
}