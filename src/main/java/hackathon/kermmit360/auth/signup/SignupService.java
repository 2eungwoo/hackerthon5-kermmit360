package hackathon.kermmit360.auth.signup;

import hackathon.kermmit360.global.error.ErrorCode;
import hackathon.kermmit360.global.error.exception.CustomException;
import hackathon.kermmit360.member.entity.MemberEntity;
import hackathon.kermmit360.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void signup(SignupRequestDto signupRequestDto){
        // MemberEntity isUserExists = memberRepository.findByUsername(signupRequestDto.getUsername());
//        if(isUserExists != null){
//            throw new CustomException(ErrorCode.USER_EMAIL_ALREADY_EXISTS);
//        }
        String encodedPassword = bCryptPasswordEncoder.encode(signupRequestDto.getPassword());

        MemberEntity memberEntity = MemberEntity.builder()
                .username(signupRequestDto.getUsername())
                .email(signupRequestDto.getEmail())
                .password(encodedPassword)
                .role("ROLE_USER")
                .build();

        memberRepository.save(memberEntity);
        // return memberEntity.getUsername();
    }
}
