package team.rescue.member.service;

import static team.rescue.error.type.ServiceError.USER_NOT_FOUND;
import static team.rescue.error.type.ServiceError.USER_PASSWORD_MISMATCH;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.rescue.cook.dto.CookDto.CookInfoDto;
import team.rescue.cook.entity.Cook;
import team.rescue.cook.repository.CookRepository;
import team.rescue.error.exception.ServiceException;
import team.rescue.error.type.ServiceError;
import team.rescue.member.dto.MemberDto.MemberDetailDto;
import team.rescue.member.dto.MemberDto.MemberNicknameUpdateDto;
import team.rescue.member.dto.MemberDto.MemberPasswordUpdateDto;
import team.rescue.member.entity.Member;
import team.rescue.member.repository.MemberRepository;
import team.rescue.review.repository.ReviewRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final CookRepository cookRepository;
	private final ReviewRepository reviewRepository;

	public MemberDetailDto getMemberInfo(String email) {
		Member member = memberRepository.findUserByEmail(email)
				.orElseThrow(() -> new ServiceException(USER_NOT_FOUND));

		return MemberDetailDto.of(member);
	}

	@Transactional
	public MemberDetailDto updateMemberNickname(String email,
			MemberNicknameUpdateDto memberNicknameUpdateDto) {
		Member member = memberRepository.findUserByEmail(email)
				.orElseThrow(() -> new ServiceException(USER_NOT_FOUND));

		member.updateNickname(memberNicknameUpdateDto.getNickname());

		Member updatedMember = memberRepository.save(member);

		return MemberDetailDto.of(updatedMember);
	}

	@Transactional
	public MemberDetailDto updateMemberPassword(String email,
			MemberPasswordUpdateDto memberPasswordUpdateDto) {
		Member member = memberRepository.findUserByEmail(email)
				.orElseThrow(() -> new ServiceException(USER_NOT_FOUND));

		boolean passwordMatch = passwordEncoder.matches(memberPasswordUpdateDto.getCurrentPassword(),
				member.getPassword());

		if (!passwordMatch) {
			throw new ServiceException(USER_PASSWORD_MISMATCH);
		}

		if (!Objects.equals(memberPasswordUpdateDto.getNewPassword(),
				memberPasswordUpdateDto.getNewPasswordCheck())) {
			throw new ServiceException(ServiceError.PASSWORD_AND_PASSWORD_CHECK_MISMATCH);
		}

		member.updatePassword(passwordEncoder.encode(memberPasswordUpdateDto.getNewPassword()));

		Member updatedMember = memberRepository.save(member);

		return MemberDetailDto.of(updatedMember);
	}

	public Page<CookInfoDto> getCompletedCooks(String email, Pageable pageable) {
		Member member = memberRepository.findUserByEmail(email)
				.orElseThrow(() -> new ServiceException(USER_NOT_FOUND));

		Page<Cook> cookPage = cookRepository.findByMember(member, pageable);

		return cookPage.map(CookInfoDto::of);
	}
}
