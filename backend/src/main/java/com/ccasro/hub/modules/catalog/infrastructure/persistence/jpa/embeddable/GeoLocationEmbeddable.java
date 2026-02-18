package com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class GeoLocationEmbeddable {

  @Column(precision = 9, scale = 6)
  public BigDecimal latitude;

  @Column(precision = 9, scale = 6)
  public BigDecimal longitude;
}
