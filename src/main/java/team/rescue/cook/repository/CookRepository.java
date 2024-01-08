package team.rescue.cook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.rescue.cook.entity.Cook;

public interface CookRepository extends JpaRepository<Cook, Long> {

}
