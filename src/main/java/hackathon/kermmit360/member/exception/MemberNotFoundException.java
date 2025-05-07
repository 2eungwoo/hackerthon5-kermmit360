package hackathon.kermmit360.member.exception;

import hackathon.kermmit360.global.error.ErrorCode;
import hackathon.kermmit360.global.error.exception.CustomException;

public class MemberNotFoundException extends CustomException {
    public MemberNotFoundException(String message) {
        super(message, ErrorCode.MEMBER_NOT_EXIST);
    }

    public MemberNotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}