package team.rescue.fridge.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.rescue.auth.user.PrincipalDetails;
import team.rescue.fridge.dto.FridgeIngredientDto.FridgeIngredientAddReqDto;
import team.rescue.fridge.service.FridgeService;
import team.rescue.validator.ListValidator;

@Slf4j
@RestController
@RequestMapping("/api/fridge")
@RequiredArgsConstructor
public class FridgeController {

	private final ListValidator listValidator;
	private final FridgeService fridgeService;

	@GetMapping
	public ResponseEntity<?> getFridgeIngredients(
			@AuthenticationPrincipal PrincipalDetails principalDetails) {

		String email = principalDetails.getUsername();
		return ResponseEntity.ok(fridgeService.getFridgeIngredients(email));
	}

	@PostMapping("/ingredients")
	public ResponseEntity<?> addIngredient(
			@RequestBody List<FridgeIngredientAddReqDto> fridgeIngredientAddDtoList,
			@AuthenticationPrincipal PrincipalDetails principalDetails, Errors errors) {

		// List<Dto> 형태는 @Valid 어노테이션이 동작하지 않아서
		// CustomValidator를 생성해서 유효성 검증을 해줘야 함.
		listValidator.validate(fridgeIngredientAddDtoList, errors);

		if (errors.hasErrors()) {
			if (errors.getFieldError() != null) {
				log.error("유효성 검증 실패: {}", errors.getFieldError().getDefaultMessage());
				return new ResponseEntity<>(errors.getFieldError().getDefaultMessage(),
						HttpStatus.BAD_REQUEST);
			}
		}

		String email = principalDetails.getUsername();

		return ResponseEntity.ok(fridgeService.addIngredient(email, fridgeIngredientAddDtoList));

	}
}
