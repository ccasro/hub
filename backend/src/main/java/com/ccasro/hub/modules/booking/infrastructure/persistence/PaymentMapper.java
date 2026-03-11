package com.ccasro.hub.modules.booking.infrastructure.persistence;

import com.ccasro.hub.modules.booking.domain.Payment;
import com.ccasro.hub.modules.booking.domain.valueobjects.BookingId;
import com.ccasro.hub.modules.booking.domain.valueobjects.PaymentId;
import com.ccasro.hub.shared.domain.valueobjects.UserId;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

  public Payment toDomain(PaymentEntity e) {
    return Payment.reconstitute(
        PaymentId.of(e.getId()),
        BookingId.of(e.getBookingId()),
        e.getPlayerId() != null ? new UserId(e.getPlayerId()) : null,
        e.getStripePaymentIntentId(),
        e.getAmount(),
        e.getCurrency(),
        e.getStatus(),
        e.getCreatedAt(),
        e.getUpdatedAt());
  }

  public PaymentEntity toEntity(Payment d) {
    PaymentEntity e = new PaymentEntity();
    fill(e, d);
    return e;
  }

  public void fill(PaymentEntity e, Payment d) {
    e.setId(d.getId().value());
    e.setBookingId(d.getBookingId().value());
    e.setPlayerId(d.getPlayerId() != null ? d.getPlayerId().value() : null);
    e.setStripePaymentIntentId(d.getStripePaymentIntentId());
    e.setAmount(d.getAmount());
    e.setCurrency(d.getCurrency());
    e.setStatus(d.getStatus());
    e.setCreatedAt(d.getCreatedAt());
    e.setUpdatedAt(d.getUpdatedAt());
  }
}
