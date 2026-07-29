create table if not exists lead_inbox (
  id text primary key,
  source text not null,
  external_conversation_id text,
  external_contact_id text,
  phone text not null,
  full_name text,
  message_preview text,
  channel text,
  assigned_seller_id text,
  assignment_rule text,
  status text not null,
  payload_json text,
  received_at timestamptz not null,
  created_at timestamptz not null default now()
);

create index if not exists idx_lead_inbox_phone on lead_inbox(phone);
create index if not exists idx_lead_inbox_assigned_seller on lead_inbox(assigned_seller_id);
create index if not exists idx_lead_inbox_received_at on lead_inbox(received_at);