package team.rescue.search.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.rescue.auth.user.PrincipalDetails;
import team.rescue.common.dto.ResponseDto;
import team.rescue.recipe.dto.RecipeDto.RecipeInfoDto;
import team.rescue.search.entity.RecipeDoc;
import team.rescue.search.service.RecipeSearchService;
import team.rescue.search.service.SearchService;

@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

	private final SearchService searchService;
	private final RecipeSearchService recipeSearchService;

	@GetMapping("/recipe/keyword")
	@PreAuthorize("permitAll()")
	public ResponseEntity<ResponseDto<List<RecipeDoc>>> searchRecipesByKeyword(
			@RequestParam String keyword,
			Pageable pageable
	) {

		// 키워드 검색이라 로직은 수정해야 하는데,
		// 우선 이쪽으로 API 테스트 해주시면 '당근' -> ingredients 필드에서 검색합니다.
		// 이후 키워드 검색으로 title, summary에서 검색하도록 수정 필요
		log.info("[레시피 키워드 검색] keyword={}", keyword);

		List<RecipeDoc> recipeDocs =
				recipeSearchService.searchRecipeByKeyword(keyword, pageable);

		return new ResponseEntity<>(
				new ResponseDto<>(keyword + "로 검색한 레시피 목록입니다.", recipeDocs),
				HttpStatus.OK
		);
	}

	@GetMapping("/recipe/fridge")
	public ResponseEntity<ResponseDto<List<RecipeDoc>>> searchRecipesByFridge(
			Pageable pageable,
			@AuthenticationPrincipal PrincipalDetails details
	) {

		log.info("[레시피 유저 재료 기반 검색] memberId={}", details.getMember().getId());

		SearchPage<RecipeInfoDto> searchHits =
				recipeSearchService.searchRecipeByFridge(details.getMember().getId(), pageable);

		return null;
	}

	/**
	 * 재료 자동완성
	 *
	 * @param keyword 키워드
	 * @return 키워드로 시작하는 재료 목록
	 */
	@GetMapping("/ingredient")
	public ResponseEntity<List<String>> getIngredient(
			@RequestParam String keyword
	) {

		List<String> recommendedIngredients = searchService.getIngredient(keyword);

		return new ResponseEntity<>(recommendedIngredients, HttpStatus.OK);
	}

}
