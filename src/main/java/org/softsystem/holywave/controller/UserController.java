package org.softsystem.holywave.controller;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.softsystem.holywave.exception.ResourceNotFoundException;
import org.softsystem.holywave.model.dto.PostDto;
import org.softsystem.holywave.model.dto.UserDto;
import org.softsystem.holywave.model.dto.UserUpdateDto;
import org.softsystem.holywave.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    ResponseEntity<UserDto> createUser(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();

        Optional<UserDto> userDtoOptional = userService.findUserByUserId(userId);
        if (userDtoOptional.isPresent()) {
            UserDto userDto = userDtoOptional.get();
            log.info("user already exists. [userId={}]", userDto.userId());
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        }

        UserDto userDto = userService.createUser(userId);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);

    }

    @GetMapping(path = "/me")
    ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        log.debug("get currentUser. [userId={}]", userId);

        UserDto userDtoFound = userService
                .findUserByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User does not exists. [userId=%s]", userId)));
        return new ResponseEntity<>(userDtoFound, HttpStatus.OK);
    }

    @GetMapping("/{email}")
    ResponseEntity<UserDto> getUserProfile(@PathVariable String email) {
        UserDto userDtoFound = userService
                .findUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User does not exists. [email=%s]", email)));
        return new ResponseEntity<>(userDtoFound, HttpStatus.OK);
    }

    @PutMapping
    ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserUpdateDto userDto, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        UserDto userDtoUpdated = userService.updateUser(userDto, userId);
        return new ResponseEntity<>(userDtoUpdated, HttpStatus.OK);
    }

    @GetMapping("/public/hello")
    public String publicEndpoint() {
        return "Hello World (public)";
    }

    @GetMapping("/{userId}/likes")
    ResponseEntity<List<PostDto>> findPostLikedByUserId(@PathVariable UUID userId) {
        List<PostDto> postDtos = userService.findPostLikedByUserId(userId);
        return ResponseEntity.ok(postDtos);
    }

    @GetMapping("/{userId}/bookmarks")
    ResponseEntity<List<PostDto>> findPostBookmarkedByUserId(@PathVariable UUID userId) {
        List<PostDto> postDtos = userService.findPostBookmarkedByUserId(userId);
        return ResponseEntity.ok(postDtos);
    }

}