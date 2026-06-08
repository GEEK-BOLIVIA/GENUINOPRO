import { useEffect, useState } from 'react';
import { apiFetch } from '../../services/api';

export default function OpportunityTimeline({ opportunityId }) {
  const [data, setData] = useState(null);

  useEffect(() => {
    if (!opportunityId) return;

    apiFetch(`/opportunities/${opportunityId}/timeline`)
      .then(setData)
      .catch(console.error);
  }, [opportunityId]);

  if (!data) return null;

  return (
    <div className="mt-8 rounded-3xl bg-white p-6 shadow">
      <h2 className="mb-4 text-xl font-black">Timeline</h2>

      <div className="space-y-3 text-sm">
        {data.events?.map((event, index) => (
          <div key={index} className="rounded-xl border border-slate-200 p-3">
            <div className="font-bold text-indigo-600">{event.type}</div>
            <div className="text-xs text-slate-400">{event.timestamp}</div>

            {event.proformaId && (
              <div className="text-xs">Proforma: {event.proformaId}</div>
            )}

            {event.actorUserId && (
              <div className="text-xs">Usuario: {event.actorUserId}</div>
            )}

            {event.reason && (
              <div className="text-xs text-rose-500">Motivo: {event.reason}</div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}