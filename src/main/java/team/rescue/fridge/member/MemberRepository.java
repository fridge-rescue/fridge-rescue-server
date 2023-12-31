package team.rescue.fridge.member;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.rescue.fridge.member.entity.Member;
import team.rescue.fridge.member.entity.ProviderType;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

	boolean existsByEmail(String email);

	Optional<Member> findByProviderAndProviderId(ProviderType provider, String providerId);
}
