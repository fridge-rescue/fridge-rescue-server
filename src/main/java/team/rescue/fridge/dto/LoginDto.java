package team.rescue.fridge.dto;

import lombok.Getter;
import lombok.Setter;
import team.rescue.fridge.member.entity.Member;
import team.rescue.fridge.member.entity.RoleType;
import team.rescue.fridge.security.dto.TokenDto;

public class LoginDto {

  @Getter
  @Setter
  public static class LoginReqDto {
    private String email;
    private String password;
  }

  @Getter
  public static class LoginResDto {

    private String name;
    private String email;
    private RoleType role;
    private TokenDto tokenDto;

    public LoginResDto(Member user, TokenDto token) {
      this.name = user.getName();
      this.email = user.getEmail();
      this.role = user.getRole();
      this.tokenDto = token;
    }
  }
}
