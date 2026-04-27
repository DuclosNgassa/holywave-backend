package org.softsystem.holywave.model.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record PostUpdateDto(UUID id,
                            String title,
                            List<UUID> categories,
                            String image,
                            String phone,
                            String email,
                            LocationDto location,
                            AddressDto address,
                            String link,
                            List<LocalDateTime> eventDates,
                            String description,
                            Double fee,
                            FrequencyDto frequency,
                            UUID author
) {
    public PostUpdateDto {
        if (frequency == null) {
            frequency = new FrequencyDto();
        }
        if (location == null) {
            location = new LocationDto();
        }
    }
}
