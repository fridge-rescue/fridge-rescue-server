package team.rescue.auth.filter;

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
import team.rescue.auth.dto.LoginDto.LoginReqDto;
import team.rescue.auth.dto.LoginDto.LoginResDto;
import team.rescue.auth.provider.JwtTokenProvider;
import team.rescue.auth.type.JwtTokenType;
import team.rescue.auth.user.PrincipalDetails;
import team.rescue.error.exception.AuthException;
import team.rescue.error.exception.ServiceException;
import team.rescue.error.type.AuthError;
import team.rescue.error.type.ServiceError;
import team.rescue.member.entity.Member;
import team.rescue.member.repository.MemberRepository;
import team.rescue.util.RedisUtil;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private static final String LOGIN_PATH = "/api/auth/email/login";
	private static final String TOKEN_PREFIX = "Bearer ";
	private static final String HEADER_ACCESS_TOKEN = "Access-Token";
	private static final String HEADER_REFRESH_TOKEN = "Refresh-Token";
	private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24;   // 24h

	private final ObjectMapper objectMapper;
	private final AuthenticationManager authenticationManager;
	private final RedisUtil redisUtil;
	private final MemberRepository memberRepository;

	public JwtAuthenticationFilter(
			AuthenticationManager authenticationManager,
			ObjectMapper objectMapper,
			RedisUtil redisUtil,
			MemberRepository memberRepository
	) {
		setFilterProcessesUrl(LOGIN_PATH);
		this.authenticationManager = authenticationManager;
		this.objectMapper = objectMapper;
		this.redisUtil = redisUtil;
		this.memberRepository = memberRepository;
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
			throw new AuthException(AuthError.AUTHENTICATION_FAILURE);
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
		final PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

		// access token 생성
		String accessToken = JwtTokenProvider.createToken(principalDetails, JwtTokenType.ACCESS_TOKEN);

		// refresh token 생성
		String refreshToken = JwtTokenProvider.createToken(principalDetails,
				JwtTokenType.REFRESH_TOKEN);

		saveRefreshToken(principalDetails, refreshToken);

		// redis 저장
		redisUtil.put(principalDetails.getUsername(), refreshToken, REFRESH_TOKEN_EXPIRE_TIME);

		LoginResDto loginResponse = new LoginResDto(principalDetails.getMember());

		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		// access token, refresh token을 Header에 담아서 클라이언트에게 전달
		response.setHeader(HEADER_ACCESS_TOKEN, TOKEN_PREFIX + accessToken);
		response.setHeader(HEADER_REFRESH_TOKEN, TOKEN_PREFIX + refreshToken);

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

	private void saveRefreshToken(PrincipalDetails principalDetails, String refreshToken) {
		Member member = memberRepository.findUserByEmail(principalDetails.getUsername())
				.orElseThrow(() -> new ServiceException(ServiceError.USER_NOT_FOUND));

		member.updateToken(refreshToken);
		memberRepository.save(member);
	}
}
