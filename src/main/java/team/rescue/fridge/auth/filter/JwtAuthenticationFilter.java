package team.rescue.fridge.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import team.rescue.fridge.auth.dto.LoginDto.LoginReqDto;
import team.rescue.fridge.auth.dto.LoginDto.LoginResDto;
import team.rescue.fridge.auth.dto.TokenDto;
import team.rescue.fridge.auth.provider.JwtTokenProvider;
import team.rescue.fridge.auth.type.JwtTokenType;
import team.rescue.fridge.auth.user.AuthUser;
import team.rescue.fridge.util.RedisUtil;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private static final String LOGIN_PATH = "/api/auth/login";
	private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24;   // 24h

	private final ObjectMapper objectMapper;
	private final AuthenticationManager authenticationManager;
	private final RedisUtil redisUtil;

	public JwtAuthenticationFilter(
			AuthenticationManager authenticationManager,
			ObjectMapper objectMapper,
			RedisUtil redisUtil) {

		setFilterProcessesUrl(LOGIN_PATH);
		this.authenticationManager = authenticationManager;
		this.objectMapper = objectMapper;
		this.redisUtil = redisUtil;
	}

	@Override
	public Authentication attemptAuthentication(
			HttpServletRequest request, HttpServletResponse response
	) throws AuthenticationException {

		log.debug("로그인 시도 : {}", request.getRequestURL());

		try {
			LoginReqDto loginReqDto = objectMapper.readValue(request.getInputStream(),
					LoginReqDto.class);
			log.debug("이메일: {}, 비밀번호: {}", loginReqDto.getEmail(), loginReqDto.getPassword());

			UsernamePasswordAuthenticationToken authRequestToken =
					new UsernamePasswordAuthenticationToken(loginReqDto.getEmail(),
							loginReqDto.getPassword());

			return authenticationManager.authenticate(authRequestToken);

		} catch (Exception e) {
			log.error("loginDto 읽기 실패");
			throw new RuntimeException();
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request,
			HttpServletResponse response,
			FilterChain chain,
			Authentication authentication) throws IOException {

		log.debug("JwtAuthenticationFilter.successfulAuthentication request={}",
				request.getRequestURI());

		// 로그인에 성공한 유저
		final AuthUser authMember = (AuthUser) authentication.getPrincipal();

		// access token 생성
		String accessToken = JwtTokenProvider.createToken(authMember, JwtTokenType.ACCESS_TOKEN);

		// refresh token 생성
		String refreshToken = JwtTokenProvider.createToken(authMember, JwtTokenType.REFRESH_TOKEN);

		// redis 저장
		redisUtil.put(authMember.getUsername(), refreshToken, REFRESH_TOKEN_EXPIRE_TIME);

		TokenDto tokenDto = new TokenDto(accessToken, refreshToken);
		LoginResDto loginResponse = new LoginResDto(authMember.getMember(), tokenDto);

		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		// access token, refresh token을 클라이언트에게 전달
		new ObjectMapper().writeValue(response.getOutputStream(), loginResponse);

	}

	/**
	 * 로그인 인증 실패 시 호출되는 메서드
	 */
	@Override
	protected void unsuccessfulAuthentication(
			HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
			throws IOException, ServletException {

		log.error("unsuccessfulAuthentication failed.getLocalizedMessage(): {}",
				failed.getLocalizedMessage());

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("code", HttpStatus.UNAUTHORIZED.value());
		body.put("error", failed.getMessage());

		new ObjectMapper().writeValue(response.getOutputStream(), body);
	}
}
