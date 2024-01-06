package team.rescue.common.dto.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import team.rescue.error.exception.ServiceException;
import team.rescue.error.type.ServiceError;
import team.rescue.util.ImageUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

	private final AmazonS3 s3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	public String uploadImageToS3(MultipartFile image) {

		// 파일 존재 여부 확인
		validateFile(image);

		InputStream inputStream = ImageUtil.resizeImage(image);
		String fileName = ImageUtil.generateImageName("default");

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(image.getContentType());

		PutObjectRequest putObjectRequest = new PutObjectRequest(
				bucketName, fileName, inputStream, objectMetadata
		);

		// Upload
		s3Client.putObject(putObjectRequest);
		return s3Client.getObject(bucketName, fileName).toString();
	}

	private void validateFile(MultipartFile file) {
		if (file.isEmpty()) {
			throw new ServiceException(ServiceError.FILE_NOT_EXIST);
		}
		if (file.getOriginalFilename() == null) {
			throw new ServiceException(ServiceError.FILE_EXTENSION_INVALID);
		}
	}
}
