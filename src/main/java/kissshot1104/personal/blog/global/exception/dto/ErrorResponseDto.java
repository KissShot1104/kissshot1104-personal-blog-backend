package kissshot1104.personal.blog.global.exception.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import kissshot1104.personal.blog.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponseDto {
    private String message;
    private String code;
    private List<FieldError> errors;

    private ErrorResponseDto(final ErrorCode errorCode, List<FieldError> errors) {
        this.message = errorCode.getMessage();
        this.code = errorCode.getCode();
        this.errors = errors;
    }

    private ErrorResponseDto(final ErrorCode errorCode) {
        this.message = errorCode.getMessage();
        this.code = errorCode.getCode();
        this.errors = new ArrayList<>();
    }

    public static ErrorResponseDto of(final ErrorCode errorCode, final BindingResult bindingResult) {
        return new ErrorResponseDto(errorCode, FieldError.of(bindingResult));
    }

    public static ErrorResponseDto of(final ErrorCode errorCode) {
        return new ErrorResponseDto(errorCode);
    }

    public static ErrorResponseDto of(final MethodArgumentTypeMismatchException e) {
        String field = e.getName();
        String value = Optional.ofNullable(e.getValue())
                .map(Object::toString)
                .orElse("");
        List<FieldError> errors = FieldError.of(field, value, e.getErrorCode());
        return new ErrorResponseDto(ErrorCode.INVALID_INPUT_VALUE, errors);
    }


    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class FieldError {
        private String field;
        private String value;
        private String message;


        private static List<FieldError> of(final String field, final String value, final String message) {
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError(field, value, message));
            return errors;
        }

        private static List<FieldError> of(final BindingResult bindingResult) {
            List<org.springframework.validation.FieldError> errors = bindingResult.getFieldErrors();

            return errors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null? "" :
                                    error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .toList();
        }

    }
}
