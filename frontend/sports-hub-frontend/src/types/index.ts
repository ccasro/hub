export interface UserProfile {
    id: string;
    email: string;
    displayName: string | null;
    description: string | null;
    phoneNumber: string | null;
    avatarUrl: string | null;
    role: "PLAYER" | "OWNER" | "ADMIN";
    ownerRequestStatus: "NONE" | "PENDING" | "APPROVED" | "REJECTED" | null;
    preferredSport: "PADEL" | "TENNIS" | "SQUASH" | "BADMINTON" | null;
    skillLevel: "BEGINNER" | "INTERMEDIATE" | "ADVANCED" | null;
    city: string | null;
    countryCode: string | null;
    onboardingCompleted: boolean;
    lastLoginAt: string;
}

export interface AdminUserProfile extends UserProfile {
    active: boolean;
    emailVerified: boolean;
    createdAt: string;
    updatedAt: string;
}

export interface Venue {
    id: string;
    ownerId: string;
    name: string;
    description: string | null;
    street: string | null;
    city: string | null;
    country: string | null;
    postalCode: string | null;
    latitude: number | null;
    longitude: number | null;
    status: "PENDING_REVIEW" | "ACTIVE" | "SUSPENDED" | "REJECTED";
    rejectReason: string | null;
    images: VenueImage[];
    createdAt: string;
    updatedAt: string;
}

export interface VenueImage {
    id: string;
    url: string;
    displayOrder: number;
}

export interface Resource {
    id: string;
    venueId: string;
    name: string;
    description: string | null;
    type: "PADEL" | "TENNIS" | "SQUASH" | "BADMINTON" | "OTHER";
    slotDurationMinutes: number;
    status: "PENDING_REVIEW" | "ACTIVE" | "SUSPENDED" | "REJECTED";
    rejectReason: string | null;
    schedules: DaySchedule[];
    priceRules: PriceRule[];
    images: ResourceImage[];
    createdAt: string;
    updatedAt: string;
}

export interface DaySchedule {
    dayOfWeek: "MON" | "TUE" | "WED" | "THU" | "FRI" | "SAT" | "SUN";
    openingTime: string;   // "HH:mm:ss"
    closingTime: string;   // "HH:mm:ss"
}

export interface PriceRule {
    id: string;
    dayType: "WEEKDAY" | "WEEKEND" | "MON" | "TUE" | "WED" | "THU" | "FRI" | "SAT" | "SUN";
    startTime: string;     // "HH:mm:ss"
    endTime: string;       // "HH:mm:ss"
    price: number;
    currency: string;
}

export interface ResourceImage {
    id: string;
    url: string;
    displayOrder: number;
}

export interface SlotAvailability {
    startTime: string;
    endTime: string;
    available: boolean;
    price: number;
    currency: string;
}

export interface Booking {
    id: string;
    resourceId: string;
    playerId: string;
    bookingDate: string;
    startTime: string;
    endTime: string;
    pricePaid: number;
    currency: string;
    status: "PENDING_PAYMENT" | "CONFIRMED" | "CANCELLED";
    paymentStatus: "PENDING" | "PAID" | "FAILED" | "REFUNDED";
    expiresAt: string | null;
    cancelledAt: string | null;
    cancelReason: string | null;
    createdAt: string;
}

export interface CreateBookingResponse {
    booking: Booking;
    clientSecret: string;   // para el fake payment o Stripe
}

export interface Payment {
    id: string;
    bookingId: string;
    stripePaymentIntentId: string;
    amount: number;
    currency: string;
    status: "PENDING" | "PAID" | "FAILED" | "REFUNDED";
    createdAt: string;
    updatedAt: string;
}

export interface CreateVenueRequest {
    name: string;
    description?: string;
    street: string;
    city: string;
    country: string;
    postalCode?: string;
    latitude: number;
    longitude: number;
}

export interface CreateResourceRequest {
    name: string;
    description?: string;
    type: Resource["type"];
    slotDurationMinutes: 60 | 90 | 120;
}

export interface SetScheduleRequest {
    dayOfWeek: DaySchedule["dayOfWeek"];
    openingTime: string;
    closingTime: string;
}

export interface AddPriceRuleRequest {
    dayType: PriceRule["dayType"];
    startTime: string;
    endTime: string;
    price: number;
    currency: string;
}

export interface CreateBookingRequest {
    resourceId: string;
    bookingDate: string;
    startTime: string;
}

export interface CancelBookingRequest {
    reason?: string;
}

export interface UpdateMeRequest {
    displayName?: string;
    description?: string;
    phoneNumber?: string;
    city?: string;
    countryCode?: string;
    preferredSport?: UserProfile["preferredSport"];
    skillLevel?: UserProfile["skillLevel"];
}

export interface UpdateAvatarRequest {
    url: string;
    publicId: string;
}

export interface ChangeRoleRequest {
    role: UserProfile["role"];
}

export interface RejectRequest {
    reason: string;
}

export interface FakePaymentRequest {
    amount: number;
    currency: string;
}

export interface Page<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
}

export interface ApiErrorResponse {
    message: string;
}