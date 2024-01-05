package team.rescue.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.rescue.auth.user.PrincipalDetails;
import team.rescue.common.file.FileService;
import team.rescue.cook.entity.Cook;
import team.rescue.cook.repository.CookRepository;
import team.rescue.error.exception.UserException;
import team.rescue.error.type.UserError;
import team.rescue.member.entity.Member;
import team.rescue.member.repository.MemberRepository;
import team.rescue.recipe.entity.Recipe;
import team.rescue.recipe.repository.RecipeRepository;
import team.rescue.review.dto.ReviewDto.ReviewDetailDto;
import team.rescue.review.dto.ReviewDto.ReviewInfoDto;
import team.rescue.review.dto.ReviewDto.ReviewReqDto;
import team.rescue.review.entity.Review;
import team.rescue.review.repository.ReviewRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final MemberRepository memberRepository;
	private final CookRepository cookRepository;
	private final RecipeRepository recipeRepository;
	private final FileService fileService;


	@Transactional
	public ReviewInfoDto createReview(
			ReviewReqDto reviewReqDto,
			MultipartFile image,
			PrincipalDetails details
	) {

		log.info("[리뷰 생성]");

		Member member = memberRepository.findUserByEmail(details.getMember().getEmail())
				.orElseThrow(() -> new UserException(UserError.NOT_FOUND_USER));

		Cook cook = cookRepository.getReferenceById(reviewReqDto.getCookId());
		Recipe recipe = recipeRepository.getReferenceById(reviewReqDto.getRecipeId());

		Review review = Review.builder()
				.member(member)
				.recipe(recipe)
				.cook(cook)
				.title(reviewReqDto.getTitle())
				.contents(reviewReqDto.getContents())
				.imageUrl(fileService.uploadImageToS3(image))
				.build();

		return ReviewInfoDto.fromEntity(reviewRepository.save(review));
	}

	public ReviewDetailDto readReview() {

		return null;
	}


}
