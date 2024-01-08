package team.rescue.fridge.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.rescue.auth.user.PrincipalDetails;
import team.rescue.common.dto.ResponseDto;
import team.rescue.error.exception.ValidationException;
import team.rescue.fridge.dto.FridgeDto;
import team.rescue.fridge.dto.FridgeIngredientDto.FridgeIngredientCreateDto;
import team.rescue.fridge.dto.FridgeIngredientDto.FridgeIngredientInfoDto;
import team.rescue.fridge.service.FridgeService;
import team.rescue.validator.ListValidator;

@Slf4j
@RestController
@RequestMapping("/api/fridge")
@RequiredArgsConstructor
public class FridgeController {

	private final ListValidator listValidator;
	private final FridgeService fridgeService;

	/**
	 * 냉장고 조회)
	 *
	 * @param principalDetails 로그인 유저
	 * @return 냉장고 및 포함된 재료 리스트
	 */
	@GetMapping
	public ResponseEntity<ResponseDto<FridgeDto>> getFridge(
			@AuthenticationPrincipal PrincipalDetails principalDetails) {

		String email = principalDetails.getUsername();
		FridgeDto fridgeDto = fridgeService.getFridgeIngredients(email);
		return ResponseEntity.ok(new ResponseDto<>(null, fridgeDto));
	}

	/**
	 * 냉장고 재료 등록
	 *
	 * @param fridgeIngredientCreateDtoList 등록할 재료 목록
	 * @param principalDetails              로그인 유저
	 * @param errors                        TODO: AOP, ControllerAdvice 타는지 확인 필요
	 * @return 등록한 재료 목록
	 */
	@PostMapping("/ingredients")
	public ResponseEntity<ResponseDto<List<FridgeIngredientInfoDto>>> addIngredient(
			@RequestBody List<FridgeIngredientCreateDto> fridgeIngredientCreateDtoList,
			@AuthenticationPrincipal PrincipalDetails principalDetails,
			Errors errors
	) {

		// List<Dto> 형태는 @Valid 어노테이션이 동작하지 않아서
		// CustomValidator를 생성해서 유효성 검증을 해줘야 함.
		listValidator.validate(fridgeIngredientCreateDtoList, errors);

		if (errors.hasErrors()) {
			if (errors.getFieldError() != null) {
				log.error("유효성 검증 실패: {}", errors.getFieldError().getDefaultMessage());
				throw new ValidationException(errors.getFieldError().getDefaultMessage(), null);
			}
		}

		String email = principalDetails.getUsername();
		List<FridgeIngredientInfoDto> fridgeIngredientInfoDtoList =
				fridgeService.addIngredient(email, fridgeIngredientCreateDtoList);

		return ResponseEntity.ok(new ResponseDto<>(null, fridgeIngredientInfoDtoList));

	}
}
