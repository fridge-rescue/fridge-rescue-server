package team.rescue.fridge.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import team.rescue.fridge.entity.FridgeIngredient;

public class FridgeIngredientDto {

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class FridgeIngredientCreateDto {

		private String name;

		private String memo;

		@Future(message = "유통기한이 이미 지난 재료입니다.")
		private LocalDateTime expiredAt;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
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

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class FridgeIngredientUpdateDto {

		private List<Long> deleteItem;
		@Valid
		private List<FridgeIngredientInfoDto> updateItem;
	}

}
