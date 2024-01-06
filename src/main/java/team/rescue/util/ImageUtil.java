package team.rescue.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import team.rescue.error.exception.ServiceException;
import team.rescue.error.type.ServiceError;

@Slf4j
@UtilityClass
public class ImageUtil {

	private static String path;

	public InputStream resizeImage(MultipartFile image) {

		try {
			File resizedFile = image.getResource().getFile();
			BufferedImage bufferedImage = ImageIO.read(image.getInputStream());

			// 가로 사이즈 360 초과하는 경우 리사이즈
			if (bufferedImage.getWidth() > 360) {
				Thumbnails.of(resizedFile).size(360, 100).toFile(path);
			}

			return new FileInputStream(resizedFile);

		} catch (IOException e) {
			throw new ServiceException(ServiceError.FILE_RESIZING_FAILURE);
		}
	}

	/**
	 * 이미지 파일 업로드 이름 생성
	 *
	 * @param originName 원본 이미지 이름
	 * @return 업로드 이름
	 */
	public String generateImageName(String originName) {

		int separatorPos = originName.lastIndexOf(File.separator); // 구분
		String extension = originName.substring(separatorPos); // 파일 확장자
		String name = originName.substring(0, separatorPos); // 파일 이름
		String now = String.valueOf(System.currentTimeMillis()); // 파일 업로드 시간

		return name + "_" + now + "_" + RandomCodeUtil.generateUUID() + File.separator + extension;
	}

	@Value("${spring.servlet.multipart.location}")
	public void setPath(String path) {
		path = path;
	}
}
