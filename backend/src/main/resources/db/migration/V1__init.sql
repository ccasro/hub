-- Base initialization
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE city (
                      id      BIGSERIAL PRIMARY KEY,
                      name    VARCHAR(100) NOT NULL,
                      country_code VARCHAR(3) NOT NULL DEFAULT 'ESP',
                      latitude  DOUBLE PRECISION NOT NULL,
                      longitude DOUBLE PRECISION NOT NULL
);

CREATE INDEX idx_city_country ON city(country_code);
INSERT INTO city (name, country_code, latitude, longitude)
VALUES
    ('Madrid',          'ESP',  40.4168,  -3.7038),
    ('Barcelona',       'ESP',  41.3851,   2.1734),
    ('Valencia',        'ESP',  39.4699,  -0.3763),
    ('Sevilla',         'ESP',  37.3891,  -5.9845),
    ('Zaragoza',        'ESP',  41.6488,  -0.8891),
    ('Málaga',          'ESP',  36.7213,  -4.4214),
    ('Murcia',          'ESP',  37.9922,  -1.1307),
    ('Palma',           'ESP',  39.5696,   2.6502),
    ('Las Palmas',      'ESP',  28.1248, -15.4300),
    ('Bilbao',          'ESP',  43.2630,  -2.9350),
    ('Alicante',        'ESP',  38.3452,  -0.4815),
    ('Córdoba',         'ESP',  37.8882,  -4.7794),
    ('Valladolid',      'ESP',  41.6523,  -4.7245),
    ('Vigo',            'ESP',  42.2328,  -8.7226),
    ('Gijón',           'ESP',  43.5453,  -5.6615),
    ('Granada',         'ESP',  37.1773,  -3.5986),
    ('Vitoria',         'ESP',  42.8467,  -2.6716),
    ('La Coruña',       'ESP',  43.3623,  -8.4115),
    ('Santa Cruz de Tenerife', 'ESP', 28.4636, -16.2518),
    ('Pamplona',        'ESP',  42.8125,  -1.6458),
    ('Almería',         'ESP',  36.8381,  -2.4597),
    ('San Sebastián',   'ESP',  43.3183,  -1.9812),
    ('Burgos',          'ESP',  42.3440,  -3.6969),
    ('Santander',       'ESP',  43.4623,  -3.8099),
    ('Castellón',       'ESP',  39.9864,  -0.0513),
    ('Logroño',         'ESP',  42.4627,  -2.4449),
    ('Salamanca',       'ESP',  40.9701,  -5.6635),
    ('Huelva',          'ESP',  37.2614,  -6.9447),
    ('Badajoz',         'ESP',  38.8794,  -6.9706),
    ('Tarragona',       'ESP',  41.1189,   1.2445),
    ('Lleida',          'ESP',  41.6176,   0.6200),
    ('Girona',          'ESP',  41.9794,   2.8214),
    ('Albacete',        'ESP',  38.9942,  -1.8585),
    ('León',            'ESP',  42.5987,  -5.5671),
    ('Cádiz',           'ESP',  36.5271,  -6.2886),
    ('Oviedo',          'ESP',  43.3614,  -5.8593),
    ('Jerez de la Frontera', 'ESP', 36.6864, -6.1372),
    ('Badalona',        'ESP',  41.4500,   2.2474),
    ('Terrassa',        'ESP',  41.5632,   2.0089),
    ('Sabadell',        'ESP',  41.5483,   2.1075);