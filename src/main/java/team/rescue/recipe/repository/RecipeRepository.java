package team.rescue.recipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.rescue.recipe.entity.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

}
