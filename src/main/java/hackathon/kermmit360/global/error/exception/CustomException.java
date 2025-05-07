package hackathon.kermmit360.global.error.exception;


import hackathon.kermmit360.global.error.ErrorCode;
import hackathon.kermmit360.global.error.ErrorResponse;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;


@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;
    private List<ErrorResponse.FieldError> errors = new ArrayList<>();

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, List<ErrorResponse.FieldError> errors) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errors = errors;
    }
}