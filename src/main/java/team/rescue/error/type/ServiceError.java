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

	// File
	FILE_NOT_EXIST(HttpStatus.BAD_REQUEST, "파일을 등록해주세요."),
	FILE_EXTENSION_INVALID(HttpStatus.BAD_REQUEST, "유효한 파일이 아닙니다."),
	FILE_RESIZING_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "파일 리사이징에 실패했습니다."),
	FILE_UPLOAD_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),

	// User
	EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "등록되지 않은 이메일입니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
	EMAIL_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
	EMAIL_CODE_MIS_MATCH(HttpStatus.BAD_REQUEST, "이메일 인증 코드가 일치하지 않습니다."),

	// Recipe
	RECIPE_NOT_FOUND(HttpStatus.NOT_FOUND, "이미 삭제된 레시피입니다."),

	// Fridge
	FRIDGE_NOT_FOUND(HttpStatus.NOT_FOUND, "냉장고 정보가 없습니다.");

	
	private final HttpStatus httpStatus;
	private final String errorMessage;
}
