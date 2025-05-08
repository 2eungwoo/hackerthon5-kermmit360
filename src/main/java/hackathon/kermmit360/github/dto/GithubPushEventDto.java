package hackathon.kermmit360.github.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GithubPushEventDto {
    private String username;
    private String repoName;
    private String createdAt;
    private int commitCount;
    private List<ZonedDateTime> commitTimestamps;
}
