package hackathon.kermmit360.login;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GIthubLoginRepository extends JpaRepository<GithubUserEntity, Integer> {
    List<GithubUserEntity> findByGithubId(Integer githubId);
}
