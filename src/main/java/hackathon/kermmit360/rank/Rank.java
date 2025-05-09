package hackathon.kermmit360.rank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@AllArgsConstructor
@Slf4j
public enum Rank {
    BRONZE("브론즈"),
    SILVER("실버"),
    GOLD("골드"),
    PLATINUM("플래티넘"),
    DIAMOND("다이아몬드"),
    MASTER("마스터"),
    GRANDMASTER("그랜드마스터"),
    CHALLENGER("챌린저");

    private final String tier;

    public static Rank getRank(int exp) {
        log.info("======= getRank() exp: {}", exp);

        if (exp >= 0 && exp <= 2) {
            return BRONZE;
        } else if (exp <= 6) {
            return SILVER;
        } else if (exp <= 10) {
            return GOLD;
        } else if (exp <= 15) {
            return PLATINUM;
        } else if (exp <= 25) {
            return DIAMOND;
        } else if (exp <= 40) {
            return MASTER;
        } else if (exp <= 66) {
            return GRANDMASTER;
        } else {
            return CHALLENGER;
        }
    }
}