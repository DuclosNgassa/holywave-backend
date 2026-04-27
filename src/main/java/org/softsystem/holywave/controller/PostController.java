package org.softsystem.holywave.controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.softsystem.holywave.model.dto.*;
import org.softsystem.holywave.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping(path = "/upload-image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    ResponseEntity<UploadImageResponse> uploadPostImage(@AuthenticationPrincipal Jwt jwt,
                                                        @RequestParam(value = "image") MultipartFile imageFile) throws BadRequestException {

        String userId = jwt.getSubject();

        UploadImageResponse uploadImageResponse = postService.uploadImage(imageFile, userId);

        return new ResponseEntity<>(uploadImageResponse, HttpStatus.CREATED);
    }

    @PostMapping
    ResponseEntity<PostDto> createPost(@AuthenticationPrincipal Jwt jwt,
                                       @RequestBody PostCreateDto postCreateDto) throws BadRequestException {

        String userId = jwt.getSubject();

        PostDto postDtoCreated = postService.createPost(postCreateDto, userId);

        return new ResponseEntity<>(postDtoCreated, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    ResponseEntity<PostByUserDto> findPostById(@PathVariable UUID id,
                                               @AuthenticationPrincipal Jwt jwt
                                               ) {
        String userId = jwt.getSubject();

        PostByUserDto postById = postService.findPostByUserDto(id, userId);
        return new ResponseEntity<>(postById, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    ResponseEntity<List<PostDto>> findPostByAuthor(@PathVariable UUID id) {
        List<PostDto> postDtos = postService.findPostByAuthor(id);
        return ResponseEntity.ok(postDtos);
    }

    @GetMapping
    public ResponseEntity<PostResponseByUserDto> getPosts(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String zipcode,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String searchParam,
            @RequestParam(required = false) String categories,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "1") int page
    ) {
        String userId = jwt.getSubject();

        PostResponseByUserDto response = postService.getPostsByUser(
                country,
                state,
                zipcode,
                city,
                searchParam,
                categories,
                userId,
                limit,
                page
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping
    ResponseEntity<PostDto> updatePost(@AuthenticationPrincipal Jwt jwt,
                                       @RequestBody PostUpdateDto postDto) throws BadRequestException {

        String userId = jwt.getSubject();

        PostDto postUpdated = postService.updatePost(postDto, userId);

        return new ResponseEntity<>(postUpdated, HttpStatus.OK);
    }

    @PostMapping("/{postId}/like/toggle")
    ResponseEntity<Void> likePost(@PathVariable UUID postId, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();

        postService.toggleLike(postId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/bookmark/toggle")
    ResponseEntity<Void> bookmarkPost(@PathVariable UUID postId, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();

        postService.toggleBookmark(postId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deletePost(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) throws BadRequestException {

        String userId = jwt.getSubject();

        postService.delete(id, userId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
