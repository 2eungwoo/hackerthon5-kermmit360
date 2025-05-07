package hackathon.kermmit360.auth.signin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SigninController {

    @GetMapping("/auth/signin")
    public String signin(){
        return "signin";
    }



}