package team.rescue.fridge.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import team.rescue.fridge.fridge.Fridge;
import team.rescue.fridge.notification.Notification;
import team.rescue.fridge.review.entity.Cook;
import team.rescue.fridge.review.entity.Review;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fridge_id")
	private Fridge fridge;

	@Column(name = "name", nullable = false, length = 15)
	private String name;

	@Column(name = "nickname", nullable = false, length = 15)
	private String nickname;

	@Column(name = "email", unique = true, nullable = false, length = 50)
	private String email;

	@Column(name = "phone", unique = true, length = 11)
	private String phone;

	@Column(name = "password", nullable = false, length = 100)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false, length = 10)
	private RoleType role;

	@Enumerated(EnumType.STRING)
	@Column(name = "provider", nullable = false, length = 10)
	private ProviderType provider;

	@Column(name = "provider_id", length = 100)
	private String providerId;

	@Column(name = "jwt_token")
	private String token;

	// 알림 조회
	@OneToMany(mappedBy = "member")
	private final List<Notification> notificationList = new ArrayList<>();

	// 요리 완료 조회
	@OneToMany(mappedBy = "member")
	private final List<Cook> cookList = new ArrayList<>();

	// 레시피 후기 조회
	@OneToMany(mappedBy = "member")
	private final List<Review> reviewList = new ArrayList<>();

	@CreatedDate
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "modified_at")
	private LocalDateTime modifiedAt;


	@Builder
	private Member(Long id, Fridge fridge, String name, String nickname, String email, String phone,
			String password, ProviderType provider, String providerId, String token, LocalDateTime createdAt,
			LocalDateTime modifiedAt) {
		this.id = id;
		this.fridge = fridge;
		this.name = name;
		this.nickname = nickname;
		this.email = email;
		this.phone = phone;
		this.password = password;
		this.provider = provider;
		this.providerId = providerId;
		this.token = token;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
	}

	public Member createmMember(
			Fridge fridge,
			String name,
			String nickname,
			String email,
			String phone,
			String password,
			ProviderType provider
	) {

		return Member.builder()
				.fridge(fridge)
				.name(name)
				.nickname(nickname)
				.email(email)
				.phone(phone)
				.password(password)
				.provider(provider)
				.build();
	}
}
