package team.rescue.search.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortType {
	HOT("HOT"), DESC("DESC");

	private final String sortBy;
}
