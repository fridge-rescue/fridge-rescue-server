package team.rescue.recipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.rescue.recipe.entity.Recipe;

@Repository
public interface RecipesRepository extends JpaRepository<Recipe, Long> {

}
