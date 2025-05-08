package hackathon.kermmit360.login;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "github_user_entity")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GithubUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer githubId;

    private String githubLogin;

    private String githubName;

    private String githubEmail;

    private String githubUrl;
}
