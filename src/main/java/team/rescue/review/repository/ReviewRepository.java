package team.rescue.review.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.rescue.cook.entity.Cook;
import team.rescue.review.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

	List<Review> findByCook(Cook cook);
}
