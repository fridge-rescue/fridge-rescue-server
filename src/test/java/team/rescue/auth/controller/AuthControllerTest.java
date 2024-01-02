package team.rescue.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import team.rescue.auth.dto.JoinDto.JoinReqDto;
import team.rescue.member.repository.MemberRepository;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@Transactional
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("이메일 회원 가입 시 인증 메일이 전송 되고, 인증 전까지 유저는 GUEST 권한을 갖는다.")
	public void emailJoin_test() throws Exception {

		// given
		JoinReqDto joinReqDto = new JoinReqDto();
		joinReqDto.setName("이름");
		joinReqDto.setNickname("닉네임");
		joinReqDto.setEmail("test@gmail.com");
		joinReqDto.setPassword("1234567890");

		String requestBody = objectMapper.writeValueAsString(joinReqDto);

		// when
		ResultActions resultActions = mockMvc.perform(
				post("/api/auth/email/join").content(requestBody)
						.contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions.andExpect(status().isCreated());
	}

}
