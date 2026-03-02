package com.ccasro.hub.shared.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "city")
@Getter
public class CityEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(name = "country_code", nullable = false, length = 3)
  private String countryCode;

  @Column(nullable = false)
  private double latitude;

  @Column(nullable = false)
  private double longitude;
}
