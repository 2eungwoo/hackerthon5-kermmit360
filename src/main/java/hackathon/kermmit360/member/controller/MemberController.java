package hackathon.kermmit360.member.controller;

import hackathon.kermmit360.github.dto.GithubPushEventDto;
import hackathon.kermmit360.github.service.GithubEventService;
import hackathon.kermmit360.global.response.ResultResponse;
import hackathon.kermmit360.login.GithubLoginService;
import hackathon.kermmit360.member.dto.MemberDto;
import hackathon.kermmit360.member.entity.MemberEntity;
import hackathon.kermmit360.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final GithubLoginService githubLoginService;
    private final GithubEventService githubEventService;

    @GetMapping(value = "/home", params = "action=list")
    public String getMemberList(Model model) {
        List<MemberDto.Response> memberList = memberService.getMemberList();
        log.info(">>>>>>>>>>> memberList: {}", memberList);
        model.addAttribute("members", memberList);
        return "home";
    }

    @GetMapping(value = "/home", params = "action=detail")
    public String getMemberDetail(String username, Model model) {
        MemberDto.Response member = memberService.getMemberById(username);
        System.out.println(">>> member: " + member);
        model.addAttribute("member", member);
        return "home";
    }
//
//    @GetMapping(value = "/home", params = "action=myinfo")
//    public String home(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        MemberDto.Response member = null;
//        // 소셜 로그인인 경우
//        // todo : member만 넘겨주지말고 daily,weekly,monthly 넘겨줘야됨
//        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
//            String socialMember = githubLoginService.userLogin(oauthToken);
//            member = memberService.getMemberById(socialMember);
//            model.addAttribute("member", member);
//        }else{// 일반 로그인
//            // todo : member만 넘겨주지말고 daily,weekly,monthly 넘겨줘야됨
//            String username = authentication.getName();
//            member = memberService.getMemberByUsername(username);
//            model.addAttribute("member", member);
//        }
//        return "home";
//    }
//
//    @GetMapping("/home")
//    public String defaultHome(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        MemberDto.Response member = null;
//        // 소셜 로그인인 경우
//        // todo : member만 넘겨주지말고 daily,weekly,monthly 넘겨줘야됨
//        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
//            String socialMember = githubLoginService.userLogin(oauthToken);
//            System.out.println(socialMember);
//            member = memberService.getMemberById(socialMember);
//            model.addAttribute("member", member);
//        }else{// 일반 로그인
//            // todo : member만 넘겨주지말고 daily,weekly,monthly 넘겨줘야됨
//            String username = authentication.getName();
//            member = memberService.getMemberByUsername(username);
//            model.addAttribute("member", member);
//        }
//        return "home";
//    }
@GetMapping(value = "/home", params = "action=myinfo")
public String home(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    MemberDto.Response member = null;
    String username;

    if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
        username = githubLoginService.userLogin(oauthToken);
        member = memberService.getMemberById(username);
    } else {
        username = authentication.getName();
        member = memberService.getMemberByUsername(username);
    }

    GithubPushEventDto pushEventDto = githubEventService.fetchAndApplyAllExp(username);
    model.addAttribute("member", member);

    if (pushEventDto != null && pushEventDto.getCommitTimestamps() != null && !pushEventDto.getCommitTimestamps().isEmpty()) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
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

    @GetMapping("/home")
    public String defaultHome(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDto.Response member = null;
        String username;

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            username = githubLoginService.userLogin(oauthToken);
            member = memberService.getMemberById(username);
        } else {
            username = authentication.getName();
            member = memberService.getMemberByUsername(username);
        }

        GithubPushEventDto pushEventDto = githubEventService.fetchAndApplyAllExp(username);
        model.addAttribute("member", member);

        if (pushEventDto != null && pushEventDto.getCommitTimestamps() != null && !pushEventDto.getCommitTimestamps().isEmpty()) {
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
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
}
