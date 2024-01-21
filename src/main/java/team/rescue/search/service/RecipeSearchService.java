package team.rescue.search.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;
import team.rescue.error.exception.ServiceException;
import team.rescue.error.type.ServiceError;
import team.rescue.fridge.entity.Fridge;
import team.rescue.fridge.entity.FridgeIngredient;
import team.rescue.fridge.repository.FridgeRepository;
import team.rescue.member.dto.MemberDto.MemberInfoDto;
import team.rescue.member.entity.Member;
import team.rescue.member.repository.MemberRepository;
import team.rescue.recipe.dto.RecipeDto.RecipeInfoDto;
import team.rescue.recipe.entity.Recipe;
import team.rescue.recipe.repository.RecipeRepository;
import team.rescue.search.entity.RecipeDoc;
import team.rescue.search.repository.RecipeSearchRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeSearchService {

	private final RecipeSearchRepository recipeSearchRepository;
	private final MemberRepository memberRepository;
	private final FridgeRepository fridgeRepository;
	private final RecipeRepository recipeRepository;

	public List<RecipeInfoDto> searchRecipeByKeyword(
			String keyword, Pageable pageable
	) {

		log.info("키워드 검색 서비스");

		List<RecipeDoc> searchHits =
				recipeSearchRepository.searchByKeyword(keyword, pageable);

		List<RecipeInfoDto> recipeInfoDtos = searchHits.stream()
				.map(hit -> {
					Recipe recipe = recipeRepository.findById(hit.getId())
							.orElseThrow(() -> {
								log.error("레시피 없음");
								return new ServiceException(ServiceError.RECIPE_NOT_FOUND);
							});
					Long memberId = recipe.getMember().getId();
					Member member = memberRepository.findById(memberId)
							.orElseThrow(() -> {
								log.error("일치하는 사용자 정보 없음");
								return new ServiceException(ServiceError.USER_NOT_FOUND);
							});
					MemberInfoDto memberInfoDto = MemberInfoDto.of(member);
					return new RecipeInfoDto(hit.getId(), hit.getTitle(), memberInfoDto);
				})
				.collect(Collectors.toList());

		return recipeInfoDtos;

//		return null;

	}

	public Page<RecipeInfoDto> searchRecipeByFridge(
			Long memberId, Pageable pageable
	) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new ServiceException(ServiceError.USER_NOT_FOUND));

		// 멤버 아이디로 해당 멤버의 냉장고 조회
		Fridge fridge = fridgeRepository.findByMember(member)
				.orElseThrow(() -> new ServiceException(ServiceError.FRIDGE_NOT_FOUND));

		// 재료를 담을 StringBuilder
		StringBuilder ingredients = new StringBuilder();

		// 냉장고 재료를 StringBuilder에 추가
		for (FridgeIngredient fridgeIngredient : fridge.getIngredientList()) {
			ingredients.append(fridgeIngredient.getName()).append(" ");
		}

		// 문자열 검색
		SearchPage<RecipeDoc> searchPage =
				recipeSearchRepository.searchByIngredients(ingredients.toString().strip(), pageable);

		List<RecipeInfoDto> recipeInfoDtoList = searchPage.getSearchHits().stream()
				.map(recipeDocSearchHit -> {
					Recipe recipe = recipeRepository.findById(recipeDocSearchHit.getContent().getId())
							.orElseThrow(() -> new ServiceException(ServiceError.RECIPE_NOT_FOUND));

					return RecipeInfoDto.builder()
							.id(recipe.getId())
							.title(recipe.getTitle())
							.author(MemberInfoDto.of(recipe.getMember()))
							.build();
				}).collect(Collectors.toList());

		return new PageImpl<>(recipeInfoDtoList);
	}

}
