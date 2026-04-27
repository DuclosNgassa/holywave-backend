package org.softsystem.holywave.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PostByUserDto {
    private UUID id;
    private String title;
    private List<CategoryDto> categories;
    private String image;
    private String phone;
    private String email;
    private LocationDto location;
    private AddressDto address;
    private String link;
    private List<LocalDateTime> eventDates;
    private String description;
    private Double fee;
    private FrequencyDto frequency;
    private UUID author;
    private String userId;
    private boolean isLiked;
    private int numberOfLikes;
    private boolean isBookmarked;
    private LocalDateTime createdAt;
}
