package team.rescue.common.file;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import team.rescue.common.dto.ImageDto;

@Slf4j
@Service
public class FileService {

	/**
	 * 이미지 S3 저장
	 * TODO: 현재 이미지 originName을 반환하도록 임시 처리, 이후 실제 로직 구현 필요
	 *
	 * @param image S3 버킷에 저장할 이미지 파일
	 * @return S3 URL
	 */
	public String uploadImageToS3(MultipartFile image) {

		log.info("[S3 이미지 업로드]");

		if (image.isEmpty()) {
			return null;
		}

		try {
			ImageDto imageDto = new ImageDto(image);

			return imageDto.getOriginFilename();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
