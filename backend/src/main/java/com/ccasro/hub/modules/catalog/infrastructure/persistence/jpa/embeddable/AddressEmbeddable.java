package com.ccasro.hub.modules.catalog.infrastructure.persistence.jpa.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AddressEmbeddable {

  @Column(length = 150)
  public String street;

  @Column(length = 100)
  public String city;

  @Column(name = "postal_code", length = 20)
  public String postalCode;

  @Column(length = 100)
  public String country;
}
