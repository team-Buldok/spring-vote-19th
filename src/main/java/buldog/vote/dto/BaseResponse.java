package buldog.vote.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseResponse<T> {
    private final HttpStatus httpStatus;
    private final String message;
    private final T value;

    public BaseResponse(HttpStatus httpStatus, String message, T value) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.value = value;
    }
}
