package team.rescue.review.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import team.rescue.auth.user.PrincipalDetails;
import team.rescue.common.dto.ResponseDto;
import team.rescue.review.dto.ReviewDto.ReviewInfoDto;
import team.rescue.review.dto.ReviewDto.ReviewReqDto;
import team.rescue.review.service.ReviewService;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping
	@PreAuthorize("hasAuthority('USER')")
	public ResponseEntity<?> createReview(
			@RequestPart ReviewReqDto data,
			@RequestPart MultipartFile image,
			@AuthenticationPrincipal PrincipalDetails details
	) {

		log.info("[리뷰 업르드] recipeId={}, title={}, imageOriginFileName={}", data.getRecipeId(),
				data.getTitle(), image.getOriginalFilename());

		ReviewInfoDto reviewInfo = reviewService.createReview(data, image, details);

		return new ResponseEntity<>(
				ResponseDto.builder().data(reviewInfo).message("레시피 리뷰가 정상적으로 등록되었습니다.").build(),
				HttpStatus.CREATED
		);

	}

}
