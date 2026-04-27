package org.softsystem.holywave.repository;

import org.softsystem.holywave.model.entities.PostRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRatingRepository extends JpaRepository<PostRating, UUID> {
}
