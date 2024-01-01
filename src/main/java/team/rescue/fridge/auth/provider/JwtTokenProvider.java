package team.rescue.fridge.auth.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import team.rescue.fridge.auth.user.AuthUser;
import team.rescue.fridge.member.entity.Member;
import team.rescue.fridge.auth.type.JwtTokenType;
import team.rescue.fridge.auth.type.RoleType;


@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

	private static final String HEADER = "Authorization";
	private static final String KEY_ID = "id";
	private static final String KEY_ROLE = "role";
	private static String JWT_TOKEN_KEY;

	/**
	 * 토큰 타입에 따라 토큰 생성
	 */
	public static String createToken(AuthUser authMember, JwtTokenType tokenType) {

		log.debug("[JWT {} 발급] email={}", tokenType.name(), authMember.getUsername());

		Date now = new Date(System.currentTimeMillis());
		Date expireDate = new Date(now.getTime() + tokenType.getExpireTime());

		return JWT.create()
				.withSubject(authMember.getUsername())
				.withIssuedAt(now)
				.withExpiresAt(expireDate)
				.withClaim(KEY_ID, authMember.getMember().getId())
				.withClaim(KEY_ROLE, authMember.getMember().getRole().name())
				.sign(Algorithm.HMAC512(JWT_TOKEN_KEY));
	}

	/**
	 * 토큰 검증
	 * <p>검증된 유저의 경우 강제 로그인 처리를 위해 UserDetails 객체를 만들어 반환
	 *
	 * @param token 검증할 토큰
	 * @return 검증된 유저의 UserDetails 객체
	 */
	public static AuthUser verify(String token) {

		log.debug("start verify token={}", token);

		DecodedJWT decodedJWT =
				JWT.require(Algorithm.HMAC512(JWT_TOKEN_KEY)).build().verify(token);

		Long id = decodedJWT.getClaim(KEY_ID).asLong();
		String email = decodedJWT.getSubject();
		String role = decodedJWT.getClaim(KEY_ROLE).asString();

		Member member = Member.builder()
				.id(id)
				.email(email)
				.role(RoleType.valueOf(role))
				.build();

		log.debug("role={}", member.getRole());
		return new AuthUser(member);
	}

	public static boolean isExpiredToken(String token) {
		return JWT.decode(token).getExpiresAt().before(new Date(System.currentTimeMillis()));
	}
}
