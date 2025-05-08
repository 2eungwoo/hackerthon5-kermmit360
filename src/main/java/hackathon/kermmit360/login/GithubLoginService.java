package hackathon.kermmit360.login;

import hackathon.kermmit360.global.response.ResponseCode;
import hackathon.kermmit360.global.response.ResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class GithubLoginService {
    @Autowired
    private GIthubLoginRepository githubLoginRepository;
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    public ResultResponse userLogin(OAuth2AuthenticationToken authentication) {
        Integer id = authentication.getPrincipal().getAttribute("id");
        GithubUserEntity user = null;
        if(!githubLoginRepository.findByGithubId(id).isEmpty()){
            user = githubLoginRepository.findByGithubId(id).get(0);
        }
        if(user == null){
            user = GithubUserEntity.builder()
                    .githubLogin(authentication.getPrincipal().getAttribute("login"))
                    .githubId(id)
                    .githubName(authentication.getPrincipal().getAttribute("name"))
                    .githubEmail(authentication.getPrincipal().getAttribute("email"))
                    .githubUrl(authentication.getPrincipal().getAttribute("html_url"))
                    .build();

            githubLoginRepository.save(user);
            return ResultResponse.of(ResponseCode.REGISTER_SUCCESS, user);
        }
        return ResultResponse.of(ResponseCode.LOGIN_SUCCESS, user);
    }
//    public void saveUser(UserDto dto) {
//        User user = User.builder()
//                .name(dto.getName())
//                .email(dto.getEmail())
//                .phoneNumber(dto.getPhoneNumber())
//                .build();
//        System.out.println(user);
//        userRepository.save(user);
//    }
}
