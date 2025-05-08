package hackathon.kermmit360.login;

import hackathon.kermmit360.global.response.ResponseCode;
import hackathon.kermmit360.global.response.ResultResponse;
import hackathon.kermmit360.member.dto.MemberDto;
import hackathon.kermmit360.member.entity.MemberEntity;
import hackathon.kermmit360.member.repository.MemberRepository;
import hackathon.kermmit360.rank.Rank;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;

@Service
@RequiredArgsConstructor
public class GithubLoginService {

    private final MemberRepository memberRepository;

    public String userLogin(OAuth2AuthenticationToken authentication) {
        Integer id = authentication.getPrincipal().getAttribute("id");
        System.out.println(id);
        MemberEntity user = null;
        if(!memberRepository.findByGithubId(id).isEmpty()){
            user = memberRepository.findByGithubId(id).get(0);
        }else{
            user = MemberEntity.builder()
                    .githubId(id)
                    .username(authentication.getPrincipal().getAttribute("name"))
                    .email(authentication.getPrincipal().getAttribute("email"))
                    .role("ROLE_USER")
                    .tier(Rank.BRONZE)
                    .exp(0)
                    .build();

            memberRepository.save(user);
        }
        return user.getUsername();
    }
}
