import { useEffect, useMemo, useState } from 'react';
import {
  MessageCircle,
  Phone,
  Search,
  Send,
  UserCircle,
} from 'lucide-react';

import { getLeads } from '../services/leadsService';

import {
  createLeadActivity,
  getLeadActivities,
} from '../services/leadActivitiesService';

export default function InboxPage() {
  const [leads, setLeads] = useState([]);
  const [selectedLead, setSelectedLead] = useState(null);
  const [replyText, setReplyText] = useState('');
  const [activities, setActivities] = useState([]);

  useEffect(() => {
    async function loadLeads() {
      const data = await getLeads();
      const safeData = Array.isArray(data) ? data : [];

      setLeads(safeData);
      setSelectedLead(safeData[0] || null);
    }

    loadLeads();
  }, []);

  useEffect(() => {
    if (!selectedLead?.id) {
      setActivities([]);
      return;
    }

    async function loadActivities() {
      const data = await getLeadActivities(selectedLead.id);
      setActivities(Array.isArray(data) ? data : []);
    }

    loadActivities();
  }, [selectedLead?.id]);

  const selectedTitle = useMemo(() => {
    if (!selectedLead) return '';

    return (
      selectedLead.company ||
      selectedLead.contact ||
      selectedLead.phone ||
      'Conversación sin nombre'
    );
  }, [selectedLead]);

  async function handleSend() {
    if (!replyText.trim() || !selectedLead?.id) return;

    const created = await createLeadActivity(selectedLead.id, {
      type: 'INBOX_NOTE',
      description: replyText.trim(),
      createdBy: 'admin',
    });

    setActivities((prev) => [created, ...prev]);
    setReplyText('');
  }

  function handleKeyDown(event) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      handleSend();
    }
  }

  return (
    <div className="space-y-6">
      <section>
        <p className="text-sm font-medium text-slate-500">
          Comunicación comercial
        </p>

        <h1 className="mt-2 text-3xl font-black tracking-tight text-slate-900">
          Inbox
        </h1>

        <p className="mt-2 max-w-3xl text-sm leading-6 text-slate-500">
          Bandeja de conversaciones y notas comerciales vinculadas a contactos.
          Desde aquí se registra seguimiento, no se administra el directorio.
        </p>
      </section>

      <section className="grid min-h-[680px] grid-cols-1 gap-6 xl:grid-cols-[380px_1fr]">
        <aside className="rounded-3xl border border-slate-200 bg-white shadow-sm">
          <div className="border-b border-slate-100 p-5">
            <div className="flex items-center gap-3 rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 opacity-70">
              <Search size={18} className="text-slate-400" />

              <input
                disabled
                placeholder="Buscar conversación próximamente..."
                className="w-full bg-transparent text-sm outline-none placeholder:text-slate-400"
              />
            </div>
          </div>

          <div className="max-h-[590px] space-y-3 overflow-y-auto p-4">
            {leads.map((lead) => (
              <InboxItem
                key={lead.id}
                lead={lead}
                active={selectedLead?.id === lead.id}
                onClick={() => setSelectedLead(lead)}
              />
            ))}

            {leads.length === 0 && (
              <div className="rounded-2xl border border-dashed border-slate-200 p-8 text-center">
                <MessageCircle className="mx-auto text-slate-300" size={34} />

                <p className="mt-3 text-sm font-bold text-slate-600">
                  Sin conversaciones
                </p>

                <p className="mt-1 text-sm text-slate-400">
                  Cuando ingresen leads o mensajes, aparecerán aquí.
                </p>
              </div>
            )}
          </div>
        </aside>

        <main className="rounded-3xl border border-slate-200 bg-white shadow-sm">
          {selectedLead ? (
            <div className="flex h-full min-h-[680px] flex-col">
              <header className="flex items-center justify-between gap-4 border-b border-slate-100 p-6">
                <div className="flex items-center gap-4">
                  <div className="rounded-2xl bg-orange-50 p-3 text-orange-600">
                    <UserCircle size={28} />
                  </div>

                  <div>
                    <h2 className="text-xl font-black text-slate-900">
                      {selectedTitle}
                    </h2>

                    <p className="mt-1 text-sm text-slate-500">
                      {selectedLead.phone || 'Sin teléfono'} ·{' '}
                      {selectedLead.channel || selectedLead.source || 'Sin canal'}
                    </p>
                  </div>
                </div>

                <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-bold text-slate-500">
                  Nota comercial
                </span>
              </header>

              <div className="flex-1 space-y-4 overflow-y-auto bg-slate-50/60 p-6">
                {selectedLead.messagePreview && (
                  <div className="max-w-xl rounded-3xl bg-white p-5 text-sm leading-6 text-slate-700 shadow-sm">
                    <p>{selectedLead.messagePreview}</p>

                    <p className="mt-3 text-xs font-bold uppercase tracking-wide text-slate-400">
                      Mensaje inicial
                    </p>
                  </div>
                )}

                {activities.map((activity) => (
                  <div
                    key={activity.id}
                    className="ml-auto max-w-xl rounded-3xl bg-orange-50 p-5 text-sm leading-6 text-slate-700 shadow-sm"
                  >
                    <p>{activity.description}</p>

                    <p className="mt-3 text-xs font-bold uppercase tracking-wide text-orange-600">
                      {activity.type || 'INBOX_NOTE'}
                    </p>
                  </div>
                ))}

                {!selectedLead.messagePreview && activities.length === 0 && (
                  <div className="flex h-full items-center justify-center text-center">
                    <div>
                      <MessageCircle
                        className="mx-auto text-slate-300"
                        size={42}
                      />

                      <h3 className="mt-4 text-lg font-black text-slate-800">
                        Sin historial todavía
                      </h3>

                      <p className="mt-2 max-w-md text-sm text-slate-500">
                        Registra una nota comercial para dejar trazabilidad del
                        seguimiento.
                      </p>
                    </div>
                  </div>
                )}
              </div>

              <footer className="border-t border-slate-100 p-5">
                <div className="flex gap-3">
                  <textarea
                    value={replyText}
                    onChange={(event) => setReplyText(event.target.value)}
                    onKeyDown={handleKeyDown}
                    rows={2}
                    placeholder="Registrar nota de seguimiento..."
                    className="flex-1 resize-none rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-orange-300"
                  />

                  <button
                    type="button"
                    onClick={handleSend}
                    disabled={!replyText.trim()}
                    className="flex items-center gap-2 rounded-2xl bg-orange-500 px-6 py-3 text-sm font-bold text-white hover:bg-orange-600 disabled:cursor-not-allowed disabled:bg-slate-300"
                  >
                    <Send size={16} />
                    Registrar
                  </button>
                </div>
              </footer>
            </div>
          ) : (
            <div className="flex min-h-[680px] items-center justify-center text-center">
              <div>
                <MessageCircle className="mx-auto text-slate-300" size={48} />

                <h2 className="mt-4 text-2xl font-black text-slate-900">
                  Selecciona una conversación
                </h2>

                <p className="mt-3 text-sm text-slate-500">
                  Aquí aparecerá el historial comercial del contacto.
                </p>
              </div>
            </div>
          )}
        </main>
      </section>
    </div>
  );
}

function InboxItem({ lead, active = false, onClick }) {
  const title =
    lead.company || lead.contact || lead.phone || 'Conversación sin nombre';

  return (
    <button
      onClick={onClick}
      className={`w-full rounded-2xl border p-4 text-left transition ${
        active
          ? 'border-orange-200 bg-orange-50'
          : 'border-slate-200 hover:bg-slate-50'
      }`}
    >
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <h3 className="truncate text-sm font-black text-slate-900">
            {title}
          </h3>

          <p className="mt-1 flex items-center gap-1 text-xs text-slate-400">
            <Phone size={12} />
            {lead.phone || 'Sin teléfono'}
          </p>
        </div>

        <span className="rounded-full bg-white px-2 py-1 text-[10px] font-bold uppercase text-slate-400">
          {lead.channel || lead.source || 'CRM'}
        </span>
      </div>

      <p className="mt-3 line-clamp-2 text-sm leading-5 text-slate-500">
        {lead.messagePreview || 'Sin mensaje inicial'}
      </p>
    </button>
  );
}