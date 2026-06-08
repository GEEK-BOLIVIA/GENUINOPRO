import { getLeads } from './leadsService';

export async function getDashboardStats() {
  const leads = await getLeads();

  const totalLeads = leads.length;
  const newLeads = leads.filter((lead) => lead.status === 'NEW').length;
  const contacted = leads.filter((lead) => lead.status === 'CONTACTED').length;
  const quoted = leads.filter((lead) => lead.status === 'QUOTED').length;
  const negotiation = leads.filter((lead) => lead.status === 'NEGOTIATION').length;
  const won = leads.filter((lead) => lead.status === 'WON').length;

  const conversionRate =
    totalLeads > 0 ? Math.round((won / totalLeads) * 100) : 0;

  return {
    totalLeads,
    newLeads,
    contacted,
    quoted,
    negotiation,
    won,
    conversionRate,
  };
}