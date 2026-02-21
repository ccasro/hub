package com.ccasro.hub.modules.booking.usecases;

import com.ccasro.hub.modules.booking.domain.Booking;
import com.ccasro.hub.modules.booking.domain.ports.out.BookingRepositoryPort;
import com.ccasro.hub.shared.application.ports.CurrentUserProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMyBookingsService {

  private final BookingRepositoryPort bookingRepository;
  private final CurrentUserProvider currentUser;

  @Transactional(readOnly = true)
  public List<Booking> execute() {
    return bookingRepository.findByPlayerId(currentUser.getUserId());
  }
}
