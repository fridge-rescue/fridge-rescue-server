package team.rescue.member.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static team.rescue.error.type.ServiceError.PASSWORD_AND_PASSWORD_CHECK_MISMATCH;
import static team.rescue.error.type.ServiceError.USER_NOT_FOUND;
import static team.rescue.error.type.ServiceError.USER_PASSWORD_MISMATCH;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import team.rescue.auth.type.RoleType;
import team.rescue.cook.dto.CookDto.CookInfoDto;
import team.rescue.cook.entity.Cook;
import team.rescue.cook.repository.CookRepository;
import team.rescue.error.exception.ServiceException;
import team.rescue.member.dto.MemberDto.MemberDetailDto;
import team.rescue.member.dto.MemberDto.MemberNicknameUpdateDto;
import team.rescue.member.dto.MemberDto.MemberPasswordUpdateDto;
import team.rescue.member.entity.Member;
import team.rescue.member.repository.MemberRepository;
import team.rescue.mock.WithMockMember;
import team.rescue.recipe.entity.Recipe;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@Mock
	MemberRepository memberRepository;

	@Mock
	CookRepository cookRepository;

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
				.nickname("테스트")
				.email("test@gmail.com")
				.build();

		given(memberRepository.findUserByEmail("test@gmail.com"))
				.willReturn(Optional.of(member));

		// when
		MemberDetailDto memberDetailDto = memberService.getMemberInfo("test@gmail.com");

		// then
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
				.nickname("테스트")
				.email("test@gmail.com")
				.build();

		given(memberRepository.findUserByEmail("test@gmail.com"))
				.willReturn(Optional.of(member));

		member.updateNickname("테스트2");

		given(memberRepository.save(member))
				.willReturn(Member.builder()
						.id(1L)
						.nickname("테스트2")
						.email("test@gmail.com")
						.build());

		// when
		MemberDetailDto memberDetailDto = memberService.updateMemberNickname("test@gmail.com",
				MemberNicknameUpdateDto.builder()
						.nickname("테스트2")
						.build());

		// then
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
						.nickname("테스트")
						.email("test@gmail.com")
						.password("0987654321")
						.build());

		// when
		MemberDetailDto memberDetailDto = memberService.updateMemberPassword("test@gmail.com",
				memberPasswordUpdateDto);

		// then
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

	@Test
	@DisplayName("완성된 요리 조회 성공")
	@WithMockMember(role = RoleType.USER)
	void successGetCompletedCooks() {
		// given
		Member member = Member.builder()
				.id(1L)
				.email("test@gmail.com")
				.build();

		Member m = Member.builder()
				.id(2L)
				.email("author@gmail.com")
				.build();

		given(memberRepository.findUserByEmail("test@gmail.com"))
				.willReturn(Optional.of(member));

		Recipe recipe1 = Recipe.builder()
				.member(m)
				.title("레시피1")
				.build();
		Recipe recipe2 = Recipe.builder()
				.member(m)
				.title("레시피2")
				.build();

		Cook cook1 = Cook.builder()
				.id(1L)
				.member(member)
				.recipe(recipe1)
				.build();

		Cook cook2 = Cook.builder()
				.id(2L)
				.member(member)
				.recipe(recipe2)
				.build();

		List<Cook> list = new ArrayList<>(Arrays.asList(cook1, cook2));

		given(cookRepository.findByMember(any(Member.class), any(Pageable.class)))
				.willReturn(new PageImpl<>(list));

		// when
		Page<CookInfoDto> cookInfoDtoPage = memberService.getCompletedCooks("test@gmail.com",
				PageRequest.of(0, 2));

		// then
		assertEquals(1L, cookInfoDtoPage.getContent().get(0).getId());
		assertEquals("레시피1", cookInfoDtoPage.getContent().get(0).getRecipeInfoDto().getTitle());
		assertEquals(2L, cookInfoDtoPage.getContent().get(1).getId());
		assertEquals("레시피2", cookInfoDtoPage.getContent().get(1).getRecipeInfoDto().getTitle());
	}

	@Test
	@DisplayName("완료된 요리 조회 실패 - 사용자 정보 없음")
	@WithMockMember(role = RoleType.USER)
	void failGetCompletedCooks_UserNotFound() {
		// given
		given(memberRepository.findUserByEmail(anyString()))
				.willReturn(Optional.empty());

		// when
		ServiceException serviceException = assertThrows(ServiceException.class,
				() -> memberService.getCompletedCooks("test@gmail.com", PageRequest.of(0, 2)));

		// then
		assertEquals(USER_NOT_FOUND.getHttpStatus(), serviceException.getStatusCode());
	}
}
