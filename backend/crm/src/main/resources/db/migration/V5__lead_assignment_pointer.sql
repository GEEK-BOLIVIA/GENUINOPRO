create table if not exists lead_assignment_pointer (
  id text primary key,
  last_assigned_seller_id text,
  updated_at timestamptz not null default now()
);

insert into lead_assignment_pointer (id, last_assigned_seller_id, updated_at)
values ('WHAPIFY_DEFAULT', null, now())
on conflict (id) do nothing;