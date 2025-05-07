package hackathon.kermmit360.member.dto;

import hackathon.kermmit360.member.entity.MemberEntity;
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
        private final int pushCount;

        public Response(MemberEntity memberEntity){
            this.username = memberEntity.getUsername();
            this.email = memberEntity.getEmail();
            this.pushCount = memberEntity.getPushCount();
        }
    }
}
