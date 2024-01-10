package team.rescue.notification.service;

import static team.rescue.error.type.ServiceError.USER_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.rescue.error.exception.ServiceException;
import team.rescue.member.entity.Member;
import team.rescue.member.repository.MemberRepository;
import team.rescue.notification.dto.NotificationDto.NotificationInfoDto;
import team.rescue.notification.entity.Notification;
import team.rescue.notification.repository.NotificationRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

	private final MemberRepository memberRepository;
	private final NotificationRepository notificationRepository;

	public Page<NotificationInfoDto> getNotifications(String email, Pageable pageable) {
		Member member = memberRepository.findUserByEmail(email)
				.orElseThrow(() -> new ServiceException(USER_NOT_FOUND));

		Page<Notification> notificationPage = notificationRepository.findByMember(member, pageable);

		return notificationPage.map(NotificationInfoDto::of);
	}
}
