# Product Requirements Document — SportsHub

**Version:** 1.0
**Date:** 2026-03-10
**Status:** Active Development

---

## 1. Overview

SportsHub is a **sports venue booking and intelligent matchmaking platform** targeting the Spanish market. It connects sports facility owners with players looking to book courts and find opponents or partners for friendly matches. The initial focus is on **padel**, with extensibility to tennis, squash, and badminton.

### 1.1 Problem Statement

- **Players** struggle to find available courts and opponents of matching skill levels for spontaneous games.
- **Venue owners** lack a unified digital platform to manage court availability, schedules, pricing, and bookings.
- Current fragmented solutions force players to use WhatsApp groups or word of mouth to find matches.

### 1.2 Vision

A single platform where any player can book a court, find opponents at their level, and show up to play — all in minutes.

---

## 2. Target Users

### Player
An individual who practices padel (or similar racket sports) and wants to:
- Find and book available courts near them.
- Create or join matches with other players of similar skill.
- Manage their bookings and match history.

### Venue Owner
A business owner or operator of a sports facility who wants to:
- List their courts with schedules, pricing, and photos.
- Accept online bookings and receive payments.
- Monitor booking activity across all their venues.

### Admin
An internal operator who manages the platform's quality and compliance:
- Review and approve venue/resource listings.
- Moderate users (ban, change roles, handle owner applications).
- Monitor system-wide activity and statistics.

---

## 3. User Stories

### Player
- As a player, I can register and set up my profile with skill level and preferred sport.
- As a player, I can search for venues near my location and view available time slots.
- As a player, I can book a court slot and pay online via card.
- As a player, I can cancel a booking (at least 24 hours in advance).
- As a player, I can search for open match requests near me filtered by sport, date, time, format, and skill level.
- As a player, I can create a match request for a booked slot to find opponents/partners.
- As a player, I can invite specific players to my match via a shareable link.
- As a player, I can join an open match request or accept/decline invitations.
- As a player, I can check in to a match via GPS when I arrive at the venue.
- As a player, I can report an unexpected absence and trigger substitute invitations.
- As a player, I can leave a match I've joined (at least 48 hours before the start).
- As a player, I can apply to become a venue owner.

### Venue Owner
- As an owner, I can create and manage venues with photos, address, and geolocation.
- As an owner, I can create resources (courts) within my venues and set their type and slot duration.
- As an owner, I can configure weekly availability schedules per resource.
- As an owner, I can define flexible pricing rules (weekday/weekend, time range, per resource).
- As an owner, I can upload photos for each court.
- As an owner, I can view all bookings across my venues.
- As an owner, I can suspend or reactivate my venues and resources.

### Admin
- As an admin, I can view a real-time dashboard with platform statistics.
- As an admin, I can approve or reject venue and resource listings after review.
- As an admin, I can approve or reject owner role requests from players.
- As an admin, I can suspend venues or users who violate platform rules.
- As an admin, I can view, manage, and cancel any booking.

---

## 4. Feature Specifications

### 4.1 Identity & Access Management

- Authentication via **Auth0** (OAuth2 / JWT). No username/password stored in the application.
- On first login, a `user_profile` record is automatically created.
- **Roles:** `PLAYER` (default), `OWNER`, `ADMIN`.
- Owner role requires admin approval. A player submits an upgrade request; admin approves or rejects it.
- Banned players (due to repeated no-shows) cannot create or join matches.

### 4.2 Venue Management

- Venues have: name, description, address (city, street, postal code), geolocation, status.
- **Venue statuses:** `PENDING_REVIEW` → `ACTIVE` → `SUSPENDED` / `REJECTED`.
- New venues are submitted for admin review before becoming publicly visible.
- Owners can upload multiple photos per venue (via Cloudinary, signed upload).
- Owners can manage multiple venues from one account.

### 4.3 Resource (Court) Management

- Each venue can have multiple resources with a type (PADEL, TENNIS, SQUASH, BADMINTON) and slot duration (e.g., 60 or 90 minutes).
- Each resource has a weekly schedule (per day of week, opening/closing times).
- Pricing is rule-based: day type (WEEKDAY / WEEKEND / specific day), time range, price in EUR.
- Resources go through the same `PENDING_REVIEW` → `ACTIVE` approval flow as venues.

### 4.4 Booking

- Players browse a venue's resource and select an available date + time slot.
- The slot availability is computed dynamically from the schedule and existing confirmed bookings.
- Booking creates a **Stripe Payment Intent**; the player has **5 minutes** to complete payment before the hold expires.
- Confirmed bookings are visible in the player's dashboard and the owner's booking panel.
- Players can cancel confirmed bookings up to **24 hours before** the slot start.
- Slots with a linked active match request are shown as unavailable for direct booking.
- An exclusion constraint at the DB level prevents double-booking (GiST + tsrange overlap).

### 4.5 Matchmaking

#### Creating a Match
- The player first books a slot; then creates a match request from that booking.
- Match formats: `ONE_VS_ONE` (singles) or `TWO_VS_TWO` (doubles).
- The organizer sets: skill level requirement, a geographic search center and radius, price per player, and expiration policy.
- The organizer must pay their share within **30 minutes** of match creation or the match is cancelled.
- Match requests expire automatically **24 hours before** the slot if not fully filled.
- A player may have at most **2 concurrent active matches**.
- Match must be created at least **48 hours before** the slot.

#### Joining a Match
- Players search for open matches by location, date, time window, format, and skill level.
- They can join via the search results or via a shareable invitation link (`/match/join/{token}`).
- On acceptance, the guest pays their share of the slot cost.
- The match transitions to `FULL` once all player spots are taken.

#### Invitations
- The organizer can generate a unique invitation link for the match.
- Invited players receive the link and can accept or decline.
- Declined or expired invitations do not block the match.

#### Absence & Substitutes
- If a confirmed player can no longer attend, they report an absence.
- The system sends substitute invitations to nearby eligible players.
- The absent player's no-show counter is not incremented if they report in advance.

#### GPS Check-In
- Players check in to a match within **30 minutes** before/after the start time.
- The device's GPS coordinates must be within **200 meters** of the venue (accuracy ≤ 100 m).
- Players who do not check in are flagged as no-shows by the background job.

#### No-Show Policy
- **3 no-shows** → player is banned from matchmaking for **30 days**.
- The ban is enforced at the API level when attempting to create or join matches.

### 4.6 Notifications (Email)

- Transactional emails sent via **Brevo** (formerly Sendinblue), rendered as HTML with Thymeleaf templates.
- Triggered by domain events: booking confirmed, booking cancelled, booking expired, match full, match invitations sent.

### 4.7 Media Uploads

- Players and owners upload images via **Cloudinary**.
- The backend generates a signed upload token; the client uploads directly to Cloudinary.
- Image URLs are stored in the database post-upload.

### 4.8 Admin Back-Office

- Dashboard: total users, active venues, bookings, and match requests.
- User management: list, filter, toggle active state, change roles, approve/reject owner requests.
- Venue pipeline: list all venues with status filter; approve, reject, or suspend.
- Resource pipeline: same as venues.
- Booking management: view all bookings, cancel any booking.

---

## 5. Technical Architecture

### 5.1 Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 3.5.x |
| Database | PostgreSQL + PostGIS, pgcrypto |
| ORM | Spring Data JPA + Hibernate Spatial |
| Migrations | Flyway |
| Auth | Auth0 (JWT, OAuth2 Resource Server) |
| Payments | Stripe (Payment Intents) |
| Email | Brevo Transactional API + Thymeleaf |
| Media | Cloudinary (signed uploads) |
| Cache | Caffeine (in-process, short TTL) |
| API Docs | SpringDoc / Swagger UI |
| Frontend | Next.js 16 (React 19, TypeScript) |
| UI Library | shadcn/ui, Tailwind CSS v4 |
| Frontend Auth | @auth0/nextjs-auth0 v4 |

### 5.2 Architecture Pattern

The backend follows **Hexagonal Architecture (Ports & Adapters)** within a **Modular Monolith**:

```
modules/
  iam/         — User identity and roles
  venue/       — Venue management
  resource/    — Court/resource management
  booking/     — Booking and payments
  matching/    — Match requests, invitations, check-in
  media/       — Cloudinary signed uploads
  admin/       — Admin back-office
  security/    — Auth filters and authorization beans
```

Each module follows:
- `domain/` — aggregates, value objects, port interfaces (no framework dependencies)
- `application/` — use case services and DTOs
- `infrastructure/api/` — REST controllers (inbound adapters)
- `infrastructure/persistence/` — JPA repositories (outbound adapters)

### 5.3 Background Jobs

| Job | Schedule | Purpose |
|---|---|---|
| `ExpirePaymentHoldsJob` | Every 1 minute | Expires PENDING_PAYMENT bookings past their hold window |
| `ExpireMatchRequestsJob` | Every 5 minutes | Cancels unpaid or unfilled match requests |
| `DetectNoShowsJob` | Every 15 minutes | Detects players who missed check-in; applies ban logic |

### 5.4 Data Integrity

- Overlapping bookings prevented by a **PostgreSQL GiST exclusion constraint** on `(resource_id, tsrange)`.
- Payments tracked via Stripe Payment Intents; local `payment` table mirrors intent status.
- Soft-delete semantics: players removed from matches via `left_at` timestamp, not hard deletes.

---

## 6. Non-Functional Requirements

| Requirement | Target |
|---|---|
| Availability | 99.5% uptime (SaaS hosting) |
| Response time | p95 < 500 ms for read endpoints |
| Slot availability cache | 2-minute TTL (Caffeine) |
| Venue list cache | 10-minute TTL (Caffeine) |
| Payment hold window | 5 minutes (configurable) |
| Security | JWT-only (stateless), HTTPS enforced, CSP + HSTS headers |
| Geo precision | PostGIS geography type (meters-accurate distance queries) |
| Code quality | Spotless (Google Java Format) enforced at build time |
| Test coverage | Integration tests via Testcontainers (PostgreSQL) |
| Observability | Spring Actuator: `/actuator/health`, `/actuator/info`, `/actuator/mappings` |

---

## 7. Business Rules Summary

| Rule | Value |
|---|---|
| Booking cancellation minimum notice | 24 hours |
| Payment hold duration | 5 minutes |
| Organizer payment window | 30 minutes |
| Match creation advance notice | ≥ 48 hours before slot |
| Match expiration before slot | 24 hours |
| Player leave notice | ≥ 48 hours before slot |
| GPS check-in window | ±30 minutes from start |
| GPS check-in radius | 200 meters |
| GPS accuracy requirement | ≤ 100 meters |
| No-show ban threshold | 3 no-shows |
| No-show ban duration | 30 days |
| Max concurrent active matches | 2 per player |

---

## 8. Out of Scope (Current Version)

- Native mobile apps (iOS / Android) — web-only for now.
- Real-time chat or in-app messaging between players.
- Tournament or league management.
- Recurring/subscription bookings for venue members.
- Multi-currency support (EUR only).
- Multi-language UI (Spanish market primary focus).
- Venue owner revenue payouts / split payment flows (Stripe Connect not implemented).
- Social features: player profiles visible to others, friends lists, ratings.

---

## 9. Glossary

| Term | Definition |
|---|---|
| Resource | A bookable court or facility within a venue |
| Match Request | An open slot where the organizer seeks opponents/partners |
| Invitation Token | A unique shareable link for joining a specific match |
| Check-In | GPS-verified confirmation that a player arrived at the venue |
| No-Show | A player who confirmed attendance but did not check in |
| Price Rule | A time/day-based pricing configuration for a resource |
| Slot | A single bookable time block derived from a resource's schedule |