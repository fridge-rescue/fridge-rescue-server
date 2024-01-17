package team.rescue.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;
import team.rescue.recipe.dto.RecipeDto.RecipeInfoDto;
import team.rescue.search.entity.RecipeDoc;
import team.rescue.search.repository.RecipeSearchRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeSearchService {

	private final RecipeSearchRepository recipeSearchRepository;

	public SearchPage<RecipeInfoDto> searchRecipeByKeyword(
			String keyword, PageRequest pageRequest
	) {

		SearchPage<RecipeDoc> searchHits =
				recipeSearchRepository.searchByKeyword(keyword, pageRequest);

		return null;
	}

}
