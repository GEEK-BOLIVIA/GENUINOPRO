create table if not exists opportunity (
  id text primary key,
  customer_id text,
  lead_inbox_id text,
  title text not null,
  stage text not null,
  source text not null,
  owner_user_id text,
  notes text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create index if not exists idx_opportunity_customer_id on opportunity(customer_id);
create index if not exists idx_opportunity_lead_inbox_id on opportunity(lead_inbox_id);
create index if not exists idx_opportunity_owner_user_id on opportunity(owner_user_id);
create index if not exists idx_opportunity_stage on opportunity(stage);
