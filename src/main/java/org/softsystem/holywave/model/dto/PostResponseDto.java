package org.softsystem.holywave.model.dto;

import java.util.List;

public record PostResponseDto(List<PostDto> posts, int size, int currentPage, Integer nextPage, int limit) {
}
