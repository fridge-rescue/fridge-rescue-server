package team.rescue.error.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 유저 관련 에러
 * <p> code: 프론트 규약 에러 코드(HTTP Status Code로 구분 불가한 경우)
 */
@Getter
@RequiredArgsConstructor
public enum UserError {

	NOT_FOUND_EMAIL(-1, HttpStatus.NOT_FOUND, "등록되지 않은 이메일입니다."),
	NOT_FOUND_USER(-1, HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
	ALREADY_EXIST_EMAIL(-1, HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.");

	private final int code;
	private final HttpStatus httpStatus;
	private final String errorMessage;
}