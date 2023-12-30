package team.rescue.fridge.fridge;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FridgeService {

	private final FridgeRepository fridgeRepository;


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

}
