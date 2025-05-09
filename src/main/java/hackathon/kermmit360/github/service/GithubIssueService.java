package hackathon.kermmit360.github.service;

import hackathon.kermmit360.github.dto.GitHubIssueDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GithubIssueService {

    private final RestTemplate restTemplate;

    public List<GitHubIssueDto> fetchIssues(String owner, String repo) {
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/issues";

        ResponseEntity<List<GitHubIssueDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }
}
