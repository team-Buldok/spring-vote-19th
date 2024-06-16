package buldog.vote.common.handler;

import buldog.vote.dto.BaseResponse;
import buldog.vote.exception.AppException;
import buldog.vote.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.slf4j.MDC;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {



    // Custom Bad Request Error
    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<BaseResponse<?>> handleBadRequestException(
            AppException appException,
            HttpServletRequest request) {
        logInfo(request, appException.getMessage());
        return ResponseEntity.status(appException.getErrorCode().getHttpStatus()).body(new BaseResponse<>(
                appException.getErrorCode().getHttpStatus(), null, appException.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<BaseResponse<?>> notFoundException(
            BadRequestException exception,
            HttpServletRequest request) {
        logInfo(request, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse<>(HttpStatus.NOT_FOUND, null, exception.getMessage()));
    }

    // Custom Unauthorized Error

    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<BaseResponse<?>> handleUnauthorizedException(
            UnauthorizedException exception,
            HttpServletRequest request) {
        logInfo(request, exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse<>(HttpStatus.UNAUTHORIZED,null,exception.getMessage()));
    }

    // Custom Internal Server Error



    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<BaseResponse<?>> handleMethodArgNotValidException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        String message = exception.getBindingResult().getFieldError().getDefaultMessage();

        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]");
        }

        logInfo(request, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>(HttpStatus.NOT_FOUND, null, builder.toString()));
    }




    private void logInfo(HttpServletRequest request, String message) {
        log.info("{} {} : {} (traceId: {})",
                request.getMethod(), request.getRequestURI(), message, getTraceId());
    }


    private String getTraceId() {
        return MDC.get("traceId");
    }
}

