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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GithubPushEventController {

    private final GithubEventService githubEventService;
    private final MemberService memberService;
    private final GithubLoginService githubLoginService;

    @PostMapping(value = "/home/api/integrate", params = "action=integrate")
    public String updateMyExp(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDto.Response member = null;
        // 소셜 로그인인 경우
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            String socialMember = githubLoginService.userLogin(oauthToken);
            System.out.println(socialMember);
            member = memberService.getMemberById(socialMember);
        }else{// 일반 로그인
            String username = authentication.getName();
            member = memberService.getMemberByUsername(username);
        }
        String username = member.getUsername();
        // 1. 경험치 반영 + PushEvent 정보 가져오기
        GithubPushEventDto pushEventDto = githubEventService.fetchAndApplyAllExp(username);

        // 2. 최신 사용자 정보 가져오기
        model.addAttribute("member", member);

        log.info("========= pushEvent : {}", pushEventDto);

        // 3. 화면에 표시할 데이터 설정
        if (pushEventDto != null && pushEventDto.getCommitTimestamps() != null && !pushEventDto.getCommitTimestamps().isEmpty()) {
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC")); // 현재 시간을 UTC 기준으로 가져옴
            List<ZonedDateTime> timestamps = pushEventDto.getCommitTimestamps();

            long daily = timestamps.stream()
                    .filter(t -> t.toLocalDate().equals(now.toLocalDate()))
                    .count();

            long weekly = timestamps.stream()
                    .filter(t -> t.toLocalDate().isAfter(now.toLocalDate().minusDays(7)))
                    .count();

            long monthly = timestamps.stream()
                    .filter(t -> t.toLocalDate().isAfter(now.toLocalDate().minusMonths(1)))
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

        return "home";
    }
    @PostMapping(value = "/home/api/fake-commit", params = "action=fake-commit")
    public String fakeCommitEvent(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        // 소셜 로그인인 경우
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            username = githubLoginService.userLogin(oauthToken);
        }else{// 일반 로그인
            username = authentication.getName();
        }
        System.out.println(username);
        MemberDto.Response member = githubEventService.fakeCommit(username);
        model.addAttribute("member", member);
        return "/home";
    }
}