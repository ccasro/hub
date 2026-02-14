create table users (
  id uuid primary key,
  auth0_sub varchar(255) not null unique,
  email varchar(255),
  display_name varchar(255),
  avatar_url text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);