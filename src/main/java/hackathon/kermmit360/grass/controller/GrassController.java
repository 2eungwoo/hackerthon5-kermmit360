//package hackathon.kermmit360.grass.controller;
//
//import hackathon.kermmit360.grass.service.GrassService;
//import hackathon.kermmit360.member.dto.MemberDto;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Controller
//@RequiredArgsConstructor
//@Slf4j
//public class GrassController {
//
//    private final GrassService grassService;
//
//    @PostMapping("/api/member/{memberId}/exp")
//    public ResponseEntity<MemberDto.Response> updateMemberExp(@PathVariable Long memberId) {
//        MemberDto.Response response = grassService.updateExp(memberId);
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping(value = "/home/api/member/{memberId}/exp", params = "action=expup")
//    public String updateMyExp(@RequestParam Long memberId, Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//
//        MemberDto.Response member = memberService.getMemberByUsername(username);
//        MemberDto.Response updatedMember = memberService.updateExp(member.getId());
//
//        model.addAttribute("member", updatedMember);
//
//        return "home";  // home.html에서 th:if="${member != null}" 영역 출력
//    }
//
//    @GetMapping(value = "/home", params = "action=myinfo")
//    public String home(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        MemberDto.Response member = memberService.getMemberByUsername(username);
//
//        model.addAttribute("member", member);
//
//        return "home";
//    }
//
//    @GetMapping(value = "/home", params = "action=detail")
//    public String getMemberDetail(@RequestParam Long memberId, Model model) {
//        MemberDto.Response member = memberService.getMemberById(memberId);
//        log.info(">>>>>>>>>>> member: {}", member);
//        model.addAttribute("member", member);
//        return "home";
//    }
//
//}
