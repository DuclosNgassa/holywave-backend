package org.softsystem.holywave.service;

import com.clerk.backend_api.Clerk;
import com.clerk.backend_api.models.operations.GetUserResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.softsystem.holywave.exception.NotAuthorizedException;
import org.softsystem.holywave.exception.ResourceNotFoundException;
import org.softsystem.holywave.mapper.PostMapper;
import org.softsystem.holywave.mapper.UserMapper;
import org.softsystem.holywave.model.dto.PostDto;
import org.softsystem.holywave.model.dto.UserDto;
import org.softsystem.holywave.model.dto.UserUpdateDto;
import org.softsystem.holywave.model.entities.Post;
import org.softsystem.holywave.model.entities.User;
import org.softsystem.holywave.repository.PostRepository;
import org.softsystem.holywave.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final Clerk clerkClient;
    private final PostRepository postRepository;

    public Optional<UserDto> findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toUserDto);
    }

    public Optional<UserDto> findUserByUserId(String clerkId) {
        return userRepository.findByUserId(clerkId)
                .map(userMapper::toUserDto);
    }

    public void toggleLike(UUID postId, UUID userId) {
        Post post = findPostById(postId);
        User userById = getUser(userId);
        var action = "liked";
        List<Post> likedPosts = userById.getLikedPosts();

        if (likedPosts.contains(post)) {
            likedPosts.remove(post);
            action = "disliked";
        } else {
            likedPosts.add(post);
        }

        userById.setLikedPosts(likedPosts);
        userRepository.save(userById);
        logAction(postId, userId, action);
    }

    public void toggleBookmark(UUID postId, UUID userId) {
        Post post = findPostById(postId);
        User userById = getUser(userId);
        var action = "bookmarked";
        List<Post> bookmarkedPosts = userById.getBookmarkedPosts();
        if (bookmarkedPosts.contains(post)) {
            bookmarkedPosts.remove(post);
            action = "unbookmarked";
        } else {
            bookmarkedPosts.add(post);
        }

        userById.setBookmarkedPosts(bookmarkedPosts);
        userRepository.save(userById);
        logAction(postId, userId, action);
    }

    public List<PostDto> findPostLikedByUserId(UUID id) {
        validateUser(id);

        List<Post> postsLikedByUserId = userRepository.findPostsLikedByUserId(id);

        log.info("postsLikedByUserId. [userId={}, posts={}]", id, postsLikedByUserId);

        return postsLikedByUserId.stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    public List<PostDto> findPostBookmarkedByUserId(UUID id) {
        validateUser(id);

        List<Post> postsBookmarkedByUserId = userRepository.findPostsBookmarkedByUserId(id);

        log.info("postsBookmarkedByUserId. [userId={}, posts={}]", id, postsBookmarkedByUserId);

        return postsBookmarkedByUserId.stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    public Optional<User> findUserById(UUID id) {
        return userRepository.findById(id);
    }

    public boolean existsNotById(UUID id) {
        return !userRepository.existsById(id);
    }

    public UserDto saveUser(UserDto userDto) {
        User userToSave = userMapper.toUser(userDto);
        User savedUser = userRepository.save(userToSave);

        return userMapper.toUserDto(savedUser);
    }

    public UserDto createUser(String userId) {

        GetUserResponse userResponse = clerkClient.users().get()
                .userId(userId)
                .call();

        com.clerk.backend_api.models.components.User clerkUser = userResponse
                .user()
                .orElseThrow(() -> new ResourceNotFoundException("Authentication not available for now"));

        String email = clerkUser
                .emailAddresses()
                .getFirst()
                .emailAddress();

        String profilePicture = clerkUser.imageUrl().orElse("");
        String userName = clerkUser.firstName().orElse("");

        UserDto userDto = UserDto.builder()
                .email(email)
                .userId(userId)
                .userName(userName)
                .profilePicture(profilePicture)
                .build();

        log.info("New user created. {}", userDto);
        return saveUser(userDto);

    }

    public UserDto updateUser(UserUpdateDto userDto, String userId) {

        User existingUser = userRepository.findById(userDto.id())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User does not exists. [id=%s]", userDto.id())));

        if (!Objects.equals(existingUser.getUserId(), userId)) {
            log.error("BadRequestException, userId in the token != userId in userDto. [userIdInToken={}, userIdInDto={}]", userId, existingUser.getUserId());
            throw new NotAuthorizedException("You can only update your own account");
        }

        existingUser.setProfilePicture(userDto.profilePicture());

        if (StringUtils.hasText(userDto.userName())) {
            existingUser.setUserName(userDto.userName());
        }
        if (StringUtils.hasText(userDto.location())) {
            existingUser.setLocation(userDto.location());
        }

        List<Post> likedPosts = postRepository.findAllById(userDto.likedPosts());
        List<Post> bookmarkedPosts = postRepository.findAllById(userDto.bookmarkedPosts());

        existingUser.setLikedPosts(likedPosts);
        existingUser.setBookmarkedPosts(bookmarkedPosts);

        User userSaved = userRepository.save(existingUser);

        log.info("Userprofile updated successfully. [userId={}]", userId);
        return userMapper.toUserDto(userSaved);
    }

    private static void logAction(UUID postId, UUID userId, String action) {
        log.info("Post {} by user. [post={}, user={}]", action, postId, userId);
    }

    private void validateUser(UUID id) {
        if (existsNotById(id)) {
            throw new ResourceNotFoundException(String.format("User does not exists. [id=%s]", id));
        }
    }

    private @NonNull Post findPostById(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Post not found. [postId=%s]", postId)));
    }

    private @NonNull User getUser(UUID userId) {
        return findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User does not exists. [id=%s]", userId)));
    }

}
