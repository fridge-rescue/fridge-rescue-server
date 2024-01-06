package team.rescue.recipe.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.rescue.common.file.FileService;
import team.rescue.error.exception.RecipeException;
import team.rescue.error.exception.UserException;
import team.rescue.error.type.RecipeError;
import team.rescue.error.type.UserError;
import team.rescue.member.entity.Member;
import team.rescue.member.repository.MemberRepository;
import team.rescue.recipe.dto.RecipeDto.RecipeResDto;
import team.rescue.recipe.dto.RecipeIngredientDto;
import team.rescue.recipe.dto.RecipeStepDto;
import team.rescue.recipe.dto.RecipesDto.RecipesReqDto;
import team.rescue.recipe.dto.RecipesDto.RecipesResDto;
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

  public RecipeResDto getRecipe(Long id) {

    Recipe recipe = recipesRepository.findById(id)
        .orElseThrow(() -> {
          log.error("레시피 없음");
          return new RecipeException(RecipeError.NOT_FOUND_RECIPE);
        });
    log.debug("레시피 {}", recipe);


    Long memberId = recipe.getMember().getId();
    log.debug("member Id {}", memberId);

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> {
          log.error("일치하는 사용자 정보 없음");
          return new UserException(UserError.USER_NOT_FOUND);
        });

    String memberNickName = member.getNickname();
    log.debug("member Name {}", memberNickName);

    List<RecipeIngredient> recipeIngredientList =
        recipeIngredientRepository.findByRecipe(recipe);
    List<RecipeIngredientDto> recipeIngredientDtoList =
        recipeIngredientList.stream().map(RecipeIngredientDto::of).toList();

    List<RecipeStep> recipeStepList =
        recipeStepRepository.findByRecipe(recipe);
    List<RecipeStepDto> recipeStepDtoList =
        recipeStepList.stream().map(RecipeStepDto::of).toList();

    return RecipeResDto.builder()
        .id(recipe.getId())
        .title(recipe.getTitle())
        .summary(recipe.getSummary())
        .recipeImageUrl(recipe.getRecipeImageUrl())   // #ToDO: s3로부터 이미지 호출
        .viewCount(recipe.getViewCount())
        .reviewCount(recipe.getReviewCount())
        .reportCount(recipe.getReportCount())
        .bookmarkCount(recipe.getBookmarkCount())
        .createdAt(recipe.getCreatedAt())
        .recipeIngredientList(recipeIngredientDtoList)
        .recipeStepList(recipeStepDtoList)
        .writerMemberId(memberId)
        .writerMemberNickName(memberNickName)
        .build();
  }

  @Transactional
  public RecipesResDto addRecipe(MultipartFile recipeImageFile, List<MultipartFile> stepImageFileList,
      RecipesReqDto recipesReqDto, String email) {

    Member member = memberRepository.findUserByEmail(email)
        .orElseThrow(() -> {
          log.error("일치하는 사용자 정보 없음");
          return new UserException(UserError.USER_NOT_FOUND);
        });

    // 레시피 대표 이미지 저장
    String recipeImageFilePath = fileService.uploadImageToS3(recipeImageFile);

    Recipe recipe = Recipe.builder()
        .title(recipesReqDto.getTitle())
        .summary(recipesReqDto.getSummary())
        .recipeImageUrl(recipeImageFilePath)
        .viewCount(0)
        .reviewCount(0)
        .reportCount(0)
        .bookmarkCount(0)
        .member(member) // 멤버 연결
        .build();

    recipesRepository.save(recipe); // 먼저 Recipe 저장

    for (RecipeIngredient recipeIngredient : recipesReqDto.getRecipeIngredientList()) {
      RecipeIngredient ingredient = RecipeIngredient.builder()
          .name(recipeIngredient.getName())
          .amount(recipeIngredient.getAmount())
          .recipe(recipe) // 재료와 레시피 연결
          .build();
      recipeIngredientRepository.save(ingredient);
    }

    for (int i = 0; i < recipesReqDto.getRecipeStepList().size(); i++) {

      RecipeStep recipeStep = recipesReqDto.getRecipeStepList().get(i);

      String stepImageFilePath = null;
      if (i < stepImageFileList.size()) {
        MultipartFile stepImageFile = stepImageFileList.get(i);
        if (!stepImageFile.isEmpty()) {
          // 스텝 이미지 저장
          stepImageFilePath = fileService.uploadImageToS3(stepImageFile);
        }
      }

      RecipeStep step = RecipeStep.builder()
          .stepNo(recipeStep.getStepNo())
          .stepImageUrl(stepImageFilePath) // URL 설정
          .stepContents(recipeStep.getStepContents())
          .stepTip(recipeStep.getStepTip())
          .recipe(recipe) // 레시피와 연결
          .build();

      recipeStepRepository.save(step);
    }

    return new RecipesResDto(recipe);
  }
}
