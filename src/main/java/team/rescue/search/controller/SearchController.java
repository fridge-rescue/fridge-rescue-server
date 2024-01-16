package team.rescue.search.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.rescue.common.dto.ResponseDto;
import team.rescue.search.service.SearchService;

@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

  private final SearchService searchService;

  /**
   *
   */
  @GetMapping("/ingredient/{keyword}")
  public ResponseEntity<ResponseDto<List<String>>> getIngredient(
      @PathVariable String keyword
  ) {

    List<String> ingredients = searchService.getIngredient(keyword);

    return new ResponseEntity<>(
        new ResponseDto<>("재료 조회에 성공하였습니다.", ingredients),
        HttpStatus.OK
    );
  }

}
