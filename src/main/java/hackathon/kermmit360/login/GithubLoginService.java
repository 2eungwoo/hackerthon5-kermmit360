package hackathon.kermmit360.login;

import hackathon.kermmit360.member.entity.MemberEntity;
import hackathon.kermmit360.member.repository.MemberRepository;
import hackathon.kermmit360.rank.Rank;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Member;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubLoginService {

    private final MemberRepository memberRepository;

    @Transactional
    public String userLogin(OAuth2AuthenticationToken authentication) {
        Integer githubId = authentication.getPrincipal().getAttribute("id");
        log.info("==================== githubId : {}",githubId);
        MemberEntity user = memberRepository.findByGithubId(githubId);
        if(user != null){

            user = MemberEntity.builder()
                    .githubId(githubId)
                    .username(authentication.getPrincipal().getAttribute("name"))
                    .email(authentication.getPrincipal().getAttribute("email"))
                    .role("ROLE_USER")
                    .build();

            memberRepository.save(user);
            log.info("==================== saved member : {}",user);
            return user.getUsername();
        }
        return null;
    }
}
