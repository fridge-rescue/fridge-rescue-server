package team.rescue.search.entity;

import jakarta.persistence.Id;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import team.rescue.recipe.entity.Recipe;
import team.rescue.recipe.entity.RecipeIngredient;

@Document(indexName = "recipes")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeDoc {

	@Id
	private Long id;

	@Field(type = FieldType.Text)
	private String title;

	@Field(type = FieldType.Text)
	private String summary;

	@Field(type = FieldType.Integer)
	private Integer reviewCount;

	@Field(type = FieldType.Text)
	private String ingredients;

	public static RecipeDoc of(Recipe recipe, List<RecipeIngredient> ingredients) {
		return RecipeDoc.builder()
				.id(recipe.getId())
				.title(recipe.getTitle())
				.summary(recipe.getSummary())
				.reviewCount(recipe.getReviewCount())
				.ingredients(ingredients.stream()
						.map(RecipeIngredient::getName)
						.collect(Collectors.joining(" ")))
				.build();
	}
}
