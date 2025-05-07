package hackathon.kermmit360.login;

import hackathon.kermmit360.global.response.ResponseCode;
import hackathon.kermmit360.global.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 관련 API")
public class GithubLoginController {

    @Autowired
    private GithubLoginService githubLoginService;
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/join")
    @Operation(summary = "사용자 조회", description = "ID로 사용자를 조회 후 회원가입.")
    public ResponseEntity<ResultResponse> findById(OAuth2AuthenticationToken authentication) {
        GithubUserEntity user = githubLoginService.userLogin(authentication);
        ResultResponse response = ResultResponse.of(ResponseCode.REGISTER_SUCCESS, user);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }
}
