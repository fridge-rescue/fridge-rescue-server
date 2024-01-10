package team.rescue.cook.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import team.rescue.cook.entity.Cook;
import team.rescue.member.dto.MemberDto.MemberInfoDto;
import team.rescue.recipe.dto.RecipeDto.RecipeInfoDto;

public class CookDto {

	@Getter
	@Setter
	@Builder
	public static class CookInfoDto {

		private Long id;
		private LocalDateTime createdAt;
		private RecipeInfoDto recipeInfoDto;

		public static CookInfoDto of(Cook cook) {
			return CookInfoDto.builder()
					.id(cook.getId())
					.createdAt(cook.getCreatedAt())
					.recipeInfoDto(RecipeInfoDto.builder()
							.id(cook.getRecipe().getId())
							.title(cook.getRecipe().getTitle())
							.author(MemberInfoDto.builder()
									.id(cook.getRecipe().getMember().getId())
									.nickname(cook.getRecipe().getMember().getNickname())
									.role(cook.getRecipe().getMember().getRole())
									.build())
							.build())
					.build();
		}
	}

}
