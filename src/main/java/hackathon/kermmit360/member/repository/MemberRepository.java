package hackathon.kermmit360.member.repository;

import hackathon.kermmit360.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    MemberEntity findByUsername(String username);
}
