package hackathon.kermmit360.github.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hackathon.kermmit360.github.dto.GithubPushEventDto;
import hackathon.kermmit360.github.dto.GithubRepositoryDto;
import hackathon.kermmit360.member.dto.MemberDto;
import hackathon.kermmit360.member.entity.MemberEntity;
import hackathon.kermmit360.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubEventService {

    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final GithubWebClientService githubWebClientService;

    @Transactional
    public GithubPushEventDto fetchAndApplyExp(String username) {
        try {
            String url = "https://api.github.com/users/" + username + "/events";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());

            List<ZonedDateTime> commitTimestamps = new ArrayList<>();
            String recentRepo = "";
            String lastCreatedAt = null;

            for (JsonNode event : root) {
                if ("PushEvent".equals(event.get("type").asText())) {
                    String createdAtStr = event.get("created_at").asText();
                    ZonedDateTime createdAt = ZonedDateTime.parse(createdAtStr);
                    JsonNode commits = event.get("payload").get("commits");

                    int commitCount = commits.size();
                    for (int i = 0; i < commitCount; i++) {
                        commitTimestamps.add(createdAt);
                    }

                    if (lastCreatedAt == null) {
                        lastCreatedAt = createdAtStr;
                        recentRepo = event.get("repo").get("name").asText();
                    }
                }
            }

            int totalCommits = commitTimestamps.size();

            updateMemberExp(username, totalCommits);

            return new GithubPushEventDto(username, recentRepo, lastCreatedAt, totalCommits, commitTimestamps, Map.of(), Map.of());

        } catch (Exception e) {
            log.error("❌ GitHub 이벤트 조회 실패: {}", e.getMessage(), e);
            return new GithubPushEventDto("", "", "", 0, null, Map.of(), Map.of());
        }
    }

    @Transactional
    public GithubPushEventDto fetchAndApplyAllExp() {
        var auth = githubWebClientService.getAuthInfo();
        var accessToken = auth.accessToken();

        try {
            WebClient webClient = createWebClient(accessToken);

            List<Map<String, Object>> allRepos = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/user/repos")
                            .queryParam("per_page", 10)
                            .queryParam("sort", "created")
                            .queryParam("direction", "desc")
                            .build())
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .collectList()
                    .block();

            if (allRepos == null || allRepos.isEmpty()) {
                log.warn("📂 사용자 레포 없음: {}", auth.username());
                return new GithubPushEventDto("", "", "", 0, null, Map.of(), Map.of());
            }

            int totalCommits = 0;
            String recentRepo = null;
            String lastCreatedAt = null;
            List<ZonedDateTime> commitTimestamps = new ArrayList<>();
            Map<String, Integer> languages = new HashMap<>();

            for (Map<String, Object> repo : allRepos) {
                String repoName = (String) repo.get("name");

                List<Map<String, Object>> commits = fetchCommitsFromRepo(webClient, auth.username(), repoName, accessToken);

                for (Map<String, Object> commit : commits) {
                    Map<String, Object> commitInfo = (Map<String, Object>) commit.get("commit");
                    Map<String, Object> authorInfo = (Map<String, Object>) commitInfo.get("author");
                    String dateStr = (String) authorInfo.get("date");
                    ZonedDateTime commitTime = ZonedDateTime.parse(dateStr);
                    commitTimestamps.add(commitTime);

                    if (lastCreatedAt == null || commitTime.isAfter(ZonedDateTime.parse(lastCreatedAt))) {
                        lastCreatedAt = dateStr;
                        recentRepo = repoName;

                        // 언어 정보 가져오기
                        Map<String, Integer> repoLanguages = fetchLanguagesForRepo(auth.username(), recentRepo);
                        languages.putAll(repoLanguages); // 언어 정보 추가
                    }
                }

                totalCommits += commits.size();

            }

            updateMemberExp(auth.username(), totalCommits);

            Map<LocalDate, Integer> commitStats = commitTimestamps.stream()
                    .map(ZonedDateTime::toLocalDate)
                    .collect(Collectors.groupingBy(
                            date -> date,
                            Collectors.reducing(0, e -> 1, Integer::sum)
                    ));

            return new GithubPushEventDto(auth.username(), recentRepo, lastCreatedAt, totalCommits, commitTimestamps, commitStats, languages);

        } catch (Exception e) {
            log.error("❌ GitHub 커밋 조회 실패: {}", e.getMessage(), e);
            return new GithubPushEventDto(auth.username(), "", "", 0, List.of(), Map.of(), Map.of());
        }
    }

    @Transactional
    public MemberDto.Response fakeCommit(String username) {
        MemberEntity memberEntity = memberRepository.findByUsername(username);
        memberEntity.addExp();
        memberRepository.save(memberEntity);
        log.info("============== member exp : {}", memberEntity.getExp());
        return new MemberDto.Response(memberEntity);
    }

    public WebClient createWebClient(String accessToken) {
        return WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();
    }

    public List<Map<String, Object>> fetchCommitsFromRepo(WebClient webClient, String username, String repoName, String accessToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/repos/{username}/{repo}/commits")
                        .queryParam("author", username)
                        .build(username, repoName))
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                .collectList()
                .onErrorResume(WebClientResponseException.NotFound.class, e -> Mono.just(Collections.emptyList()))
                .block();
    }

    public MemberDto.Response updateMemberExp(String username, int totalCommits) {
        MemberEntity member = memberRepository.findByUsername(username);
        member.assignExp(totalCommits);
        member.updateTier();
        memberRepository.save(member);
        log.info("🎯 [GitHub] {} 커밋 총합: {}", username, totalCommits);
        return new MemberDto.Response(member);
    }


    // 언어 정보를 가져오는 메서드
    public Map<String, Integer> fetchLanguagesForRepo(String username, String repoName) {
        Map<String, Integer> languages = new HashMap<>();
        String url = "https://api.github.com/repos/" + username + "/" + repoName + "/languages";

        try {
            ResponseEntity<Map<String, Integer>> response = restTemplate.exchange(url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<Map<String, Integer>>() {});
            if (response.getBody() != null) {
                languages.putAll(response.getBody());
            }
        } catch (Exception e) {
            log.error("❌ 언어 정보 가져오기 실패: {}", e.getMessage(), e);
        }

        return languages;
    }



    public List<Map<String, Object>> getOrgs(){
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

        List<Map<String, Object>> orgs = webClient.get()
                .uri("/user/orgs")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                .collectList()
                .block();

        System.out.println(accessToken);
        return orgs;
    }

    public List<GithubRepositoryDto> getMyRepos(){
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

        // ⭐ null 처리 추가
        if (client == null || username == null) {
            log.warn("🔒 비로그인 상태 또는 GitHub 인증 정보 없음");
            return List.of(); // 빈 리스트 반환
        }

        String accessToken = client.getAccessToken().getTokenValue();

        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();

        List<GithubRepositoryDto> myRepos = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/user/repos")
                        .queryParam("per_page", "100")
                        .queryParam("sort", "created")
                        .queryParam("direction", "desc")
                        .build())
                .retrieve()
                .bodyToFlux(GithubRepositoryDto.class)
                .collectList()
                .block();

        return myRepos;
    }
}