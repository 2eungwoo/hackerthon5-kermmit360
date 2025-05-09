package hackathon.kermmit360.github.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GithubRepositoryDto {
    @JsonProperty("name")
    private String name;
    @JsonProperty("html_url")
    private String htmlUrl;
    private String description;
    @JsonProperty("stargazers_count")
    private int stargazersCount;
    @JsonProperty("forks_count")
    private int forksCount;
    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String createdAt;
    private OwnerDTO owner;
    private boolean isPrivate;
    private String language;           // ✅ 주 언어
    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String updatedAt;          // ✅ 마지막 업데이트
    @JsonProperty("open_issues_count")
    private Integer openIssuesCount;   // ✅ 오픈 이슈 수

    @Getter
    @Setter
    public static class OwnerDTO {
        private String login;
        private String avatarUrl;
    }
}

