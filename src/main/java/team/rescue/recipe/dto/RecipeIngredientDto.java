package team.rescue.recipe.dto;

import lombok.Getter;
import lombok.Setter;
import team.rescue.recipe.entity.RecipeIngredient;

@Getter
@Setter
public class RecipeIngredientDto {

	private String name;
	private String amount;

	public static RecipeIngredientDto of(RecipeIngredient recipeIngredient) {
		RecipeIngredientDto recipeIngredientDto = new RecipeIngredientDto();
		recipeIngredientDto.setName(recipeIngredient.getName());
		recipeIngredientDto.setAmount(recipeIngredient.getAmount());

		return recipeIngredientDto;
	}

}
