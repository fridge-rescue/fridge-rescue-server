package team.rescue.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import team.rescue.error.type.UserError;

/**
 * TODO: RuntimeException을 extend 하는 전체 서비스 커스텀 에러 하나만 남기도록 작업 필요
 * BuisnessException에 포함 처리
 */
@Getter
public class UserException extends RuntimeException {

	int code;
	HttpStatus statusCode;
	String errorMessage;

	public UserException(UserError userError) {
		this.code = userError.getCode();
		this.statusCode = userError.getHttpStatus();
		this.errorMessage = userError.getErrorMessage();
	}
}
