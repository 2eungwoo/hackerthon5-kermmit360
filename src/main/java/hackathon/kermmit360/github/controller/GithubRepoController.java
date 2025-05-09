package hackathon.kermmit360.github.controller;

import hackathon.kermmit360.github.dto.GitHubRepoDto;
import hackathon.kermmit360.github.service.GithubRepoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GithubRepoController {

    private final GithubRepoService githubRepoService;

    @GetMapping("/repos")
    public String showRepositories(Model model, Authentication authentication) {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String accessToken = oauthToken.getPrincipal().getAttribute("access_token");

        List<GitHubRepoDto> repos = githubRepoService.getUserRepositories(accessToken);
        model.addAttribute("repos", repos);

        return "repo-list";
    }



}
