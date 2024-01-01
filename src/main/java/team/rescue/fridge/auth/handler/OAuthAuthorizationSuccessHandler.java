package team.rescue.fridge.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import team.rescue.fridge.auth.user.OAuthUser;

@Slf4j
public class OAuthAuthorizationSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(
			HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication
	) throws IOException, ServletException {
		
		// TODO: OAuth2 인증 성공 후 실행할 로직 작성

		// SecurityContext에 저장된 Authentication 객체
		OAuthUser principalDetails = (OAuthUser) authentication.getPrincipal();
		log.info(principalDetails.getAttributes().toString());

		// 여기서 jwt 토큰을 가지고 로그인 로직 구현
	}
}
