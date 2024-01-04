package team.rescue.member.dto;

import lombok.Getter;
import lombok.Setter;
import team.rescue.member.entity.Member;

public class MemberDto {

	@Getter
	@Setter
	public static class MemberInfoDto {

		private Long id;
		private String nickname;

		public static MemberInfoDto fromEntity(Member member) {
			MemberInfoDto memberInfo = new MemberInfoDto();
			memberInfo.setId(member.getId());
			memberInfo.setNickname(member.getNickname());

			return memberInfo;
		}
	}
}
