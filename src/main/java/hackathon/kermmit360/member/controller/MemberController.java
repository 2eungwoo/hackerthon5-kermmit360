package hackathon.kermmit360.member.controller;

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

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final GithubLoginService githubLoginService;


//
//    @GetMapping("/home/members")
//    public String getMemberList(Model model) {
//        List<MemberDto.Response> memberList = memberService.getMemberList();
//        model.addAttribute("members", memberList);
//        return "home";
//    }
//
//    @GetMapping("/home/member/{memberId}")
//    public String getMemberDetail(@PathVariable Long memberId, Model model) {
//        MemberDto.Response member = memberService.getMemberById(memberId);
//        model.addAttribute("member", member);
//        return "home";
//    }

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

    @GetMapping(value = "/home", params = "action=myinfo")
    public String home(Model model) {
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
        System.out.println(">>> member: " + member);
        model.addAttribute("member", member);

        return "home";
    }

//    @GetMapping("/home")
//    public String defaultHome(Model model,
//                              @RequestParam(value = "username", required = false) String username) {
//
//        List<MemberDto.Response> memberList = memberService.getMemberList();
//        model.addAttribute("members", memberList);
//
//        if (username != null) {
//            MemberDto.Response member = memberService.getMemberById(username);
//            model.addAttribute("member", member);
//        }
//
//        return "home";
//    }

    @GetMapping("/home")
    public String defaultHome(Model model) {
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
        model.addAttribute("member", member);
        return "home";
    }
}
