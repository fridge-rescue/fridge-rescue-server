package team.rescue.fridge.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import team.rescue.fridge.entity.FridgeIngredient;

public class FridgeIngredientDto {

	@Getter
	@Setter
	@Builder
	public static class FridgeIngredientCreateDto {

		private String name;

		private String memo;

		@Future(message = "유통기한이 이미 지난 재료입니다.")
		private LocalDateTime expiredAt;
	}

	@Getter
	@Setter
	@Builder
	public static class FridgeIngredientInfoDto {

		private Long id;
		private String name;
		private String memo;

		@Future(message = "유통기한이 이미 지난 재료입니다.")
		private LocalDateTime expiredAt;

		public static FridgeIngredientInfoDto of(FridgeIngredient fridgeIngredient) {
			return FridgeIngredientInfoDto.builder()
					.id(fridgeIngredient.getId())
					.name(fridgeIngredient.getName())
					.memo(fridgeIngredient.getMemo())
					.expiredAt(fridgeIngredient.getExpiredAt())
					.build();
		}
	}

	@Getter
	@Setter
	@Builder
	public static class FridgeIngredientUpdateDto {

		private List<Long> deleteItem;
		@Valid
		private List<FridgeIngredientInfoDto> updateItem;
	}

	@Getter
	@Setter
	@Builder
	public static class FridgeIngredientUseDto {

		private Long id;
		private String memo;
	}

}
