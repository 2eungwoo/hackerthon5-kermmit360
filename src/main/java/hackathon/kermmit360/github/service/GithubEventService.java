package hackathon.kermmit360.github.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hackathon.kermmit360.github.dto.GithubPushEventDto;
import hackathon.kermmit360.member.dto.MemberDto;
import hackathon.kermmit360.member.entity.MemberEntity;
import hackathon.kermmit360.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubEventService {

    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;

    public GithubPushEventDto fetchAndApplyExp(String username) {
        try {
            String url = "https://api.github.com/users/" + username + "/events";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());

            for (JsonNode event : root) {
                if ("PushEvent".equals(event.get("type").asText())) {
                    String createdAt = event.get("created_at").asText();
                    String repoName = event.get("repo").get("name").asText();
                    int commitCount = event.get("payload").get("commits").size();

                    MemberEntity member = memberRepository.findByUsername(username);
                    member.assignExp(commitCount);
                    memberRepository.save(member);

                    log.info("✔️ [GitHub] {}: {} commits to {} at {}", username, commitCount, repoName, createdAt);
                    return new GithubPushEventDto(username, repoName, createdAt, commitCount);
                }
            }
        } catch (Exception e) {
            log.error("❌ GitHub 이벤트 조회 실패: {}", e.getMessage(), e);
        }

        return null;
    }

    @Transactional
    public MemberDto.Response fakeCommit(String username){
        MemberEntity memberEntity = memberRepository.findByUsername(username);
        memberEntity.addExp();
        memberRepository.save(memberEntity);
        log.info("============== member exp : {}", memberEntity.getExp());
        return new MemberDto.Response(memberEntity);
    }
}