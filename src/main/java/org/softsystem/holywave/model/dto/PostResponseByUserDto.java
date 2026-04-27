package org.softsystem.holywave.model.dto;

import java.util.List;

public record PostResponseByUserDto(List<PostByUserDto> posts, int size, int currentPage, Integer nextPage, int limit) {
}
