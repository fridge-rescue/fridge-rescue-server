package team.rescue.common.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
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
	private static final String S3_BUCKET_UPLOAD_DIR_PREFIX = "origin/";
	private static final String S3_BUCKET_DOWNLOAD_DIR_PREFIX = "resized/";

	/**
	 * S3 버킷 이미지 등록
	 *
	 * @param image Multipart 이미지 파일
	 * @return 리사이징 처리 된 이미지 파일 접근 주소
	 */
	public String uploadImageToS3(MultipartFile image) {

		log.info("[S3 이미지 업로드] imageName={}", image.getOriginalFilename());

		// 파일 존재 여부 확인
		validateFile(image);

		if (image.getOriginalFilename() == null) {
			throw new ServiceException(ServiceError.FILE_EXTENSION_INVALID);
		}
		String fileName = ImageUtil.generateImageName(image.getOriginalFilename());

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(image.getContentType());

		try {

			PutObjectRequest putObjectRequest = new PutObjectRequest(
					bucketName,
					S3_BUCKET_UPLOAD_DIR_PREFIX + fileName,
					image.getInputStream(),
					objectMetadata
			);
			s3Client.putObject(putObjectRequest);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return s3Client.getUrl(bucketName, S3_BUCKET_DOWNLOAD_DIR_PREFIX + fileName).toString();
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
