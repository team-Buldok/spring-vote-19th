package buldog.vote.exception;

public class UnauthorizedException extends RuntimeException{
    public UnauthorizedException(ErrorCode code) {
        super(code.getMessage());
    }
}
