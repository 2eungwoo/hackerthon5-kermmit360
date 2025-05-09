package hackathon.kermmit360.github.dto;

import lombok.Data;

@Data
public class GitHubIssueDto {
    private String title;
    private String html_url;
    private String state; // open, closed
}
