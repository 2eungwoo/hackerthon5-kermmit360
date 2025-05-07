package hackathon.kermmit360.member.entity;

import hackathon.kermmit360.rank.Rank;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "push_count", nullable = false)
    private int pushCount = 0;

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
        this.pushCount++;
    }

    public void addExp() {
        this.exp += 1;
        this.tier = Rank.getRank(this.exp);
    }
}