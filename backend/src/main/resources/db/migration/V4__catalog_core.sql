
-- =========================
-- VENUES
-- =========================
create table venues (
  id uuid primary key,

  owner_user_id uuid not null
    references users(id)
    on delete restrict,

  name varchar(80) not null,
  description text null,

  primary_image_public_id text null,
  primary_image_url text null,

  street varchar(150) null,
  city varchar(100) null,
  postal_code varchar(20) null,
  country varchar(100) null,

  latitude numeric(10,8) null,
  longitude numeric(11,8) null,

  status varchar(20) not null default 'DRAFT',

  created_at timestamptz not null,
  updated_at timestamptz not null
);

create index idx_venue_owner_id on venues(id, owner_user_id);
create index idx_venues_city on venues(city);

-- =========================
-- VENUE IMAGES
-- =========================
create table venue_images (
  id uuid primary key,

  venue_id uuid not null
    references venues(id)
    on delete cascade,

  public_id text not null,
  url text not null,

  position int not null,
  is_primary boolean not null default false,

  created_at timestamptz not null,

  constraint uq_venue_images_public unique (venue_id, public_id),
  constraint uq_venue_images_position unique (venue_id, position)
);

create index idx_venue_images_venue_id on venue_images(venue_id);
create index idx_venue_images_venue_id_position on venue_images(venue_id, position);

create unique index uq_venue_images_one_primary
on venue_images(venue_id)
where is_primary = true;

-- =========================
-- RESOURCES
-- =========================
create table resources (
  id uuid primary key,

  venue_id uuid not null
    references venues(id)
    on delete cascade,

  name varchar(60) not null,
  description text null,

  base_price_amount numeric(19,4) not null,
  base_price_currency char(3) not null
    check (base_price_currency ~ '^[A-Z]{3}$'),

  primary_image_public_id text null,
  primary_image_url text null,

  status varchar(20) not null default 'DRAFT',

  created_at timestamptz not null,
  updated_at timestamptz not null
);

create index idx_resources_venue_id on resources(venue_id);
create index idx_resources_venue_id_name on resources(venue_id, name);

-- =========================
-- RESOURCE IMAGES
-- =========================
create table resource_images (
  id uuid primary key,

  resource_id uuid not null
    references resources(id)
    on delete cascade,

  public_id text not null,
  url text not null,

  position int not null,
  is_primary boolean not null default false,

  created_at timestamptz not null,

  constraint uq_resource_images_public unique (resource_id, public_id),
  constraint uq_resource_images_position unique (resource_id, position)
);

create index idx_resource_images_resource_id on resource_images(resource_id);
create index idx_resource_images_resource_id_position on resource_images(resource_id, position);

create unique index uq_resource_images_one_primary
on resource_images(resource_id)
where is_primary = true;