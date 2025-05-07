package hackathon.kermmit360.member.dto;

import hackathon.kermmit360.member.entity.MemberEntity;
import hackathon.kermmit360.rank.Rank;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class MemberDto {

    @Getter
    @AllArgsConstructor
    public static class Request {
        private String username;
        private String email;
        private int pushCount;
    }

    @Getter
    public static class Response {
        private final String username;
        private final String email;
        private final int exp;
        private final Rank tier;
        private int rank;

        public Response(MemberEntity memberEntity){
            this.username = memberEntity.getUsername();
            this.email = memberEntity.getEmail();
            this.exp = memberEntity.getExp();
            this.tier = memberEntity.getTier();
        }

        public Response(MemberEntity memberEntity, int rank){
            this.username = memberEntity.getUsername();
            this.email = memberEntity.getEmail();
            this.exp = memberEntity.getExp();
            this.tier = memberEntity.getTier();
            this.rank = rank;
        }
    }
}
