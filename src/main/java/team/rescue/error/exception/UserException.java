package team.rescue.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import team.rescue.error.type.UserError;

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
