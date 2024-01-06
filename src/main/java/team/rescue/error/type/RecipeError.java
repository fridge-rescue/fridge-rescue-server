package team.rescue.error.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecipeError {

  NOT_FOUND_RECIPE(-1, HttpStatus.NOT_FOUND, "해당 레시피를 찾을 수 없습니다.")
  ;

  private final int code;
  private final HttpStatus httpStatus;
  private final String errorMessage;

}
