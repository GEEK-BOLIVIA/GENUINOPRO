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
    <div className="-mx-6 mb-5 border-b border-slate-200 bg-white px-6 pb-5">
      <div className="flex flex-col gap-3 lg:flex-row lg:items-start lg:justify-between">
        <div className="min-w-0">
          <h2 className="text-xl font-black text-slate-950">
            {title}
          </h2>

          {subtitle && (
            <p className="mt-1 truncate text-xs text-slate-400">
              {subtitle}
            </p>
          )}
        </div>

        {statusLabel && (
          <span className="w-fit shrink-0 rounded-full bg-amber-50 px-3 py-1 text-xs font-black text-amber-700">
            {statusLabel}
          </span>
        )}
      </div>

      {meta.length > 0 && (
        <div className="mt-4 flex flex-wrap items-center gap-x-6 gap-y-3">
          {meta.map((item) => (
            <div key={item.label} className="min-w-[120px] max-w-[220px]">
              <p className="text-[9px] font-black uppercase tracking-wider text-slate-400">
                {item.label}
              </p>

              <p
                className="mt-0.5 truncate text-sm font-bold text-slate-800"
                title={item.value || '-'}
              >
                {item.value || '-'}
              </p>
            </div>
          ))}
        </div>
      )}

      {tabs.length > 0 && (
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
      )}
    </div>
  );
}