package org.softsystem.holywave.repository;

import org.softsystem.holywave.model.entities.Post;
import org.softsystem.holywave.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUserId(String userId);

    boolean existsByUserId(String userId);

    @Query(value = """
            SELECT p.* FROM post p
            JOIN user_post_liked upl ON p.id = upl.post_id
            WHERE upl.user_id = :userId
            ORDER BY p.created_at DESC
            """, nativeQuery = true)
    List<Post> findPostsLikedByUserId(@Param("userId") UUID userId);

    @Query(value = """
            SELECT p.* FROM post p
            JOIN user_post_bookmarked upb ON p.id = upb.post_id
            WHERE upb.user_id = :userId
            ORDER BY p.created_at DESC
            """, nativeQuery = true)
    List<Post> findPostsBookmarkedByUserId(@Param("userId") UUID userId);

}
