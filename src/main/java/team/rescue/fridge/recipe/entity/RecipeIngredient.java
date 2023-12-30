package team.rescue.fridge.recipe.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "recipe_ingredient")
@Getter
public class RecipeIngredient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recipe_ingredient_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recipe_id")
	private Recipe recipe;

	@Column(name = "name", nullable = false, length = 20)
	private String name;

	@Column(name = "amount", nullable = false, length = 20)
	private String amount;

}
