//package hackathon.kermmit360.graph.commitstat.controller;
//
//import hackathon.kermmit360.graph.commitstat.domain.dto.CommitStatDto;
//import hackathon.kermmit360.graph.commitstat.domain.repository.CommitStatRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//public class CommitStatController {
//
//    private final CommitStatRepository commitStatRepository;
//
//    @GetMapping("/api/commits/{username}")
//    public List<CommitStatDto> getCommitStats(@PathVariable String username) {
//        return commitStatRepository.findCommitStatsByUsername(username).stream()
//                .map(c -> new CommitStatDto(c.getCommitDate(), c.getCommitCount()))
//                .toList();
//    }
//}