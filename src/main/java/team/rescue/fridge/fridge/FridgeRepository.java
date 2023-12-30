package team.rescue.fridge.fridge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FridgeRepository extends JpaRepository<Fridge, Long> {

}
