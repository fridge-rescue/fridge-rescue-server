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

	FILE_NOT_EXIST(-1, HttpStatus.BAD_REQUEST, "파일을 등록해주세요."),
	FILE_EXTENSION_INVALID(-1, HttpStatus.BAD_REQUEST, "유효한 파일이 아닙니다."),
	FILE_RESIZING_FAILURE(-1, HttpStatus.INTERNAL_SERVER_ERROR, "파일 리사이징에 실패했습니다."),
	FILE_UPLOAD_FAILURE(-1, HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),

	NOT_FOUND_RECIPE(-1, HttpStatus.NOT_FOUND, "이미 삭제된 레시피입니다."),

	FRIDGE_NOT_FOUND(-1, HttpStatus.NOT_FOUND, "냉장고 정보가 없습니다."),
	RECIPE_NOT_FOUND(-1, HttpStatus.NOT_FOUND, "해당 레시피를 찾을 수 없습니다.");


	private final int code;
	private final HttpStatus httpStatus;
	private final String errorMessage;
}
