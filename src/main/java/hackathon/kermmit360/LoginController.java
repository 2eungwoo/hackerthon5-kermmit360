package hackathon.kermmit360;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/commits")
    public ResponseEntity<?> getCommits(OAuth2AuthenticationToken authentication) {

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );

        String accessToken = client.getAccessToken().getTokenValue();

        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();

        String username = authentication.getPrincipal().getAttribute("login");
        String email = authentication.getPrincipal().getAttribute("email");
        String eventsUrl = String.format("/users/%s/events", username);

        String rawJson = webClient.get()
                .uri(eventsUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();

//        List<String> commits = webClient.get()
//                .uri(eventsUrl)
//                .retrieve()
//                .bodyToFlux(Map.class)
//                .flatMap(event -> {
//                    if ("PushEvent".equals(event.get("type"))) {
//                        Map payload = (Map) event.get("payload");
//                        List<Map<String, String>> commitList = (List<Map<String, String>>) payload.get("commits");
//                        if (commitList != null) {
//                            return Flux.fromIterable(commitList)
//                                    .map(c -> c.get("message"));
//                        }
//                    }
//                    return Flux.empty();
//                })
//                .collectList()
//                .block();

        System.out.println(rawJson);
        return ResponseEntity.ok(rawJson);
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/allRepo")
    public ResponseEntity<?> allRepo(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );
        String accessToken = client.getAccessToken().getTokenValue();

        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();

        String username = authentication.getPrincipal().getAttribute("login");

        List<String> repoNames = webClient.get()
                .uri("/user/repos", username)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToFlux(Map.class)
                .map(repo -> (String) repo.get("name"))
                .collectList()
                .block();

        List<String> allCommitMessages = new ArrayList<>();

        for (String repo : repoNames) {
            List<String> commits = webClient.get()
                    .uri("/repos/{username}/{repo}/commits?author={username}", username, repo, username)
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .bodyToFlux(Map.class)
                    .map(commit -> {
                        Map commitInfo = (Map) commit.get("commit");
                        return commitInfo.get("message").toString();
                    })
                    .collectList()
                    .block();

            allCommitMessages.addAll(commits);
        }
        log.info(allCommitMessages.toString());
        return ResponseEntity.ok(allCommitMessages);
    }
}