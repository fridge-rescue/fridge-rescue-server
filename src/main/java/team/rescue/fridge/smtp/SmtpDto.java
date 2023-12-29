package team.rescue.fridge.smtp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmtpDto {

	private String receiver;
	private String title;
	private String contents;
}
