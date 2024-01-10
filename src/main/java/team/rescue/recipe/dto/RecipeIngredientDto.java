package team.rescue.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.rescue.recipe.entity.RecipeIngredient;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeIngredientDto {

  private String name;
  private String amount;

  public static RecipeIngredientDto of (RecipeIngredient recipeIngredient) {
    return RecipeIngredientDto.builder()
        .name(recipeIngredient.getName())
        .amount(recipeIngredient.getAmount())
        .build();
  }

}
