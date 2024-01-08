package team.rescue.review.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import team.rescue.auth.user.PrincipalDetails;
import team.rescue.common.dto.ResponseDto;
import team.rescue.review.dto.ReviewDto.ReviewDetailDto;
import team.rescue.review.dto.ReviewDto.ReviewInfoDto;
import team.rescue.review.dto.ReviewDto.ReviewReqDto;
import team.rescue.review.service.ReviewService;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	/**
	 * 리뷰 등록
	 *
	 * @param data    리뷰 등록 요청 데이터
	 * @param image   리뷰 사진
	 * @param details 로그인 유저
	 * @return 생성된 리뷰 요약 데이터
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<ResponseDto<ReviewInfoDto>> createReview(
			@RequestPart ReviewReqDto data,
			@RequestPart MultipartFile image,
			@AuthenticationPrincipal PrincipalDetails details
	) {

		log.info("[리뷰 업로드] recipeId={}, title={}, imageOriginFileName={}", data.getRecipeId(),
				data.getTitle(), image.getOriginalFilename());

		ReviewInfoDto reviewInfo = reviewService.createReview(data, image, details);

		return new ResponseEntity<>(
				new ResponseDto<>("레시피 리뷰가 등록되었습니다.", reviewInfo),
				HttpStatus.CREATED
		);

	}

	/**
	 * 특정 리뷰 상세 조회
	 *
	 * @param reviewId 조회할 리뷰 아이디
	 * @return 해당 리뷰 상세 DTO
	 */
	@GetMapping("/{reviewId}")
	@PreAuthorize("permitAll()")
	public ResponseEntity<ResponseDto<ReviewDetailDto>> getReviewDetail(
			@PathVariable Long reviewId
	) {

		ReviewDetailDto reviewDetailDto = reviewService.getReview(reviewId);

		return new ResponseEntity<>(
				new ResponseDto<>(null, reviewDetailDto),
				HttpStatus.CREATED
		);
	}

}
