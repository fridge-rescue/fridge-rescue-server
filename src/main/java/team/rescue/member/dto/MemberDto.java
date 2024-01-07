package team.rescue.member.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import team.rescue.auth.type.RoleType;
import team.rescue.member.entity.Member;

public class MemberDto {

	@Getter
	@Setter
	public static class MemberInfoDto {

		private Long id;
		private String nickname;
		private RoleType role;

		public static MemberInfoDto of(Member member) {
			MemberInfoDto memberInfo = new MemberInfoDto();
			memberInfo.setId(member.getId());
			memberInfo.setNickname(member.getNickname());
			memberInfo.setRole(member.getRole());

			return memberInfo;
		}
	}

	@Getter
	@Setter
	@Builder
	public static class MemberDetailDto {

		private Long id;
		private String name;
		private String nickname;
		private String email;

		public static MemberDetailDto of(Member member) {
			return MemberDetailDto.builder()
					.id(member.getId())
					.name(member.getName())
					.nickname(member.getNickname())
					.email(member.getEmail())
					.build();
		}
	}

	@Getter
	@Setter
	public static class MemberNicknameUpdateDto {

		@Pattern(regexp = "^[a-zA-Z가-힣0-9]{2,15}", message = "한글, 영문 또는 숫자를 포함한 최소 2글자, 최대 15자의 닉네임을 입력해주세요.")
		private String nickname;
	}

	@Getter
	@Setter
	public static class MemberPasswordUpdateDto {

		@NotEmpty(message = "현재 비밀번호를 입력해주세요.")
		private String currentPassword;

		@NotEmpty(message = "변경할 새 비밀번호를 입력해주세요.")
		@Size(min = 8, max = 20, message = "최소 8글자, 최대 20글자의 비밀번호를 입력해주세요.")
		private String newPassword;

		@NotEmpty(message = "변경할 새 비밀번호를 한 번 더 입력해주세요.")
		private String newPasswordCheck;
	}
}
