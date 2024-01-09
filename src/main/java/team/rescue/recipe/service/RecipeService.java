package team.rescue.recipe.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.rescue.auth.user.PrincipalDetails;
import team.rescue.common.file.FileService;
import team.rescue.error.exception.ServiceException;
import team.rescue.error.type.ServiceError;
import team.rescue.member.dto.MemberDto.MemberInfoDto;
import team.rescue.member.entity.Member;
import team.rescue.member.repository.MemberRepository;
import team.rescue.recipe.dto.RecipeDto.RecipeCreateDto;
import team.rescue.recipe.dto.RecipeDto.RecipeDetailDto;
import team.rescue.recipe.dto.RecipeIngredientDto;
import team.rescue.recipe.dto.RecipeStepDto.RecipeStepCreateDto;
import team.rescue.recipe.dto.RecipeStepDto.RecipeStepInfoDto;
import team.rescue.recipe.entity.Recipe;
import team.rescue.recipe.entity.RecipeIngredient;
import team.rescue.recipe.entity.RecipeStep;
import team.rescue.recipe.repository.RecipeIngredientRepository;
import team.rescue.recipe.repository.RecipeStepRepository;
import team.rescue.recipe.repository.RecipesRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {

	private final RecipesRepository recipesRepository;
	private final RecipeIngredientRepository recipeIngredientRepository;
	private final RecipeStepRepository recipeStepRepository;
	private final MemberRepository memberRepository;
	private final FileService fileService;

	public RecipeDetailDto getRecipe(Long id) {

		Recipe recipe = recipesRepository.findById(id)
				.orElseThrow(() -> {
					log.error("레시피 없음");
					return new ServiceException(ServiceError.RECIPE_NOT_FOUND);
				});
		log.debug("레시피 {}", recipe);

		Long memberId = recipe.getMember().getId();
		log.debug("member Id {}", memberId);

		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> {
					log.error("일치하는 사용자 정보 없음");
					return new ServiceException(ServiceError.USER_NOT_FOUND);
				});

		MemberInfoDto memberInfoDto = MemberInfoDto.of(member);

		List<RecipeIngredient> recipeIngredientList =
				recipeIngredientRepository.findByRecipe(recipe);
		List<RecipeIngredientDto> recipeIngredientDtoList =
				recipeIngredientList.stream().map(RecipeIngredientDto::of).toList();

		List<RecipeStep> recipeStepList =
				recipeStepRepository.findByRecipe(recipe);
		List<RecipeStepInfoDto> recipeStepDtoList =
				recipeStepList.stream().map(RecipeStepInfoDto::of).toList();

		return RecipeDetailDto.builder()
				.id(recipe.getId())
				.title(recipe.getTitle())
				.summary(recipe.getSummary())
				.recipeImageUrl(recipe.getRecipeImageUrl())
				.viewCount(recipe.getViewCount())
				.reviewCount(recipe.getReviewCount())
				.reportCount(recipe.getReportCount())
				.bookmarkCount(recipe.getBookmarkCount())
				.createdAt(recipe.getCreatedAt())
				.recipeIngredients(recipeIngredientDtoList)
				.recipeSteps(recipeStepDtoList)
				.author(memberInfoDto)
				.build();
	}

	@Transactional
	public RecipeCreateDto addRecipe(RecipeCreateDto recipeCreateDto, PrincipalDetails principalDetails) {

		String memberEmail = principalDetails.getMember().getEmail();

		Member member = memberRepository.findUserByEmail(memberEmail)
				.orElseThrow(() -> {
					log.error("일치하는 사용자 정보 없음");
					return new ServiceException(ServiceError.USER_NOT_FOUND);
				});

		// 레시피 대표 이미지 저장
		String recipeImageFilePath = fileService.uploadImageToS3(recipeCreateDto.getRecipeImageUrl());

		Recipe recipe = Recipe.builder()
				.title(recipeCreateDto.getTitle())
				.summary(recipeCreateDto.getSummary())
				.recipeImageUrl(recipeImageFilePath)
				.viewCount(0)
				.reviewCount(0)
				.reportCount(0)
				.bookmarkCount(0)
				.member(member) // 멤버 연결
				.build();

		recipesRepository.save(recipe); // 먼저 Recipe 저장

		for (RecipeIngredientDto recipeIngredientDto : recipeCreateDto.getRecipeIngredients()) {
			RecipeIngredient ingredient = RecipeIngredient.builder()
					.name(recipeIngredientDto.getName())
					.amount(recipeIngredientDto.getAmount())
					.recipe(recipe) // 재료와 레시피 연결
					.build();
			recipeIngredientRepository.save(ingredient);
		}

		// 레시피 스탭들 저장
		for (RecipeStepCreateDto recipeStepCreateDto : recipeCreateDto.getRecipeSteps()) {

			String stepImageFilePath = "";	// 빈 문자열
			if (!recipeStepCreateDto.getStepImageUrl().isEmpty()) {
				// 스탭 이미지 저장
				stepImageFilePath = fileService.uploadImageToS3(recipeStepCreateDto.getStepImageUrl());
			}

			RecipeStep step = RecipeStep.builder()
					.stepNo(recipeStepCreateDto.getStepNo())
					.stepImageUrl(stepImageFilePath) // URL 설정
					.stepContents(recipeStepCreateDto.getStepContents())
					.stepTip(recipeStepCreateDto.getStepTip())
					.recipe(recipe) // 레시피와 연결
					.build();

			recipeStepRepository.save(step);
		}

		return RecipeCreateDto.of(recipe);
	}
}
