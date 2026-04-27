package org.softsystem.holywave.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record UserDto(
        @NotNull(message = "id is mandatory") UUID id,
        @NotBlank(message = "email is mandatory") String email,
        @NotBlank(message = "userId is mandatory") String userId,
        String userName,
        String profilePicture,
        String location,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<UUID> createdPosts,
        List<UUID> likedPosts,
        List<UUID> bookmarkedPosts
        ) {
}
