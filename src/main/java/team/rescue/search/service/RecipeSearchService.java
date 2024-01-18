package team.rescue.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;
import team.rescue.fridge.repository.FridgeRepository;
import team.rescue.recipe.dto.RecipeDto.RecipeInfoDto;
import team.rescue.search.entity.RecipeDoc;
import team.rescue.search.repository.RecipeSearchRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeSearchService {

	private final RecipeSearchRepository recipeSearchRepository;
	private final FridgeRepository fridgeRepository;

	public SearchPage<RecipeInfoDto> searchRecipeByKeyword(
			String keyword, PageRequest pageRequest
	) {

		SearchPage<RecipeDoc> searchHits =
				recipeSearchRepository.searchByKeyword(keyword, pageRequest);

		return null;
	}

	public SearchPage<RecipeInfoDto> searchRecipeByFridge(
			Long memberId, PageRequest pageRequest
	) {

		// 멤버 아이디로 해당 멤버의 냉장고 조회
//		Fridge fridge = fridgeRepository.findByMember()
		// 재료 목록 문자열로 변경
		String ingredients = "";
		// 문자열 검색
		SearchPage<RecipeDoc> searchHits =
				recipeSearchRepository.searchByIngredients(ingredients, pageRequest);

		return null;
	}

}
