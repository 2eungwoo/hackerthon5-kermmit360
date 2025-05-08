package hackathon.kermmit360.github.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class GithubPushEventDto {
    private String username;
    private String repoName;
    private String createdAt;
    private int commitCount;
    private List<ZonedDateTime> commitTimestamps;
    private Map<LocalDate, Integer> commitStats; // <날짜,커밋수>
    private Map<String, Integer> languages; // <언어, 커밋 수>
}
