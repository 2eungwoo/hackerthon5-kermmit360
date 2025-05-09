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
        // 이름 기반으로 repo정보 가져오기
        GithubRepositoryDto dto = githubRepositoryDetailService.getRepoDetails(name);
        // 참여자 정보
        List<Map<String, Object>> contributors = githubRepositoryDetailService.getRepoContributors(name);
//        // 이슈 정보
//        List<GithubRepositoryIssueDto> issues = githubRepositoryDetailService.getIssueStatus(name);
//        // 상태별 이슈 분류
//        long openIssueCount = issues.stream().filter(issue -> "open".equals(issue.getState())).count();
//        long closedIssueCount = issues.stream().filter(issue -> "closed".equals(issue.getState())).count();
//        long totalIssueCount = issues.size();

        List<GithubRepositoryCommitDto> commits = githubRepositoryDetailService.getCommits(name);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 총 커밋 수
        int totalCommits = commits.size();

        // 날짜별 커밋 수
        Map<String, Long> commitsPerDate = commits.stream()
                .collect(Collectors.groupingBy(commit ->
                                LocalDate.parse(commit.getCommit().getAuthor().getDate().substring(0, 10)).format(formatter),
                        TreeMap::new, // 날짜 순 정렬
                        Collectors.counting()
                ));

        // 날짜별 커밋 기여자 순위
        Map<String, Map<String, Long>> dailyAuthorRanking = commits.stream()
                .collect(Collectors.groupingBy(commit ->
                                commit.getCommit().getAuthor().getDate().substring(0, 10), // 날짜
                        Collectors.groupingBy(commit -> {
                            GithubRepositoryCommitDto.Author author = commit.getAuthor();
                            return (author != null && author.getLogin() != null) ? author.getLogin() : "unknown";
                        }, Collectors.counting())
                ));


        // 모델에 데이터를 추가
//        model.addAttribute("totalIssueCount", totalIssueCount);
//        model.addAttribute("openIssueCount", openIssueCount);
//        model.addAttribute("closedIssueCount", closedIssueCount);
//        model.addAttribute("issues", issues);
        model.addAttribute("repo", dto);
        model.addAttribute("contributors", contributors);
        model.addAttribute("commits", commits);
        model.addAttribute("totalCommits", totalCommits);
        model.addAttribute("commitsPerDate", commitsPerDate);
        model.addAttribute("dailyAuthorRanking", dailyAuthorRanking);

        System.out.println(contributors.get(0));
        return "repoDetail"; // detail.html로 이동
    }
}
