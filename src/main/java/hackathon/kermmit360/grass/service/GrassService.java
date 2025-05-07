package hackathon.kermmit360.grass.service;

import hackathon.kermmit360.global.error.ErrorCode;
import hackathon.kermmit360.member.dto.MemberDto;
import hackathon.kermmit360.member.entity.MemberEntity;
import hackathon.kermmit360.member.exception.MemberNotFoundException;
import hackathon.kermmit360.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GrassService {
    private final MemberRepository memberRepository;

    @Transactional
    public MemberDto.Response updateExp(String username) {
        MemberEntity member = memberRepository.findByUsername(username);

        member.addPushCount();
        memberRepository.save(member);
        return new MemberDto.Response(member);
    }
}
