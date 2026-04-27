package org.softsystem.holywave.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PostCreateDto(
                            @NotBlank String title,
                            @NotEmpty List<UUID> categories,
                            String image,
                            String phone,
                            String email,
                            LocationDto location,
                            AddressDto address,
                            String link,
                            List<LocalDateTime> eventDates,
                            String description,
                            Double fee,
                            FrequencyDto frequency
                   ) {
    public PostCreateDto {
        if (frequency == null) {
            frequency = new FrequencyDto();
        }

        if (location == null) {
            location = new LocationDto();
        }
    }
}
