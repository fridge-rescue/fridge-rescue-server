package team.rescue.fridge.smtp;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.rescue.fridge.smtp.dto.JoinDto;
import team.rescue.fridge.smtp.dto.JoinDto.JoinResDto;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final MailProvider smtpService;
	private final AuthService authService;

	/**
	 * Email 회원가입
	 *
	 * @param joinReqDto 이메일 가입 시 입력값
	 * @return GUEST 회원 정보
	 */
	@PostMapping("/email")
	public ResponseEntity<?> emailJoin(
			@RequestBody @Valid JoinDto.JoinReqDto joinReqDto
	) {

		log.info("[AuthController.join] name={}, email={}, password={}",
				joinReqDto.getName(),
				joinReqDto.getEmail(), joinReqDto.getPassword());

		JoinResDto joinResDto = authService.createEmailUser(joinReqDto);

		return ResponseEntity.ok(joinResDto);
	}

}
