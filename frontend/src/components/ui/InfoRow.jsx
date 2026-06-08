export default function InfoRow({ icon: Icon, label, value }) {
  return (
    <div className="rounded-2xl border border-slate-100 bg-white p-4 shadow-sm">
      <div className="flex items-center gap-2 text-[10px] font-bold uppercase tracking-[0.14em] text-slate-400">
        <Icon size={12} className="text-indigo-500" />
        {label}
      </div>
      <div className="mt-1 text-sm font-semibold text-slate-800">{value || '-'}</div>
    </div>
  );
}