package team.rescue.fridge.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.rescue.fridge.auth.dto.JoinDto;
import team.rescue.fridge.auth.dto.JoinDto.JoinResDto;
import team.rescue.fridge.auth.service.AuthService;
import team.rescue.fridge.auth.type.ProviderType;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	/**
	 * Email 회원 가입
	 *
	 * @param joinReqDto 이메일 가입 시 입력값
	 * @return GUEST 회원 정보
	 */
	@PostMapping("/email/join")
	public ResponseEntity<?> joinWithEmail(
			@RequestBody @Valid JoinDto.JoinReqDto joinReqDto
	) {

		log.info("[이메일 회원 가입] email={}", joinReqDto.getEmail());

		JoinResDto joinResDto = authService.createEmailUser(joinReqDto);

		return ResponseEntity.ok(joinResDto);
	}

	/**
	 * OAuth 회원 가입/로그인
	 *
	 * @param response     OAuth Type 별 리다이렉트
	 * @param providerType OAuth Provider Type
	 */
	@GetMapping("/oauth")
	public void joinWithOAuth(
			HttpServletResponse response,
			@RequestParam ProviderType providerType
	) throws IOException {

		if (providerType == ProviderType.GOOGLE) {
			log.info("[Google 회원 가입]");
			response.sendRedirect("/oauth2/authorization/google");
		}
	}

}