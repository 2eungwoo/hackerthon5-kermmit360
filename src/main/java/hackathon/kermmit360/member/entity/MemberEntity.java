package hackathon.kermmit360.member.entity;

import hackathon.kermmit360.rank.Rank;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
@Builder
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "github_id")
    private int githubId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "exp", nullable = false)
    private int exp = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier", nullable = false)
    private Rank tier = Rank.BRONZE;

    @Builder
    public MemberEntity(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.exp = 0;
        this.tier = Rank.BRONZE;
    }

    public void addPushCount() {
        addExp();
    }

    public void addExp() {
        this.exp += 1;
        this.tier = Rank.getRank(this.exp);
    }

    public void assignExp(int commitCount){
        this.exp = commitCount;
    }

}