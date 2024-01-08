package team.rescue.member.dto;

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
}
