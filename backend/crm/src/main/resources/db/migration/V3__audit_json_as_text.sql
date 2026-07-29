alter table audit_event
  alter column before_json type text using before_json::text,
  alter column after_json type text using after_json::text;