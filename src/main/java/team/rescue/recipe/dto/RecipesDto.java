package team.rescue.recipe.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import team.rescue.recipe.entity.Recipe;
import team.rescue.recipe.entity.RecipeIngredient;
import team.rescue.recipe.entity.RecipeStep;

public class RecipesDto {

  @Getter
  @Builder
  public static class RecipesReqDto {

    private String title;
    private String summary;
    private List<RecipeIngredient> recipeIngredientList;
    private List<RecipeStep> recipeStepList;

  }

  @Getter
  public static class RecipesResDto {

    private String title;

    @Builder
    public RecipesResDto(Recipe recipe) {
      this.title = recipe.getTitle();
    }
  }

}
