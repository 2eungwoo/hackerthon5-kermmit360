package hackathon.kermmit360.github.controller;

import hackathon.kermmit360.github.dto.GithubPushEventDto;
import hackathon.kermmit360.github.service.GithubEventService;
import hackathon.kermmit360.member.dto.MemberDto;
import hackathon.kermmit360.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GithubPushEventController {

    private final GithubEventService githubEventService;
    private final MemberService memberService;

    @PostMapping(value = "/home/api/integrate", params = "action=integrate")
    public String updateMyExp(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // 1. 경험치 반영 + PushEvent 정보 가져오기
        GithubPushEventDto pushEventDto = githubEventService.fetchAndApplyExp(username);

        // 2. 최신 사용자 정보 가져오기
        MemberDto.Response member = memberService.getMemberByUsername(username);
        model.addAttribute("member", member);

        log.info("========= pushEvent : {}", pushEventDto);

        // 3. 화면에 표시할 데이터 설정
        if (pushEventDto != null) {
            model.addAttribute("recentRepo", pushEventDto.getRepoName());
            model.addAttribute("dailyCommits", pushEventDto.getCommitCount());
            model.addAttribute("weeklyCommits", pushEventDto.getCommitCount());
            model.addAttribute("monthlyCommits", pushEventDto.getCommitCount());
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
        String username = authentication.getName();
        MemberDto.Response member = githubEventService.fakeCommit(username);
        model.addAttribute("member", member);
        return "/home";
    }
}