package hackathon.kermmit360.github.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hackathon.kermmit360.github.dto.GithubPushEventDto;
import hackathon.kermmit360.member.dto.MemberDto;
import hackathon.kermmit360.member.entity.MemberEntity;
import hackathon.kermmit360.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
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

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubEventService {

    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;

//    public GithubPushEventDto fetchAndApplyExp(String username) {
//        try {
//            String url = "https://api.github.com/users/" + username + "/events";
//            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode root = objectMapper.readTree(response.getBody());
//
//            List<ZonedDateTime> commitTimestamps = new ArrayList<>();
//            String recentRepo = "";
//            String lastCreatedAt = null;
//
//            for (JsonNode event : root) {
//                if ("PushEvent".equals(event.get("type").asText())) {
//                    String createdAtStr = event.get("created_at").asText(); // 푸시 이벤트 시간
//                    ZonedDateTime createdAt = ZonedDateTime.parse(createdAtStr);
//                    JsonNode commits = event.get("payload").get("commits");
//
//                    int commitCount = commits.size();
//                    for (int i = 0; i < commitCount; i++) {
//                        commitTimestamps.add(createdAt); // 모든 커밋에 대해 타임스탬프 추가
//                    }
//
//                    if (lastCreatedAt == null) {
//                        // 가장 최근 이벤트 정보 저장
//                        lastCreatedAt = createdAtStr;
//                        recentRepo = event.get("repo").get("name").asText();
//                    }
//                }
//            }
//
//            int totalCommits = commitTimestamps.size();
//
//            // 경험치 반영
//            MemberEntity member = memberRepository.findByUsername(username);
//            member.assignExp(totalCommits);
//            memberRepository.save(member);
//
//            log.info("✔️ [GitHub] {}: {} commits collected", username, totalCommits);
//
//            return new GithubPushEventDto(username, recentRepo, lastCreatedAt, totalCommits, commitTimestamps, Map.of());
//
//        } catch (Exception e) {
//            log.error("❌ GitHub 이벤트 조회 실패: {}", e.getMessage(), e);
//            return new GithubPushEventDto("", "", "", 0, null,Map.of());
//        }
//    }

    public GithubPushEventDto fetchAndApplyAllExp() {
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

        String accessToken = client.getAccessToken().getTokenValue();

        try {
            WebClient webClient = WebClient.builder()
                    .baseUrl("https://api.github.com")
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .build();

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
                log.warn("📂 사용자 레포 없음: {}", username);
                return new GithubPushEventDto(username, "", "", 0, List.of(), Map.of());
            }

            int totalCommits = 0;
            String recentRepo = null;
            String lastCreatedAt = null;
            List<ZonedDateTime> commitTimestamps = new ArrayList<>();

            for (Map<String, Object> repo : allRepos) {
                String repoName = (String) repo.get("name");

                List<Map<String, Object>> commits = webClient.get()
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

                for (Map<String, Object> commit : commits) {
                    Map<String, Object> commitInfo = (Map<String, Object>) commit.get("commit");
                    Map<String, Object> authorInfo = (Map<String, Object>) commitInfo.get("author");
                    String dateStr = (String) authorInfo.get("date");
                    ZonedDateTime commitTime = ZonedDateTime.parse(dateStr);
                    commitTimestamps.add(commitTime);

                    if (lastCreatedAt == null || commitTime.isAfter(ZonedDateTime.parse(lastCreatedAt))) {
                        lastCreatedAt = dateStr;
                        recentRepo = repoName;
                    }
                }

                totalCommits += commits.size();
            }

            // 경험치 반영
            MemberEntity member = memberRepository.findByUsername(username);
            member.assignExp(totalCommits);
            member.updateTier();
            memberRepository.save(member);

            // 날짜별 커밋 수 Map<LocalDate, Integer> 생성
            Map<LocalDate, Integer> commitStats = commitTimestamps.stream()
                    .map(ZonedDateTime::toLocalDate)
                    .collect(Collectors.groupingBy(
                            date -> date,
                            Collectors.reducing(0, e -> 1, Integer::sum)
                    ));

            log.info("🎯 [GitHub] {} 커밋 총합: {}", username, totalCommits);

            return new GithubPushEventDto(username, recentRepo, lastCreatedAt, totalCommits, commitTimestamps, commitStats);

        } catch (Exception e) {
            log.error("❌ GitHub 커밋 조회 실패: {}", e.getMessage(), e);
            return null;
        }
    }

    @Transactional
    public MemberDto.Response fakeCommit(String username){
        MemberEntity memberEntity = memberRepository.findByUsername(username);
        memberEntity.addExp();
        memberRepository.save(memberEntity);
        log.info("============== member exp : {}", memberEntity.getExp());
        return new MemberDto.Response(memberEntity);
    }

}