package team.rescue.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.rescue.error.exception.UserException;
import team.rescue.error.type.UserError;
import team.rescue.member.dto.MemberDto.MemberResDto;
import team.rescue.member.entity.Member;
import team.rescue.member.repository.MemberRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	public MemberResDto getMembersInfo(String email) {
		Member member = memberRepository.findUserByEmail(email)
				.orElseThrow(() -> new UserException(UserError.USER_NOT_FOUND));

		return MemberResDto.of(member);
	}
}
