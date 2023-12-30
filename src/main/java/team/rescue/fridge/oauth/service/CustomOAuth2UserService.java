package team.rescue.fridge.oauth.service;

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
import team.rescue.fridge.member.MemberRepository;
import team.rescue.fridge.member.entity.Member;
import team.rescue.fridge.member.entity.ProviderType;
import team.rescue.fridge.member.entity.RoleType;
import team.rescue.fridge.oauth.PrincipalDetails;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		log.info(oAuth2User.getAttributes().toString());

		ProviderType provider = ProviderType.valueOf(
				userRequest.getClientRegistration().getRegistrationId().toUpperCase()); // google
		String providerId = oAuth2User.getAttribute("sub");
		String nickname = oAuth2User.getAttribute("given_name");
		String name = oAuth2User.getAttribute("name");
		String email = oAuth2User.getAttribute("email");

		String createdPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
		String encryptedPassword = passwordEncoder.encode(createdPassword);

		Optional<Member> findMember
				= memberRepository.findByProviderAndProviderId(provider, providerId);

		if (findMember.isEmpty()) {
			Member savedMember = memberRepository.save(Member.builder()
					.name(name)
					.email(email)
					.nickname(nickname)
					.password(encryptedPassword)
					.role(RoleType.USER)
					.provider(provider)
					.providerId(providerId)
					.build());

			return new PrincipalDetails(savedMember, oAuth2User.getAttributes());
		} else {
			return new PrincipalDetails(findMember.get(), oAuth2User.getAttributes());
		}
	}
}
