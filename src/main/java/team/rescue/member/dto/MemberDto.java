package team.rescue.member.dto;

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
	public static class MemberResDto {

		private Long id;
		private String name;
		private String nickname;
		private String email;

		public static MemberResDto of(Member member) {
			return MemberResDto.builder()
					.id(member.getId())
					.name(member.getName())
					.nickname(member.getNickname())
					.email(member.getEmail())
					.build();
		}
	}
}
