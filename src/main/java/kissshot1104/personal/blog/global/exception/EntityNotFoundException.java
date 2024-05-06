package kissshot1104.personal.blog.global.exception;


public class EntityNotFoundException extends BusinessException {
    private ErrorCode errorCode;
    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
