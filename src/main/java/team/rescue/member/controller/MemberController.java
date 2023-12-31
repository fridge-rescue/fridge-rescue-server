package team.rescue.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.rescue.auth.user.PrincipalDetails;
import team.rescue.common.dto.ResponseDto;
import team.rescue.member.dto.MemberDto;
import team.rescue.member.dto.MemberDto.MemberDetailDto;
import team.rescue.member.service.MemberService;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/info")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<ResponseDto<MemberDetailDto>> getMemberInfo(
			@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		String email = principalDetails.getUsername();

		MemberDetailDto memberDetailDto = memberService.getMemberInfo(email);

		return ResponseEntity.ok(new ResponseDto<>("회원 정보 조회에 성공하였습니다.", memberDetailDto));
	}

	@PatchMapping("/info/nickname")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<ResponseDto<MemberDetailDto>> updateMemberNickname(
			@RequestBody @Valid MemberDto.MemberNicknameUpdateDto memberNicknameUpdateDto,
			BindingResult bindingResult,
			@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		String email = principalDetails.getUsername();

		MemberDetailDto memberDetailDto = memberService.updateMemberNickname(email,
				memberNicknameUpdateDto);

		return ResponseEntity.ok(new ResponseDto<>("회원 닉네임 변경에 성공하였습니다.", memberDetailDto));
	}

	@PatchMapping("/info/password")
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<ResponseDto<MemberDetailDto>> updateMemberPassword(
			@RequestBody @Valid MemberDto.MemberPasswordUpdateDto memberPasswordUpdateDto,
			BindingResult bindingResult,
			@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		String email = principalDetails.getUsername();

		MemberDetailDto memberDetailDto = memberService.updateMemberPassword(email,
				memberPasswordUpdateDto);

		return ResponseEntity.ok(new ResponseDto<>("회원 비밀번호 변경에 성공하였습니다.", memberDetailDto));
	}
}
