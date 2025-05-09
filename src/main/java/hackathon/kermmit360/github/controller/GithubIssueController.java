package hackathon.kermmit360.github.controller;

import hackathon.kermmit360.github.dto.GitHubIssueDto;
import hackathon.kermmit360.github.service.GithubIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GithubIssueController {

    private final GithubIssueService githubIssueService;

    @GetMapping("/repos/{owner}/{repo}/issues")
    public String listRepoIssues(@PathVariable String owner,
                                 @PathVariable String repo,
                                 Model model,
                                 Authentication authentication) {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String username = oauthToken.getPrincipal().getAttribute("login");

        List<GitHubIssueDto> issues = githubIssueService.fetchIssues(owner, repo);

        model.addAttribute("issues", issues);
        model.addAttribute("repo", repo);
        model.addAttribute("owner", owner);

        return "issues"; // -> templates/issues.html 로 이동
    }
}
