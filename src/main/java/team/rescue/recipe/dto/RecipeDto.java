package team.rescue.recipe.dto;

import lombok.Getter;
import lombok.Setter;
import team.rescue.member.dto.MemberDto.MemberInfoDto;
import team.rescue.recipe.entity.Recipe;

public class RecipeDto {

	@Getter
	@Setter
	public static class RecipeInfoDto {

		private Long id;
		private String title;
		private MemberInfoDto author;

		public static RecipeInfoDto fromEntity(Recipe recipe) {
			RecipeInfoDto recipeInfo = new RecipeInfoDto();
			recipeInfo.setId(recipe.getId());
			recipeInfo.setTitle(recipe.getTitle());
			recipeInfo.setAuthor(MemberInfoDto.fromEntity(recipe.getMember()));

			return recipeInfo;
		}
	}

}
