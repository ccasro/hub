package com.ccasro.hub.modules.catalog.application.command;

public record PatchVenueDetailsCommand(
    String name, String description, AddressCommand address, GeoLocationCommand location) {}
