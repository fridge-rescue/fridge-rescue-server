package team.rescue.fridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.rescue.fridge.entity.Fridge;

@Repository
public interface FridgeRepository extends JpaRepository<Fridge, Long> {

}
