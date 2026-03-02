package com.ccasro.hub.modules.booking.infrastructure.api.dto;

import com.ccasro.hub.modules.booking.application.dto.MyBookingView;
import com.ccasro.hub.modules.booking.application.dto.MyVenueBookingView;
import com.ccasro.hub.modules.booking.domain.Booking;
import java.util.List;

public final class BookingResponseMapper {

  private BookingResponseMapper() {}

  public static BookingResponse from(Booking booking) {
    return BookingResponse.from(booking);
  }

  public static BookingResponse from(MyBookingView view) {
    return BookingResponse.from(view);
  }

  public static BookingResponse from(MyVenueBookingView view) {
    return BookingResponse.from(view);
  }

  public static List<BookingResponse> fromBookings(List<Booking> bookings) {
    return bookings.stream().map(BookingResponse::from).toList();
  }

  public static List<BookingResponse> fromMyBookings(List<MyBookingView> bookings) {
    return bookings.stream().map(BookingResponse::from).toList();
  }

  public static List<BookingResponse> fromVenueBookings(List<MyVenueBookingView> bookings) {
    return bookings.stream().map(BookingResponse::from).toList();
  }
}
