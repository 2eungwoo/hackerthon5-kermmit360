package hackathon.kermmit360.grass.controller;

import hackathon.kermmit360.grass.service.GrassService;
import hackathon.kermmit360.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
public class GrassController {

    private final GrassService grassService;

    @PostMapping(value = "/home/api/commit", params = "action=commit")
    public String updateMyExp(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        MemberDto.Response member = grassService.updateExp(username);

        model.addAttribute("member", member);

        return "home";
    }


}
