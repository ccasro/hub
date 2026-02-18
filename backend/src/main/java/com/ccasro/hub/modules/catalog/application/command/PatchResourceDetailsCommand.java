package com.ccasro.hub.modules.catalog.application.command;

public record PatchResourceDetailsCommand(
    String name, String description, String basePriceAmount, String basePriceCurrency) {}
