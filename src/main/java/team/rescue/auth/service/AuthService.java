package team.rescue.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.rescue.auth.dto.JoinDto.JoinReqDto;
import team.rescue.auth.dto.JoinDto.JoinResDto;
import team.rescue.auth.provider.MailProvider;
import team.rescue.auth.type.ProviderType;
import team.rescue.auth.type.RoleType;
import team.rescue.auth.user.PrincipalDetails;
import team.rescue.fridge.service.FridgeService;
import team.rescue.member.entity.Member;
import team.rescue.member.repository.MemberRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

	private final static String ENCODING = "UTF-8";

	private final MailProvider mailProvider;
	private final PasswordEncoder passwordEncoder;
	private final FridgeService fridgeService;
	private final MemberRepository memberRepository;


	@Override
	public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
		log.debug("[+] loadUserByUsername start");
		Member member = memberRepository.findUserByEmail(userEmail)
				.orElseThrow(() -> {
					log.error("Invalid User Email!!");
					return new RuntimeException();
				});

		log.debug(member.getEmail());
		return new PrincipalDetails(member);
	}

	/**
	 * 유저 생성
	 * <p> 사용자 Name, Email, Password 를 받아 새로운 유저 생성
	 * <p> Email 중복 여부 검증
	 * <p> 인증 Email 전송
	 *
	 * @param joinReqDto Email 회원가입 요청 DTO
	 * @return 생성된 User Entity
	 */
	@Transactional
	public JoinResDto createEmailUser(JoinReqDto joinReqDto) {

		log.info("[Email 회원 가입] email={}", joinReqDto.getEmail());

		// Email 중복 검증
		validateCreateMember(joinReqDto.getEmail());

		Member member = Member.builder()
				.name(joinReqDto.getName())
				.nickname(joinReqDto.getNickname())
				.email(joinReqDto.getEmail())
				.password(passwordEncoder.encode(joinReqDto.getPassword()))
				.role(RoleType.GUEST)
				.provider(ProviderType.EMAIL)
				.build();

		// 인증 Email 전송 및 DTO 반환
		return new JoinResDto(sendConfirmEmail(member));
	}

	/**
	 * 인증 이메일 전송
	 *
	 * @param member 전송할 유저
	 * @return 이메일 인증 코드 업데이트 처리 된 유저
	 */
	@Transactional
	public Member sendConfirmEmail(Member member) {

		log.info("[인증 메일 전송] email={}", member.getEmail());

		String emailCode = mailProvider.sendEmail(member);
		member.updateEmailCode(emailCode);

		log.info("[인증 메일 전송 완료]");

		return memberRepository.save(member);
	}

	/**
	 * 회원 생성 검증
	 * <p> 이메일 중복 불가
	 *
	 * @param email 검증할 이메일
	 */
	private void validateCreateMember(String email) {

		// TODO: 에러 핸들링
		if (memberRepository.existsByEmail(email)) {
			log.error("Email 중복");
			throw new RuntimeException();
		}
	}
}
