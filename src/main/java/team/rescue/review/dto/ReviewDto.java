package team.rescue.review.dto;

import lombok.Getter;
import lombok.Setter;
import team.rescue.member.dto.MemberDto.MemberInfoDto;
import team.rescue.recipe.dto.RecipeDto.RecipeInfoDto;
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
		private MemberInfoDto author;

		public static ReviewInfoDto fromEntity(Review review) {

			ReviewInfoDto reviewInfo = new ReviewInfoDto();
			reviewInfo.setId(review.getId());
			reviewInfo.setTitle(review.getTitle());
			reviewInfo.setImageUrl(review.getImageUrl());
			reviewInfo.setAuthor(MemberInfoDto.fromEntity(review.getMember()));

			return reviewInfo;
		}

	}

	@Getter
	@Setter
	public static class ReviewDetailDto {

		private Long id;
		private String title;
		private String imageUrl;
		private String contents;
		private MemberInfoDto author;
		private RecipeInfoDto recipe;

		public static ReviewDetailDto fromEntity(Review review) {

			ReviewDetailDto reviewDetail = new ReviewDetailDto();
			reviewDetail.setId(review.getId());
			reviewDetail.setTitle(review.getTitle());
			reviewDetail.setImageUrl(review.getImageUrl());
			reviewDetail.setContents(review.getContents());
			reviewDetail.setAuthor(MemberInfoDto.fromEntity(review.getMember()));
			reviewDetail.setRecipe(RecipeInfoDto.fromEntity(review.getRecipe()));

			return reviewDetail;
		}

	}

}
