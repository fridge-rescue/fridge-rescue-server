package team.rescue.search.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortType {
	HOT("reviewCount"), DESC("createdAt");

	private final String sortBy;
}
