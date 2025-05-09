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

    private final OAuth2AuthorizedClientService authorizedClientService;

    public GithubRepositoryDto getRepoDetails(String repoName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthorizedClient client = null;
        String username = null;

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            username = oauthToken.getPrincipal().getAttribute("login");
            client = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            );
        }

        if (client == null || username == null) {
            return null; // 비로그인 사용자는 null 반환
        }

        String accessToken = client.getAccessToken().getTokenValue();
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();

        return webClient.get()
                .uri("/repos/{owner}/{repo}", username, repoName)
                .retrieve()
                .bodyToMono(GithubRepositoryDto.class)
                .block();
    }

    public List<Map<String, Object>> getRepoContributors(String repoName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthorizedClient client = null;
        String username = null;

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            username = oauthToken.getPrincipal().getAttribute("login");
            client = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            );
        }

        if (client == null || username == null) {
            return List.of(); // 빈 리스트 반환
        }

        String accessToken = client.getAccessToken().getTokenValue();
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();

        return webClient.get()
                .uri("/repos/" + username + "/" + repoName + "/contributors")
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                .collectList()
                .block();
    }

    public List<GithubRepositoryIssueDto> getIssueStatus(String repoName) {
        // 소셜 로그인 사용자의 client 정보
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthorizedClient client = null;
        String username;
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            username = oauthToken.getPrincipal().getAttribute("login");
            client = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            );
        } else {
            username = null;
        }
        // github 용 accessToken
        String accessToken = client.getAccessToken().getTokenValue();

        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();

        // GitHub에서 모든 이슈 데이터 가져오기
        List<GithubRepositoryIssueDto> issues = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repos/" + username + "/" + repoName + "/issues")
                        .queryParam("state", "all")  // 모든 이슈 가져오기
                        .queryParam("per_page", "100")  // 최대 100개 이슈 가져오기
                        .build())
                .retrieve()
                .bodyToFlux(GithubRepositoryIssueDto.class)
                .collectList()
                .block();

        return issues;
    }

    public List<GithubRepositoryCommitDto> getCommitsLast7Days(String repoName) {
        // 소셜 로그인 사용자의 client 정보
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthorizedClient client = null;
        String username;
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            username = oauthToken.getPrincipal().getAttribute("login");
            client = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            );
        } else {
            username = null;
        }
        // github 용 accessToken
        String accessToken = client.getAccessToken().getTokenValue();

        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since = now.minusDays(7);

        String sinceISO = since.format(DateTimeFormatter.ISO_DATE_TIME);
        String untilISO = now.format(DateTimeFormatter.ISO_DATE_TIME);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repos/" + username + "/" + repoName + "/commits")
                        .queryParam("per_page", "100")
                        .queryParam("page", 1)
                        .build())
                .retrieve()
                .bodyToFlux(GithubRepositoryCommitDto.class)
                .collectList()
                .block();
    }

    public Map<String, Long> getAuthorCommitCount(List<GithubRepositoryCommitDto> commits) {
        return commits.stream()
                .map(commit -> {
                    GithubRepositoryCommitDto.Author author = commit.getAuthor();
                    return (author != null && author.getLogin() != null) ? author.getLogin() : "unknown";
                })
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }


}
