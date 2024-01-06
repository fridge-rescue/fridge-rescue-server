package team.rescue.error.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 비즈니스 로직 관련 에러
 * <p> code: 프론트 규약 에러 코드(HTTP Status Code로 구분 불가한 경우)
 */
@Getter
@RequiredArgsConstructor
public enum ServiceError {

	FRIDGE_NOT_FOUND(-1, HttpStatus.NOT_FOUND, "냉장고 정보가 없습니다."),
	RECIPE_NOT_FOUND(-1, HttpStatus.NOT_FOUND, "해당 레시피를 찾을 수 없습니다.");


	private final int code;
	private final HttpStatus httpStatus;
	private final String errorMessage;
}
