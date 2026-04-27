package org.softsystem.holywave.repository;

import org.softsystem.holywave.model.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query(value = """
                SELECT p.* FROM Post p
                WHERE LOWER(p.country) = LOWER(:country)
                  AND LOWER(p.state) = LOWER(:state)
                  AND (:zipcode IS NULL OR LOWER(p.zip_code) = LOWER(:zipcode))
                  AND (:city IS NULL OR LOWER(p.city) = LOWER(:city))
                ORDER BY p.created_at DESC LIMIT :limit
            """, nativeQuery = true)
    List<Post> searchNearbyPosts(
            @Param("country") String country,
            @Param("state") String state,
            @Param("zipcode") String zipcode,
            @Param("city") String city,
            @Param("limit") int limit
    );

    @Query(value = """
                SELECT p.* FROM Post p
                ORDER BY p.created_at DESC LIMIT :limit OFFSET :skip
            """, nativeQuery = true)
    List<Post> fetchPosts(
            @Param("skip") int skip,
            @Param("limit") int limit
    );

    @Query(value = """
                SELECT p.*
                FROM post p
                WHERE
                (
                    -- 🔍 Full-text search
                    to_tsvector('simple',
                        coalesce(p.title,'') || ' ' ||
                        coalesce(p.description,'') || ' ' ||
                        coalesce(p.city,'') || ' ' ||
                        coalesce(p.state,'')
                    )
                    @@ websearch_to_tsquery(:searchParam)
            
                    -- 🔄 Fuzzy fallback
                    OR similarity(p.title, :searchParam) > 0.3
                    OR similarity(p.description, :searchParam) > 0.3
                )
            
                -- 🎯 Category filter (many-to-many safe)
                AND (
                    CAST(:categoryIds AS uuid[]) IS NULL
                    OR EXISTS (
                        SELECT 1
                        FROM post_category pc
                        WHERE pc.post_id = p.id
                          AND pc.category_id = ANY(CAST(:categoryIds AS uuid[]))
                    )
                )
            
                ORDER BY
            
                    -- 🧠 TEXT RELEVANCE (like Mongo score)
                    ts_rank(
                        to_tsvector('simple',
                            coalesce(p.title,'') || ' ' ||
                            coalesce(p.description,'')
                        ),
                        websearch_to_tsquery(:searchParam)
                    ) DESC,
            
                    -- 🎯 CATEGORY BOOST (counts matching categories)
                    (
                        SELECT COUNT(*)
                        FROM post_category pc
                        WHERE pc.post_id = p.id
                          AND pc.category_id = ANY(CAST(:categoryIds AS uuid[]))
                    ) DESC,
            
                    -- 🔄 FUZZY SCORE BOOST
                    GREATEST(
                        similarity(p.title, :searchParam),
                        similarity(p.description, :searchParam)
                    ) DESC,
            
                    -- 🕒 RECENCY
                    p.created_at DESC
            
                LIMIT :limit OFFSET :skip
            """, nativeQuery = true)
    List<Post> searchPosts(
            @Param("searchParam") String searchParam,
            @Param("categoryIds") UUID[] categoryIds,
            @Param("skip") int skip,
            @Param("limit") int limit
    );

    @Query(value = """
            SELECT p.* FROM post p
            JOIN post_category pc ON p.id = pc.post_id
            WHERE pc.category_id IN :categoryIds
            ORDER BY p.created_at DESC
            LIMIT :limit OFFSET :skip
            """, nativeQuery = true)
    List<Post> findPostsByCategoryIds(@Param("categoryIds") List<UUID> categoryIds,
                                              @Param("skip") int skip,
                                      @Param("limit") int limit);

    @Query(value = """
            SELECT p.* FROM post p
            WHERE p.user_id = :userId
            ORDER BY p.created_at DESC
            """, nativeQuery = true)
    List<Post> findPostsByAuthor(@Param("userId") UUID userId);

}
