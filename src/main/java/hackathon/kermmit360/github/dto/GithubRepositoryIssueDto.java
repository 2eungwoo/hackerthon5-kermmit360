package hackathon.kermmit360.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GithubRepositoryIssueDto {

    @JsonProperty("title")
    private String title;   // 이슈 제목

    @JsonProperty("html_url")
    private String url;     // 이슈 URL

    @JsonProperty("state")
    private String state;   // 이슈 상태 (open, closed)
}