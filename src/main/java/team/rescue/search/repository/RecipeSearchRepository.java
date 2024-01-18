package team.rescue.search.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;
import team.rescue.search.entity.RecipeDoc;

@Component
@RequiredArgsConstructor
public class RecipeSearchRepository {

	private final ElasticsearchOperations searchOperations;

	/**
	 * Recipe Document 저장
	 *
	 * @param recipeDoc 저장할 Recipe Document
	 * @return 저장한 Recipe Document
	 */
	public RecipeDoc save(RecipeDoc recipeDoc) {
		return searchOperations.save(recipeDoc);
	}

	/**
	 * Recipe Document 재료 기반 검색
	 *
	 * @param ingredients 검색할 재료 문자열
	 * @param pageRequest 페이지네이션 정보
	 * @return 재료와 매치되는 Recipe Documents
	 */
	public SearchPage<RecipeDoc> searchByIngredients(String ingredients, PageRequest pageRequest) {
		Criteria criteria = Criteria.where("ingredients").contains(ingredients);
		Query query = new CriteriaQuery(criteria).setPageable(pageRequest);
		SearchHits<RecipeDoc> searchHits = searchOperations.search(query, RecipeDoc.class);
		return SearchHitSupport.searchPageFor(searchHits, query.getPageable());
//		return searchHit.stream()
//				.map(SearchHit::getContent)
//				.collect(Collectors.toList());

	}

	/**
	 * Recipe Document 키워드 기반 검색
	 *
	 * @param keyword     검색할 keyword
	 * @param pageRequest 페이지네이션 정보
	 * @return 키워드와 매치되는 Recipe Documents
	 */
	public SearchPage<RecipeDoc> searchByKeyword(String keyword, PageRequest pageRequest) {
		Criteria criteria = Criteria.where("").contains(keyword);
		Query query = new CriteriaQuery(criteria).setPageable(pageRequest);
		SearchHits<RecipeDoc> searchHits = searchOperations.search(query, RecipeDoc.class);
		return SearchHitSupport.searchPageFor(searchHits, query.getPageable());
	}


}
