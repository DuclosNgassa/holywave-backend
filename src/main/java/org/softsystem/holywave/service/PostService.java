package org.softsystem.holywave.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.jspecify.annotations.NonNull;
import org.softsystem.holywave.exception.NotAuthorizedException;
import org.softsystem.holywave.exception.ResourceNotFoundException;
import org.softsystem.holywave.mapper.PostMapper;
import org.softsystem.holywave.mapper.UserMapper;
import org.softsystem.holywave.model.dto.*;
import org.softsystem.holywave.model.entities.Category;
import org.softsystem.holywave.model.entities.Post;
import org.softsystem.holywave.repository.PostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final Cloudinary cloudinaryClient;
    private final UserService userService;
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final UserMapper userMapper;
    private final CategoryService categoryService;
    @Value("${cloudinary.folder}")
    private String cloudinaryFolder;

    public PostDto createPost(PostCreateDto postCreateDto, String userId) throws BadRequestException {
        UserDto userDtoFound = findUserByUserId(userId);

        validatePost(postCreateDto.title());
        try {
            //TODO map PostCreateDto to post
            Post postToSave = postMapper.toPost(postCreateDto);
            List<Category> categories = categoryService
                    .findAllById(postCreateDto.categories());

            postToSave.setUserEmail(userDtoFound.email());
            postToSave.setAuthor(userMapper.toUser(userDtoFound));
            postToSave.setCategories(categories);

            Post postSaved = postRepository.save(postToSave);

            return postMapper.toPostDto(postSaved);
        } catch (Exception e) {
            //TODO rollback and delete image in cloudinnary if an exception occurs
            log.error("Exception occurs while creating a post. [errorMessage={}]", e.getMessage());
            if (postCreateDto.image() != null) {
                deleteImage(postCreateDto.image());
            }
            throw new RuntimeException(e);
        }
    }

    public UploadImageResponse uploadImage(MultipartFile imageFile, String userId) throws BadRequestException {
        findUserByUserId(userId);
        validateImage(imageFile);
        String imageUrl;

        try {
            Map uploadResponse = cloudinaryClient.uploader().upload(
                    imageFile.getBytes(),
                    ObjectUtils.asMap("folder", cloudinaryFolder)
            );

            imageUrl = (String) uploadResponse.get("secure_url");

        } catch (Exception e) {
            log.error("Failed to upload image to cloudinary. [errorMessage={}]", e.getMessage());
            throw new BadRequestException("Failed to upload image");
        }

        return new UploadImageResponse(imageUrl);
    }

    public PostResponseByUserDto getPostsByUser(
            String country,
            String state,
            String zipcode,
            String city,
            String searchParam,
            String categories,
            String userId,
            int limit,
            int currentPage
    ) {

        int skip = (currentPage - 1) * limit;

        UUID[] categoryIds = getUuidsFromString(categories);

        // pagination => infinite loading
        if (country == null && state == null) {

            List<PostByUserDto> posts;

            if (StringUtils.hasText(searchParam)) {
                log.info("search with param: {} ", searchParam);
                posts = searchPostsByUser(searchParam, categoryIds, userId, skip, limit);
            } else if (categoryIds != null && categoryIds.length > 0) {
                List<UUID> categoryIdsList = Arrays.stream(categoryIds).toList();
                log.info("search by categories: {} ", categoryIdsList);
                posts = postRepository.findPostsByCategoryIds(categoryIdsList, skip, limit)
                        .stream()
                        .map(post -> postMapper.toPostByUserDto(post, userId))
                        .toList();
            } else {
                log.info("fetch all posts. [skip={}, limit={}]", skip, limit);
                posts = postRepository.fetchPosts(skip, limit)
                        .stream()
                        .map(post -> postMapper.toPostByUserDto(post, userId))
                        .toList();
            }

            Integer nextPage = (posts.size() == limit) ? currentPage + 1 : null;

            return new PostResponseByUserDto(posts, posts.size(), currentPage, nextPage, limit);

        } else if (country != null && state != null) {

            List<PostByUserDto> posts = getPostsNearByByUser(country, state, zipcode, city, userId);
            return new PostResponseByUserDto(posts, posts.size(), currentPage, null, 0);
        }

        throw new IllegalArgumentException("Invalid parameters");
    }

    public PostByUserDto findPostByUserDto(UUID postId, String userId) {
        Post post = findPostById(postId);

        return postMapper.toPostByUserDto(post, userId);
    }

    public PostDto findPostDtoById(UUID postId) {
        Post post = findPostById(postId);

        return postMapper.toPostDto(post);
    }

    public PostDto updatePost(PostUpdateDto postDtoToUpdate, String userId) throws BadRequestException {
        UserDto userDto = findUserByUserId(userId);
        Post postExisting = findPostById(postDtoToUpdate.id());
        String oldImage = postExisting.getImage();
        if (!Objects.equals(userDto.id(), postExisting.getAuthor().getId())) {
            throw new NotAuthorizedException("You can only update your own post");
        }
        //TODO check if image is the same. If not delete otherwise keept it and do not upload again
        validatePost(postDtoToUpdate.title());
        List<Category> categories = categoryService
                .findAllById(postDtoToUpdate.categories());

        Post postToUpdate = postMapper.toPost(postDtoToUpdate);
        postToUpdate.setCategories(categories);
        postToUpdate.setAuthor(postExisting.getAuthor());

        Post postUpdated = postRepository.save(postToUpdate);

        // delete image from cloudinary
        if (StringUtils.hasText(oldImage)) {
            deleteImage(oldImage);
        }

        log.debug("Post successfully updated. [oldPost={}, newPost={}]", postToUpdate, postUpdated);

        return postMapper.toPostDto(postUpdated);
    }

    public List<PostDto> findPostByAuthor(UUID id) {
        validateUser(id);

        List<Post> postsByAuthor = postRepository.findPostsByAuthor(id);

        log.info("getPostByAuthor by author. [author={}, posts={}]", id, postsByAuthor);

        return postsByAuthor.stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    public void toggleLike(UUID postId, String userId) {
        UserDto userDto = findUserByUserId(userId);
        userService.toggleLike(postId, userDto.id());
    }

    public void toggleBookmark(UUID postId, String userId) {
        UserDto userDto = findUserByUserId(userId);
        userService.toggleBookmark(postId, userDto.id());
    }

    public void delete(UUID id, String userId) throws BadRequestException {
        log.info("Post deleting... [postId={}]", id);
        UserDto userDto = findUserByUserId(userId);
        PostDto postDtoExisting = findPostDtoById(id);

        if (!Objects.equals(userDto.id(), postDtoExisting.author())) {
            throw new NotAuthorizedException("You can only delete your own post");
        }

        String image = postDtoExisting.image();

        postRepository.deleteById(id);

        if (StringUtils.hasText(image)) {
            deleteImage(image);
        }

        log.info("Post deleted successfully. [postId={}]", id);

    }

    private @NonNull Post findPostById(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Post not found. [postId=%s]", postId)));
    }

    private List<PostByUserDto> getPostsNearByByUser(String country, String state, String zipcode, String city, String userId) {

        List<PostByUserDto> result = postRepository.searchNearbyPosts(country, state, zipcode, city, 20)
                .stream()
                .map(post -> postMapper.toPostByUserDto(post, userId))
                .toList();
        log.info("Search all nearby posts by address. [country={}, state={}, zipcode={}, city={}, result={}]", country, state, zipcode, city, result);

        return result;
    }

    private List<PostByUserDto> searchPostsByUser(String searchParam, UUID[] categoryIds, String userId, int skip, int limit) {

        if (searchParam == null || searchParam.isBlank()) {
            return List.of();
        }

        List<PostByUserDto> result = postRepository.searchPosts(searchParam, categoryIds, skip, limit)
                .stream()
                .map(post -> postMapper.toPostByUserDto(post, userId))
                .toList();

        log.info("Search result. [searchParam={}, categoryIds={}, skpi={}, limit={}, result={}]", searchParam, categoryIds, skip, limit, result);

        return result;
    }

    private static UUID[] getUuidsFromString(String categories) {
        UUID[] categoryIds = null;

        if (categories != null && !categories.isBlank()) {
            categoryIds = Arrays.stream(categories.split(","))
                    .map(String::trim)
                    .filter(s -> {
                        try {
                            UUID.fromString(s);
                            return true;
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .map(String::trim)
                    .map(UUID::fromString)
                    .toArray(UUID[]::new);
        }
        return categoryIds;
    }

    private UserDto findUserByUserId(String userId) {
        log.debug("Get user. [userId={}]", userId);

        return userService
                .findUserByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User does not exists. [userId=%s]", userId)));

    }

    private void validateUser(UUID id) {
        if (userService.existsNotById(id)) {
            throw new ResourceNotFoundException(String.format("User does not exists. [id=%s]", id));
        }
    }

    private static void validatePost(String title) throws BadRequestException {
        if (!StringUtils.hasText(title)) {
            throw new BadRequestException("Post must contain a title");
        }
    }

    private static void validateImage(MultipartFile imageFile) throws BadRequestException {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new BadRequestException("image is empty");
        }
    }

    private void deleteImage(String imageUrl) throws BadRequestException {
        final String publicId = extractPublicId(imageUrl, cloudinaryFolder);
        try {
            Map result = cloudinaryClient.uploader().destroy(publicId, ObjectUtils.emptyMap());
            if ("ok".equals(result.get("result"))) {
                log.debug("Image successfully deleted from cloudinary. [image={}, publicId={}]", imageUrl, publicId);
            } else {
                log.error("Fail to delete image from cloudinary. [image={}, publicId={}, result={}]", imageUrl, publicId, result);
            }
        } catch (IOException e) {
            log.error("Exception occurs while deleting image. [image={}, errorMessage={}]", imageUrl, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String extractPublicId(String imageUrl, String cloudinaryFolder) throws BadRequestException {
        String publicId = null;
        if (!StringUtils.hasText(cloudinaryFolder)) {
            log.error("Image's publicId could not be extracted since cloudinaryFolder is null. [cloudinaryFolder={}]", cloudinaryFolder);
            throw new BadRequestException("Image could not be extracted since cloudinaryFolder is null.");
        }
        if (!StringUtils.hasText(imageUrl)) {
            log.warn("ImageUrl is null. [image=${}]", imageUrl);
            throw new BadRequestException(String.format("Image could not be deleted since it is not saved in cloudinary. [image=%s]", imageUrl));
        }
        if (!imageUrl.contains("cloudinary")) {
            log.warn("ImageUrl is not a cloudinary valid one. [image=${}]", imageUrl);
            throw new BadRequestException(String.format("ImageUrl is not a cloudinary image and will not be deleted. [image=%s]", imageUrl));
        }
        if (imageUrl.contains(cloudinaryFolder)) {
            publicId = imageUrl.substring(imageUrl.indexOf(cloudinaryFolder));
            int dotIndex = publicId.lastIndexOf(".");
            if (dotIndex != -1) {
                publicId = publicId.substring(0, dotIndex);
            }
        }
        log.info("image publicId: {}", publicId);
        return publicId;
    }
}
