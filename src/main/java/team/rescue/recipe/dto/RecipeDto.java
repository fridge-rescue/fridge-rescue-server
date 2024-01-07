package team.rescue.recipe.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import team.rescue.member.dto.MemberDto.MemberInfoDto;
import team.rescue.recipe.entity.Recipe;
import team.rescue.recipe.entity.RecipeIngredient;
import team.rescue.recipe.entity.RecipeStep;

public class RecipeDto {

	// 레시피 등록(생성) 요청 DTO
	@Getter
	@Setter
	public static class RecipeCreateDto {

		private String title;
		private String summary;
		private List<RecipeIngredient> recipeIngredients;
		private List<RecipeStep> recipeSteps;

	}

	// 레시피 요약 조회 응답 DTO
	@Getter
	@Setter
	public static class RecipeInfoDto {

		private Long id;
		private String title;
		private MemberInfoDto author;

		public static RecipeInfoDto of(Recipe recipe) {
			RecipeInfoDto recipeInfo = new RecipeInfoDto();
			recipeInfo.setId(recipe.getId());
			recipeInfo.setTitle(recipe.getTitle());
			recipeInfo.setAuthor(MemberInfoDto.of(recipe.getMember()));

			return recipeInfo;
		}
	}

	// 레시피 상세 조회 응답 DTO
	@Getter
	@Setter
	public static class RecipeDetailDto {

		private Long id;
		private String title;
		private String summary;
		private String recipeImageUrl;
		private Integer viewCount;
		private Integer reviewCount;
		private Integer reportCount;
		private Integer bookmarkCount;
		private LocalDateTime createdAt;
		private List<RecipeIngredientDto> recipeIngredients;
		private List<RecipeStepDto> recipeSteps;
		private MemberInfoDto author;
	}

	// TODO: 상세 조회 DTO 사용하도록 변경
	@Getter
	@Builder
	public static class RecipeResDto {

		private Long id;
		private String title;
		private String summary;
		private String recipeImageUrl;
		private Integer viewCount;
		private Integer reviewCount;
		private Integer reportCount;
		private Integer bookmarkCount;
		private LocalDateTime createdAt;
		private List<RecipeIngredientDto> recipeIngredientList;
		private List<RecipeStepDto> recipeStepList;
		private Long writerMemberId;
		private String writerMemberNickName;

	}

}
