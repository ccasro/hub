package com.ccasro.hub.modules.catalog.application.command;

import java.math.BigDecimal;

public record GeoLocationCommand(BigDecimal latitude, BigDecimal longitude) {}
