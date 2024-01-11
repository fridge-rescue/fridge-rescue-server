package team.rescue.recipe.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.rescue.auth.user.PrincipalDetails;
import team.rescue.common.dto.ResponseDto;
import team.rescue.recipe.dto.RecipeDto.RecipeCreateDto;
import team.rescue.recipe.dto.RecipeDto.RecipeDetailDto;
import team.rescue.recipe.dto.RecipeDto.RecipeInfoDto;
import team.rescue.recipe.dto.RecipeDto.RecipeUpdateDto;
import team.rescue.recipe.service.RecipeService;

@Slf4j
@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

	private final RecipeService recipeService;

	/**
	 * 특정 레시피 상세 조회
	 *
	 * @param recipeId 조회할 레시피 ID
	 * @return 해당 레시피 상세 데이터
	 */
	@GetMapping("/{recipeId}")
	public ResponseEntity<ResponseDto<RecipeDetailDto>> getRecipe(
			@PathVariable Long recipeId
	) {

		RecipeDetailDto recipeDetailDto = recipeService.getRecipe(recipeId);
		return new ResponseEntity<>(
				new ResponseDto<>("레시피 조회에 성공하였습니다.", recipeDetailDto),
				HttpStatus.OK
		);
	}

	/**
	 * 레시피 등록
	 *
	 * @param recipeCreateDto  등록할 레시피 데이터
	 * @param principalDetails 로그인 유저
	 * @return 등록한 레시피 데이터
	 */
	@PutMapping("/recipes")
	public ResponseEntity<ResponseDto<RecipeInfoDto>> addRecipe(
			@ModelAttribute RecipeCreateDto recipeCreateDto,
			BindingResult bindingResult,
			@AuthenticationPrincipal PrincipalDetails principalDetails) {

		RecipeInfoDto createDto =
				recipeService.addRecipe(recipeCreateDto, principalDetails);

		return new ResponseEntity<>(
				new ResponseDto<>("레시피가 성공적으로 등록되었습니다.", createDto),
				HttpStatus.CREATED
		);
	}

	@PatchMapping("/{recipeId}")
//	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<ResponseDto<RecipeDetailDto>> updateRecipe(
			@PathVariable Long recipeId,
			@ModelAttribute RecipeUpdateDto recipeUpdateDto,
			@AuthenticationPrincipal PrincipalDetails principalDetails) {

		RecipeDetailDto recipeDetailDto =
				recipeService.updateRecipe(recipeId, recipeUpdateDto, principalDetails);

		return new ResponseEntity<>(
				new ResponseDto<>("레시피가 성공적으로 수정되었습니다.", recipeDetailDto),
				HttpStatus.OK
		);
	}

	@DeleteMapping("/{recipeId}")
//	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<ResponseDto<RecipeInfoDto>> deleteRecipe(
			@PathVariable Long recipeId,
			@AuthenticationPrincipal PrincipalDetails principalDetails) {

		RecipeInfoDto recipeDeleteDto =
				recipeService.deleteRecipe(recipeId, principalDetails);

		return new ResponseEntity<>(
				new ResponseDto<>("레시피가 성공적으로 삭제되었습니다.", recipeDeleteDto),
				HttpStatus.OK
		);
	}
}
