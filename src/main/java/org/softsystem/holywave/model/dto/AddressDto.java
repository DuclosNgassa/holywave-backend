package org.softsystem.holywave.model.dto;

public record AddressDto(String street,
                         String houseNumber,
                         String zipCode,
                         String city,
                         String state,
                         String country) {
}
