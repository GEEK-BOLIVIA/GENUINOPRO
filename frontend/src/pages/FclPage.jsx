import { useNavigate } from 'react-router-dom';
import FclOperationalSimulator from '../modules/proforma/fcl/FclOperationalSimulator';
import { useParams } from 'react-router-dom';
import FclProformaDetail from '../modules/proforma/fcl/FclProformaDetail';


export default function FclPage({ mode = 'list' }) {
  const navigate = useNavigate();
  const { id } = useParams();

    if (mode === 'detail' && id) {
    return (
        <div className="space-y-5">
        <button
            onClick={() => navigate('/proformas')}
            className="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm font-bold text-slate-600 hover:bg-slate-50"
        >
            ← Volver a proformas
        </button>

        <FclProformaDetail id={id} />
        </div>
    );
    }

  if (mode === 'new' || mode === 'edit') {
    return (
      <div className="space-y-5">
        <button
          onClick={() => navigate('/proformas')}
          className="rounded-2xl border border-slate-200 bg-white px-4 py-2 text-sm font-bold text-slate-600 hover:bg-slate-50"
        >
          ← Volver a proformas
        </button>

        <section>
          <p className="text-sm font-medium text-slate-500">
            Nueva cotización
          </p>

        <h1 className="mt-2 text-3xl font-black tracking-tight text-slate-900">
          {mode === 'edit'
            ? 'Editar Proforma FCL'
            : 'Proforma FCL'}
        </h1>

          <p className="mt-2 max-w-3xl text-sm leading-6 text-slate-500">
            Simulador operativo para contenedores completos 20', 40' y 40HQ.
          </p>
        </section>

        <FclOperationalSimulator
          mode={mode}
          proformaId={id}
        />
      </div>
    );
  }

  return (
    <div className="rounded-3xl border border-slate-200 bg-white p-8 shadow-sm">
      <h1 className="text-3xl font-black text-slate-900">
        Proformas FCL
      </h1>

      <p className="mt-3 text-slate-500">
        Utiliza la bandeja principal de Proformas para listar las cotizaciones FCL.
      </p>
    </div>
  );
}