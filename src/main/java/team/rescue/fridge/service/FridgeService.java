package team.rescue.fridge.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
	 * @return 생성된 냉장고
	 * TODO: 이메일 인증 시 생성
	 */
	@Transactional
	public Fridge createFridge() {

		return fridgeRepository.save(Fridge.builder().build());
	}

	/**
	 * <p>냉장고 재료를 조회하는 메소드
	 *
	 * @param email
	 * @return
	 */
	public FridgeDto getFridgeIngredients(String email) {
		Member member = memberRepository.findUserByEmail(email)
				.orElseThrow(() -> {
					log.error("일치하는 사용자 정보 없음");
					return new RuntimeException();
				});

		Fridge fridge = fridgeRepository.findByMember(member)
				.orElseThrow(() -> {
					log.error("해당 회원은 냉장고가 없음");
					return new RuntimeException();
				});

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
				.orElseThrow(() -> {
					log.error("일치하는 사용자 정보 없음");
					return new RuntimeException();
				});

		Fridge fridge = fridgeRepository.findByMember(member)
				.orElseThrow(() -> {
					log.error("해당 회원은 냉장고가 없음");
					return new RuntimeException();
				});

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
