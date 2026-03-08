
-- 2) USER_PROFILE (10)
-- auth0_id: NOT NULL UNIQUE
-- preferred_sport: PADEL/TENNIS/SQUASH (según CHECK)
-- =========================
INSERT INTO user_profile (
    id,
    auth0_id,
    email,
    email_verified,
    display_name,
    description,
    phone_number,
    avatar_url,
    avatar_public_id,
    role,
    owner_request_status,
    preferred_sport,
    skill_level,
    city,
    country_code,
    active,
    onboarding_completed,
    match_notifications_enabled,
    match_search_radius_km,
    city_id,
    created_at,
    updated_at,
    last_login_at
)
VALUES

-- ADMIN
(
    '00000000-0000-0000-0000-000000000001',
    'auth0|admin0001',
    'admin@padelhub.dev',
    true,
    'Admin PadelHub',
    'System administrator',
    '+34910000001',
    'https://res.cloudinary.com/demo/image/upload/avatars/admin.jpg',
    'avatars/admin',
    'ADMIN',
    NULL,
    'PADEL',
    'ADVANCED',
    'Madrid',
    'ES',
    true,
    true,
    false,
    10,
    1,
    now() - interval '90 days',
    now() - interval '1 day',
    now() - interval '1 day'
),

-- OWNER 1 (Google login)
(
    '00000000-0000-0000-0000-000000000002',
    'google-oauth2|117245030007219025441',
    'owner1@gmail.com',
    true,
    'Carlos Martínez',
    'Club owner in Madrid',
    '+34910000002',
    'https://res.cloudinary.com/demo/image/upload/avatars/owner1.jpg',
    'avatars/owner1',
    'OWNER',
    'APPROVED',
    'PADEL',
    'INTERMEDIATE',
    'Madrid',
    'ES',
    true,
    true,
    true,
    12,
    1,
    now() - interval '60 days',
    now() - interval '2 days',
    now() - interval '2 days'
),

-- OWNER 2 (Auth0 DB login)
(
    '00000000-0000-0000-0000-000000000003',
    'auth0|owner0002',
    'owner2@padelhub.dev',
    true,
    'Laura Gómez',
    'Barcelona venue manager',
    '+34910000003',
    'https://res.cloudinary.com/demo/image/upload/avatars/owner2.jpg',
    'avatars/owner2',
    'OWNER',
    'APPROVED',
    'TENNIS',
    'INTERMEDIATE',
    'Barcelona',
    'ES',
    true,
    true,
    true,
    10,
    2,
    now() - interval '55 days',
    now() - interval '3 days',
    now() - interval '3 days'
),

-- OWNER REQUEST PENDING
(
    '00000000-0000-0000-0000-000000000004',
    'google-oauth2|998877665544332211',
    'futureowner@gmail.com',
    true,
    'Miguel Ruiz',
    'Requested owner role',
    '+34910000004',
    'https://res.cloudinary.com/demo/image/upload/avatars/owner3.jpg',
    'avatars/owner3',
    'PLAYER',
    'PENDING',
    'PADEL',
    'BEGINNER',
    'Valencia',
    'ES',
    true,
    false,
    false,
    10,
    3,
    now() - interval '20 days',
    now() - interval '5 days',
    now() - interval '5 days'
),

-- PLAYERS
(
    '00000000-0000-0000-0000-000000000005',
    'google-oauth2|111111111111111111111',
    'player1@gmail.com',
    true,
    'Sergio López',
    'Beginner padel player',
    '+34910000005',
    'https://res.cloudinary.com/demo/image/upload/avatars/player1.jpg',
    'avatars/player1',
    'PLAYER',
    NULL,
    'PADEL',
    'BEGINNER',
    'Madrid',
    'ES',
    true,
    true,
    true,
    8,
    1,
    now() - interval '30 days',
    now() - interval '1 hour',
    now() - interval '1 hour'
),

(
    '00000000-0000-0000-0000-000000000006',
    'google-oauth2|222222222222222222222',
    'player2@gmail.com',
    true,
    'Ana Torres',
    'Weekend matches',
    '+34910000006',
    'https://res.cloudinary.com/demo/image/upload/avatars/player2.jpg',
    'avatars/player2',
    'PLAYER',
    NULL,
    'PADEL',
    'INTERMEDIATE',
    'Barcelona',
    'ES',
    true,
    true,
    true,
    10,
    2,
    now() - interval '25 days',
    now() - interval '6 hours',
    now() - interval '6 hours'
),

(
    '00000000-0000-0000-0000-000000000007',
    'auth0|player0003',
    'player3@padelhub.dev',
    true,
    'David Fernández',
    'Looking for competitive matches',
    '+34910000007',
    'https://res.cloudinary.com/demo/image/upload/avatars/player3.jpg',
    'avatars/player3',
    'PLAYER',
    NULL,
    'SQUASH',
    'INTERMEDIATE',
    'Sevilla',
    'ES',
    true,
    true,
    false,
    10,
    4,
    now() - interval '80 days',
    now() - interval '2 days',
    now() - interval '2 days'
),

(
    '00000000-0000-0000-0000-000000000008',
    'google-oauth2|333333333333333333333',
    'player4@gmail.com',
    true,
    'Lucía Martín',
    'Advanced player',
    '+34910000008',
    'https://res.cloudinary.com/demo/image/upload/avatars/player4.jpg',
    'avatars/player4',
    'PLAYER',
    NULL,
    'PADEL',
    'ADVANCED',
    'Bilbao',
    'ES',
    true,
    true,
    true,
    15,
    5,
    now() - interval '15 days',
    now() - interval '12 hours',
    now() - interval '12 hours'
),

(
    '00000000-0000-0000-0000-000000000009',
    'google-oauth2|444444444444444444444',
    'player5@gmail.com',
    true,
    'Marcos Díaz',
    'Casual games',
    '+34910000009',
    'https://res.cloudinary.com/demo/image/upload/avatars/player5.jpg',
    'avatars/player5',
    'PLAYER',
    NULL,
    'TENNIS',
    'BEGINNER',
    'Málaga',
    'ES',
    true,
    false,
    false,
    10,
    6,
    now() - interval '10 days',
    now() - interval '10 days',
    now() - interval '10 days'
),

(
    '00000000-0000-0000-0000-000000000010',
    'auth0|player0006',
    'player6@padelhub.dev',
    true,
    'Javier Romero',
    'Morning slots',
    '+34910000010',
    'https://res.cloudinary.com/demo/image/upload/avatars/player6.jpg',
    'avatars/player6',
    'PLAYER',
    NULL,
    'PADEL',
    'INTERMEDIATE',
    'Valencia',
    'ES',
    true,
    true,
    true,
    9,
    3,
    now() - interval '40 days',
    now() - interval '30 minutes',
    now() - interval '30 minutes'
),
-- BARCELONA
(
    '00000000-0000-0000-0000-000000000011',
    'google-oauth2|555555555555555555551',
    'barcelona1@gmail.com',
    true,
    'Pol Navarro',
    'Evening padel matches',
    '+34910000011',
    'https://res.cloudinary.com/demo/image/upload/avatars/player7.jpg',
    'avatars/player7',
    'PLAYER',
    NULL,
    'PADEL',
    'INTERMEDIATE',
    'Barcelona',
    'ES',
    true,
    true,
    true,
    12,
    2,
    now() - interval '12 days',
    now() - interval '3 hours',
    now() - interval '3 hours'
),

(
    '00000000-0000-0000-0000-000000000012',
    'auth0|player0012',
    'barcelona2@padelhub.dev',
    true,
    'Claudia Ríos',
    'Competitive tournaments',
    '+34910000012',
    'https://res.cloudinary.com/demo/image/upload/avatars/player8.jpg',
    'avatars/player8',
    'PLAYER',
    NULL,
    'PADEL',
    'ADVANCED',
    'Barcelona',
    'ES',
    true,
    true,
    true,
    15,
    2,
    now() - interval '18 days',
    now() - interval '5 hours',
    now() - interval '5 hours'
),

-- BADALONA
(
    '00000000-0000-0000-0000-000000000013',
    'google-oauth2|555555555555555555552',
    'badalona1@gmail.com',
    true,
    'Marc Vila',
    'Weekend games',
    '+34910000013',
    'https://res.cloudinary.com/demo/image/upload/avatars/player9.jpg',
    'avatars/player9',
    'PLAYER',
    NULL,
    'PADEL',
    'BEGINNER',
    'Badalona',
    'ES',
    true,
    true,
    true,
    8,
    38,
    now() - interval '9 days',
    now() - interval '1 day',
    now() - interval '1 day'
),

-- TERRASSA
(
    '00000000-0000-0000-0000-000000000014',
    'auth0|player0014',
    'terrassa1@padelhub.dev',
    true,
    'Núria Soler',
    'Looking for mixed matches',
    '+34910000014',
    'https://res.cloudinary.com/demo/image/upload/avatars/player10.jpg',
    'avatars/player10',
    'PLAYER',
    NULL,
    'PADEL',
    'INTERMEDIATE',
    'Terrassa',
    'ES',
    true,
    true,
    true,
    10,
    39,
    now() - interval '7 days',
    now() - interval '2 hours',
    now() - interval '2 hours'
),

-- SABADELL
(
    '00000000-0000-0000-0000-000000000015',
    'google-oauth2|555555555555555555553',
    'sabadell1@gmail.com',
    true,
    'Adrià Costa',
    'Morning training sessions',
    '+34910000015',
    'https://res.cloudinary.com/demo/image/upload/avatars/player11.jpg',
    'avatars/player11',
    'PLAYER',
    NULL,
    'TENNIS',
    'INTERMEDIATE',
    'Sabadell',
    'ES',
    true,
    true,
    false,
    10,
    40,
    now() - interval '14 days',
    now() - interval '6 hours',
    now() - interval '6 hours'
),

-- GIRONA
(
    '00000000-0000-0000-0000-000000000016',
    'auth0|player0016',
    'girona1@padelhub.dev',
    true,
    'Helena Puig',
    'Competitive spirit',
    '+34910000016',
    'https://res.cloudinary.com/demo/image/upload/avatars/player12.jpg',
    'avatars/player12',
    'PLAYER',
    NULL,
    'PADEL',
    'ADVANCED',
    'Girona',
    'ES',
    true,
    true,
    true,
    20,
    32,
    now() - interval '5 days',
    now() - interval '4 hours',
    now() - interval '4 hours'
),

-- TARRAGONA
(
    '00000000-0000-0000-0000-000000000017',
    'google-oauth2|555555555555555555554',
    'tarragona1@gmail.com',
    true,
    'Joan Ferrer',
    'Casual padel games',
    '+34910000017',
    'https://res.cloudinary.com/demo/image/upload/avatars/player13.jpg',
    'avatars/player13',
    'PLAYER',
    NULL,
    'PADEL',
    'BEGINNER',
    'Tarragona',
    'ES',
    true,
    true,
    true,
    12,
    30,
    now() - interval '11 days',
    now() - interval '8 hours',
    now() - interval '8 hours'
),

-- LLEIDA
(
    '00000000-0000-0000-0000-000000000018',
    'auth0|player0018',
    'lleida1@padelhub.dev',
    true,
    'Oriol Pons',
    'Evening competitions',
    '+34910000018',
    'https://res.cloudinary.com/demo/image/upload/avatars/player14.jpg',
    'avatars/player14',
    'PLAYER',
    NULL,
    'PADEL',
    'INTERMEDIATE',
    'Lleida',
    'ES',
    true,
    true,
    true,
    18,
    31,
    now() - interval '16 days',
    now() - interval '9 hours',
    now() - interval '9 hours'
),

-- BARCELONA
(
    '00000000-0000-0000-0000-000000000019',
    'google-oauth2|555555555555555555555',
    'barcelona3@gmail.com',
    true,
    'Marta Iglesias',
    'Looking for advanced matches',
    '+34910000019',
    'https://res.cloudinary.com/demo/image/upload/avatars/player15.jpg',
    'avatars/player15',
    'PLAYER',
    NULL,
    'PADEL',
    'ADVANCED',
    'Barcelona',
    'ES',
    true,
    true,
    true,
    15,
    2,
    now() - interval '6 days',
    now() - interval '1 hour',
    now() - interval '1 hour'
),

-- BADALONA
(
    '00000000-0000-0000-0000-000000000020',
    'auth0|player0020',
    'badalona2@padelhub.dev',
    true,
    'Álex Moreno',
    'Flexible schedule',
    '+34910000020',
    'https://res.cloudinary.com/demo/image/upload/avatars/player16.jpg',
    'avatars/player16',
    'PLAYER',
    NULL,
    'SQUASH',
    'INTERMEDIATE',
    'Badalona',
    'ES',
    true,
    true,
    true,
    10,
    38,
    now() - interval '8 days',
    now() - interval '30 minutes',
    now() - interval '30 minutes'
)
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- 3) VENUE (10)
-- location: geography(Point,4326)
-- status default PENDING_REVIEW; usamos ACTIVE/PENDING_REVIEW/SUSPENDED/REJECTED (strings)
-- =========================
INSERT INTO venue (
    id, owner_id, name, description, street, city, country, postal_code,
    location, status, reject_reason, created_at, updated_at
)
VALUES
    ('10000000-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000002','Padel Madrid Center','Indoor club with 6 courts','Calle Atocha 123','Madrid','ES','28012',
     ST_SetSRID(ST_MakePoint(-3.6942, 40.4098), 4326)::geography,'ACTIVE',NULL, now()-interval '50 days', now()-interval '1 day'),

    ('10000000-0000-0000-0000-000000000002','00000000-0000-0000-0000-000000000002','Retiro Padel Park','Outdoor courts near Retiro','Av. Menéndez Pelayo 10','Madrid','ES','28009',
     ST_SetSRID(ST_MakePoint(-3.6834, 40.4155), 4326)::geography,'ACTIVE',NULL, now()-interval '45 days', now()-interval '2 days'),

    ('10000000-0000-0000-0000-000000000003','00000000-0000-0000-0000-000000000002','BCN Padel Hub','Premium padel experience','Carrer de Mallorca 401','Barcelona','ES','08013',
     ST_SetSRID(ST_MakePoint(2.1744, 41.4036), 4326)::geography,'ACTIVE',NULL, now()-interval '40 days', now()-interval '3 days'),

    ('10000000-0000-0000-0000-000000000004','00000000-0000-0000-0000-000000000002','Sants Sports Club','Multi-sport club','Carrer de Sants 55','Barcelona','ES','08014',
     ST_SetSRID(ST_MakePoint(2.1360, 41.3763), 4326)::geography,'ACTIVE',NULL, now()-interval '35 days', now()-interval '4 days'),

    ('10000000-0000-0000-0000-000000000005','00000000-0000-0000-0000-000000000002','Chamartín Courts','3 courts, good lighting','C/ Príncipe de Vergara 250','Madrid','ES','28016',
     ST_SetSRID(ST_MakePoint(-3.6795, 40.4540), 4326)::geography,'ACTIVE',NULL, now()-interval '30 days', now()-interval '1 day'),

    ('10000000-0000-0000-0000-000000000006','00000000-0000-0000-0000-000000000002','Gràcia Padel','Neighborhood club','C/ Gran de Gràcia 15','Barcelona','ES','08012',
     ST_SetSRID(ST_MakePoint(2.1565, 41.4011), 4326)::geography,'SUSPENDED','Noise complaints', now()-interval '25 days', now()-interval '5 days'),

    ('10000000-0000-0000-0000-000000000007','00000000-0000-0000-0000-000000000002','Madrid Norte Arena','Competitive courts','Paseo de la Castellana 200','Madrid','ES','28046',
     ST_SetSRID(ST_MakePoint(-3.6890, 40.4607), 4326)::geography,'ACTIVE',NULL, now()-interval '20 days', now()-interval '2 days'),

    ('10000000-0000-0000-0000-000000000008','00000000-0000-0000-0000-000000000002','Poble Nou Padel','Close to the beach','C/ Pallars 100','Barcelona','ES','08018',
     ST_SetSRID(ST_MakePoint(2.2040, 41.3995), 4326)::geography,'ACTIVE',NULL, now()-interval '18 days', now()-interval '1 day'),

    ('10000000-0000-0000-0000-000000000009','00000000-0000-0000-0000-000000000002','Valencia Smash','New venue pending review','C/ Xàtiva 20','Valencia','ES','46002',
     ST_SetSRID(ST_MakePoint(-0.3773, 39.4692), 4326)::geography,'PENDING_REVIEW',NULL, now()-interval '10 days', now()-interval '1 day'),

    ('10000000-0000-0000-0000-000000000010','00000000-0000-0000-0000-000000000002','Valencia Green Courts','Awaiting approval','Av. del Cid 5','Valencia','ES','46018',
     ST_SetSRID(ST_MakePoint(-0.3990, 39.4750), 4326)::geography,'PENDING_REVIEW',NULL, now()-interval '8 days', now()-interval '1 day'),
    -- BARCELONA (5)
    ('10000000-0000-0000-0000-000000000011','00000000-0000-0000-0000-000000000002','Diagonal Padel Club','Modern indoor courts','Av. Diagonal 500','Barcelona','ES','08006',
     ST_SetSRID(ST_MakePoint(2.1500,41.3920),4326)::geography,'ACTIVE',NULL,now()-interval '20 days',now()-interval '1 day'),

    ('10000000-0000-0000-0000-000000000012','00000000-0000-0000-0000-000000000002','Eixample Courts','Central location','C/ Aragó 200','Barcelona','ES','08011',
     ST_SetSRID(ST_MakePoint(2.1600,41.3890),4326)::geography,'ACTIVE',NULL,now()-interval '18 days',now()-interval '1 day'),

    ('10000000-0000-0000-0000-000000000013','00000000-0000-0000-0000-000000000002','Clot Padel Arena','Friendly club','C/ Clot 99','Barcelona','ES','08018',
     ST_SetSRID(ST_MakePoint(2.1900,41.4090),4326)::geography,'ACTIVE',NULL,now()-interval '17 days',now()-interval '1 day'),

    ('10000000-0000-0000-0000-000000000014','00000000-0000-0000-0000-000000000002','Les Corts Indoor','Indoor premium','Travessera de les Corts 120','Barcelona','ES','08028',
     ST_SetSRID(ST_MakePoint(2.1300,41.3850),4326)::geography,'ACTIVE',NULL,now()-interval '16 days',now()-interval '1 day'),

    ('10000000-0000-0000-0000-000000000015','00000000-0000-0000-0000-000000000002','Sant Andreu Padel','Community courts','C/ Gran de Sant Andreu 250','Barcelona','ES','08030',
     ST_SetSRID(ST_MakePoint(2.1905,41.4350),4326)::geography,'ACTIVE',NULL,now()-interval '15 days',now()-interval '1 day'),

-- BADALONA (3)
    ('10000000-0000-0000-0000-000000000016','00000000-0000-0000-0000-000000000002','Badalona Smash Club','Near marina','C/ del Mar 45','Badalona','ES','08911',
     ST_SetSRID(ST_MakePoint(2.2470,41.4505),4326)::geography,'ACTIVE',NULL,now()-interval '14 days',now()-interval '1 day'),

    ('10000000-0000-0000-0000-000000000017','00000000-0000-0000-0000-000000000002','Montigalà Courts','Spacious club','Av. Puigfred 20','Badalona','ES','08917',
     ST_SetSRID(ST_MakePoint(2.2400,41.4600),4326)::geography,'ACTIVE',NULL,now()-interval '13 days',now()-interval '1 day'),

    ('10000000-0000-0000-0000-000000000018','00000000-0000-0000-0000-000000000002','Badalona Indoor','All weather courts','C/ Independència 12','Badalona','ES','08915',
     ST_SetSRID(ST_MakePoint(2.2300,41.4480),4326)::geography,'ACTIVE',NULL,now()-interval '12 days',now()-interval '1 day'),

-- TERRASSA (3)
    ('10000000-0000-0000-0000-000000000019','00000000-0000-0000-0000-000000000002','Terrassa Pro Padel','Competitive focus','C/ Colom 300','Terrassa','ES','08222',
     ST_SetSRID(ST_MakePoint(2.0080,41.5630),4326)::geography,'ACTIVE',NULL,now()-interval '11 days',now()-interval '1 day'),

    ('10000000-0000-0000-0000-000000000020','00000000-0000-0000-0000-000000000002','Vallès Padel Center','Family club','Av. Abat Marcet 100','Terrassa','ES','08225',
     ST_SetSRID(ST_MakePoint(2.0100,41.5700),4326)::geography,'ACTIVE',NULL,now()-interval '10 days',now()-interval '1 day'),

    ('10000000-0000-0000-0000-000000000021','00000000-0000-0000-0000-000000000002','Terrassa Indoor Arena','Modern courts','C/ Nord 55','Terrassa','ES','08221',
     ST_SetSRID(ST_MakePoint(2.0030,41.5600),4326)::geography,'ACTIVE',NULL,now()-interval '9 days',now()-interval '1 day'),

-- SABADELL (3)
    ('10000000-0000-0000-0000-000000000022','00000000-0000-0000-0000-000000000002','Sabadell Padel Hub','Central courts','C/ Gràcia 40','Sabadell','ES','08201',
     ST_SetSRID(ST_MakePoint(2.1070,41.5480),4326)::geography,'ACTIVE',NULL,now()-interval '8 days',now()-interval '1 day'),

    ('10000000-0000-0000-0000-000000000023','00000000-0000-0000-0000-000000000002','Can Rull Courts','Outdoor courts','Av. Matadepera 10','Sabadell','ES','08207',
     ST_SetSRID(ST_MakePoint(2.0900,41.5500),4326)::geography,'ACTIVE',NULL,now()-interval '7 days',now()-interval '1 day'),

    ('10000000-0000-0000-0000-000000000024','00000000-0000-0000-0000-000000000002','Sabadell Indoor','Premium lights','C/ Sol i Padrís 88','Sabadell','ES','08203',
     ST_SetSRID(ST_MakePoint(2.1050,41.5400),4326)::geography,'ACTIVE',NULL,now()-interval '6 days',now()-interval '1 day'),

-- GIRONA (2)
    ('10000000-0000-0000-0000-000000000025','00000000-0000-0000-0000-000000000002','Girona Elite Padel','High level club','C/ Barcelona 120','Girona','ES','17002',
     ST_SetSRID(ST_MakePoint(2.8200,41.9790),4326)::geography,'ACTIVE',NULL,now()-interval '5 days',now()-interval '1 day'),

    ('10000000-0000-0000-0000-000000000026','00000000-0000-0000-0000-000000000002','Costa Brava Courts','Training center','Av. França 50','Girona','ES','17007',
     ST_SetSRID(ST_MakePoint(2.8300,41.9900),4326)::geography,'ACTIVE',NULL,now()-interval '4 days',now()-interval '1 day'),

-- TARRAGONA (2)
    ('10000000-0000-0000-0000-000000000027','00000000-0000-0000-0000-000000000002','Tarragona Padel Club','Near port','C/ Reial 15','Tarragona','ES','43004',
     ST_SetSRID(ST_MakePoint(1.2440,41.1180),4326)::geography,'ACTIVE',NULL,now()-interval '3 days',now()-interval '1 day'),

    ('10000000-0000-0000-0000-000000000028','00000000-0000-0000-0000-000000000002','Mediterrani Courts','Outdoor complex','Av. Catalunya 200','Tarragona','ES','43002',
     ST_SetSRID(ST_MakePoint(1.2500,41.1200),4326)::geography,'ACTIVE',NULL,now()-interval '3 days',now()-interval '1 day'),

-- LLEIDA (2)
    ('10000000-0000-0000-0000-000000000029','00000000-0000-0000-0000-000000000002','Lleida Indoor Padel','All season courts','C/ Prat de la Riba 10','Lleida','ES','25004',
     ST_SetSRID(ST_MakePoint(0.6200,41.6170),4326)::geography,'ACTIVE',NULL,now()-interval '2 days',now()-interval '1 day'),

    ('10000000-0000-0000-0000-000000000030','00000000-0000-0000-0000-000000000002','Segrià Padel Center','Training courts','Av. Alcalde Rovira Roure 100','Lleida','ES','25006',
     ST_SetSRID(ST_MakePoint(0.6300,41.6200),4326)::geography,'ACTIVE',NULL,now()-interval '2 days',now()-interval '1 day')

    ON CONFLICT (id) DO NOTHING;

-- =========================
-- 5) RESOURCE (10)
-- resource_type: string (tu dominio). Ej: PADEL/TENNIS/SQUASH
-- slot_duration: INT
-- =========================
INSERT INTO resource (
    id, venue_id, name, description, resource_type, slot_duration,
    status, reject_reason, created_at, updated_at
)
VALUES
    ('20000000-0000-0000-0000-000000000001','10000000-0000-0000-0000-000000000001','Court 1','Panoramic court','PADEL',90,'ACTIVE',NULL, now()-interval '40 days', now()-interval '1 day'),
    ('20000000-0000-0000-0000-000000000002','10000000-0000-0000-0000-000000000001','Court 2','Standard court','PADEL',90,'ACTIVE',NULL, now()-interval '40 days', now()-interval '1 day'),
    ('20000000-0000-0000-0000-000000000003','10000000-0000-0000-0000-000000000002','Court A','Outdoor court','PADEL',60,'ACTIVE',NULL, now()-interval '35 days', now()-interval '2 days'),
    ('20000000-0000-0000-0000-000000000004','10000000-0000-0000-0000-000000000003','Court 1','Premium indoor','PADEL',90,'ACTIVE',NULL, now()-interval '30 days', now()-interval '2 days'),
    ('20000000-0000-0000-0000-000000000005','10000000-0000-0000-0000-000000000003','Court 2','Premium indoor','PADEL',90,'ACTIVE',NULL, now()-interval '30 days', now()-interval '2 days'),
    ('20000000-0000-0000-0000-000000000006','10000000-0000-0000-0000-000000000004','Tennis 1','Hard court','TENNIS',60,'ACTIVE',NULL, now()-interval '28 days', now()-interval '4 days'),
    ('20000000-0000-0000-0000-000000000007','10000000-0000-0000-0000-000000000005','Court 1','Good lighting','PADEL',90,'ACTIVE',NULL, now()-interval '25 days', now()-interval '1 day'),
    ('20000000-0000-0000-0000-000000000008','10000000-0000-0000-0000-000000000007','Court Pro','Competition court','PADEL',90,'ACTIVE',NULL, now()-interval '20 days', now()-interval '2 days'),
    ('20000000-0000-0000-0000-000000000009','10000000-0000-0000-0000-000000000008','Court Beach','Near the sea','PADEL',90,'ACTIVE',NULL, now()-interval '15 days', now()-interval '1 day'),
    ('20000000-0000-0000-0000-000000000010','10000000-0000-0000-0000-000000000009','Court Pending','Waiting approval','PADEL',90,'PENDING_REVIEW',NULL, now()-interval '8 days', now()-interval '1 day'),
    ('20000000-0000-0000-0000-000000000011','10000000-0000-0000-0000-000000000011','Court 1','Standard court','PADEL',90,'ACTIVE',NULL, now()-interval '10 days', now()-interval '1 day'),
    ('20000000-0000-0000-0000-000000000012','10000000-0000-0000-0000-000000000011','Court 2','Panoramic court','PADEL',90,'ACTIVE',NULL, now()-interval '10 days', now()-interval '1 day'),

-- Venue 012
    ('20000000-0000-0000-0000-000000000013','10000000-0000-0000-0000-000000000012','Court 1','Indoor court','PADEL',90,'ACTIVE',NULL, now()-interval '9 days', now()-interval '1 day'),
    ('20000000-0000-0000-0000-000000000014','10000000-0000-0000-0000-000000000012','Court 2','Indoor court','PADEL',90,'ACTIVE',NULL, now()-interval '9 days', now()-interval '1 day'),

-- Venue 013
    ('20000000-0000-0000-0000-000000000015','10000000-0000-0000-0000-000000000013','Court 1','Community court','PADEL',90,'ACTIVE',NULL, now()-interval '8 days', now()-interval '1 day'),
    ('20000000-0000-0000-0000-000000000016','10000000-0000-0000-0000-000000000013','Court 2','Community court','PADEL',90,'ACTIVE',NULL, now()-interval '8 days', now()-interval '1 day'),

-- Venue 014
    ('20000000-0000-0000-0000-000000000017','10000000-0000-0000-0000-000000000014','Court 1','Premium indoor','PADEL',90,'ACTIVE',NULL, now()-interval '7 days', now()-interval '1 day'),
    ('20000000-0000-0000-0000-000000000018','10000000-0000-0000-0000-000000000014','Court 2','Premium indoor','PADEL',90,'ACTIVE',NULL, now()-interval '7 days', now()-interval '1 day'),

-- Venue 015
    ('20000000-0000-0000-0000-000000000019','10000000-0000-0000-0000-000000000015','Court 1','Standard court','PADEL',90,'ACTIVE',NULL, now()-interval '6 days', now()-interval '1 day'),
    ('20000000-0000-0000-0000-000000000020','10000000-0000-0000-0000-000000000015','Court 2','Standard court','PADEL',90,'ACTIVE',NULL, now()-interval '6 days', now()-interval '1 day'),

-- Venue 016
    ('20000000-0000-0000-0000-000000000021','10000000-0000-0000-0000-000000000016','Court 1','Outdoor court','PADEL',90,'ACTIVE',NULL, now()-interval '5 days', now()-interval '1 day'),
    ('20000000-0000-0000-0000-000000000022','10000000-0000-0000-0000-000000000016','Court 2','Outdoor court','PADEL',90,'ACTIVE',NULL, now()-interval '5 days', now()-interval '1 day'),

-- Venue 017
    ('20000000-0000-0000-0000-000000000023','10000000-0000-0000-0000-000000000017','Court 1','Indoor court','PADEL',90,'ACTIVE',NULL, now()-interval '4 days', now()-interval '1 day'),
    ('20000000-0000-0000-0000-000000000024','10000000-0000-0000-0000-000000000017','Court 2','Indoor court','PADEL',90,'ACTIVE',NULL, now()-interval '4 days', now()-interval '1 day'),

-- Venue 018
    ('20000000-0000-0000-0000-000000000025','10000000-0000-0000-0000-000000000018','Court 1','All weather','PADEL',90,'ACTIVE',NULL, now()-interval '3 days', now()-interval '1 day'),
    ('20000000-0000-0000-0000-000000000026','10000000-0000-0000-0000-000000000018','Court 2','All weather','PADEL',90,'ACTIVE',NULL, now()-interval '3 days', now()-interval '1 day'),

-- Venue 019
    ('20000000-0000-0000-0000-000000000027','10000000-0000-0000-0000-000000000019','Court 1','Competition court','PADEL',90,'ACTIVE',NULL, now()-interval '2 days', now()-interval '1 day'),
    ('20000000-0000-0000-0000-000000000028','10000000-0000-0000-0000-000000000019','Court 2','Competition court','PADEL',90,'ACTIVE',NULL, now()-interval '2 days', now()-interval '1 day'),

-- Venue 020
    ('20000000-0000-0000-0000-000000000029','10000000-0000-0000-0000-000000000020','Court 1','Standard court','PADEL',90,'ACTIVE',NULL, now()-interval '1 day', now()-interval '1 day'),
    ('20000000-0000-0000-0000-000000000030','10000000-0000-0000-0000-000000000020','Court 2','Standard court','PADEL',90,'ACTIVE',NULL, now()-interval '1 day', now()-interval '1 day')

    ON CONFLICT (id) DO NOTHING;

-- =========================
-- 6) RESOURCE_SCHEDULE (10)
-- (CUIDADO: index UNIQUE por resource_id+day+opening+closing)
-- =========================
INSERT INTO resource_schedule (id, resource_id, day_of_week, opening_time, closing_time)
VALUES
    ('21000000-0000-0000-0000-000000000001','20000000-0000-0000-0000-000000000001','MON','08:00','22:00'),
    ('21000000-0000-0000-0000-000000000002','20000000-0000-0000-0000-000000000002','MON','08:00','22:00'),
    ('21000000-0000-0000-0000-000000000003','20000000-0000-0000-0000-000000000003','TUE','09:00','21:00'),
    ('21000000-0000-0000-0000-000000000004','20000000-0000-0000-0000-000000000004','WED','08:00','23:00'),
    ('21000000-0000-0000-0000-000000000005','20000000-0000-0000-0000-000000000005','THU','08:00','23:00'),
    ('21000000-0000-0000-0000-000000000006','20000000-0000-0000-0000-000000000006','FRI','09:00','22:00'),
    ('21000000-0000-0000-0000-000000000007','20000000-0000-0000-0000-000000000007','SAT','09:00','22:00'),
    ('21000000-0000-0000-0000-000000000008','20000000-0000-0000-0000-000000000008','SUN','09:00','21:00'),
    ('21000000-0000-0000-0000-000000000009','20000000-0000-0000-0000-000000000009','SAT','08:00','22:00'),
    ('21000000-0000-0000-0000-000000000010','20000000-0000-0000-0000-000000000010','MON','10:00','20:00'),
    ('21000000-0000-0000-0000-000000000011','20000000-0000-0000-0000-000000000011','MON','08:00','22:00'),
    ('21000000-0000-0000-0000-000000000012','20000000-0000-0000-0000-000000000012','MON','08:00','22:00'),
    ('21000000-0000-0000-0000-000000000013','20000000-0000-0000-0000-000000000013','TUE','08:00','22:00'),
    ('21000000-0000-0000-0000-000000000014','20000000-0000-0000-0000-000000000014','TUE','08:00','22:00'),
    ('21000000-0000-0000-0000-000000000015','20000000-0000-0000-0000-000000000015','WED','08:00','22:00'),
    ('21000000-0000-0000-0000-000000000016','20000000-0000-0000-0000-000000000016','WED','08:00','22:00'),
    ('21000000-0000-0000-0000-000000000017','20000000-0000-0000-0000-000000000017','THU','08:00','22:00'),
    ('21000000-0000-0000-0000-000000000018','20000000-0000-0000-0000-000000000018','THU','08:00','22:00'),
    ('21000000-0000-0000-0000-000000000019','20000000-0000-0000-0000-000000000019','FRI','08:00','22:00'),
    ('21000000-0000-0000-0000-000000000020','20000000-0000-0000-0000-000000000020','FRI','08:00','22:00'),
    ('21000000-0000-0000-0000-000000000021','20000000-0000-0000-0000-000000000021','SAT','09:00','22:00'),
    ('21000000-0000-0000-0000-000000000022','20000000-0000-0000-0000-000000000022','SAT','09:00','22:00'),
    ('21000000-0000-0000-0000-000000000023','20000000-0000-0000-0000-000000000023','SUN','09:00','21:00'),
    ('21000000-0000-0000-0000-000000000024','20000000-0000-0000-0000-000000000024','SUN','09:00','21:00'),
    ('21000000-0000-0000-0000-000000000025','20000000-0000-0000-0000-000000000025','MON','08:00','22:00'),
    ('21000000-0000-0000-0000-000000000026','20000000-0000-0000-0000-000000000026','MON','08:00','22:00'),
    ('21000000-0000-0000-0000-000000000027','20000000-0000-0000-0000-000000000027','TUE','08:00','22:00'),
    ('21000000-0000-0000-0000-000000000028','20000000-0000-0000-0000-000000000028','TUE','08:00','22:00'),
    ('21000000-0000-0000-0000-000000000029','20000000-0000-0000-0000-000000000029','WED','08:00','22:00'),
    ('21000000-0000-0000-0000-000000000030','20000000-0000-0000-0000-000000000030','WED','08:00','22:00')
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- 7) RESOURCE_PRICE_RULE (10)
-- day_type: string (tu dominio). Ej: WEEKDAY/WEEKEND/MON... etc.
-- =========================
INSERT INTO resource_price_rule (id, resource_id, day_type, start_time, end_time, price, currency)
VALUES
    ('22000000-0000-0000-0000-000000000001','20000000-0000-0000-0000-000000000001','WEEKDAY','08:00','17:00',22.00,'EUR'),
    ('22000000-0000-0000-0000-000000000002','20000000-0000-0000-0000-000000000001','WEEKDAY','17:00','22:00',30.00,'EUR'),
    ('22000000-0000-0000-0000-000000000003','20000000-0000-0000-0000-000000000002','WEEKDAY','08:00','17:00',20.00,'EUR'),
    ('22000000-0000-0000-0000-000000000004','20000000-0000-0000-0000-000000000002','WEEKEND','09:00','22:00',34.00,'EUR'),
    ('22000000-0000-0000-0000-000000000005','20000000-0000-0000-0000-000000000003','WEEKDAY','09:00','21:00',18.00,'EUR'),
    ('22000000-0000-0000-0000-000000000006','20000000-0000-0000-0000-000000000004','WEEKDAY','08:00','18:00',28.00,'EUR'),
    ('22000000-0000-0000-0000-000000000007','20000000-0000-0000-0000-000000000004','WEEKDAY','18:00','23:00',36.00,'EUR'),
    ('22000000-0000-0000-0000-000000000008','20000000-0000-0000-0000-000000000006','WEEKEND','09:00','22:00',16.00,'EUR'),
    ('22000000-0000-0000-0000-000000000009','20000000-0000-0000-0000-000000000008','WEEKDAY','10:00','22:00',40.00,'EUR'),
    ('22000000-0000-0000-0000-000000000010','20000000-0000-0000-0000-000000000009','WEEKEND','09:00','21:00',32.00,'EUR'),
    ('22000000-0000-0000-0000-000000000011','20000000-0000-0000-0000-000000000011','WEEKDAY','08:00','17:00',24.00,'EUR'),
    ('22000000-0000-0000-0000-000000000012','20000000-0000-0000-0000-000000000011','WEEKDAY','17:00','22:00',34.00,'EUR'),

    ('22000000-0000-0000-0000-000000000013','20000000-0000-0000-0000-000000000012','WEEKDAY','08:00','17:00',24.00,'EUR'),
    ('22000000-0000-0000-0000-000000000014','20000000-0000-0000-0000-000000000012','WEEKDAY','17:00','22:00',34.00,'EUR'),

    ('22000000-0000-0000-0000-000000000015','20000000-0000-0000-0000-000000000013','WEEKDAY','08:00','17:00',22.00,'EUR'),
    ('22000000-0000-0000-0000-000000000016','20000000-0000-0000-0000-000000000013','WEEKDAY','17:00','22:00',32.00,'EUR')
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- 9) BOOKING (10)
-- OJO: constraint booking_no_overlap aplica si status IN ('PENDING_PAYMENT','CONFIRMED','PENDING_MATCH')
-- Evito solapes (mismo recurso/fecha con slots separados)
-- =========================
INSERT INTO booking (
    id, resource_id, player_id,
    booking_date, start_time, end_time,
    price_paid, currency,
    status, payment_status,
    cancelled_at, cancel_reason,
    created_at, updated_at, expires_at
)
VALUES
    -- Past confirmed (no overlaps)
    ('30000000-0000-0000-0000-000000000001','20000000-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000005',
     current_date - 7, '18:00','19:30',30.00,'EUR','CONFIRMED','PAID',NULL,NULL, now()-interval '8 days', now()-interval '8 days', NULL),

    ('30000000-0000-0000-0000-000000000002','20000000-0000-0000-0000-000000000002','00000000-0000-0000-0000-000000000006',
     current_date - 3, '20:00','21:30',34.00,'EUR','CONFIRMED','PAID',NULL,NULL, now()-interval '4 days', now()-interval '4 days', NULL),

    -- Cancelled doesn't participate in overlap constraint (but still valid)
    ('30000000-0000-0000-0000-000000000003','20000000-0000-0000-0000-000000000003','00000000-0000-0000-0000-000000000007',
     current_date - 1, '10:00','11:00',18.00,'EUR','CANCELLED','REFUNDED', now()-interval '20 hours','Change of plans', now()-interval '2 days', now()-interval '20 hours', NULL),

    -- Future pending payment (has expires_at)
    ('30000000-0000-0000-0000-000000000004','20000000-0000-0000-0000-000000000004','00000000-0000-0000-0000-000000000008',
     current_date + 1, '19:00','20:30',36.00,'EUR','PENDING_PAYMENT','PENDING',NULL,NULL, now()-interval '2 hours', now()-interval '2 hours', now()+interval '30 minutes'),

    -- Future confirmed
    ('30000000-0000-0000-0000-000000000005','20000000-0000-0000-0000-000000000005','00000000-0000-0000-0000-000000000010',
     current_date + 2, '08:00','09:30',28.00,'EUR','CONFIRMED','PAID',NULL,NULL, now()-interval '1 hour', now()-interval '1 hour', NULL),

    ('30000000-0000-0000-0000-000000000006','20000000-0000-0000-0000-000000000006','00000000-0000-0000-0000-000000000006',
     current_date + 5, '12:00','13:00',16.00,'EUR','CONFIRMED','PAID',NULL,NULL, now()-interval '3 hours', now()-interval '3 hours', NULL),

    -- Pending match blocks overlap too; give unique resource/time
    ('30000000-0000-0000-0000-000000000007','20000000-0000-0000-0000-000000000007','00000000-0000-0000-0000-000000000005',
     current_date + 6, '21:00','22:30',30.00,'EUR','PENDING_MATCH','PENDING',NULL,NULL, now()-interval '30 minutes', now()-interval '30 minutes', now()+interval '2 hours'),

    ('30000000-0000-0000-0000-000000000008','20000000-0000-0000-0000-000000000008','00000000-0000-0000-0000-000000000008',
     current_date + 3, '17:00','18:30',40.00,'EUR','CONFIRMED','PAID',NULL,NULL, now()-interval '6 hours', now()-interval '6 hours', NULL),

    ('30000000-0000-0000-0000-000000000009','20000000-0000-0000-0000-000000000009','00000000-0000-0000-0000-000000000007',
     current_date + 4, '09:00','10:30',32.00,'EUR','CONFIRMED','PAID',NULL,NULL, now()-interval '10 hours', now()-interval '10 hours', NULL),

    ('30000000-0000-0000-0000-000000000010','20000000-0000-0000-0000-000000000003','00000000-0000-0000-0000-000000000009',
     current_date + 7, '16:00','17:00',18.00,'EUR','PENDING_PAYMENT','PENDING',NULL,NULL, now()-interval '15 minutes', now()-interval '15 minutes', now()+interval '45 minutes')
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- 10) PAYMENT (10)
-- stripe_payment_intent_id UNIQUE NOT NULL
-- =========================
INSERT INTO payment (
    id, booking_id, stripe_payment_intent_id,
    amount, currency, status,
    created_at, updated_at
)
VALUES
    ('50000000-0000-0000-0000-000000000001','30000000-0000-0000-0000-000000000001','pi_dev_0001',30.00,'EUR','SUCCEEDED', now()-interval '8 days', now()-interval '8 days'),
    ('50000000-0000-0000-0000-000000000002','30000000-0000-0000-0000-000000000002','pi_dev_0002',34.00,'EUR','SUCCEEDED', now()-interval '4 days', now()-interval '4 days'),
    ('50000000-0000-0000-0000-000000000003','30000000-0000-0000-0000-000000000003','pi_dev_0003',18.00,'EUR','REFUNDED',  now()-interval '2 days', now()-interval '20 hours'),
    ('50000000-0000-0000-0000-000000000004','30000000-0000-0000-0000-000000000004','pi_dev_0004',36.00,'EUR','PENDING',   now()-interval '2 hours', now()-interval '2 hours'),
    ('50000000-0000-0000-0000-000000000005','30000000-0000-0000-0000-000000000005','pi_dev_0005',28.00,'EUR','SUCCEEDED', now()-interval '1 hour',  now()-interval '1 hour'),
    ('50000000-0000-0000-0000-000000000006','30000000-0000-0000-0000-000000000006','pi_dev_0006',16.00,'EUR','SUCCEEDED', now()-interval '3 hours', now()-interval '3 hours'),
    ('50000000-0000-0000-0000-000000000007','30000000-0000-0000-0000-000000000007','pi_dev_0007',30.00,'EUR','PENDING',   now()-interval '30 minutes', now()-interval '30 minutes'),
    ('50000000-0000-0000-0000-000000000008','30000000-0000-0000-0000-000000000008','pi_dev_0008',40.00,'EUR','SUCCEEDED', now()-interval '6 hours', now()-interval '6 hours'),
    ('50000000-0000-0000-0000-000000000009','30000000-0000-0000-0000-000000000009','pi_dev_0009',32.00,'EUR','SUCCEEDED', now()-interval '10 hours', now()-interval '10 hours'),
    ('50000000-0000-0000-0000-000000000010','30000000-0000-0000-0000-000000000010','pi_dev_0010',18.00,'EUR','PENDING',   now()-interval '15 minutes', now()-interval '15 minutes')
    ON CONFLICT (id) DO NOTHING;

INSERT INTO venue_image (
    id,
    venue_id,
    url,
    public_id,
    display_order,
    created_at
) VALUES
      ('8b100539-d0e9-4ea5-be7e-d15a645bbb6f','10000000-0000-0000-0000-000000000001','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772468881/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000001/72f65f82-28f5-480c-ba66-3ed5b7d06cc5.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000001/72f65f82-28f5-480c-ba66-3ed5b7d06cc5',0,'2026-03-02 16:28:01.561935+00'),
      ('b07429a4-d1a5-4606-9acf-c398f9949c05','10000000-0000-0000-0000-000000000002','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772468928/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000002/7aa73666-33f1-4dad-933c-6d7aa313de30.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000002/7aa73666-33f1-4dad-933c-6d7aa313de30',0,'2026-03-02 16:28:49.539585+00'),
      ('0aae5192-c480-49a0-83ac-cb094f6f8e4b','10000000-0000-0000-0000-000000000003','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772468948/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000003/7d2ff74b-ff66-43a7-9e73-934a3386a844.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000003/7d2ff74b-ff66-43a7-9e73-934a3386a844',0,'2026-03-02 16:29:07.844972+00'),
      ('34f37958-94cc-4788-816c-7ff36e693e19','10000000-0000-0000-0000-000000000004','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772468969/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000004/7cb2a50e-5bd3-4ba1-ac36-e7dfa9781147.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000004/7cb2a50e-5bd3-4ba1-ac36-e7dfa9781147',0,'2026-03-02 16:29:28.819906+00'),
      ('194a65c5-207e-4ce0-97ff-7d0dd58809ec','10000000-0000-0000-0000-000000000005','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772468989/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000005/c2b50d51-8ae1-43cd-9fe1-efc37df321b2.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000005/c2b50d51-8ae1-43cd-9fe1-efc37df321b2',0,'2026-03-02 16:29:50.248431+00'),
      ('ec7e54d8-3138-47bf-b969-def06bc3d7e7','10000000-0000-0000-0000-000000000006','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469001/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000006/e67aadfa-b0c7-400b-88ef-a665ff3ee6a7.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000006/e67aadfa-b0c7-400b-88ef-a665ff3ee6a7',0,'2026-03-02 16:30:02.94263+00'),

      ('5ec49868-c9d2-4981-bdda-e62dba20e67a','10000000-0000-0000-0000-000000000007','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469050/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000007/ef7899c8-f0bf-4af9-9808-ddd686826dfd.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000007/ef7899c8-f0bf-4af9-9808-ddd686826dfd',0,'2026-03-02 16:30:51.772383+00'),
      ('409f21f8-08cc-41a1-8a77-ddae69bf7a49','10000000-0000-0000-0000-000000000008','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469083/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000008/cc5a1262-b42f-4be6-b997-5bbde2054fee.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000008/cc5a1262-b42f-4be6-b997-5bbde2054fee',0,'2026-03-02 16:31:23.98595+00'),
      ('0529b166-4f4a-47f6-a279-5f4e6aed3a49','10000000-0000-0000-0000-000000000011','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469030/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000011/0dfbd7fe-dcc2-4b85-808a-2301b854521a.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000011/0dfbd7fe-dcc2-4b85-808a-2301b854521a',0,'2026-03-02 16:30:29.947519+00'),

      ('f2fca8ce-a0d6-42f0-9cb1-bb82dbd88d54','10000000-0000-0000-0000-000000000012','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469102/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000012/93ad495a-070b-4611-b272-668fd0801530.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000012/93ad495a-070b-4611-b272-668fd0801530',0,'2026-03-02 16:31:41.708736+00'),
      ('f4c057ad-7fff-487f-bb84-2e037ab7bbc8','10000000-0000-0000-0000-000000000013','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469115/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000013/1772e508-5177-485b-b885-34301c135289.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000013/1772e508-5177-485b-b885-34301c135289',0,'2026-03-02 16:31:55.767444+00'),
      ('78cfa30c-03f8-4f08-a02d-f34b0330ccff','10000000-0000-0000-0000-000000000014','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469127/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000014/859305ea-94b7-4d20-af1f-c740a17caa64.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000014/859305ea-94b7-4d20-af1f-c740a17caa64',0,'2026-03-02 16:32:08.468228+00'),

      ('d42f9b45-e613-404e-b90b-f87664425e90','10000000-0000-0000-0000-000000000015','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469147/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000015/3e438f40-b7e3-4ac3-b4dd-7b87289fbaa1.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000015/3e438f40-b7e3-4ac3-b4dd-7b87289fbaa1',0,'2026-03-02 16:32:27.74556+00'),
      ('2cfd0ba9-d87a-44af-9e56-70a4adf2e9c0','10000000-0000-0000-0000-000000000016','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469306/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000016/70d936d6-4ec5-42b7-ae7b-a7df18437725.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000016/70d936d6-4ec5-42b7-ae7b-a7df18437725',0,'2026-03-02 16:35:07.302079+00'),

      ('747ced7e-d18d-454e-80be-fb4d2fc44703','10000000-0000-0000-0000-000000000017','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469320/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000017/34ca7d85-af00-41b4-83ae-5db989820866.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000017/34ca7d85-af00-41b4-83ae-5db989820866',0,'2026-03-02 16:35:20.569603+00'),
      ('dcb8e982-64a7-480e-b3a1-4c5ae080a055','10000000-0000-0000-0000-000000000018','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469351/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000018/7c899d0a-2ec2-4e6a-8477-2a7f03e675c6.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000018/7c899d0a-2ec2-4e6a-8477-2a7f03e675c6',0,'2026-03-02 16:35:50.923108+00'),

      ('172c7a94-c57a-45d7-b66d-9cb12c2dafad','10000000-0000-0000-0000-000000000019','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469366/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000019/d7b2ec99-667d-42aa-92d1-409be2b224aa.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000019/d7b2ec99-667d-42aa-92d1-409be2b224aa',0,'2026-03-02 16:36:05.734587+00'),
      ('24cc6be7-b5fd-4539-85c3-674ae43f8304','10000000-0000-0000-0000-000000000020','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469376/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000020/49c038bd-8564-4610-a50f-71d89223a12b.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000020/49c038bd-8564-4610-a50f-71d89223a12b',0,'2026-03-02 16:36:17.215687+00'),

      ('584b4806-2f7b-41ee-9fe8-3c8fbd57a858','10000000-0000-0000-0000-000000000021','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469396/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000021/97a25f3d-8521-4949-9708-c74b0e60e390.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000021/97a25f3d-8521-4949-9708-c74b0e60e390',0,'2026-03-02 16:36:35.502289+00'),
      ('e321ccdf-33c6-4049-a93d-c1515b798e51','10000000-0000-0000-0000-000000000022','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469416/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000022/728d5df1-80d2-4e7f-8c5e-6ec64c075e14.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000022/728d5df1-80d2-4e7f-8c5e-6ec64c075e14',0,'2026-03-02 16:36:56.106562+00'),

      ('0d222bd0-7732-4d17-a479-92d6722062af','10000000-0000-0000-0000-000000000023','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469429/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000023/1205cf24-e8d5-4061-ac9d-774ccde0cc83.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000023/1205cf24-e8d5-4061-ac9d-774ccde0cc83',0,'2026-03-02 16:37:08.841594+00'),
      ('58193865-fd66-41ec-8866-ac59201e7037','10000000-0000-0000-0000-000000000024','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469442/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000024/31731fe0-8655-4a59-b3a2-449814a54bd1.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000024/31731fe0-8655-4a59-b3a2-449814a54bd1',0,'2026-03-02 16:37:22.802276+00'),

      ('41541cab-6487-4e49-8082-a539efd61a41','10000000-0000-0000-0000-000000000025','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469457/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000025/d139466f-9ffb-4b86-9008-de73283757da.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000025/d139466f-9ffb-4b86-9008-de73283757da',0,'2026-03-02 16:37:37.339292+00'),
      ('e571463c-efcf-4b33-8d26-b90436ab6bf0','10000000-0000-0000-0000-000000000026','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469663/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000026/e421913d-15b2-4c1a-95a9-6fbfd092b3c9.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000026/e421913d-15b2-4c1a-95a9-6fbfd092b3c9',0,'2026-03-02 16:41:03.668051+00'),

      ('7812f4ef-6599-4449-be16-aa43c6cb9496','10000000-0000-0000-0000-000000000027','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469688/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000027/5386df00-8b36-4a9c-a9c0-38d56ab61178.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000027/5386df00-8b36-4a9c-a9c0-38d56ab61178',0,'2026-03-02 16:41:27.574266+00'),
      ('ffcbe4f1-e6fc-472c-a35c-6e66e48bda05','10000000-0000-0000-0000-000000000028','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469675/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000028/2f9af56d-66c4-4f78-ad76-3cacd1eb0168.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000028/2f9af56d-66c4-4f78-ad76-3cacd1eb0168',0,'2026-03-02 16:41:17.382221+00'),

      ('920769cb-d738-4e64-b593-4305b7433667','10000000-0000-0000-0000-000000000029','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469715/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000029/d29c3b90-c2da-4eda-ac38-e456301c23f0.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000029/d29c3b90-c2da-4eda-ac38-e456301c23f0',0,'2026-03-02 16:41:55.279407+00'),
      ('cea661f5-8aed-421a-85f2-f1e8b8853184','10000000-0000-0000-0000-000000000030','https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469702/users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000030/ab19896d-e82e-4b64-b612-94be32bbf955.png','users/google-oauth2_117245030007219025441/venues/10000000-0000-0000-0000-000000000030/ab19896d-e82e-4b64-b612-94be32bbf955',0,'2026-03-02 16:41:42.899841+00')
        ON CONFLICT (id) DO NOTHING;


INSERT INTO resource_image (
    id,
    resource_id,
    url,
    public_id,
    display_order,
    created_at
) VALUES
      (
          '819b47ce-e8b2-49c3-851d-ffec839b8661',
          '20000000-0000-0000-0000-000000000004',
          'https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469850/users/google-oauth2_117245030007219025441/resources/20000000-0000-0000-0000-000000000004/a8a00ea5-48c1-494a-94e2-70a3cf2f7fcf.png',
          'users/google-oauth2_117245030007219025441/resources/20000000-0000-0000-0000-000000000004/a8a00ea5-48c1-494a-94e2-70a3cf2f7fcf',
          0,
          '2026-03-02 16:44:10.141807+00'
      ),
      (
          'c15e9593-dccb-47a2-b91d-b27c4e46efe1',
          '20000000-0000-0000-0000-000000000005',
          'https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469871/users/google-oauth2_117245030007219025441/resources/20000000-0000-0000-0000-000000000005/60bdddc4-a31c-4ebd-a898-75b6abed7e5e.png',
          'users/google-oauth2_117245030007219025441/resources/20000000-0000-0000-0000-000000000005/60bdddc4-a31c-4ebd-a898-75b6abed7e5e',
          0,
          '2026-03-02 16:44:31.563282+00'
      ),
      (
          '87a10f61-bf47-4dae-8d79-25f05fd2f028',
          '20000000-0000-0000-0000-000000000027',
          'https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469917/users/google-oauth2_117245030007219025441/resources/20000000-0000-0000-0000-000000000027/39952a13-2809-4c73-b575-7d64e5e734b3.png',
          'users/google-oauth2_117245030007219025441/resources/20000000-0000-0000-0000-000000000027/39952a13-2809-4c73-b575-7d64e5e734b3',
          0,
          '2026-03-02 16:45:18.624866+00'
      ),
      (
          '944b22c3-0701-4d9c-8d43-10faa91b85e3',
          '20000000-0000-0000-0000-000000000028',
          'https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469907/users/google-oauth2_117245030007219025441/resources/20000000-0000-0000-0000-000000000028/a8663ba6-33b9-49e6-89a6-0c6533c124ff.png',
          'users/google-oauth2_117245030007219025441/resources/20000000-0000-0000-0000-000000000028/a8663ba6-33b9-49e6-89a6-0c6533c124ff',
          0,
          '2026-03-02 16:45:07.139096+00'
      ),
      (
          '9b534f30-26f2-454a-bd4c-d09374463c31',
          '20000000-0000-0000-0000-000000000029',
          'https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469898/users/google-oauth2_117245030007219025441/resources/20000000-0000-0000-0000-000000000029/0c595d1c-b7a6-4025-b5f7-3f9f300feb3d.png',
          'users/google-oauth2_117245030007219025441/resources/20000000-0000-0000-0000-000000000029/0c595d1c-b7a6-4025-b5f7-3f9f300feb3d',
          0,
          '2026-03-02 16:44:58.83774+00'
      ),
      (
          '0e227164-3812-412a-88bd-8b5e8f0966d0',
          '20000000-0000-0000-0000-000000000030',
          'https://res.cloudinary.com/dvclcs8wp/image/upload/v1772469893/users/google-oauth2_117245030007219025441/resources/20000000-0000-0000-0000-000000000030/3bfa33f1-77ea-4d86-8ccb-acb92ab9a71a.png',
          'users/google-oauth2_117245030007219025441/resources/20000000-0000-0000-0000-000000000030/3bfa33f1-77ea-4d86-8ccb-acb92ab9a71a',
          0,
          '2026-03-02 16:44:53.429308+00'
      ) ON CONFLICT (id) DO NOTHING;

-- =========================
-- 11) MATCH_REQUEST (10)
-- invitation_token: UUID UNIQUE NOT NULL
-- status default OPEN
-- =========================
INSERT INTO match_request (
    id, organizer_id, resource_id,
    booking_date, start_time, slot_duration_minutes,
    format, skill_level, custom_message,
    invitation_token, search_lat, search_lng, search_radius_km,
    status, expires_at, created_at
)
VALUES
    ('40000000-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000005','20000000-0000-0000-0000-000000000001',
     current_date + 1, '18:00', 90, 'TWO_VS_TWO','ANY','Busco pareja para 2vs2',
     '60000000-0000-0000-0000-000000000001', 40.4168, -3.7038, 10.0,
     'OPEN', now()+interval '12 hours', now()-interval '1 hour'),

    ('40000000-0000-0000-0000-000000000002','00000000-0000-0000-0000-000000000006','20000000-0000-0000-0000-000000000002',
     current_date + 2, '20:00', 90, 'TWO_VS_TWO','INTERMEDIATE','Nivel intermedio, buen rollo',
     '60000000-0000-0000-0000-000000000002', 40.4607, -3.6890, 8.0,
     'OPEN', now()+interval '10 hours', now()-interval '2 hours'),

    ('40000000-0000-0000-0000-000000000003','00000000-0000-0000-0000-000000000007','20000000-0000-0000-0000-000000000003',
     current_date + 3, '10:00', 60, 'ONE_VS_ONE','BEGINNER','Partido suave',
     '60000000-0000-0000-0000-000000000003', 40.4155, -3.6834, 6.0,
     'OPEN', now()+interval '24 hours', now()-interval '3 hours'),

    ('40000000-0000-0000-0000-000000000004','00000000-0000-0000-0000-000000000008','20000000-0000-0000-0000-000000000004',
     current_date + 4, '19:00', 90, 'TWO_VS_TWO','ADVANCED','Nivel alto',
     '60000000-0000-0000-0000-000000000004', 41.3851,  2.1734, 10.0,
     'OPEN', now()+interval '8 hours', now()-interval '30 minutes'),

    ('40000000-0000-0000-0000-000000000005','00000000-0000-0000-0000-000000000010','20000000-0000-0000-0000-000000000005',
     current_date + 5, '08:00', 90, 'TWO_VS_TWO','ANY','Mañanero',
     '60000000-0000-0000-0000-000000000005', 41.4036,  2.1744, 12.0,
     'OPEN', now()+interval '6 hours', now()-interval '4 hours'),

    ('40000000-0000-0000-0000-000000000006','00000000-0000-0000-0000-000000000009','20000000-0000-0000-0000-000000000006',
     current_date + 6, '12:00', 60, 'ONE_VS_ONE','INTERMEDIATE','Tennis 1v1',
     '60000000-0000-0000-0000-000000000006', 41.3763,  2.1360, 7.0,
     'OPEN', now()+interval '5 hours', now()-interval '1 day'),

    ('40000000-0000-0000-0000-000000000007','00000000-0000-0000-0000-000000000005','20000000-0000-0000-0000-000000000007',
     current_date + 2, '21:00', 90, 'TWO_VS_TWO','ANY','Reserva para match',
     '60000000-0000-0000-0000-000000000007', 40.4540, -3.6795, 5.0,
     'CANCELLED', now()-interval '1 hour', now()-interval '2 days'),

    ('40000000-0000-0000-0000-000000000008','00000000-0000-0000-0000-000000000008','20000000-0000-0000-0000-000000000008',
     current_date + 3, '17:00', 90, 'TWO_VS_TWO','ADVANCED','Competitivo',
     '60000000-0000-0000-0000-000000000008', 40.4607, -3.6890, 15.0,
     'OPEN', now()+interval '9 hours', now()-interval '6 hours'),

    ('40000000-0000-0000-0000-000000000009','00000000-0000-0000-0000-000000000007','20000000-0000-0000-0000-000000000009',
     current_date + 4, '09:00', 90, 'TWO_VS_TWO','BEGINNER','Aprendiendo',
     '60000000-0000-0000-0000-000000000009', 41.3995,  2.2040, 9.0,
     'OPEN', now()+interval '20 hours', now()-interval '8 hours'),

    ('40000000-0000-0000-0000-000000000010','00000000-0000-0000-0000-000000000006','20000000-0000-0000-0000-000000000003',
     current_date + 7, '16:00', 60, 'ONE_VS_ONE','ANY','Slot corto',
     '60000000-0000-0000-0000-000000000010', 40.4098, -3.6942, 10.0,
     'OPEN', now()+interval '18 hours', now()-interval '15 minutes')
    ON CONFLICT (id) DO NOTHING;

-- =========================
-- 12) MATCH_PLAYER (10)
-- team/role sin checks -> strings libres
-- constraint UNIQUE (match_request_id, player_id)
-- =========================
INSERT INTO match_player (
    id, match_request_id, player_id,
    team, role, joined_at
)
VALUES
    ('70000000-0000-0000-0000-000000000001','40000000-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000005','TEAM_1','ORGANIZER', now()-interval '1 hour'),
    ('70000000-0000-0000-0000-000000000002','40000000-0000-0000-0000-000000000001','00000000-0000-0000-0000-000000000006','TEAM_2','GUEST',    now()-interval '55 minutes'),

    ('70000000-0000-0000-0000-000000000003','40000000-0000-0000-0000-000000000002','00000000-0000-0000-0000-000000000006','TEAM_1','ORGANIZER', now()-interval '2 hours'),

    ('70000000-0000-0000-0000-000000000004','40000000-0000-0000-0000-000000000003','00000000-0000-0000-0000-000000000007','TEAM_1','ORGANIZER', now()-interval '3 hours'),

    ('70000000-0000-0000-0000-000000000005','40000000-0000-0000-0000-000000000004','00000000-0000-0000-0000-000000000008','TEAM_1','ORGANIZER', now()-interval '30 minutes'),
    ('70000000-0000-0000-0000-000000000006','40000000-0000-0000-0000-000000000004','00000000-0000-0000-0000-000000000010','TEAM_2','GUEST',    now()-interval '25 minutes'),

    ('70000000-0000-0000-0000-000000000007','40000000-0000-0000-0000-000000000005','00000000-0000-0000-0000-000000000010','TEAM_1','ORGANIZER', now()-interval '4 hours'),

    ('70000000-0000-0000-0000-000000000008','40000000-0000-0000-0000-000000000008','00000000-0000-0000-0000-000000000008','TEAM_1','ORGANIZER', now()-interval '6 hours'),

    ('70000000-0000-0000-0000-000000000009','40000000-0000-0000-0000-000000000009','00000000-0000-0000-0000-000000000007','TEAM_1','ORGANIZER', now()-interval '8 hours'),

    ('70000000-0000-0000-0000-000000000010','40000000-0000-0000-0000-000000000010','00000000-0000-0000-0000-000000000006','TEAM_1','ORGANIZER', now()-interval '15 minutes')
    ON CONFLICT (id) DO NOTHING;