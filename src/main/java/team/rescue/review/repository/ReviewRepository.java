package team.rescue.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.rescue.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}
