package team.rescue.fridge.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import team.rescue.fridge.dto.AuthMember;
import team.rescue.fridge.member.MemberRepository;
import team.rescue.fridge.member.entity.Member;

@Slf4j
@Component
public class MemberDetailService implements UserDetailsService {
  private final MemberRepository memberRepository;

  public MemberDetailService(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
    log.debug("[+] loadUserByUsername start");
    Member member = memberRepository.findUserByEmail(userEmail)
        .orElseThrow(() -> {
              log.error("Invalid User Email!!");
              return new RuntimeException();
            });
    log.debug(member.getEmail());
    return new AuthMember(member);
  }
}
