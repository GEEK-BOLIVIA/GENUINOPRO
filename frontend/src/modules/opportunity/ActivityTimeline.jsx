import { Loader2 } from 'lucide-react';
import EmptyState from '../../components/ui/EmptyState';
import {
  formatActivityDate,
  getActivityLabel,
  getActivityIcon,
  getActivityBadgeClass,
} from '../../utils/crm';

export default function ActivityTimeline({ activities, isLoadingTimeline }) {
  return (
    <div className="rounded-[26px] border border-slate-200 bg-slate-50/80 p-3 shadow-inner">
      {isLoadingTimeline ? (
        <div className="flex items-center justify-center py-10 text-sm text-slate-400">
          <Loader2 size={18} className="mr-2 animate-spin" /> Cargando historial...
        </div>
      ) : activities.length ? (
        <div className="max-h-[320px] overflow-y-auto pr-2">
          <div className="relative pl-4">
            <div className="absolute bottom-0 left-[19px] top-0 w-px bg-gradient-to-b from-slate-200 via-slate-300 to-slate-200" />
            <div className="space-y-3">
              {activities.map((activity, index) => {
                const Icon = getActivityIcon(activity.type);
                return (
                  <div key={activity.id || index} className="relative flex gap-3">
                    <div className="relative z-10 mt-1 flex h-7 w-7 shrink-0 items-center justify-center rounded-full border border-white bg-white text-sm shadow-sm ring-1 ring-slate-200">
                      <Icon size={14} className="text-slate-600" />
                    </div>

                    <div className="min-w-0 flex-1 rounded-2xl border border-slate-200 bg-white px-4 py-3 shadow-sm transition hover:shadow-md">
                      <div className="mb-2 flex flex-wrap items-center gap-2">
                        <span
                          className={`inline-flex items-center rounded-full px-2.5 py-1 text-[11px] font-semibold uppercase tracking-[0.12em] ${getActivityBadgeClass(
                            activity.type,
                            activity.source
                          )}`}
                        >
                          {getActivityLabel(activity.type)}
                        </span>

                        {activity.source ? (
                          <span className="inline-flex items-center rounded-full bg-slate-100 px-2.5 py-1 text-[11px] font-medium text-slate-500">
                            {activity.source === 'MANUAL' ? 'Manual' : 'Sistema'}
                          </span>
                        ) : null}
                      </div>

                      <div className="text-[15px] font-semibold text-slate-900">
                        {activity.title || activity.text || 'Actividad'}
                      </div>

                      {activity.description ? (
                        <div className="mt-1 text-sm leading-relaxed text-slate-600">
                          {activity.description}
                        </div>
                      ) : null}

                      <div className="mt-3 flex flex-wrap items-center gap-x-3 gap-y-1 text-xs text-slate-400">
                        <span>{formatActivityDate(activity.date)}</span>
                        {activity.createdBy ? <span>• {activity.createdBy}</span> : null}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      ) : (
        <EmptyState text="Este lead aún no tiene actividades registradas." />
      )}
    </div>
  );
}