package hackathon.kermmit360.auth.signup;

import hackathon.kermmit360.github.service.GithubEventService;
import hackathon.kermmit360.global.error.ErrorCode;
import hackathon.kermmit360.global.error.exception.CustomException;
import hackathon.kermmit360.member.entity.MemberEntity;
import hackathon.kermmit360.member.repository.MemberRepository;
import hackathon.kermmit360.rank.Rank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void signup(SignupRequestDto signupRequestDto){
         boolean isUserExists = memberRepository.existsByUsername(signupRequestDto.getUsername());
        if(isUserExists){
            throw new CustomException(ErrorCode.USER_EMAIL_ALREADY_EXISTS);
        }

        String encodedPassword = bCryptPasswordEncoder.encode(signupRequestDto.getPassword());

        MemberEntity memberEntity = MemberEntity.builder()
                .username(signupRequestDto.getUsername())
                .email(signupRequestDto.getEmail())
                .password(encodedPassword)
                .role("ROLE_USER")
                .exp(0)
                .tier(Rank.BRONZE)
                .build();

        memberRepository.save(memberEntity);
        // return memberEntity.getUsername();
    }
}
