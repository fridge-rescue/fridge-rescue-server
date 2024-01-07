package team.rescue.recipe.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import team.rescue.member.dto.MemberDto.MemberInfoDto;
import team.rescue.recipe.entity.Recipe;

public class RecipeDto {

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

}
