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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

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

            List<ZonedDateTime> commitTimestamps = new ArrayList<>();
            String recentRepo = "";
            String lastCreatedAt = null;

            for (JsonNode event : root) {
                if ("PushEvent".equals(event.get("type").asText())) {
                    String createdAtStr = event.get("created_at").asText(); // 푸시 이벤트 시간
                    ZonedDateTime createdAt = ZonedDateTime.parse(createdAtStr);
                    JsonNode commits = event.get("payload").get("commits");

                    int commitCount = commits.size();
                    for (int i = 0; i < commitCount; i++) {
                        commitTimestamps.add(createdAt); // 모든 커밋에 대해 타임스탬프 추가
                    }

                    if (lastCreatedAt == null) {
                        // 가장 최근 이벤트 정보 저장
                        lastCreatedAt = createdAtStr;
                        recentRepo = event.get("repo").get("name").asText();
                    }
                }
            }

            int totalCommits = commitTimestamps.size();

            // 경험치 반영
            MemberEntity member = memberRepository.findByUsername(username);
            member.assignExp(totalCommits);
            memberRepository.save(member);

            log.info("✔️ [GitHub] {}: {} commits collected", username, totalCommits);

            return new GithubPushEventDto(username, recentRepo, lastCreatedAt, totalCommits, commitTimestamps);

        } catch (Exception e) {
            log.error("❌ GitHub 이벤트 조회 실패: {}", e.getMessage(), e);
            return null;
        }
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