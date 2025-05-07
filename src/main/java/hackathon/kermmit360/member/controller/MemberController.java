package hackathon.kermmit360.member.controller;

import hackathon.kermmit360.member.dto.MemberDto;
import hackathon.kermmit360.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;


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
    public String getMemberDetail(@RequestParam Long memberId, Model model) {
        MemberDto.Response member = memberService.getMemberById(memberId);
        System.out.println(">>> member: " + member);
        model.addAttribute("member", member);
        return "home";
    }

    @GetMapping(value = "/home", params = "action=myinfo")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        MemberDto.Response member = memberService.getMemberByUsername(username);
        System.out.println(">>> member: " + member);
        model.addAttribute("member", member);

        return "home";
    }
}
