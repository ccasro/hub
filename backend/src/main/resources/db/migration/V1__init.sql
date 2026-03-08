-- Base initialization
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE city (
                      id           BIGSERIAL PRIMARY KEY,
                      name         VARCHAR(100) NOT NULL,
                      country_code VARCHAR(3) NOT NULL DEFAULT 'ES',
                      latitude     DOUBLE PRECISION NOT NULL,
                      longitude    DOUBLE PRECISION NOT NULL,
                      location     geography(Point, 4326) NOT NULL
);

CREATE INDEX idx_city_country   ON city(country_code);
CREATE INDEX idx_city_location  ON city USING GIST(location);

INSERT INTO city (name, country_code, latitude, longitude, location)
VALUES
    ('Madrid',          'ES',  40.4168,  -3.7038, ST_SetSRID(ST_MakePoint(-3.7038,   40.4168), 4326)::geography),
    ('Barcelona',       'ES',  41.3851,   2.1734, ST_SetSRID(ST_MakePoint( 2.1734,   41.3851), 4326)::geography),
    ('Valencia',        'ES',  39.4699,  -0.3763, ST_SetSRID(ST_MakePoint(-0.3763,   39.4699), 4326)::geography),
    ('Sevilla',         'ES',  37.3891,  -5.9845, ST_SetSRID(ST_MakePoint(-5.9845,   37.3891), 4326)::geography),
    ('Zaragoza',        'ES',  41.6488,  -0.8891, ST_SetSRID(ST_MakePoint(-0.8891,   41.6488), 4326)::geography),
    ('Málaga',          'ES',  36.7213,  -4.4214, ST_SetSRID(ST_MakePoint(-4.4214,   36.7213), 4326)::geography),
    ('Murcia',          'ES',  37.9922,  -1.1307, ST_SetSRID(ST_MakePoint(-1.1307,   37.9922), 4326)::geography),
    ('Palma',           'ES',  39.5696,   2.6502, ST_SetSRID(ST_MakePoint( 2.6502,   39.5696), 4326)::geography),
    ('Las Palmas',      'ES',  28.1248, -15.4300, ST_SetSRID(ST_MakePoint(-15.4300,  28.1248), 4326)::geography),
    ('Bilbao',          'ES',  43.2630,  -2.9350, ST_SetSRID(ST_MakePoint(-2.9350,   43.2630), 4326)::geography),
    ('Alicante',        'ES',  38.3452,  -0.4815, ST_SetSRID(ST_MakePoint(-0.4815,   38.3452), 4326)::geography),
    ('Córdoba',         'ES',  37.8882,  -4.7794, ST_SetSRID(ST_MakePoint(-4.7794,   37.8882), 4326)::geography),
    ('Valladolid',      'ES',  41.6523,  -4.7245, ST_SetSRID(ST_MakePoint(-4.7245,   41.6523), 4326)::geography),
    ('Vigo',            'ES',  42.2328,  -8.7226, ST_SetSRID(ST_MakePoint(-8.7226,   42.2328), 4326)::geography),
    ('Gijón',           'ES',  43.5453,  -5.6615, ST_SetSRID(ST_MakePoint(-5.6615,   43.5453), 4326)::geography),
    ('Granada',         'ES',  37.1773,  -3.5986, ST_SetSRID(ST_MakePoint(-3.5986,   37.1773), 4326)::geography),
    ('Vitoria',         'ES',  42.8467,  -2.6716, ST_SetSRID(ST_MakePoint(-2.6716,   42.8467), 4326)::geography),
    ('La Coruña',       'ES',  43.3623,  -8.4115, ST_SetSRID(ST_MakePoint(-8.4115,   43.3623), 4326)::geography),
    ('Santa Cruz de Tenerife', 'ES', 28.4636, -16.2518, ST_SetSRID(ST_MakePoint(-16.2518, 28.4636), 4326)::geography),
    ('Pamplona',        'ES',  42.8125,  -1.6458, ST_SetSRID(ST_MakePoint(-1.6458,   42.8125), 4326)::geography),
    ('Almería',         'ES',  36.8381,  -2.4597, ST_SetSRID(ST_MakePoint(-2.4597,   36.8381), 4326)::geography),
    ('San Sebastián',   'ES',  43.3183,  -1.9812, ST_SetSRID(ST_MakePoint(-1.9812,   43.3183), 4326)::geography),
    ('Burgos',          'ES',  42.3440,  -3.6969, ST_SetSRID(ST_MakePoint(-3.6969,   42.3440), 4326)::geography),
    ('Santander',       'ES',  43.4623,  -3.8099, ST_SetSRID(ST_MakePoint(-3.8099,   43.4623), 4326)::geography),
    ('Castellón',       'ES',  39.9864,  -0.0513, ST_SetSRID(ST_MakePoint(-0.0513,   39.9864), 4326)::geography),
    ('Logroño',         'ES',  42.4627,  -2.4449, ST_SetSRID(ST_MakePoint(-2.4449,   42.4627), 4326)::geography),
    ('Salamanca',       'ES',  40.9701,  -5.6635, ST_SetSRID(ST_MakePoint(-5.6635,   40.9701), 4326)::geography),
    ('Huelva',          'ES',  37.2614,  -6.9447, ST_SetSRID(ST_MakePoint(-6.9447,   37.2614), 4326)::geography),
    ('Badajoz',         'ES',  38.8794,  -6.9706, ST_SetSRID(ST_MakePoint(-6.9706,   38.8794), 4326)::geography),
    ('Tarragona',       'ES',  41.1189,   1.2445, ST_SetSRID(ST_MakePoint( 1.2445,   41.1189), 4326)::geography),
    ('Lleida',          'ES',  41.6176,   0.6200, ST_SetSRID(ST_MakePoint( 0.6200,   41.6176), 4326)::geography),
    ('Girona',          'ES',  41.9794,   2.8214, ST_SetSRID(ST_MakePoint( 2.8214,   41.9794), 4326)::geography),
    ('Albacete',        'ES',  38.9942,  -1.8585, ST_SetSRID(ST_MakePoint(-1.8585,   38.9942), 4326)::geography),
    ('León',            'ES',  42.5987,  -5.5671, ST_SetSRID(ST_MakePoint(-5.5671,   42.5987), 4326)::geography),
    ('Cádiz',           'ES',  36.5271,  -6.2886, ST_SetSRID(ST_MakePoint(-6.2886,   36.5271), 4326)::geography),
    ('Oviedo',          'ES',  43.3614,  -5.8593, ST_SetSRID(ST_MakePoint(-5.8593,   43.3614), 4326)::geography),
    ('Jerez de la Frontera', 'ES', 36.6864, -6.1372, ST_SetSRID(ST_MakePoint(-6.1372, 36.6864), 4326)::geography),
    ('Badalona',        'ES',  41.4500,   2.2474, ST_SetSRID(ST_MakePoint( 2.2474,   41.4500), 4326)::geography),
    ('Terrassa',        'ES',  41.5632,   2.0089, ST_SetSRID(ST_MakePoint( 2.0089,   41.5632), 4326)::geography),
    ('Sabadell',        'ES',  41.5483,   2.1075, ST_SetSRID(ST_MakePoint( 2.1075,   41.5483), 4326)::geography);