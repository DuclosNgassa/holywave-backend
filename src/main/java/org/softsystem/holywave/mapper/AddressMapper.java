package org.softsystem.holywave.mapper;

import org.mapstruct.Mapper;
import org.softsystem.holywave.model.dto.AddressDto;
import org.softsystem.holywave.model.entities.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toAddress(AddressDto addressDto);

    AddressDto toAddressDto(Address address);
}