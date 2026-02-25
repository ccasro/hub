import type {Resource, SlotAvailability} from "../../../../../../Downloads/b_ntBGpBNoDH6-1771964575451/lib/types";

// Resources for each active venue (publicly visible, only ACTIVE ones)
export const venueResources: Record<string, Resource[]> = {
  "venue-1": [
    {
      id: "res-1a",
      venueId: "venue-1",
      name: "Pista Central",
      description: "Pista principal con cristal panoramico y grada para espectadores. Suelo de cesped artificial de ultima generacion.",
      type: "PADEL",
      slotDurationMinutes: 90,
      status: "ACTIVE",
      rejectReason: null,
      schedules: [
        { dayOfWeek: "MON", openingTime: "07:00", closingTime: "22:00" },
        { dayOfWeek: "TUE", openingTime: "07:00", closingTime: "22:00" },
        { dayOfWeek: "WED", openingTime: "07:00", closingTime: "22:00" },
        { dayOfWeek: "THU", openingTime: "07:00", closingTime: "22:00" },
        { dayOfWeek: "FRI", openingTime: "07:00", closingTime: "23:00" },
        { dayOfWeek: "SAT", openingTime: "08:00", closingTime: "23:00" },
        { dayOfWeek: "SUN", openingTime: "08:00", closingTime: "21:00" },
      ],
      priceRules: [
        { id: "pr-1", dayType: "WEEKDAY", startTime: "07:00", endTime: "14:00", price: 18, currency: "EUR" },
        { id: "pr-2", dayType: "WEEKDAY", startTime: "14:00", endTime: "22:00", price: 24, currency: "EUR" },
        { id: "pr-3", dayType: "WEEKEND", startTime: "08:00", endTime: "23:00", price: 30, currency: "EUR" },
      ],
      images: [
        { id: "rimg-1a", url: "/images/venues/venue-1.jpg", displayOrder: 0 },
      ],
      createdAt: "2025-06-05T10:00:00Z",
      updatedAt: "2025-06-10T12:00:00Z",
    },
    {
      id: "res-1b",
      venueId: "venue-1",
      name: "Pista 2",
      description: "Pista cubierta con cesped artificial de ultima generacion. Iluminacion LED regulable.",
      type: "PADEL",
      slotDurationMinutes: 60,
      status: "ACTIVE",
      rejectReason: null,
      schedules: [
        { dayOfWeek: "MON", openingTime: "08:00", closingTime: "22:00" },
        { dayOfWeek: "TUE", openingTime: "08:00", closingTime: "22:00" },
        { dayOfWeek: "WED", openingTime: "08:00", closingTime: "22:00" },
        { dayOfWeek: "THU", openingTime: "08:00", closingTime: "22:00" },
        { dayOfWeek: "FRI", openingTime: "08:00", closingTime: "23:00" },
        { dayOfWeek: "SAT", openingTime: "09:00", closingTime: "23:00" },
        { dayOfWeek: "SUN", openingTime: "09:00", closingTime: "20:00" },
      ],
      priceRules: [
        { id: "pr-4", dayType: "WEEKDAY", startTime: "08:00", endTime: "14:00", price: 16, currency: "EUR" },
        { id: "pr-5", dayType: "WEEKDAY", startTime: "14:00", endTime: "22:00", price: 22, currency: "EUR" },
        { id: "pr-6", dayType: "WEEKEND", startTime: "09:00", endTime: "23:00", price: 28, currency: "EUR" },
      ],
      images: [],
      createdAt: "2025-06-05T11:00:00Z",
      updatedAt: "2025-06-10T12:00:00Z",
    },
  ],
  "venue-2": [
    {
      id: "res-2a",
      venueId: "venue-2",
      name: "Pista Indoor 1",
      description: "Pista cubierta climatizada con suelo profesional WPT.",
      type: "PADEL",
      slotDurationMinutes: 90,
      status: "ACTIVE",
      rejectReason: null,
      schedules: [
        { dayOfWeek: "MON", openingTime: "06:00", closingTime: "23:00" },
        { dayOfWeek: "TUE", openingTime: "06:00", closingTime: "23:00" },
        { dayOfWeek: "WED", openingTime: "06:00", closingTime: "23:00" },
        { dayOfWeek: "THU", openingTime: "06:00", closingTime: "23:00" },
        { dayOfWeek: "FRI", openingTime: "06:00", closingTime: "00:00" },
        { dayOfWeek: "SAT", openingTime: "07:00", closingTime: "00:00" },
        { dayOfWeek: "SUN", openingTime: "07:00", closingTime: "22:00" },
      ],
      priceRules: [
        { id: "pr-7", dayType: "WEEKDAY", startTime: "06:00", endTime: "14:00", price: 20, currency: "EUR" },
        { id: "pr-8", dayType: "WEEKDAY", startTime: "14:00", endTime: "23:00", price: 28, currency: "EUR" },
        { id: "pr-9", dayType: "WEEKEND", startTime: "07:00", endTime: "00:00", price: 34, currency: "EUR" },
      ],
      images: [
        { id: "rimg-2a", url: "/images/venues/venue-2.jpg", displayOrder: 0 },
      ],
      createdAt: "2025-05-25T10:00:00Z",
      updatedAt: "2025-06-10T12:00:00Z",
    },
    {
      id: "res-2b",
      venueId: "venue-2",
      name: "Pista Indoor 2",
      description: "Pista cubierta con vistas al interior del complejo.",
      type: "PADEL",
      slotDurationMinutes: 60,
      status: "ACTIVE",
      rejectReason: null,
      schedules: [
        { dayOfWeek: "MON", openingTime: "06:00", closingTime: "23:00" },
        { dayOfWeek: "TUE", openingTime: "06:00", closingTime: "23:00" },
        { dayOfWeek: "WED", openingTime: "06:00", closingTime: "23:00" },
        { dayOfWeek: "THU", openingTime: "06:00", closingTime: "23:00" },
        { dayOfWeek: "FRI", openingTime: "06:00", closingTime: "00:00" },
        { dayOfWeek: "SAT", openingTime: "07:00", closingTime: "00:00" },
        { dayOfWeek: "SUN", openingTime: "07:00", closingTime: "22:00" },
      ],
      priceRules: [
        { id: "pr-10", dayType: "WEEKDAY", startTime: "06:00", endTime: "14:00", price: 18, currency: "EUR" },
        { id: "pr-11", dayType: "WEEKDAY", startTime: "14:00", endTime: "23:00", price: 26, currency: "EUR" },
        { id: "pr-12", dayType: "WEEKEND", startTime: "07:00", endTime: "00:00", price: 32, currency: "EUR" },
      ],
      images: [],
      createdAt: "2025-05-25T11:00:00Z",
      updatedAt: "2025-06-10T12:00:00Z",
    },
    {
      id: "res-2c",
      venueId: "venue-2",
      name: "Pista Tenis Cubierta",
      description: "Pista de tenis indoor con superficie dura profesional.",
      type: "TENNIS",
      slotDurationMinutes: 60,
      status: "ACTIVE",
      rejectReason: null,
      schedules: [
        { dayOfWeek: "MON", openingTime: "07:00", closingTime: "22:00" },
        { dayOfWeek: "TUE", openingTime: "07:00", closingTime: "22:00" },
        { dayOfWeek: "WED", openingTime: "07:00", closingTime: "22:00" },
        { dayOfWeek: "THU", openingTime: "07:00", closingTime: "22:00" },
        { dayOfWeek: "FRI", openingTime: "07:00", closingTime: "22:00" },
        { dayOfWeek: "SAT", openingTime: "08:00", closingTime: "22:00" },
        { dayOfWeek: "SUN", openingTime: "08:00", closingTime: "20:00" },
      ],
      priceRules: [
        { id: "pr-13", dayType: "WEEKDAY", startTime: "07:00", endTime: "22:00", price: 35, currency: "EUR" },
        { id: "pr-14", dayType: "WEEKEND", startTime: "08:00", endTime: "22:00", price: 42, currency: "EUR" },
      ],
      images: [],
      createdAt: "2025-05-26T10:00:00Z",
      updatedAt: "2025-06-10T12:00:00Z",
    },
  ],
  "venue-3": [
    {
      id: "res-3a",
      venueId: "venue-3",
      name: "Pista Mar",
      description: "Pista con vistas al mar Mediterraneo. Ambiente unico para jugar al atardecer.",
      type: "PADEL",
      slotDurationMinutes: 90,
      status: "ACTIVE",
      rejectReason: null,
      schedules: [
        { dayOfWeek: "MON", openingTime: "08:00", closingTime: "21:00" },
        { dayOfWeek: "TUE", openingTime: "08:00", closingTime: "21:00" },
        { dayOfWeek: "WED", openingTime: "08:00", closingTime: "21:00" },
        { dayOfWeek: "THU", openingTime: "08:00", closingTime: "21:00" },
        { dayOfWeek: "FRI", openingTime: "08:00", closingTime: "22:00" },
        { dayOfWeek: "SAT", openingTime: "09:00", closingTime: "22:00" },
        { dayOfWeek: "SUN", openingTime: "09:00", closingTime: "20:00" },
      ],
      priceRules: [
        { id: "pr-15", dayType: "WEEKDAY", startTime: "08:00", endTime: "21:00", price: 22, currency: "EUR" },
        { id: "pr-16", dayType: "WEEKEND", startTime: "09:00", endTime: "22:00", price: 30, currency: "EUR" },
      ],
      images: [
        { id: "rimg-3a", url: "/images/venues/venue-3.jpg", displayOrder: 0 },
      ],
      createdAt: "2025-04-20T10:00:00Z",
      updatedAt: "2025-06-12T12:00:00Z",
    },
  ],
  "venue-4": [
    {
      id: "res-4a",
      venueId: "venue-4",
      name: "Pista Noche 1",
      description: "Pista con iluminacion LED RGB y sistema de sonido ambiental.",
      type: "PADEL",
      slotDurationMinutes: 60,
      status: "ACTIVE",
      rejectReason: null,
      schedules: [
        { dayOfWeek: "MON", openingTime: "16:00", closingTime: "02:00" },
        { dayOfWeek: "TUE", openingTime: "16:00", closingTime: "02:00" },
        { dayOfWeek: "WED", openingTime: "16:00", closingTime: "02:00" },
        { dayOfWeek: "THU", openingTime: "16:00", closingTime: "02:00" },
        { dayOfWeek: "FRI", openingTime: "16:00", closingTime: "03:00" },
        { dayOfWeek: "SAT", openingTime: "16:00", closingTime: "03:00" },
        { dayOfWeek: "SUN", openingTime: "16:00", closingTime: "00:00" },
      ],
      priceRules: [
        { id: "pr-17", dayType: "WEEKDAY", startTime: "16:00", endTime: "20:00", price: 20, currency: "EUR" },
        { id: "pr-18", dayType: "WEEKDAY", startTime: "20:00", endTime: "02:00", price: 28, currency: "EUR" },
        { id: "pr-19", dayType: "WEEKEND", startTime: "16:00", endTime: "03:00", price: 32, currency: "EUR" },
      ],
      images: [
        { id: "rimg-4a", url: "/images/venues/venue-4.jpg", displayOrder: 0 },
      ],
      createdAt: "2025-03-15T18:00:00Z",
      updatedAt: "2025-06-08T20:00:00Z",
    },
    {
      id: "res-4b",
      venueId: "venue-4",
      name: "Pista Noche 2",
      description: "Segunda pista nocturna con suelo fluorescente.",
      type: "PADEL",
      slotDurationMinutes: 60,
      status: "ACTIVE",
      rejectReason: null,
      schedules: [
        { dayOfWeek: "MON", openingTime: "16:00", closingTime: "02:00" },
        { dayOfWeek: "TUE", openingTime: "16:00", closingTime: "02:00" },
        { dayOfWeek: "WED", openingTime: "16:00", closingTime: "02:00" },
        { dayOfWeek: "THU", openingTime: "16:00", closingTime: "02:00" },
        { dayOfWeek: "FRI", openingTime: "16:00", closingTime: "03:00" },
        { dayOfWeek: "SAT", openingTime: "16:00", closingTime: "03:00" },
        { dayOfWeek: "SUN", openingTime: "16:00", closingTime: "00:00" },
      ],
      priceRules: [
        { id: "pr-20", dayType: "WEEKDAY", startTime: "16:00", endTime: "20:00", price: 18, currency: "EUR" },
        { id: "pr-21", dayType: "WEEKDAY", startTime: "20:00", endTime: "02:00", price: 26, currency: "EUR" },
        { id: "pr-22", dayType: "WEEKEND", startTime: "16:00", endTime: "03:00", price: 30, currency: "EUR" },
      ],
      images: [],
      createdAt: "2025-03-15T18:30:00Z",
      updatedAt: "2025-06-08T20:00:00Z",
    },
  ],
  "venue-5": [
    {
      id: "res-5a",
      venueId: "venue-5",
      name: "Pista Skyline",
      description: "Pista en la azotea con vistas panoramicas a Bilbao. Techo retractil.",
      type: "PADEL",
      slotDurationMinutes: 90,
      status: "ACTIVE",
      rejectReason: null,
      schedules: [
        { dayOfWeek: "MON", openingTime: "09:00", closingTime: "21:00" },
        { dayOfWeek: "TUE", openingTime: "09:00", closingTime: "21:00" },
        { dayOfWeek: "WED", openingTime: "09:00", closingTime: "21:00" },
        { dayOfWeek: "THU", openingTime: "09:00", closingTime: "21:00" },
        { dayOfWeek: "FRI", openingTime: "09:00", closingTime: "22:00" },
        { dayOfWeek: "SAT", openingTime: "10:00", closingTime: "22:00" },
        { dayOfWeek: "SUN", openingTime: "10:00", closingTime: "20:00" },
      ],
      priceRules: [
        { id: "pr-23", dayType: "WEEKDAY", startTime: "09:00", endTime: "21:00", price: 26, currency: "EUR" },
        { id: "pr-24", dayType: "WEEKEND", startTime: "10:00", endTime: "22:00", price: 35, currency: "EUR" },
      ],
      images: [
        { id: "rimg-5a", url: "/images/venues/venue-5.jpg", displayOrder: 0 },
      ],
      createdAt: "2025-02-25T09:00:00Z",
      updatedAt: "2025-06-05T11:30:00Z",
    },
  ],
  "venue-6": [
    {
      id: "res-6a",
      venueId: "venue-6",
      name: "Pista Principal",
      description: "Pista principal rodeada de jardines con cesped artificial premium.",
      type: "PADEL",
      slotDurationMinutes: 90,
      status: "ACTIVE",
      rejectReason: null,
      schedules: [
        { dayOfWeek: "MON", openingTime: "07:00", closingTime: "22:00" },
        { dayOfWeek: "TUE", openingTime: "07:00", closingTime: "22:00" },
        { dayOfWeek: "WED", openingTime: "07:00", closingTime: "22:00" },
        { dayOfWeek: "THU", openingTime: "07:00", closingTime: "22:00" },
        { dayOfWeek: "FRI", openingTime: "07:00", closingTime: "23:00" },
        { dayOfWeek: "SAT", openingTime: "08:00", closingTime: "23:00" },
        { dayOfWeek: "SUN", openingTime: "08:00", closingTime: "21:00" },
      ],
      priceRules: [
        { id: "pr-25", dayType: "WEEKDAY", startTime: "07:00", endTime: "14:00", price: 15, currency: "EUR" },
        { id: "pr-26", dayType: "WEEKDAY", startTime: "14:00", endTime: "22:00", price: 20, currency: "EUR" },
        { id: "pr-27", dayType: "WEEKEND", startTime: "08:00", endTime: "23:00", price: 26, currency: "EUR" },
      ],
      images: [
        { id: "rimg-6a", url: "/images/venues/venue-6.jpg", displayOrder: 0 },
      ],
      createdAt: "2025-01-20T07:00:00Z",
      updatedAt: "2025-06-14T13:00:00Z",
    },
    {
      id: "res-6b",
      venueId: "venue-6",
      name: "Pista Infantil",
      description: "Pista adaptada para ninos y clases de iniciacion.",
      type: "PADEL",
      slotDurationMinutes: 60,
      status: "ACTIVE",
      rejectReason: null,
      schedules: [
        { dayOfWeek: "MON", openingTime: "09:00", closingTime: "20:00" },
        { dayOfWeek: "TUE", openingTime: "09:00", closingTime: "20:00" },
        { dayOfWeek: "WED", openingTime: "09:00", closingTime: "20:00" },
        { dayOfWeek: "THU", openingTime: "09:00", closingTime: "20:00" },
        { dayOfWeek: "FRI", openingTime: "09:00", closingTime: "20:00" },
        { dayOfWeek: "SAT", openingTime: "09:00", closingTime: "21:00" },
        { dayOfWeek: "SUN", openingTime: "09:00", closingTime: "19:00" },
      ],
      priceRules: [
        { id: "pr-28", dayType: "WEEKDAY", startTime: "09:00", endTime: "20:00", price: 12, currency: "EUR" },
        { id: "pr-29", dayType: "WEEKEND", startTime: "09:00", endTime: "21:00", price: 16, currency: "EUR" },
      ],
      images: [],
      createdAt: "2025-01-20T08:00:00Z",
      updatedAt: "2025-06-14T13:00:00Z",
    },
  ],
};

// Helper to generate slot availability from a resource's schedules and price rules for a given date
export function generateSlots(resource: Resource, date: string): SlotAvailability[] {
  const d = new Date(date);
  const days: Record<number, Resource["schedules"][0]["dayOfWeek"]> = {
    0: "SUN", 1: "MON", 2: "TUE", 3: "WED", 4: "THU", 5: "FRI", 6: "SAT",
  };
  const dayOfWeek = days[d.getDay()];
  const schedule = resource.schedules.find((s) => s.dayOfWeek === dayOfWeek);
  if (!schedule) return [];

  const isWeekend = dayOfWeek === "SAT" || dayOfWeek === "SUN";
  const openH = parseInt(schedule.openingTime.split(":")[0]);
  const closeH = parseInt(schedule.closingTime.split(":")[0]) || 24;
  const durationMin = resource.slotDurationMinutes;
  const slots: SlotAvailability[] = [];

  for (let minutes = openH * 60; minutes + durationMin <= closeH * 60; minutes += durationMin) {
    const startH = Math.floor(minutes / 60);
    const startM = minutes % 60;
    const endMinutes = minutes + durationMin;
    const endH = Math.floor(endMinutes / 60);
    const endM = endMinutes % 60;

    const startTime = `${String(startH).padStart(2, "0")}:${String(startM).padStart(2, "0")}:00`;
    const endTime = `${String(endH).padStart(2, "0")}:${String(endM).padStart(2, "0")}:00`;

    // Find matching price rule
    let price = 20; // default
    for (const rule of resource.priceRules) {
      const ruleStartMin = parseInt(rule.startTime.split(":")[0]) * 60 + parseInt(rule.startTime.split(":")[1]);
      const ruleEndMin = (parseInt(rule.endTime.split(":")[0]) || 24) * 60 + parseInt(rule.endTime.split(":")[1]);
      
      const matchesDay =
        rule.dayType === dayOfWeek ||
        (rule.dayType === "WEEKDAY" && !isWeekend) ||
        (rule.dayType === "WEEKEND" && isWeekend);

      if (matchesDay && minutes >= ruleStartMin && minutes < ruleEndMin) {
        price = rule.price;
        break;
      }
    }

    // Randomly mark some past-ish or popular slots as unavailable
    const isPast = d < new Date("2026-02-24");
    const randomBooked = Math.random() > 0.7;
    const available = !isPast && !randomBooked;

    slots.push({ startTime, endTime, available, price, currency: "EUR" });
  }

  return slots;
}
