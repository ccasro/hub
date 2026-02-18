package com.ccasro.hub.modules.catalog.application.usecase.resource;

import com.ccasro.hub.common.application.ports.CurrentUserProvider;
import com.ccasro.hub.common.domain.model.vo.Description;
import com.ccasro.hub.common.domain.model.vo.Money;
import com.ccasro.hub.modules.catalog.application.command.CreateResourceCommand;
import com.ccasro.hub.modules.catalog.application.policy.VenueOwnershipPolicy;
import com.ccasro.hub.modules.catalog.domain.model.resource.Resource;
import com.ccasro.hub.modules.catalog.domain.model.resource.ResourceName;
import com.ccasro.hub.modules.catalog.domain.model.venue.VenueId;
import com.ccasro.hub.modules.catalog.domain.port.ResourceRepositoryPort;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateResourceUseCase {

  private final ResourceRepositoryPort resources;
  private final CurrentUserProvider currentUser;
  private final VenueOwnershipPolicy ownership;

  @Transactional
  public Resource create(VenueId venueId, CreateResourceCommand cmd) {
    Objects.requireNonNull(venueId, "venueId is required");
    Objects.requireNonNull(cmd, "cmd is required");

    var callerId = currentUser.getUserId();

    ownership.assertOwner(callerId, venueId);

    Money basePrice =
        new Money(
            new BigDecimal(cmd.basePriceAmount().trim()),
            Currency.getInstance(cmd.basePriceCurrency().trim()));

    Resource resource =
        Resource.create(
            venueId, new ResourceName(cmd.name()), new Description(cmd.description()), basePrice);

    resources.save(resource);
    return resource;
  }
}
