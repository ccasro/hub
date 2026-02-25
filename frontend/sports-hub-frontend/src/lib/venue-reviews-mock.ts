export interface VenueReview {
  id: string;
  userId: string;
  userName: string;
  userAvatar: string | null;
  rating: number; // 1–5
  comment: string;
  createdAt: string;
}

export const venueReviews: Record<string, VenueReview[]> = {
  "venue-1": [
    {
      id: "rev-1",
      userId: "u-10",
      userName: "Laura Garcia",
      userAvatar: null,
      rating: 5,
      comment:
        "Instalaciones de primer nivel. Las pistas de cristal son espectaculares y los vestuarios estan impecables. Sin duda mi club favorito en Madrid.",
      createdAt: "2026-02-10T14:00:00Z",
    },
    {
      id: "rev-2",
      userId: "u-11",
      userName: "Pablo Ruiz",
      userAvatar: null,
      rating: 4,
      comment:
        "Muy buen club, buen ambiente y pistas en perfecto estado. Solo le faltaria un poco mas de parking.",
      createdAt: "2026-01-28T09:30:00Z",
    },
    {
      id: "rev-3",
      userId: "u-12",
      userName: "Ana Lopez",
      userAvatar: null,
      rating: 5,
      comment:
        "La cafeteria es genial para esperar entre partidos. El personal es muy amable y las pistas siempre limpias.",
      createdAt: "2026-01-15T17:45:00Z",
    },
    {
      id: "rev-4",
      userId: "u-13",
      userName: "Diego Fernandez",
      userAvatar: null,
      rating: 4,
      comment:
        "Buena relacion calidad/precio. Las pistas estan geniales aunque a veces hay que reservar con bastante antelacion.",
      createdAt: "2025-12-20T11:00:00Z",
    },
  ],
  "venue-2": [
    {
      id: "rev-5",
      userId: "u-14",
      userName: "Marta Sanchez",
      userAvatar: null,
      rating: 5,
      comment:
        "El mejor centro indoor de Barcelona. Climatizacion perfecta y suelo WPT de calidad profesional.",
      createdAt: "2026-02-15T10:00:00Z",
    },
    {
      id: "rev-6",
      userId: "u-15",
      userName: "Jordi Puig",
      userAvatar: null,
      rating: 4,
      comment:
        "Buen club con muchas pistas. Los fines de semana se llena bastante pero entre semana es perfecto.",
      createdAt: "2026-01-20T18:30:00Z",
    },
    {
      id: "rev-7",
      userId: "u-16",
      userName: "Sofia Costa",
      userAvatar: null,
      rating: 5,
      comment: "La pista de tenis cubierta es fantastica. Iluminacion increible y servicio excelente.",
      createdAt: "2025-12-10T14:00:00Z",
    },
  ],
  "venue-3": [
    {
      id: "rev-8",
      userId: "u-17",
      userName: "Carlos Vidal",
      userAvatar: null,
      rating: 5,
      comment:
        "Jugar al padel con vistas al mar no tiene precio. El atardecer desde la pista es magico.",
      createdAt: "2026-02-18T19:00:00Z",
    },
    {
      id: "rev-9",
      userId: "u-18",
      userName: "Elena Torres",
      userAvatar: null,
      rating: 4,
      comment:
        "Ubicacion inmejorable. Las pistas estan bien cuidadas y el ambiente es muy relajado.",
      createdAt: "2026-01-05T12:00:00Z",
    },
  ],
  "venue-4": [
    {
      id: "rev-10",
      userId: "u-19",
      userName: "Raul Moreno",
      userAvatar: null,
      rating: 5,
      comment:
        "Experiencia unica. Las luces LED crean un ambiente increible y el bar deportivo es genial.",
      createdAt: "2026-02-12T23:30:00Z",
    },
    {
      id: "rev-11",
      userId: "u-20",
      userName: "Isabel Navarro",
      userAvatar: null,
      rating: 4,
      comment: "El concepto nocturno esta muy bien. La musica a veces esta un poco alta, pero la experiencia mola.",
      createdAt: "2026-01-30T22:00:00Z",
    },
    {
      id: "rev-12",
      userId: "u-21",
      userName: "Miguel Angel Ramos",
      userAvatar: null,
      rating: 5,
      comment: "El mejor plan de viernes noche en Sevilla. Partidos, cocteles y buena musica. Top.",
      createdAt: "2025-11-15T01:00:00Z",
    },
  ],
  "venue-5": [
    {
      id: "rev-13",
      userId: "u-22",
      userName: "Iker Etxebarria",
      userAvatar: null,
      rating: 5,
      comment:
        "Las vistas de Bilbao desde la azotea son impresionantes. El techo retractil funciona genial los dias de lluvia.",
      createdAt: "2026-02-08T16:00:00Z",
    },
    {
      id: "rev-14",
      userId: "u-23",
      userName: "Leire Zabala",
      userAvatar: null,
      rating: 4,
      comment: "Club moderno y bien equipado. El parking gratuito es un puntazo.",
      createdAt: "2026-01-12T11:00:00Z",
    },
  ],
  "venue-6": [
    {
      id: "rev-15",
      userId: "u-24",
      userName: "Carmen Ruiz",
      userAvatar: null,
      rating: 5,
      comment:
        "Perfecto para ir en familia. Mis hijos van a la escuela de padel y mientras tanto yo juego en las pistas de adultos.",
      createdAt: "2026-02-20T10:30:00Z",
    },
    {
      id: "rev-16",
      userId: "u-25",
      userName: "Antonio Perez",
      userAvatar: null,
      rating: 4,
      comment:
        "Los jardines hacen que el ambiente sea muy agradable. El restaurante tiene buena comida y la terraza es genial.",
      createdAt: "2026-01-25T13:00:00Z",
    },
    {
      id: "rev-17",
      userId: "u-26",
      userName: "Rosa Martinez",
      userAvatar: null,
      rating: 5,
      comment: "Llevo jugando aqui 2 anos y no cambio de club. El personal es encantador y las instalaciones perfectas.",
      createdAt: "2025-11-28T09:00:00Z",
    },
  ],
};

export function getAverageRating(venueId: string): number {
  const reviews = venueReviews[venueId] || [];
  if (reviews.length === 0) return 0;
  const sum = reviews.reduce((acc, r) => acc + r.rating, 0);
  return parseFloat((sum / reviews.length).toFixed(1));
}
