package team.rescue.cook.service;

import static team.rescue.error.type.ServiceError.INGREDIENT_NOT_FOUND;
import static team.rescue.error.type.ServiceError.RECIPE_NOT_FOUND;
import static team.rescue.error.type.ServiceError.USER_NOT_FOUND;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.rescue.cook.dto.CookDto.CookCreateDto;
import team.rescue.cook.dto.CookDto.CookInfoDto;
import team.rescue.cook.entity.Cook;
import team.rescue.cook.repository.CookRepository;
import team.rescue.error.exception.ServiceException;
import team.rescue.fridge.dto.FridgeIngredientDto.FridgeIngredientUseDto;
import team.rescue.fridge.entity.FridgeIngredient;
import team.rescue.fridge.repository.FridgeIngredientRepository;
import team.rescue.fridge.repository.FridgeRepository;
import team.rescue.member.entity.Member;
import team.rescue.member.repository.MemberRepository;
import team.rescue.recipe.entity.Recipe;
import team.rescue.recipe.repository.RecipeRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CookService {

	private final MemberRepository memberRepository;
	private final RecipeRepository recipeRepository;
	private final CookRepository cookRepository;
	private final FridgeIngredientRepository fridgeIngredientRepository;
	private final FridgeRepository fridgeRepository;

	@Transactional
	public CookInfoDto completeCook(CookCreateDto cookCreateDto, String email) {
		Member member = memberRepository.findUserByEmail(email)
				.orElseThrow(() -> new ServiceException(USER_NOT_FOUND));

		List<FridgeIngredientUseDto> fridgeIngredientUseDtoList = cookCreateDto.getFridgeIngredientUseDtoList();

		for (FridgeIngredientUseDto fridgeIngredientUseDto : fridgeIngredientUseDtoList) {
			FridgeIngredient fridgeIngredient = fridgeIngredientRepository.findById(
							fridgeIngredientUseDto.getId())
					.orElseThrow(() -> new ServiceException(INGREDIENT_NOT_FOUND));

			fridgeIngredient.updateFridgeIngredient(fridgeIngredient.getName(),
					fridgeIngredientUseDto.getMemo(), fridgeIngredient.getExpiredAt());

			fridgeIngredientRepository.save(fridgeIngredient);
		}

		Recipe recipe = recipeRepository.findById(cookCreateDto.getRecipeId())
				.orElseThrow(() -> new ServiceException(RECIPE_NOT_FOUND));

		Cook savedCook = cookRepository.save(Cook.builder()
				.recipe(recipe)
				.member(member)
				.build());

		return CookInfoDto.of(savedCook);
	}
}
