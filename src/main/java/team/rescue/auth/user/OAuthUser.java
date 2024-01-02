package team.rescue.auth.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import team.rescue.member.entity.Member;

@Getter
@AllArgsConstructor
public class OAuthUser implements OAuth2User {

	private Member member;
	private Map<String, Object> attributes;

	@Override
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect = new ArrayList<>();
		collect.add((GrantedAuthority) () -> member.getRole().toString());

		return collect;
	}

	@Override
	public String getName() {
		return attributes.get("sub").toString();
	}
}
