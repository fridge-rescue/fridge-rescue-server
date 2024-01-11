package team.rescue.recipe.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import team.rescue.member.dto.MemberDto.MemberInfoDto;
import team.rescue.recipe.dto.RecipeStepDto.RecipeStepCreateDto;
import team.rescue.recipe.dto.RecipeStepDto.RecipeStepInfoDto;
import team.rescue.recipe.entity.Recipe;

public class RecipeDto {

	// 레시피 등록(생성) 요청 DTO
	@Getter
	@Setter
	@Builder
	public static class RecipeCreateDto {

		private String title;
		private String summary;
		private MultipartFile recipeImageUrl;
		private List<RecipeIngredientDto> recipeIngredients;
		private List<RecipeStepCreateDto> recipeSteps;

		public static RecipeCreateDto of(Recipe recipe) {
			return RecipeCreateDto.builder()
					.title(recipe.getTitle())
					.summary(recipe.getSummary())
					.build();
		}

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
		private List<RecipeIngredientDto> recipeIngredients;
		private List<RecipeStepInfoDto> recipeSteps;
		private MemberInfoDto author;
	}

}
