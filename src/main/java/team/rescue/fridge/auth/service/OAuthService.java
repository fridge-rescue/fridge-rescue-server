package team.rescue.fridge.auth.service;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import team.rescue.fridge.auth.user.OAuthUser;
import team.rescue.fridge.member.entity.Member;
import team.rescue.fridge.member.repository.MemberRepository;
import team.rescue.fridge.auth.type.ProviderType;
import team.rescue.fridge.auth.type.RoleType;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService extends DefaultOAuth2UserService {

	private static final String PROVIDER_ID = "sub";
	private static final String NICKNAME = "given_name";
	private static final String NAME = "name";
	private static final String EMAIL = "email";

	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		log.info(oAuth2User.getAttributes().toString());

		ProviderType provider = ProviderType.valueOf(
				userRequest.getClientRegistration().getRegistrationId().toUpperCase()); // google
		String providerId = oAuth2User.getAttribute(PROVIDER_ID);
		String nickname = oAuth2User.getAttribute(NICKNAME);
		String name = oAuth2User.getAttribute(NAME);
		String email = oAuth2User.getAttribute(EMAIL);

		String createdPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
		String encryptedPassword = passwordEncoder.encode(createdPassword);

		Optional<Member> findMember
				= memberRepository.findByProviderAndProviderId(provider, providerId);

		if (findMember.isEmpty()) {
			if (memberRepository.existsByEmail(email)) {
				log.error("이미 존재하는 email. 다른 email을 사용하도록 하기 위한 방법 필요.");
				throw new RuntimeException();
			}

			Member savedMember = memberRepository.save(Member.builder()
					.name(name)
					.email(email)
					.nickname(nickname)
					.password(encryptedPassword)
					.role(RoleType.USER)
					.provider(provider)
					.providerId(providerId)
					.build());

			return new OAuthUser(savedMember, oAuth2User.getAttributes());
		} else {
			return new OAuthUser(findMember.get(), oAuth2User.getAttributes());
		}
	}
}
