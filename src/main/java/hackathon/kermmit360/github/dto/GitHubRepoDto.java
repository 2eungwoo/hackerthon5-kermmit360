package hackathon.kermmit360.github.dto;

import lombok.Data;

@Data
public class GitHubRepoDto {
    private String name;
    private String full_name;
    private String html_url;
    private String description;
    private boolean fork;
}
