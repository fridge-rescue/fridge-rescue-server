package team.rescue.notification.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.rescue.auth.user.PrincipalDetails;
import team.rescue.common.dto.ResponseDto;
import team.rescue.notification.dto.NotificationDto.NotificationInfoDto;
import team.rescue.notification.service.NotificationService;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<ResponseDto<Page<NotificationInfoDto>>> getNotifications(
			@AuthenticationPrincipal PrincipalDetails principalDetails,
			@PageableDefault Pageable pageable
	) {
		Page<NotificationInfoDto> notifications = notificationService.getNotifications(
				principalDetails.getUsername(), pageable);

		return ResponseEntity.ok(new ResponseDto<>("알림 조회 성공", notifications));
	}
}
