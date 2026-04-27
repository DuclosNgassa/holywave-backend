package org.softsystem.holywave.model.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record PostDto(UUID id,
                      String title,
                      List<CategoryDto> categories,
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
                      UUID author,
                      String userId,
                      LocalDateTime createdAt
                   ) {

    public PostDto {
        if (frequency == null) {
            frequency = new FrequencyDto();
        }
    }
}
