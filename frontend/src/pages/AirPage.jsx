import {
  useNavigate,
  useParams,
} from 'react-router-dom';

import AirOperationalSimulator from '../modules/proforma/air/AirOperationalSimulator';

import AirProformaDetail from '../modules/proforma/air/AirProformaDetail';

export default function AirPage({
  mode = 'list',
}) {
  const navigate =
    useNavigate();

  const { id } =
    useParams();

  if (mode === 'new') {
    return (
      <div className="space-y-5">

        <button
          type="button"
          onClick={() =>
            navigate('/lcl')
          }
          className="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm font-bold text-slate-600 transition hover:bg-slate-50"
        >
          ← Volver a proformas
        </button>

        <section>
          <p className="text-sm font-medium text-slate-500">
            Nueva cotización
          </p>

          <h1 className="mt-2 text-3xl font-black text-slate-900">
            Proforma Aérea
          </h1>

          <p className="mt-2 text-sm text-slate-500">
            Simulación de importación vía aérea.
          </p>
        </section>

        <AirOperationalSimulator
          mode="new"
          onSaved={(data) =>
            navigate(
              `/air/${data.id}`
            )
          }
        />

      </div>
    );
  }

  if (
    mode === 'detail' &&
    id
  ) {
    return (
      <AirProformaDetail
        id={id}
      />
    );
  }

  if (
    mode === 'edit' &&
    id
  ) {
    return (
      <div className="space-y-5">

        <button
          type="button"
          onClick={() =>
            navigate(
              `/air/${id}`
            )
          }
          className="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm font-bold text-slate-600"
        >
          ← Volver al detalle
        </button>

        <section>
          <p className="text-sm font-medium text-slate-500">
            Corrección de cotización
          </p>

          <h1 className="mt-2 text-3xl font-black text-slate-900">
            Editar Proforma Aérea
          </h1>
        </section>

        <AirOperationalSimulator
          mode="edit"
          proformaId={id}
          onSaved={(data) =>
            navigate(
              `/air/${data.id}`
            )
          }
        />

      </div>
    );
  }

  return null;
}