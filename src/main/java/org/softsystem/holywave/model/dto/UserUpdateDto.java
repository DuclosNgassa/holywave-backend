package org.softsystem.holywave.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record UserUpdateDto(
        @NotNull(message = "id is mandatory") UUID id,
        @NotBlank(message = "email is mandatory") String email,
        String userName,
        String profilePicture,
        String location,
        List<UUID> createdPosts,
        List<UUID> likedPosts,
        List<UUID> bookmarkedPosts
) {
}
