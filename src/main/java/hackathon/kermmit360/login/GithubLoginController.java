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
import org.springframework.web.bind.annotation.*;

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
        String response = githubLoginService.userLogin(authentication);
        System.out.println(response);
//        ResultResponse response = ResultResponse.of(ResponseCode.REGISTER_SUCCESS, user);
        return new ResponseEntity<>(HttpStatus.OK);
//        return null;
    }

//    @PostMapping("/webhook")
//    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
//                                                @RequestHeader(value = "X-GitHub-Event") String event,
//                                                @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature) {
//        System.out.println("📥 GitHub Event: " + event);
//        System.out.println("📦 Payload: " + payload);
//        System.out.println("🔑 Signature: " + signature); // 보안 키를 설정했다면
//
//        // 여기에 로직 작성 (예: 로그 저장, 특정 이벤트 처리 등)
//
//        return ResponseEntity.ok("Received");
//    }
}
