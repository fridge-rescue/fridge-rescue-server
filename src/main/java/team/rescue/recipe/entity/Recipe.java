package team.rescue.recipe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import team.rescue.member.entity.Member;

@Entity
@Table(name = "recipe")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Recipe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recipe_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Column(name = "title", nullable = false, length = 100)
	private String title;

	@Column(name = "summary", length = 100)
	private String summary;

	@Column(name = "recipe_image_url", length = 150)
	private String recipeImageUrl;

	@Column(name = "view_count", nullable = false)
	private Integer viewCount;

	@Column(name = "review_count", nullable = false)
	private Integer reviewCount;

	@Column(name = "report_count", nullable = false)
	private Integer reportCount;

	@Column(name = "bookmark_count", nullable = false)
	private Integer bookmarkCount;

	@CreatedDate
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "modified_at")
	private LocalDateTime modifiedAt;

	public void update(String title, String summary, String recipeImageUrl) {
		this.title = title;
		this.summary = summary;
		this.recipeImageUrl = recipeImageUrl;
	}
}
