package team.rescue.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import team.rescue.auth.type.RoleType;
import team.rescue.member.entity.Member;

public class JoinDto {

	@Getter
	@Setter
	public static class JoinReqDto {

		@NotEmpty(message = "이름을 입력해주세요.")
		@Pattern(regexp = "^[a-zA-Z가-힣]{1,15}", message = "한글 또는 영문으로 이루어진 최소 1글자, 최대 15자의 이름을 입력해주세요.")
		private String name;

		@NotEmpty(message = "사용할 닉네임을 입력해주세요.")
		@Pattern(regexp = "^[a-zA-Z가-힣0-9]{2,15}", message = "한글, 영문 또는 숫자를 포함한 최소 2글자, 최대 15자의 닉네임을 입력해주세요.")
		private String nickname;

		// 최대 30자
		@NotEmpty(message = "로그인에 사용할 이메일을 입력해주세요.")
		@Pattern(regexp = "^[a-zA-Z0-9+-_.]{1,30}@[a-zA-Z0-9-]+\\.[a-zA-Z]+$", message = "올바른 이메일 형식이 아닙니다(이메일 아이디는 최대 30자까지 입력 가능합니다).")
		@Size(max = 50, message = "50자 미만의 이메일을 입력해주세요.")
		private String email;

		// 8 ~ 20
		@NotEmpty(message = "로그인에 사용할 비밀번호를 입력해주세요.")
		@Size(min = 8, max = 20, message = "최소 8글자, 최대 20글자의 비밀번호를 입력해주세요.")
		private String password;
	}

	@Getter
	@Setter
	public static class EmailConfirmDto {

		@NotEmpty(message = "이메일 인증 코드를 입력해주세요.")
		@Size(min = 6, max = 6, message = "올바른 인증 코드가 아닙니다.")
		private String code;
	}


	@Getter
	@Setter
	public static class JoinResDto {

		private Long id;
		private String name;
		private String nickname;
		private String email;
		private RoleType role;
		private LocalDateTime createdAt;

		public JoinResDto(Member member) {

			this.id = member.getId();
			this.name = member.getName();
			this.nickname = member.getNickname();
			this.email = member.getEmail();
			this.role = member.getRole();
			this.createdAt = member.getCreatedAt();
		}
	}
}
