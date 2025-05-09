package hackathon.kermmit360.github.controller;

import hackathon.kermmit360.github.dto.GithubRepositoryCommitDto;
import hackathon.kermmit360.github.dto.GithubRepositoryDto;
import hackathon.kermmit360.github.dto.GithubRepositoryIssueDto;
import hackathon.kermmit360.github.service.GithubRepositoryDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GithubRepoDetailController {

    private final GithubRepositoryDetailService githubRepositoryDetailService;

    @PostMapping("/repo/{name}")
    public String getRepositoryDetail(@PathVariable String name, Model model) {
        GithubRepositoryDto dto = githubRepositoryDetailService.getRepoDetails(name);
        List<Map<String, Object>> contributors = githubRepositoryDetailService.getRepoContributors(name);
        List<GithubRepositoryCommitDto> commits = githubRepositoryDetailService.getCommitsLast7Days(name);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int totalCommits = (commits != null) ? commits.size() : 0;

        Map<String, Long> commitsPerDate = (commits != null) ? commits.stream()
                .collect(Collectors.groupingBy(commit ->
                                LocalDate.parse(commit.getCommit().getAuthor().getDate().substring(0, 10)).format(formatter),
                        TreeMap::new,
                        Collectors.counting()
                )) : Map.of();

        Map<String, Map<String, Long>> dailyAuthorRanking = (commits != null) ? commits.stream()
                .collect(Collectors.groupingBy(commit ->
                                commit.getCommit().getAuthor().getDate().substring(0, 10),
                        Collectors.groupingBy(commit -> {
                            GithubRepositoryCommitDto.Author author = commit.getAuthor();
                            return (author != null && author.getLogin() != null) ? author.getLogin() : "unknown";
                        }, Collectors.counting())
                )) : Map.of();

        if(dto == null || contributors == null || commits == null
                || totalCommits == 0 || commitsPerDate == null || dailyAuthorRanking == null){
            model.addAttribute("repo", null);
            model.addAttribute("contributors", null);
            model.addAttribute("commits", 0);
            model.addAttribute("totalCommits", 0);
            model.addAttribute("commitsPerDate", null);
            model.addAttribute("dailyAuthorRanking", null);

        }
        model.addAttribute("repo", dto);
        model.addAttribute("contributors", contributors);
        model.addAttribute("commits", commits);
        model.addAttribute("totalCommits", totalCommits);
        model.addAttribute("commitsPerDate", commitsPerDate);
        model.addAttribute("dailyAuthorRanking", dailyAuthorRanking);

        return "repoDetail";
    }
}
