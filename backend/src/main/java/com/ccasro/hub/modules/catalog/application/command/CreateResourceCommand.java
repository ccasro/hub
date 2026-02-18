package com.ccasro.hub.modules.catalog.application.command;

public record CreateResourceCommand(
    String name, String description, String basePriceAmount, String basePriceCurrency) {}
