package hackathon.kermmit360.github.controller;

import hackathon.kermmit360.github.dto.GithubPushEventDto;
import hackathon.kermmit360.github.service.GithubEventService;
import hackathon.kermmit360.login.GithubLoginService;
import hackathon.kermmit360.member.dto.MemberDto;
import hackathon.kermmit360.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GithubPushEventController {

    private final GithubEventService githubEventService;
    private final MemberService memberService;
    private final GithubLoginService githubLoginService;

    @PostMapping(value = "/home/api/integrate", params = "action=integrate")
    public String integrateWithGithub(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDto.Response member;
        GithubPushEventDto pushEventDto;

        // 로그인 방식에 따라 분기
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            String socialMember = githubLoginService.userLogin(oauthToken);
            member = memberService.getMemberById(socialMember);
            pushEventDto = githubEventService.fetchAndApplyAllExp();
        } else {
            String username = authentication.getName();
            member = memberService.getMemberByUsername(username);
            //pushEventDto = githubEventService.fetchAndApplyExp(username);
            pushEventDto = githubEventService.fetchAndApplyAllExp();
        }

        model.addAttribute("member", member);
        log.info("========= pushEvent : {}", pushEventDto);

        // 커밋 통계 처리
        if (pushEventDto != null && pushEventDto.getCommitTimestamps() != null && !pushEventDto.getCommitTimestamps().isEmpty()) {
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
            List<ZonedDateTime> timestamps = pushEventDto.getCommitTimestamps();

            long daily = timestamps.stream()
                    .filter(t -> t.toLocalDate().equals(now.toLocalDate()))
                    .count();

            long weekly = timestamps.stream()
                    .filter(t -> !t.toLocalDate().isBefore(now.toLocalDate().minusDays(7)))
                    .count();

            long monthly = timestamps.stream()
                    .filter(t -> !t.toLocalDate().isBefore(now.toLocalDate().minusMonths(1)))
                    .count();

            model.addAttribute("recentRepo", pushEventDto.getRepoName());
            model.addAttribute("dailyCommits", daily);
            model.addAttribute("weeklyCommits", weekly);
            model.addAttribute("monthlyCommits", monthly);
        } else {
            model.addAttribute("recentRepo", "없음");
            model.addAttribute("dailyCommits", 0);
            model.addAttribute("weeklyCommits", 0);
            model.addAttribute("monthlyCommits", 0);
        }

        // 날짜별 커밋 수 데이터 -> Chart.js용
        Map<LocalDate, Integer> commitStats = pushEventDto.getCommitStats();
        List<String> dates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();

        commitStats.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    dates.add(entry.getKey().toString());
                    counts.add(entry.getValue());
                });

        model.addAttribute("commitDates", dates);
        model.addAttribute("commitCounts", counts);

        return "home";
    }

    @PostMapping(value = "/home/api/fake-commit", params = "action=fake-commit")
    public String fakeCommitEvent(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            username = githubLoginService.userLogin(oauthToken);
        } else {
            username = authentication.getName();
        }

        System.out.println(username);
        MemberDto.Response member = githubEventService.fakeCommit(username);
        model.addAttribute("member", member);
        return "/home";
    }
}