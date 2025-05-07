package hackathon.kermmit360.member.service;

import hackathon.kermmit360.common.util.finder.EntityFinder;
import hackathon.kermmit360.member.dto.MemberDto;
import hackathon.kermmit360.member.entity.MemberEntity;
import hackathon.kermmit360.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final EntityFinder<MemberEntity, Long> memberFinder;

    @Transactional(readOnly = true)
    public MemberDto.Response getMemberById(Long memberId){
        MemberEntity member = memberFinder.findByIdOrThrow(memberId);
        // todo : id -> username
        // MemberEntity member = memberRepository.findByUsername(username);
        return new MemberDto.Response(member);
    }

    @Transactional(readOnly = true)
    public List<MemberDto.Response> getMemberList(){
        List<MemberEntity> memberList = memberRepository.findAll();
        return memberList.stream()
                .map(MemberDto.Response::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public MemberDto.Response getMemberByUsername(String username) {
        MemberEntity member = memberRepository.findByUsername(username);
        return new MemberDto.Response(member);
    }

}
