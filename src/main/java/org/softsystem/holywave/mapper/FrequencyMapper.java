package org.softsystem.holywave.mapper;

import org.mapstruct.Mapper;
import org.softsystem.holywave.model.dto.FrequencyDto;
import org.softsystem.holywave.model.entities.Frequency;

@Mapper(componentModel = "spring")
public interface FrequencyMapper {
    Frequency toFrequency(FrequencyDto frequencyDto);

    FrequencyDto toFrequencyDto(Frequency frequency);
}