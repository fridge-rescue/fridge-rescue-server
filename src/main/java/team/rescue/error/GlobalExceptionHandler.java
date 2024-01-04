package team.rescue.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import team.rescue.common.dto.ResponseDto;
import team.rescue.error.exception.UserException;
import team.rescue.error.type.ValidationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 유저 관련 에러 핸들링
	 *
	 * @param e UserException
	 * @return Error Response with custom UserException Status Code
	 */
	@ExceptionHandler(UserException.class)
	public ResponseEntity<?> userException(UserException e) {
		
		log.error(e.getErrorMessage());

		ResponseDto<?> response = ResponseDto.builder()
				.code(e.getCode())
				.message(e.getErrorMessage())
				.data(null).build();

		return new ResponseEntity<>(response, e.getStatusCode());
	}


	/**
	 * 유효성 검사 관련 에러 핸들링
	 *
	 * @param e ValidationException
	 * @return Error Response with BAD_REQUEST(400)
	 */
	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<?> validationException(ValidationException e) {

		log.error(e.getMessage());

		ResponseDto<?> response = ResponseDto.builder()
				.code(-1)
				.message(e.getMessage())
				.data(e.getErrorMap()).build();

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
}
