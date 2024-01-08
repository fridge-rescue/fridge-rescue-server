package team.rescue.member.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static team.rescue.error.type.ServiceError.PASSWORD_AND_PASSWORD_CHECK_MISMATCH;
import static team.rescue.error.type.ServiceError.USER_NOT_FOUND;
import static team.rescue.error.type.ServiceError.USER_PASSWORD_MISMATCH;

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
import team.rescue.member.dto.MemberDto.MemberDetailDto;
import team.rescue.member.dto.MemberDto.MemberNicknameUpdateDto;
import team.rescue.member.dto.MemberDto.MemberPasswordUpdateDto;
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
		MemberDetailDto memberDetailDto = memberService.getMemberInfo("test@gmail.com");

		// then
		assertEquals("test", memberDetailDto.getName());
		assertEquals("테스트", memberDetailDto.getNickname());
		assertEquals("test@gmail.com", memberDetailDto.getEmail());
	}

	@Test
	@DisplayName("회원 정보 조회 실패 - 회원 정보 없음")
	void failGetMemberInfo_UserNotFound() {
		// given
		given(memberRepository.findUserByEmail("test@gmail.com"))
				.willReturn(Optional.empty());

		// when
		ServiceException serviceException = assertThrows(ServiceException.class,
				() -> memberService.getMemberInfo("test@gmail.com"));

		// then
		assertEquals(USER_NOT_FOUND.getHttpStatus(), serviceException.getStatusCode());
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
		MemberDetailDto memberDetailDto = memberService.updateMemberNickname("test@gmail.com",
				MemberNicknameUpdateDto.builder()
						.nickname("테스트2")
						.build());

		// then
		assertEquals("test", memberDetailDto.getName());
		assertEquals("테스트2", memberDetailDto.getNickname());
		assertEquals("test@gmail.com", memberDetailDto.getEmail());
	}

	@Test
	@DisplayName("회원 닉네임 변경 실패 - 사용자 정보 없음")
	void failUpdateMemberNickname_UserNotFound() {
		// given
		given(memberRepository.findUserByEmail("test@gmail.com"))
				.willReturn(Optional.empty());

		// when
		ServiceException serviceException = assertThrows(ServiceException.class,
				() -> memberService.getMemberInfo("test@gmail.com"));

		// then
		assertEquals(USER_NOT_FOUND.getHttpStatus(), serviceException.getStatusCode());
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

		MemberPasswordUpdateDto memberPasswordUpdateDto = MemberPasswordUpdateDto.builder()
				.currentPassword("1234567890")
				.newPassword("0987654321")
				.newPasswordCheck("0987654321")
				.build();

		given(memberRepository.findUserByEmail("test@gmail.com"))
				.willReturn(Optional.of(member));

		given(passwordEncoder.matches(memberPasswordUpdateDto.getCurrentPassword(),
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
		MemberDetailDto memberDetailDto = memberService.updateMemberPassword("test@gmail.com",
				memberPasswordUpdateDto);

		// then
		assertEquals("test", memberDetailDto.getName());
		assertEquals("테스트", memberDetailDto.getNickname());
		assertEquals("test@gmail.com", memberDetailDto.getEmail());
	}

	@Test
	@DisplayName("회원 비밀번호 변경 실패 - 사용자 정보 없음")
	void failUpdateMemberPassword_UserNotFound() {
		// given
		given(memberRepository.findUserByEmail("test@gmail.com"))
				.willReturn(Optional.empty());

		// when
		ServiceException serviceException = assertThrows(ServiceException.class,
				() -> memberService.getMemberInfo("test@gmail.com"));

		// then
		assertEquals(USER_NOT_FOUND.getHttpStatus(), serviceException.getStatusCode());
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

		MemberPasswordUpdateDto memberPasswordUpdateDto = MemberPasswordUpdateDto.builder()
				.currentPassword("1234567890")
				.newPassword("0987654321")
				.newPasswordCheck("0987654321")
				.build();

		given(memberRepository.findUserByEmail("test@gmail.com"))
				.willReturn(Optional.of(member));

		given(passwordEncoder.matches(memberPasswordUpdateDto.getCurrentPassword(),
				member.getPassword()))
				.willReturn(false);

		// when
		ServiceException serviceException = assertThrows(ServiceException.class,
				() -> memberService.updateMemberPassword("test@gmail.com", memberPasswordUpdateDto));

		// then
		assertEquals(USER_PASSWORD_MISMATCH.getHttpStatus(), serviceException.getStatusCode());
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

		MemberPasswordUpdateDto memberPasswordUpdateDto = MemberPasswordUpdateDto.builder()
				.currentPassword("1234567890")
				.newPassword("0987654321")
				.newPasswordCheck("123124124")
				.build();

		// when
		ServiceException serviceException = assertThrows(ServiceException.class,
				() -> passwordAndPasswordCheckMismatch(memberPasswordUpdateDto));

		// then
		assertEquals(PASSWORD_AND_PASSWORD_CHECK_MISMATCH.getHttpStatus(),
				serviceException.getStatusCode());
		verify(memberRepository, never()).save(any());
	}

	private void passwordAndPasswordCheckMismatch(
			MemberPasswordUpdateDto memberPasswordUpdateDto) {
		if (!Objects.equals(memberPasswordUpdateDto.getNewPassword(),
				memberPasswordUpdateDto.getNewPasswordCheck())) {
			throw new ServiceException(PASSWORD_AND_PASSWORD_CHECK_MISMATCH);
		}
	}
}