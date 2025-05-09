//package hackathon.kermmit360.graph.commitstat.domain.repository;
//
//import hackathon.kermmit360.graph.commitstat.domain.entity.CommitStatEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.List;
//
//public interface CommitStatRepository extends JpaRepository<CommitStatEntity, Long> {
//
//    @Query("SELECT c FROM CommitStatEntity c WHERE c.member.username = :username ORDER BY c.commitDate ASC")
//    List<CommitStatEntity> findCommitStatsByUsername(@Param("username") String username);
//}
