package hackathon.kermmit360.common.util.finder;

import hackathon.kermmit360.member.entity.MemberEntity;
import hackathon.kermmit360.member.repository.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FinderConfig {

    @Bean
    public EntityFinder<MemberEntity, Long> postFinder(MemberRepository memberRepository) {
        return new JpaEntityFinder<>(memberRepository, "MemberEntity");
    }

    // 기타 등등
}
