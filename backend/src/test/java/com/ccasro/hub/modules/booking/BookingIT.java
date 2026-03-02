package com.ccasro.hub.modules.booking;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ccasro.hub.BaseIT;
import com.jayway.jsonpath.JsonPath;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class BookingIT extends BaseIT {

  private String resourceId;

  @BeforeEach
  void setup() throws Exception {
    givenOwner();
    givenPlayer();
    givenPlayer2();

    String venueResponse =
        mvc.perform(
                post("/api/owner/venues")
                    .header("Authorization", bearer(OWNER_TOKEN))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                    {
                      "name": "Test Club",
                      "street": "Carrer Test 1",
                      "city": "Barcelona",
                      "country": "España",
                      "postalCode": "08001",
                      "latitude": 41.3851,
                      "longitude": 2.1734
                    }
                    """))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String venueId = JsonPath.read(venueResponse, "$.id");

    givenAdmin();
    mvc.perform(
            patch("/api/admin/venues/" + venueId + "/approve")
                .header("Authorization", bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk());

    String resourceResponse =
        mvc.perform(
                post("/api/owner/venues/" + venueId + "/resources")
                    .header("Authorization", bearer(OWNER_TOKEN))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                    {
                      "name": "Pista Padel 1",
                      "type": "PADEL",
                      "slotDurationMinutes": 90
                    }
                    """))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    resourceId = JsonPath.read(resourceResponse, "$.id");

    mvc.perform(
            patch("/api/admin/resources/" + resourceId + "/approve")
                .header("Authorization", bearer(ADMIN_TOKEN)))
        .andExpect(status().isOk());

    for (String day : new String[] {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"}) {
      String open = day.equals("SAT") || day.equals("SUN") ? "08:00" : "09:00";
      String close = day.equals("SUN") ? "21:00" : "22:00";
      mvc.perform(
              put("/api/owner/resources/" + resourceId + "/schedules")
                  .header("Authorization", bearer(OWNER_TOKEN))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      """
                  {"dayOfWeek": "%s", "openingTime": "%s", "closingTime": "%s"}
                  """
                          .formatted(day, open, close)))
          .andExpect(status().isOk());
    }

    mvc.perform(
            post("/api/owner/resources/" + resourceId + "/price-rules")
                .header("Authorization", bearer(OWNER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {"dayType": "WEEKDAY", "startTime": "09:00", "endTime": "22:00",
                 "price": 18.00, "currency": "EUR"}
                """))
        .andExpect(status().isOk());

    mvc.perform(
            post("/api/owner/resources/" + resourceId + "/price-rules")
                .header("Authorization", bearer(OWNER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {"dayType": "WEEKEND", "startTime": "08:00", "endTime": "22:00",
                 "price": 22.00, "currency": "EUR"}
                """))
        .andExpect(status().isOk());
  }

  @Test
  void booking_sin_token_devuelve_401() throws Exception {
    mvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void booking_slot_valido_devuelve_201_y_crea_booking() throws Exception {
    String body =
        """
        {"resourceId": "%s", "bookingDate": "%s", "startTime": "09:00"}
        """
            .formatted(resourceId, nextWeekday());

    String response =
        mvc.perform(
                post("/api/bookings")
                    .header("Authorization", bearer(PLAYER_TOKEN))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.booking.status").value("PENDING_PAYMENT"))
            .andExpect(jsonPath("$.clientSecret").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String bookingId = JsonPath.read(response, "$.booking.id");
    assertThat(bookingId).isNotNull();
  }

  @Test
  void booking_slot_ocupado_devuelve_409() throws Exception {
    String body =
        """
        {"resourceId": "%s", "bookingDate": "%s", "startTime": "09:00"}
        """
            .formatted(resourceId, nextWeekday());

    mvc.perform(
            post("/api/bookings")
                .header("Authorization", bearer(PLAYER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isCreated());

    mvc.perform(
            post("/api/bookings")
                .header("Authorization", bearer(PLAYER2_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isConflict());
  }

  @Test
  void mis_bookings_devuelve_lista() throws Exception {
    mvc.perform(get("/api/bookings/my").header("Authorization", bearer(PLAYER_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  void cancelar_booking_propio_devuelve_200() throws Exception {
    String body =
        """
        {"resourceId": "%s", "bookingDate": "%s", "startTime": "10:30"}
        """
            .formatted(resourceId, nextWeekday());

    String response =
        mvc.perform(
                post("/api/bookings")
                    .header("Authorization", bearer(PLAYER_TOKEN))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String bookingId = JsonPath.read(response, "$.booking.id");

    mvc.perform(
            patch("/api/bookings/" + bookingId + "/cancel")
                .header("Authorization", bearer(PLAYER_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {"reason": "test cancel"}
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CANCELLED"));
  }

  @Test
  void cancelar_booking_ajeno_devuelve_403() throws Exception {
    String body =
        """
        {"resourceId": "%s", "bookingDate": "%s", "startTime": "12:00"}
        """
            .formatted(resourceId, nextWeekday());

    String response =
        mvc.perform(
                post("/api/bookings")
                    .header("Authorization", bearer(PLAYER_TOKEN))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String bookingId = JsonPath.read(response, "$.booking.id");

    mvc.perform(
            patch("/api/bookings/" + bookingId + "/cancel")
                .header("Authorization", bearer(PLAYER2_TOKEN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {"reason": "trying to cancel someone else's booking"}
                """))
        .andExpect(status().isForbidden());
  }

  private LocalDate nextWeekday() {
    LocalDate d = LocalDate.now().plusDays(3);
    while (d.getDayOfWeek().getValue() > 5) d = d.plusDays(1);
    return d;
  }
}
