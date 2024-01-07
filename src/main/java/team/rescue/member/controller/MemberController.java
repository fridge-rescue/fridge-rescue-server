package team.rescue.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.rescue.auth.user.PrincipalDetails;
import team.rescue.common.dto.ResponseDto;
import team.rescue.member.dto.MemberDto.MemberResDto;
import team.rescue.member.service.MemberService;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/info")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<ResponseDto<MemberResDto>> getMembersInfo(
			@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		String email = principalDetails.getUsername();

		MemberResDto memberResDto = memberService.getMembersInfo(email);

		return ResponseEntity.ok(new ResponseDto<>(1, "회원 정보 조회에 성공하였습니다.", memberResDto));
	}
}
