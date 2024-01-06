package team.rescue.common.dto;

import java.io.IOException;
import java.io.InputStream;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ImageDto {

	private String name;
	private String originFilename;
	private Resource resource;
	private InputStream inputStream;
	private byte[] bytes;
	private String contentType;
	private long size;
	private String url;


	public ImageDto(MultipartFile image) throws IOException {

		this.name = image.getName();
		this.originFilename = image.getOriginalFilename();
		this.resource = image.getResource();
		this.inputStream = image.getInputStream();
		this.bytes = image.getBytes();
		this.contentType = image.getContentType();
		this.size = image.getSize();
	}
}
