package team.rescue.error.exception;

import org.springframework.http.HttpStatus;
import team.rescue.error.type.RecipeError;

public class RecipeException extends RuntimeException {

  int code;
  HttpStatus statusCode;
  String errorMessage;

  public RecipeException(RecipeError recipeError) {
    this.code = recipeError.getCode();
    this.statusCode = recipeError.getHttpStatus();
    this.errorMessage = recipeError.getErrorMessage();
  }
}
