//package hackathon.kermmit360.graph.commitstat.domain.entity;
//
//import hackathon.kermmit360.member.entity.MemberEntity;
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDate;
//
//@Getter
//@Entity
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class CommitStatEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private LocalDate commitDate;
//
//    private int commitCount;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    private MemberEntity member;
//}
