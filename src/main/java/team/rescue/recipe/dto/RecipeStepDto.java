package team.rescue.recipe.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import team.rescue.recipe.entity.RecipeStep;

@Getter
@Builder
public class RecipeStepDto {

  // 레시피 스탭 생성 DTO
  @Getter
  @Setter
  public static class RecipeStepCreateDto {

    private int stepNo;
    private MultipartFile stepImageUrl;
    private String stepContents;
    private String stepTip;

  }

  // 레시피 스탭 조회 DTO
  @Getter
  @Builder
  public static class RecipeStepInfoDto {

    private int stepNo;
    private String stepImageUrl;
    private String stepContents;
    private String stepTip;

    public static RecipeStepInfoDto of(RecipeStep recipeStep) {
      return RecipeStepInfoDto.builder()
          .stepNo(recipeStep.getStepNo())
          .stepImageUrl(recipeStep.getStepImageUrl())
          .stepContents(recipeStep.getStepContents())
          .stepTip(recipeStep.getStepTip())
          .build();
    }
  }


}
