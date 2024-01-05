package team.rescue.review.dto;

import lombok.Getter;
import lombok.Setter;
import team.rescue.member.dto.MemberDto.MemberInfoDto;
import team.rescue.review.entity.Review;

public class ReviewDto {

	@Getter
	@Setter
	public static class ReviewReqDto {

		// TODO: Valid 추가 필요

		private Long recipeId;
		private Long cookId;
		private String title;
		private String contents;
	}

	@Getter
	@Setter
	public static class ReviewInfoDto {

		private Long id;
		private String title;
		private String imageUrl;
		private MemberInfoDto member;

		public static ReviewInfoDto fromEntity(Review review) {

			ReviewInfoDto reviewInfo = new ReviewInfoDto();
			reviewInfo.setId(review.getId());
			reviewInfo.setTitle(review.getTitle());
			reviewInfo.setImageUrl(review.getImageUrl());
			reviewInfo.setMember(MemberInfoDto.fromEntity(review.getMember()));

			return reviewInfo;
		}

	}

	@Getter
	@Setter
	public static class ReviewDetailDto {

	}

}
