package team.rescue.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseDto<T> {

	private final int code;
	private final String message;
	private final T data;

	@Builder
	public ResponseDto(int code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}
}
