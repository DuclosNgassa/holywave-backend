package org.softsystem.holywave.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity(name="Post")
@Table(name = "post")
@Data
@ToString(exclude = {"author", "categories", "likedBy", "bookmarkedBy", ""})
public class Post extends AbstractAuditingEntity {

    @Column(name="title")
    private String title;

    @ManyToMany
    @JoinTable(
            name = "post_category",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @Column(name="image")
    private String image;

    @Column(name="userEmail")
    private String userEmail;

    @Column(name="phone")
    private String phone;

    @Embedded
    private Location location;

    @Embedded
    private Address address;

    @Column(name="link")
    private String link;

    @Column(name="eventDates")
    private List<LocalDateTime> eventDates;

    @Column(name="description")
    private String description;

    @Column(name="fee")
    private Double fee;

    @Embedded
    private Frequency frequency;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User author;

    @ManyToMany(mappedBy = "likedPosts")
    @Column(name="likedBy")
    private List<User> likedBy;

    @ManyToMany(mappedBy = "bookmarkedPosts")
    @Column(name="bookmarkedBy")
    private List<User> bookmarkedBy;

    @OneToMany(mappedBy = "post")
    private List<PostRating> postRatings;

}
