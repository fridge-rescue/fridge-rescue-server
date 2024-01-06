package team.rescue.recipe.dto;

import lombok.Builder;
import lombok.Getter;
import team.rescue.recipe.entity.RecipeStep;

@Getter
@Builder
public class RecipeStepDto {

  private int stepNo;
  private String stepImageUrl;
  private String stepContents;
  private String stepTip;

  public static RecipeStepDto of (RecipeStep recipeStep) {
    return RecipeStepDto.builder()
        .stepNo(recipeStep.getStepNo())
        .stepImageUrl(recipeStep.getStepImageUrl())
        .stepContents(recipeStep.getStepContents())
        .stepTip(recipeStep.getStepTip())
        .build();
  }

}
