export default function EnterprisePageHeader({
  title,
  subtitle,
  statusLabel,
  tabs = [],
  activeTab,
  onTabChange,
  meta = [],
}) {
  return (
    <div className="sticky top-0 z-40 -mx-6 mb-6 border-b border-slate-200 bg-white/95 px-6 py-4 backdrop-blur">
      <div className="flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
        <div>
          <h2 className="text-xl font-black text-slate-950">
            {title}
          </h2>

          {subtitle && (
            <p className="mt-1 text-xs text-slate-400">
              {subtitle}
            </p>
          )}
        </div>

        {statusLabel && (
          <span className="w-fit rounded-full bg-amber-50 px-3 py-1 text-xs font-black text-amber-700">
            {statusLabel}
          </span>
        )}
      </div>

      {meta.length > 0 && (
        <div className="mt-4 grid gap-3 text-sm md:grid-cols-3 xl:grid-cols-6">
          {meta.map((item) => (
            <div key={item.label} className="rounded-2xl bg-slate-50 p-3">
              <p className="text-[10px] font-black uppercase tracking-wide text-slate-400">
                {item.label}
              </p>
              <p className="mt-1 truncate font-bold text-slate-800">
                {item.value || '-'}
              </p>
            </div>
          ))}
        </div>
      )}

      <div className="mt-4 flex flex-wrap gap-2">
        {tabs.map((tab) => (
          <button
            key={tab}
            type="button"
            onClick={() => onTabChange?.(tab)}
            className={`rounded-xl px-4 py-2 text-sm font-bold ${
              activeTab === tab
                ? 'bg-orange-500 text-white'
                : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
            }`}
          >
            {tab}
          </button>
        ))}
      </div>
    </div>
  );
}