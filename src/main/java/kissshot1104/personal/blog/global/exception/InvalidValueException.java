package kissshot1104.personal.blog.global.exception;

public class InvalidValueException extends BusinessException {
    public InvalidValueException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
