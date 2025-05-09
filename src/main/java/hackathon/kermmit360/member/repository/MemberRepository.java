package hackathon.kermmit360.member.repository;

import hackathon.kermmit360.member.dto.MemberDto;
import hackathon.kermmit360.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    MemberEntity findByUsername(String username);
    boolean existsByUsername(String username);

    @Query("SELECT m FROM MemberEntity m ORDER BY m.exp DESC")
    List<MemberEntity> findAllOrderByExpDesc();

    @Query(value =
            "SELECT COUNT(*) + 1 " +
                    "FROM member " +
                    "WHERE exp > (SELECT exp FROM member WHERE username = :username)",
            nativeQuery = true)
    int findRankByUsername(@Param("username") String username);

    MemberEntity findByGithubId(int githubId);

    boolean existsByGithubId(int githubId);
}
