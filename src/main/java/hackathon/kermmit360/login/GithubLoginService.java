package hackathon.kermmit360.login;

import hackathon.kermmit360.member.entity.MemberEntity;
import hackathon.kermmit360.member.repository.MemberRepository;
import hackathon.kermmit360.rank.Rank;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;

@Service
@RequiredArgsConstructor
public class GithubLoginService {

    private final MemberRepository memberRepository;

    public String userLogin(OAuth2AuthenticationToken authentication) {
        Integer githubId = authentication.getPrincipal().getAttribute("id");
        System.out.println(githubId);
        MemberEntity user = null;
        if(!memberRepository.findByGithubId(githubId).isEmpty()){
            user = memberRepository.findByGithubId(githubId).get(0);
        }else{
            user = MemberEntity.builder()
                    .githubId(githubId)
                    .username(authentication.getPrincipal().getAttribute("name"))
                    .email(authentication.getPrincipal().getAttribute("email"))
                    .role("ROLE_USER")
                    .build();

            memberRepository.save(user);
        }
        return user.getUsername();
    }
}
