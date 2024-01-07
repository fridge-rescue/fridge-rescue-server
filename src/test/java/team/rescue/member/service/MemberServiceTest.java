package team.rescue.member.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static team.rescue.error.type.UserError.USER_NOT_FOUND;
import static team.rescue.error.type.UserError.USER_PASSWORD_MISMATCH;

import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import team.rescue.error.exception.ServiceException;
import team.rescue.error.exception.UserException;
import team.rescue.error.type.ServiceError;
import team.rescue.member.dto.MemberDto.MemberNicknameUpdateReqDto;
import team.rescue.member.dto.MemberDto.MemberPasswordUpdateReqDto;
import team.rescue.member.dto.MemberDto.MemberResDto;
import team.rescue.member.entity.Member;
import team.rescue.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@Mock
	MemberRepository memberRepository;

	@Spy
	PasswordEncoder passwordEncoder;

	@InjectMocks
	MemberService memberService;

	@Test
	@DisplayName("회원 정보 조회 성공")
	void successGetMemberInfo() {
		// given
		Member member = Member.builder()
				.id(1L)
				.name("test")
				.nickname("테스트")
				.email("test@gmail.com")
				.build();

		given(memberRepository.findUserByEmail("test@gmail.com"))
				.willReturn(Optional.of(member));

		// when
		MemberResDto memberResDto = memberService.getMemberInfo("test@gmail.com");

		// then
		assertEquals("test", memberResDto.getName());
		assertEquals("테스트", memberResDto.getNickname());
		assertEquals("test@gmail.com", memberResDto.getEmail());
	}

	@Test
	@DisplayName("회원 정보 조회 실패 - 회원 정보 없음")
	void failGetMemberInfo_UserNotFound() {
		// given
		given(memberRepository.findUserByEmail("test@gmail.com"))
				.willReturn(Optional.empty());

		// when
		UserException userException = assertThrows(UserException.class,
				() -> memberService.getMemberInfo("test@gmail.com"));

		// then
		assertEquals(USER_NOT_FOUND.getHttpStatus(), userException.getStatusCode());
	}

	@Test
	@DisplayName("회원 닉네임 변경 성공")
	void successUpdateMemberNickname() {
		// given
		Member member = Member.builder()
				.id(1L)
				.name("test")
				.nickname("테스트")
				.email("test@gmail.com")
				.build();

		given(memberRepository.findUserByEmail("test@gmail.com"))
				.willReturn(Optional.of(member));

		member.updateNickname("테스트2");

		given(memberRepository.save(member))
				.willReturn(Member.builder()
						.id(1L)
						.name("test")
						.nickname("테스트2")
						.email("test@gmail.com")
						.build());

		// when
		MemberResDto memberResDto = memberService.updateMemberNickname("test@gmail.com",
				MemberNicknameUpdateReqDto.builder()
						.nickname("테스트2")
						.build());

		// then
		assertEquals("test", memberResDto.getName());
		assertEquals("테스트2", memberResDto.getNickname());
		assertEquals("test@gmail.com", memberResDto.getEmail());
	}

	@Test
	@DisplayName("회원 닉네임 변경 실패 - 사용자 정보 없음")
	void failUpdateMemberNickname_UserNotFound() {
		// given
		given(memberRepository.findUserByEmail("test@gmail.com"))
				.willReturn(Optional.empty());

		// when
		UserException userException = assertThrows(UserException.class,
				() -> memberService.getMemberInfo("test@gmail.com"));

		// then
		assertEquals(USER_NOT_FOUND.getHttpStatus(), userException.getStatusCode());
	}

	@Test
	@DisplayName("회원 비밀번호 변경 성공")
	void successUpdateMemberPassword() {
		// given
		Member member = Member.builder()
				.id(1L)
				.name("test")
				.nickname("테스트")
				.email("test@gmail.com")
				.password("1234567890")
				.build();

		MemberPasswordUpdateReqDto memberPasswordUpdateReqDto = MemberPasswordUpdateReqDto.builder()
				.currentPassword("1234567890")
				.newPassword("0987654321")
				.newPasswordCheck("0987654321")
				.build();

		given(memberRepository.findUserByEmail("test@gmail.com"))
				.willReturn(Optional.of(member));

		given(passwordEncoder.matches(memberPasswordUpdateReqDto.getCurrentPassword(),
				member.getPassword()))
				.willReturn(true);

		given(memberRepository.save(any()))
				.willReturn(Member.builder()
						.id(1L)
						.name("test")
						.nickname("테스트")
						.email("test@gmail.com")
						.password("0987654321")
						.build());

		// when
		MemberResDto memberResDto = memberService.updateMemberPassword("test@gmail.com",
				memberPasswordUpdateReqDto);

		// then
		assertEquals("test", memberResDto.getName());
		assertEquals("테스트", memberResDto.getNickname());
		assertEquals("test@gmail.com", memberResDto.getEmail());
	}

	@Test
	@DisplayName("회원 비밀번호 변경 실패 - 사용자 정보 없음")
	void failUpdateMemberPassword_UserNotFound() {
		// given
		given(memberRepository.findUserByEmail("test@gmail.com"))
				.willReturn(Optional.empty());

		// when
		UserException userException = assertThrows(UserException.class,
				() -> memberService.getMemberInfo("test@gmail.com"));

		// then
		assertEquals(USER_NOT_FOUND.getHttpStatus(), userException.getStatusCode());
	}

	@Test
	@DisplayName("회원 비밀번호 변경 실패 - 현재 비밀번호 불일치")
	void failUpdateMemberPassword_UserPasswordMismatch() {
		// given
		Member member = Member.builder()
				.id(1L)
				.name("test")
				.nickname("테스트")
				.email("test@gmail.com")
				.password("1234567890")
				.build();

		MemberPasswordUpdateReqDto memberPasswordUpdateReqDto = MemberPasswordUpdateReqDto.builder()
				.currentPassword("1234567890")
				.newPassword("0987654321")
				.newPasswordCheck("0987654321")
				.build();

		given(memberRepository.findUserByEmail("test@gmail.com"))
				.willReturn(Optional.of(member));

		given(passwordEncoder.matches(memberPasswordUpdateReqDto.getCurrentPassword(),
				member.getPassword()))
				.willReturn(false);

		// when
		UserException userException = assertThrows(UserException.class,
				() -> memberService.updateMemberPassword("test@gmail.com", memberPasswordUpdateReqDto));

		// then
		assertEquals(USER_PASSWORD_MISMATCH.getHttpStatus(), userException.getStatusCode());
	}

	@Test
	@DisplayName("회원 비밀번호 변경 실패 - 새 비밀번호와 비밀번호 확인 불일치")
	void failUpdateMemberPassword_PasswordAndPasswordCheckMismatch() {
		// given
		Member member = Member.builder()
				.id(1L)
				.name("test")
				.nickname("테스트")
				.email("test@gmail.com")
				.password("1234567890")
				.build();

		MemberPasswordUpdateReqDto memberPasswordUpdateReqDto = MemberPasswordUpdateReqDto.builder()
				.currentPassword("1234567890")
				.newPassword("0987654321")
				.newPasswordCheck("123124124")
				.build();

		// when
		ServiceException serviceException = assertThrows(ServiceException.class,
				() -> passwordAndPasswordCheckMismatch(memberPasswordUpdateReqDto));

		// then
		assertEquals(ServiceError.PASSWORD_AND_PASSWORD_CHECK_MISMATCH.getHttpStatus(),
				serviceException.getStatusCode());
		verify(memberRepository, never()).save(any());
	}

	private void passwordAndPasswordCheckMismatch(
			MemberPasswordUpdateReqDto memberPasswordUpdateReqDto) {
		if (!Objects.equals(memberPasswordUpdateReqDto.getNewPassword(),
				memberPasswordUpdateReqDto.getNewPasswordCheck())) {
			throw new ServiceException(ServiceError.PASSWORD_AND_PASSWORD_CHECK_MISMATCH);
		}
	}
}