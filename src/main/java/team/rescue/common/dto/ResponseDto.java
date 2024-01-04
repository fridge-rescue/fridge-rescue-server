package team.rescue.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseDto<T> {

	private int code = -1;
	private String message = null;
	private final T data;

	@Builder
	public ResponseDto(int code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}
}
