package hackathon.kermmit360.github.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GithubPushEventDto {
    private String username;
    private String repoName;
    private String createdAt;
    private int commitCount;
}
