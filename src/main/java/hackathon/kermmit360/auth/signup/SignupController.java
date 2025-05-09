package hackathon.kermmit360.auth.signup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class SignupController {

    private final SignupService signupService;

    @GetMapping("/auth/signup")
    public String signup(){
        return "signup";
    }

    @PostMapping("/auth/signup")
    public String signup(SignupRequestDto signupRequestDto){
        // return signupService.signup(signupRequestDto);
        signupService.signup(signupRequestDto);
        return "redirect:/auth/signin";
    }

}
