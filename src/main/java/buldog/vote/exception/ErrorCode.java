package buldog.vote.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    DATA_ALREADY_EXISTED(CONFLICT, ""),
    EMAIL_ALREADY_EXISTED(CONFLICT, "same email already exists"),
    NO_DATA_EXISTED(NOT_FOUND, ""),
    NOT_NULL(NO_CONTENT, ""),
    VOTE_TO_ANOTHER_PART(CONFLICT, "vote for another part"),
    ALREADY_VOTED(CONFLICT, "already voted"),
    VOTE_FOR_YOUR_TEAM(CONFLICT,"could't vote for your team"),

    ID_DUPLICATED(CONFLICT, "id already exists"),
    INVALID_PASSWORD(UNAUTHORIZED, ""),

    NO_DATA_ALLOCATED(FAILED_DEPENDENCY, ""),
    INVALID_REQUEST_DATA(BAD_REQUEST, ""),

    KEYWORD_TOO_SHORT(BAD_REQUEST, ""),
    INVALID_URI_ACCESS(NOT_FOUND, ""),
    NOT_INCLUDED_USER_ACCESS(CONFLICT, ""),
    REFRESH_NOT_EXIST(BAD_REQUEST, ""),
    TOKEN_EXPIRED(BAD_REQUEST, ""),


    //로그인 관련
    ID_NOT_MATCH(BAD_REQUEST,"login id not match"),
    PASSWORD_NOT_MATCH(BAD_REQUEST,"password not match"),
    REFRESH_TOKEN_NOT_MATCH(BAD_REQUEST,"refresh token not match"),
    ACCESS_TOKEN_NOT_MATCH(BAD_REQUEST,"access token not match");


    private final HttpStatus httpStatus;
    private final String message;
}
