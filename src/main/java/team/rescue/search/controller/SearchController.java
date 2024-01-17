package team.rescue.search.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
  @GetMapping("/ingredient")
  public List<String> getIngredient(
      @RequestParam String keyword
  ) {

    List<String> ingredients = searchService.getIngredient(keyword);

    return ingredients;
  }

}
