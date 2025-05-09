package hackathon.kermmit360.github.service;

import hackathon.kermmit360.github.dto.GithubRepositoryCommitDto;
import hackathon.kermmit360.github.dto.GithubRepositoryDto;
import hackathon.kermmit360.github.dto.GithubRepositoryIssueDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubRepositoryDetailService {

    private final GithubWebClientService githubWebClientService;

    public GithubRepositoryDto getRepoDetails(String repoName) {
        var auth = githubWebClientService.getAuthInfo();
        var webClient = githubWebClientService.getWebClient(auth.accessToken());

        return webClient.get()
                .uri("/repos/{owner}/{repo}", auth.username(), repoName)
                .retrieve()
                .bodyToMono(GithubRepositoryDto.class)
                .block();
    }

    public List<Map<String, Object>> getRepoContributors(String repoName) {
        var auth = githubWebClientService.getAuthInfo();
        var webClient = githubWebClientService.getWebClient(auth.accessToken());

        return webClient.get()
                .uri("/repos/{owner}/{repo}/contributors", auth.username(), repoName)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                .collectList()
                .block();
    }

    public List<GithubRepositoryIssueDto> getIssueStatus(String repoName) {
        var auth = githubWebClientService.getAuthInfo();
        var webClient = githubWebClientService.getWebClient(auth.accessToken());

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repos/{owner}/{repo}/issues")
                        .queryParam("state", "all")
                        .queryParam("per_page", "100")
                        .build(auth.username(), repoName))
                .retrieve()
                .bodyToFlux(GithubRepositoryIssueDto.class)
                .collectList()
                .block();
    }

    public List<GithubRepositoryCommitDto> getCommits(String repoName) {
        var auth = githubWebClientService.getAuthInfo();
        var webClient = githubWebClientService.getWebClient(auth.accessToken());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since = now.minusDays(7);
        String sinceISO = since.format(DateTimeFormatter.ISO_DATE_TIME);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repos/{owner}/{repo}/commits")
                        .queryParam("per_page", "100")
                        .queryParam("page", 1)
                        .build(auth.username(), repoName))
                .retrieve()
                .bodyToFlux(GithubRepositoryCommitDto.class)
                .collectList()
                .block();
    }

    public Map<String, Long> getAuthorCommitCount(List<GithubRepositoryCommitDto> commits) {
        return commits.stream()
                .map(commit -> {
                    var author = commit.getAuthor();
                    return (author != null && author.getLogin() != null) ? author.getLogin() : "unknown";
                })
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }


}
