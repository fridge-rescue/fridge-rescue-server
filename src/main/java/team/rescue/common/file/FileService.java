package team.rescue.common.file;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import team.rescue.common.dto.ImageDto;

@Slf4j
@Service
public class FileService {

	// TODO: 이후 이미지 리사이징 후 S3 업로드 로직으로 변경
	// 현재는 이름값만 반환
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
