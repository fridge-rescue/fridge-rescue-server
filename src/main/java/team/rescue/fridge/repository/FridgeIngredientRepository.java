package team.rescue.fridge.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.rescue.fridge.entity.Fridge;
import team.rescue.fridge.entity.FridgeIngredient;

@Repository
public interface FridgeIngredientRepository extends JpaRepository<FridgeIngredient, Long> {

	boolean existsByNameAndMemoAndExpiredAt(String name, String memo, LocalDateTime expiredAt);

	List<FridgeIngredient> findByFridge(Fridge fridge);
}
