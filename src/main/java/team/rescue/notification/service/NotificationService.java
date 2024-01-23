package team.rescue.notification.service;

import static team.rescue.error.type.AuthError.ACCESS_DENIED;
import static team.rescue.error.type.ServiceError.NOTIFICATION_NOT_FOUND;
import static team.rescue.error.type.ServiceError.USER_NOT_FOUND;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import team.rescue.error.exception.AuthException;
import team.rescue.error.exception.ServiceException;
import team.rescue.member.entity.Member;
import team.rescue.member.repository.MemberRepository;
import team.rescue.notification.dto.NotificationDto.NotificationCheckDto;
import team.rescue.notification.dto.NotificationDto.NotificationInfoDto;
import team.rescue.notification.entity.Notification;
import team.rescue.notification.event.NotificationEvent;
import team.rescue.notification.repository.NotificationRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

	private final MemberRepository memberRepository;
	private final NotificationRepository notificationRepository;
	private final RedisMessageService redisMessageService;
	private final SseEmitterService sseEmitterService;

	public Page<NotificationInfoDto> getNotifications(String email, Pageable pageable) {
		Member member = memberRepository.findUserByEmail(email)
				.orElseThrow(() -> new ServiceException(USER_NOT_FOUND));

		Page<Notification> notificationPage = notificationRepository.findByMember(member, pageable);

		return notificationPage.map(NotificationInfoDto::of);
	}

	@Transactional
	public void checkNotifications(NotificationCheckDto notificationCheckDto, String email) {
		Member member = memberRepository.findUserByEmail(email)
				.orElseThrow(() -> new ServiceException(USER_NOT_FOUND));

		for (Long notificationId : notificationCheckDto.getNotificationIds()) {
			Notification notification = notificationRepository.findById(notificationId)
					.orElseThrow(() -> new ServiceException(NOTIFICATION_NOT_FOUND));

			if (!Objects.equals(notification.getMember().getId(), member.getId())) {
				throw new AuthException(ACCESS_DENIED);
			}

			notification.updateCheckedAt(LocalDateTime.now());
			notificationRepository.save(notification);
		}
	}

	@Transactional
	public NotificationInfoDto checkNotification(Long notificationId, String email) {
		Member member = memberRepository.findUserByEmail(email)
				.orElseThrow(() -> new ServiceException(USER_NOT_FOUND));

		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new ServiceException(NOTIFICATION_NOT_FOUND));

		if (!Objects.equals(notification.getMember().getId(), member.getId())) {
			throw new AuthException(ACCESS_DENIED);
		}

		notification.updateCheckedAt(LocalDateTime.now());
		Notification updateNotification = notificationRepository.save(notification);

		return NotificationInfoDto.of(updateNotification);
	}

	public SseEmitter subscribe(String email, String lastEventId) {
		String id = email + "_" + System.currentTimeMillis();

		SseEmitter sseEmitter = sseEmitterService.createEmitter(id);

		sseEmitter.onTimeout(() -> sseEmitterService.deleteEmitter(id));
		sseEmitter.onError(e -> sseEmitter.complete());
		sseEmitter.onCompletion(() -> {
			sseEmitterService.deleteEmitter(id);
			redisMessageService.removeSubscribe(email);
		});

		// 503 에러 방지를 위한 더미 데이터 추가
		sseEmitterService.send("EventStream Created. [userEmail=" + email + "]", email, sseEmitter);

		if (!lastEventId.isEmpty()) {
			Map<String, Object> eventCache = sseEmitterService.getEventCache(email);
			eventCache.entrySet().stream()
					.filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
					.forEach(entry -> sseEmitterService.send(entry.getValue(), entry.getKey(), sseEmitter));
		}

		redisMessageService.subscribe(email);

		return sseEmitter;
	}

	@Transactional
	public void sendNotification(NotificationEvent event) {
		Member member = memberRepository.findUserByEmail(event.email())
				.orElseThrow(() -> new ServiceException(USER_NOT_FOUND));

		Notification notification = Notification.builder()
				.member(member)
				.notificationType(event.notificationType())
				.notificationProperty(event.notificationProperty())
				.createdAt(event.createdAt())
				.checkedAt(event.checkedAt())
				.build();

		if (!notificationRepository.existsByMemberAndNotificationTypeAndNotificationProperty(
				member,
				notification.getNotificationType(),
				notification.getNotificationProperty()
		)) {

			notificationRepository.save(notification);

			Map<String, SseEmitter> emitters = sseEmitterService.getEmitters(member.getEmail());
			emitters.forEach(
					(key, emitter) -> {
						sseEmitterService.saveEventCache(key, notification);
						sseEmitterService.send(NotificationInfoDto.of(notification), key, emitter);
					}
			);

			redisMessageService.publish(event.email(), NotificationInfoDto.of(notification));
		}
	}
}
