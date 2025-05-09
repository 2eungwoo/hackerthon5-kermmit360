package hackathon.kermmit360.github.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class GithubWebClientService {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public GithubAuthInfo getAuthInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            String username = oauthToken.getPrincipal().getAttribute("login");
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            );
            String accessToken = client.getAccessToken().getTokenValue();
            return new GithubAuthInfo(username, accessToken);
        }
        throw new IllegalStateException("OAuth2AuthenticationToken not found.");
    }

    public WebClient getWebClient(String accessToken) {
        return WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();
    }

    public record GithubAuthInfo(String username, String accessToken) {}
}
