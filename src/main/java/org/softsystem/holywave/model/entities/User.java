package org.softsystem.holywave.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Entity(name = "users")
@Table(name = "users")
@Data
@ToString(exclude = "createdPosts")
public class User extends AbstractAuditingEntity{

    @NotBlank(message = "email is mandatory")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "userId is mandatory")
    @Column(name = "userId", nullable = false, unique = true)
    private String userId;

    @Column(name = "userName", nullable = false)
    private String userName;

    @Column(name = "profilePicture")
    private String profilePicture;

    @Column(name = "location")
    private String location;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> createdPosts = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_post_liked",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private List<Post> likedPosts;

    @ManyToMany
    @JoinTable(
            name = "user_post_bookmarked",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private List<Post> bookmarkedPosts;

    @OneToMany(mappedBy = "user")
    private List<PostRating> postRatings;
}
