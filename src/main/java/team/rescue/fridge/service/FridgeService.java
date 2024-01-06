package team.rescue.fridge.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.rescue.error.exception.ServiceException;
import team.rescue.error.exception.UserException;
import team.rescue.error.type.ServiceError;
import team.rescue.error.type.UserError;
import team.rescue.fridge.dto.FridgeDto;
import team.rescue.fridge.dto.FridgeIngredientDto.FridgeIngredientAddReqDto;
import team.rescue.fridge.dto.FridgeIngredientDto.FridgeIngredientResDto;
import team.rescue.fridge.entity.Fridge;
import team.rescue.fridge.entity.FridgeIngredient;
import team.rescue.fridge.repository.FridgeIngredientRepository;
import team.rescue.fridge.repository.FridgeRepository;
import team.rescue.member.entity.Member;
import team.rescue.member.repository.MemberRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FridgeService {

	private final FridgeRepository fridgeRepository;
	private final MemberRepository memberRepository;
	private final FridgeIngredientRepository fridgeIngredientRepository;

	/**
	 * 냉장고 생성
	 *
	 * @param member 냉장고 소유 유저
	 * @return 생성된 냉장고
	 */
	@Transactional
	public Fridge createFridge(Member member) {

		return fridgeRepository.save(Fridge.builder().member(member).build());
	}

	/**
	 * <p>냉장고 재료를 조회하는 메소드
	 *
	 * @param email
	 * @return
	 */
	public FridgeDto getFridgeIngredients(String email) {
		Member member = memberRepository.findUserByEmail(email)
				.orElseThrow(() -> new UserException(UserError.USER_NOT_FOUND));

		Fridge fridge = fridgeRepository.findByMember(member)
				.orElseThrow(() -> new ServiceException(ServiceError.FRIDGE_NOT_FOUND));

		List<FridgeIngredient> fridgeIngredients = fridgeIngredientRepository.findByFridge(fridge);
		List<FridgeIngredientResDto> fridgeIngredientResDtoList = fridgeIngredients.stream()
				.map(FridgeIngredientResDto::of)
				.collect(Collectors.toList());

		return FridgeDto.builder()
				.id(fridge.getId())
				.fridgeIngredientResDtoList(fridgeIngredientResDtoList)
				.build();
	}

	/**
	 * <p>입력받은 리스트를 순회하면서 냉장고에 재료를 저장
	 * 이름, 메모, 유통기한이 모두 동일한 재료가 두 번 입력되는 경우 재료 등록 처리 하지 않음
	 *
	 * @param email
	 * @param fridgeIngredientAddReqDtoList
	 * @return
	 */
	@Transactional
	public List<FridgeIngredientResDto> addIngredient(String email,
			List<FridgeIngredientAddReqDto> fridgeIngredientAddReqDtoList) {
		Member member = memberRepository.findUserByEmail(email)
				.orElseThrow(() -> new UserException(UserError.USER_NOT_FOUND));

		Fridge fridge = fridgeRepository.findByMember(member)
				.orElseThrow(() -> new ServiceException(ServiceError.FRIDGE_NOT_FOUND));

		for (FridgeIngredientAddReqDto fridgeIngredientAddReqDto : fridgeIngredientAddReqDtoList) {
			if (!fridgeIngredientRepository.existsByNameAndMemoAndExpiredAt(
					fridgeIngredientAddReqDto.getName(),
					fridgeIngredientAddReqDto.getMemo(),
					fridgeIngredientAddReqDto.getExpiredAt())) {

				FridgeIngredient fridgeIngredient = FridgeIngredient.builder()
						.fridge(fridge)
						.name(fridgeIngredientAddReqDto.getName())
						.memo(fridgeIngredientAddReqDto.getMemo())
						.expiredAt(fridgeIngredientAddReqDto.getExpiredAt())
						.build();

				fridgeIngredientRepository.save(fridgeIngredient);
			}
		}

		List<FridgeIngredient> fridgeIngredients = fridgeIngredientRepository.findByFridge(fridge);
		return fridgeIngredients.stream().map(FridgeIngredientResDto::of)
				.collect(Collectors.toList());
	}

}
