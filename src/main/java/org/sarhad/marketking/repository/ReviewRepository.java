package org.sarhad.marketking.repository;

import org.sarhad.marketking.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
