package org.softsystem.holywave.mapper;

import org.mapstruct.*;
import org.softsystem.holywave.model.dto.*;
import org.softsystem.holywave.model.entities.Category;
import org.softsystem.holywave.model.entities.Post;
import org.softsystem.holywave.model.entities.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface PostMapper {

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Post toPost(PostCreateDto post);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Post toPost(PostUpdateDto post);

    @Mapping(source = "author.id", target = "author")
    @Mapping(source = "author.userId", target = "userId")
    @Mapping(target = "categories", qualifiedByName = "categoryListMapping")
    PostDto toPostDto(Post post);

    @Named("categoryListMapping")
    default List<CategoryDto> mapCategories(List<Category> categories) {
        if (categories == null) return null;

        return categories.stream()
                .map(category -> new CategoryDto(category.getId(), category.getName()))
                .toList();
    }

    @Mapping(source = "author.id", target = "author")
    @Mapping(source = "author.userId", target = "userId")
    @Mapping(target = "isLiked", ignore = true)
    @Mapping(target = "isBookmarked", ignore = true)
    @Mapping(target= "numberOfLikes", expression = "java(post.getLikedBy().size())")
    @Mapping(target = "categories", qualifiedByName = "categoryListMapping")
    PostByUserDto toPostByUserDto(Post post, @Context String userId);

    @AfterMapping
    default void afterPostByUserDto(Post post, @Context String userId, @MappingTarget PostByUserDto dto) {
        if (post.getLikedBy().stream().map(User::getUserId).anyMatch(it->it.equals(userId))) {
            dto.setLiked(true);
        }
        if (post.getBookmarkedBy().stream().map(User::getUserId).anyMatch(it->it.equals(userId))) {
            dto.setBookmarked(true);
        }
    }
}