package team.rescue.recipe.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import team.rescue.auth.type.RoleType;
import team.rescue.member.dto.MemberDto.MemberInfoDto;
import team.rescue.member.entity.Member;
import team.rescue.recipe.dto.RecipeIngredientDto.RecipeIngredientCreateDto;
import team.rescue.recipe.dto.RecipeIngredientDto.RecipeIngredientInfoDto;
import team.rescue.recipe.dto.RecipeStepDto.RecipeStepCreateDto;
import team.rescue.recipe.dto.RecipeStepDto.RecipeStepInfoDto;
import team.rescue.recipe.entity.Recipe;
import team.rescue.recipe.entity.RecipeIngredient;
import team.rescue.search.entity.RecipeDoc;

public class RecipeDto {

	// 레시피 등록(생성) 요청 DTO
	@Getter
	@Setter
	@Builder
	public static class RecipeCreateDto {

		private String title;
		private String summary;
		private List<RecipeIngredientCreateDto> ingredients;
		private List<RecipeStepCreateDto> steps;
	}

	// 레시피 수정 요청 DTO
	@Getter
	@Setter
	@Builder
	public static class RecipeUpdateDto {

		private String title;
		private String summary;
		private List<RecipeIngredientCreateDto> ingredients;
		private List<RecipeStepCreateDto> steps;

	}

	// 레시피 요약 조회 응답 DTO
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class RecipeInfoDto {

		private Long id;
		private String title;
		private String summary;
		private Integer reviewCount;
		private String ingredients;
		private LocalDateTime createdAt;
		private String image;
		private MemberInfoDto author;

		public static RecipeInfoDto of(Recipe recipe) {
			RecipeInfoDto recipeInfo = new RecipeInfoDto();
			recipeInfo.setId(recipe.getId());
			recipeInfo.setTitle(recipe.getTitle());
			recipeInfo.setAuthor(MemberInfoDto.of(recipe.getMember()));

			return recipeInfo;
		}
		public static RecipeInfoDto of(
				Recipe recipe, String ingredients,
				Member member, String recipeImageFilePath) {

			return RecipeInfoDto.builder()
					.id(recipe.getId())
					.title(recipe.getTitle())
					.summary(recipe.getSummary())
					.reviewCount(recipe.getReviewCount())
					.ingredients(ingredients)
					.createdAt(recipe.getCreatedAt())
					.image(recipeImageFilePath)
					.author(MemberInfoDto.of(member))
					.build();
		}
	}

	// 레시피 상세 조회 응답 DTO
	@Getter
	@Setter
	@Builder
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
		private List<RecipeIngredientInfoDto> recipeIngredients;
		private List<RecipeStepInfoDto> recipeSteps;
		private MemberInfoDto author;

		public static RecipeDetailDto of(Recipe recipe) {
			return RecipeDetailDto.builder()
					.id(recipe.getId())
					.title(recipe.getTitle())
					.summary(recipe.getSummary())
					.recipeImageUrl(recipe.getRecipeImageUrl())
					.viewCount(recipe.getViewCount())
					.reviewCount(recipe.getReviewCount())
					.reportCount(recipe.getReportCount())
					.bookmarkCount(recipe.getBookmarkCount())
					.createdAt(recipe.getCreatedAt())
					.build();
		}
	}

}
