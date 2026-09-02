import { useNavigate, useParams } from 'react-router-dom';

import HblOperationalSimulator
  from '../modules/proforma/hbl/HblOperationalSimulator';

  import HblProformaDetail
  from '../modules/proforma/hbl/HblProformaDetail';

export default function HblPage({
  mode = 'list',
}) {
  const navigate = useNavigate();
  const { id } = useParams();

  if (mode === 'new') {
    return (
      <div className="space-y-5">

        <button
          type="button"
          onClick={() => navigate('/lcl')}
          className="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm font-bold text-slate-600 hover:bg-slate-50"
        >
          ← Volver a proformas
        </button>

        <section>
          <p className="text-sm font-medium text-slate-500">
            Nueva cotización
          </p>

          <h1 className="mt-2 text-3xl font-black tracking-tight text-slate-900">
            Proforma HBL
          </h1>

          <p className="mt-2 max-w-3xl text-sm leading-6 text-slate-500">
            Simulación y generación de proforma para operaciones HBL.
          </p>
        </section>

        <HblOperationalSimulator />

      </div>
    );
  }

  if (mode === 'edit' && id) {
  return (
    <div className="space-y-5">

      <button
        type="button"
        onClick={() =>
          navigate(`/hbl/${id}`)
        }
        className="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm font-bold text-slate-600"
      >
        ← Volver al detalle
      </button>

      <section>
        <p className="text-sm font-medium text-slate-500">
          Corrección de proforma
        </p>

        <h1 className="mt-2 text-3xl font-black text-slate-900">
          Editar proforma HBL
        </h1>

        <p className="mt-2 text-sm text-slate-500">
          Corrige los datos observados y recalcula la operación.
        </p>
      </section>

      <HblOperationalSimulator
        mode="edit"
        proformaId={id}
      />

    </div>
  );
}

if (mode === 'detail' && id) {
  return (
    <div className="space-y-5">

      <button
        type="button"
        onClick={() =>
          navigate('/lcl')
        }
        className="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm font-bold text-slate-600 hover:bg-slate-50"
      >
        ← Volver a proformas
      </button>

      <HblProformaDetail id={id} />

    </div>
  );
}

  navigate('/lcl');

  return null;
}