package team.rescue.member.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import team.rescue.auth.type.RoleType;
import team.rescue.member.dto.MemberDto.MemberNicknameUpdateReqDto;
import team.rescue.member.dto.MemberDto.MemberPasswordUpdateReqDto;
import team.rescue.member.dto.MemberDto.MemberResDto;
import team.rescue.member.repository.MemberRepository;
import team.rescue.member.service.MemberService;
import team.rescue.mock.MockMember;
import team.rescue.mock.WithMockMember;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
@Transactional
class MemberControllerTest extends MockMember {

	@MockBean
	MemberService memberService;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	MemberRepository memberRepository;

	@Test
	@DisplayName("회원 정보 조회 성공")
	@WithMockMember(role = RoleType.USER)
	void successGetMemberInfo() throws Exception {
		// given
		MemberResDto memberResDto = MemberResDto.builder()
				.id(1L)
				.name("test")
				.nickname("테스트")
				.email("test@gmail.com")
				.build();

		given(memberService.getMemberInfo("test@gmail.com"))
				.willReturn(memberResDto);

		// when
		// then
		mockMvc.perform(get("/api/members/info"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(1L))
				.andExpect(jsonPath("$.data.name").value("test"))
				.andExpect(jsonPath("$.data.nickname").value("테스트"))
				.andExpect(jsonPath("$.data.email").value("test@gmail.com"))
				.andDo(print());
	}

	@Test
	@DisplayName("회원 닉네임 변경 성공")
	@WithMockMember(role = RoleType.USER)
	void successUpdateMemberNickname() throws Exception {
		// given
		MemberNicknameUpdateReqDto memberNicknameUpdateReqDto = MemberNicknameUpdateReqDto.builder()
				.nickname("테스트2")
				.build();

		MemberResDto memberResDto = MemberResDto.builder()
				.id(1L)
				.name("test")
				.nickname("테스트2")
				.email("test@gmail.com")
				.build();

		given(memberService.updateMemberNickname("test@gmail.com", memberNicknameUpdateReqDto))
				.willReturn(memberResDto);

		// when
		// then
		mockMvc.perform(patch("/api/members/info/nickname")
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(
								memberNicknameUpdateReqDto
						)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(1L))
				.andExpect(jsonPath("$.data.name").value("test"))
				.andExpect(jsonPath("$.data.nickname").value("테스트2"))
				.andExpect(jsonPath("$.data.email").value("test@gmail.com"))
				.andDo(print());
	}

	@Test
	@DisplayName("회원 비밀번호 변경 성공")
	@WithMockMember(role = RoleType.USER)
	void successUpdateMemberPassword() throws Exception {
		// given
		MemberPasswordUpdateReqDto memberPasswordUpdateReqDto = MemberPasswordUpdateReqDto.builder()
				.currentPassword("asdfasdfasdf")
				.newPassword("qwerqwerqwer")
				.newPasswordCheck("qwerqwerqwer")
				.build();

		given(memberService.updateMemberPassword("test@gmail.com", memberPasswordUpdateReqDto))
				.willReturn(MemberResDto.builder()
						.id(1L)
						.name("test")
						.nickname("테스트")
						.email("test@gmail.com")
						.build());

		// when
		// then
		mockMvc.perform(patch("/api/members/info/password")
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(
								memberPasswordUpdateReqDto
						)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("회원 비밀번호 변경에 성공하였습니다."))
				.andExpect(jsonPath("$.data.id").value(1L))
				.andExpect(jsonPath("$.data.name").value("test"))
				.andExpect(jsonPath("$.data.nickname").value("테스트"))
				.andExpect(jsonPath("$.data.email").value("test@gmail.com"))
				.andDo(print());

	}
}