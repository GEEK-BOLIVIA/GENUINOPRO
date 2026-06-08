import { useAuth } from '../../security/AuthProvider';

export default function HomePage() {
  const auth = useAuth();

  return (
    <div className="p-8">
      <h1 className="text-3xl font-black text-slate-800 mb-4">
        Bienvenido, {auth?.username}
      </h1>

      <p className="text-slate-500 mb-8">
        Genuino CRM PRO+
      </p>

      <div className="grid grid-cols-3 gap-6">
        <Card title="Proformas LCL" />
        <Card title="Oportunidades" />
        <Card title="Parámetros" />
      </div>
    </div>
  );
}

function Card({ title }) {
  return (
    <div className="rounded-3xl bg-white p-6 shadow hover:shadow-lg cursor-pointer">
      <h3 className="text-lg font-bold text-slate-700">{title}</h3>
    </div>
  );
}