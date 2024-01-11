package team.rescue.recipe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.rescue.member.entity.Member;
import team.rescue.recipe.entity.Bookmark;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

	Page<Bookmark> findByMember(Member member, Pageable pageable);
}
