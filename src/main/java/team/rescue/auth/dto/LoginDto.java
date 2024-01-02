package team.rescue.auth.dto;

import lombok.Getter;
import lombok.Setter;
import team.rescue.auth.type.RoleType;
import team.rescue.member.entity.Member;

public class LoginDto {

	@Getter
	@Setter
	public static class LoginReqDto {

		private String email;
		private String password;
	}

	@Getter
	public static class LoginResDto {

		private final String name;
		private final String email;
		private final RoleType role;
		private final TokenDto tokenDto;

		public LoginResDto(Member user, TokenDto token) {
			this.name = user.getName();
			this.email = user.getEmail();
			this.role = user.getRole();
			this.tokenDto = token;
		}
	}
}
