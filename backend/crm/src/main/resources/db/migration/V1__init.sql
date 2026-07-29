-- AUDIT (append-only)
create table if not exists audit_event (
  audit_id uuid primary key,
  ts timestamptz not null,
  actor_user_id text,
  actor_role text,
  ip text,
  user_agent text,
  trace_id text,
  request_id text,
  action text not null,
  entity_type text,
  entity_id text,
  before_json jsonb,
  after_json jsonb,
  reason text,
  result text not null,
  error_code text
);

create index if not exists idx_audit_ts on audit_event(ts);
create index if not exists idx_audit_entity on audit_event(entity_type, entity_id);
create index if not exists idx_audit_actor on audit_event(actor_user_id);

-- CRM: Customer (mínimo)
create table if not exists customer (
  id text primary key,
  name text not null,
  tax_id text,
  email text,
  phone text,
  address text,
  owner_user_id text,
  status text not null default 'ACTIVE',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create index if not exists idx_customer_owner on customer(owner_user_id);

-- QUOTING: Proforma core
create table if not exists proforma (
  id text primary key,
  customer_id text not null references customer(id),
  status text not null,
  currency text not null,
  subtotal numeric(14,2) not null,
  discount numeric(14,2) not null default 0,
  total numeric(14,2) not null,
  series text not null default 'A',
  year int not null,
  number int,
  created_by text not null,
  pdf_s3_key text,
  version bigint not null default 0,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

-- Único cuando number no es null (Postgres permite índices parciales)
create unique index if not exists uq_proforma_number
  on proforma(year, series, number)
  where number is not null;

create table if not exists proforma_item (
  id text primary key,
  proforma_id text not null references proforma(id) on delete cascade,
  description text not null,
  qty numeric(14,2) not null,
  unit_price numeric(14,2) not null,
  line_total numeric(14,2) not null
);

create table if not exists proforma_sequence (
  year int not null,
  series text not null,
  last_value int not null,
  version bigint not null default 0,
  primary key (year, series)
);

create table if not exists idempotency_key (
  key text primary key,
  endpoint text not null,
  request_hash text not null,
  response_json jsonb not null,
  created_at timestamptz not null default now()
);