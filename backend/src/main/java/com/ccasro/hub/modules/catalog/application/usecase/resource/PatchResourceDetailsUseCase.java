package com.ccasro.hub.modules.catalog.application.usecase.resource;

import com.ccasro.hub.common.application.ports.CurrentUserProvider;
import com.ccasro.hub.common.domain.exception.NotFoundException;
import com.ccasro.hub.common.domain.model.vo.Description;
import com.ccasro.hub.common.domain.model.vo.Money;
import com.ccasro.hub.modules.catalog.application.command.PatchResourceDetailsCommand;
import com.ccasro.hub.modules.catalog.application.policy.VenueOwnershipPolicy;
import com.ccasro.hub.modules.catalog.domain.model.resource.Resource;
import com.ccasro.hub.modules.catalog.domain.model.resource.ResourceId;
import com.ccasro.hub.modules.catalog.domain.model.resource.ResourceName;
import com.ccasro.hub.modules.catalog.domain.port.ResourceRepositoryPort;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PatchResourceDetailsUseCase {

  private final ResourceRepositoryPort resources;
  private final CurrentUserProvider currentUser;
  private final VenueOwnershipPolicy ownership;

  public Resource patch(ResourceId resourceId, PatchResourceDetailsCommand cmd) {
    Objects.requireNonNull(resourceId, "resourceId is required");
    Objects.requireNonNull(cmd, "cmd is required");

    var resource =
        resources
            .findById(resourceId)
            .orElseThrow(() -> new NotFoundException("Resource not found: " + resourceId));

    ownership.assertOwner(currentUser.getUserId(), resource.venueId());

    if (cmd.name() != null) {
      resource.rename(new ResourceName(cmd.name()));
    }

    if (cmd.description() != null) {
      resource.updateDescription(new Description(cmd.description()));
    }

    if (cmd.basePriceAmount() != null && cmd.basePriceCurrency() != null) {
      Money newPrice =
          new Money(
              new BigDecimal(cmd.basePriceAmount().trim()),
              Currency.getInstance(cmd.basePriceCurrency().trim()));
      resource.changeBasePrice(newPrice);
    }

    resources.save(resource);
    return resource;
  }
}
