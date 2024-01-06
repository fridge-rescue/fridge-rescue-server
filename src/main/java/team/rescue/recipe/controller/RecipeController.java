package team.rescue.recipe.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import team.rescue.auth.user.PrincipalDetails;
import team.rescue.common.dto.ResponseDto;
import team.rescue.recipe.dto.RecipeDto.RecipeResDto;
import team.rescue.recipe.dto.RecipesDto.RecipesReqDto;
import team.rescue.recipe.dto.RecipesDto.RecipesResDto;
import team.rescue.recipe.service.RecipeService;

@Slf4j
@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

  private final RecipeService recipeService;

  @GetMapping("/{recipeId}")
  public ResponseEntity<ResponseDto<RecipeResDto>> getRecipe(@PathVariable Long recipeId) {
    RecipeResDto recipeResDto = recipeService.getRecipe(recipeId);
    return new ResponseEntity<>(
        new ResponseDto<>(1, "레시피 조회에 성공하였습니다.", recipeResDto),
        HttpStatus.OK
    );
  }

  @PutMapping("/recipes")
  public ResponseEntity<ResponseDto<RecipesResDto>> addRecipe(
      @RequestPart("file") MultipartFile recipeImageFile,
      @RequestPart("stepFiles") List<MultipartFile> stepImageFileList,
      @RequestPart("add") RecipesReqDto recipesReqDto,
      @AuthenticationPrincipal PrincipalDetails principalDetails) {

    String email = principalDetails.getUsername();

    RecipesResDto recipesResDto =
        recipeService.addRecipe(recipeImageFile, stepImageFileList, recipesReqDto, email);

      return new ResponseEntity<>(
          new ResponseDto<>(1, "레시피가 성공적으로 등록되었습니다.", recipesResDto),
          HttpStatus.CREATED
      );
  }
}
