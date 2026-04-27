package org.softsystem.holywave.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.softsystem.holywave.model.dto.UserDto;
import org.softsystem.holywave.model.dto.UserUpdateDto;
import org.softsystem.holywave.model.entities.Post;
import org.softsystem.holywave.model.entities.User;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "createdPosts", ignore = true)
    @Mapping(target = "likedPosts", ignore = true)
    @Mapping(target = "bookmarkedPosts", ignore = true)
    User toUser(UserDto userDto);

    @Mapping(target = "createdPosts", ignore = true)
    @Mapping(target = "likedPosts", ignore = true)
    @Mapping(target = "bookmarkedPosts", ignore = true)
    User toUser(UserUpdateDto userDto);

    @Mapping(target = "createdPosts", ignore = true)
    UserDto toUserDto(User user);

    default UUID map(Post post) {
        return post.getId();
    }

/*
    default List<UUID> mapLikedPosts(List<Post> likedPosts) {
        if (likedPosts == null) return null;

        return likedPosts.stream()
                .map(Post::getId)
                .toList();
    }

    default List<UUID> mapBookmarkedPosts(List<Post> bookmarkedPosts) {
        if (bookmarkedPosts == null) return null;

        return bookmarkedPosts.stream()
                .map(Post::getId)
                .toList();
    }
*/

}