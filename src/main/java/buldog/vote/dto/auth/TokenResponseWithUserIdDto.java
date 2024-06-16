package buldog.vote.dto.auth;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseCookie;

@Getter
public class TokenResponseWithUserIdDto {
    private String grantType;
    private String accessToken;
    private ResponseCookie responseCookie;
    private Long accessTokenExpirationTime;
    private String userId;

    @Builder
    public TokenResponseWithUserIdDto(String grantType,String accessToken,ResponseCookie responseCookie, Long accessTokenExpirationTime,
            String userId){
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.responseCookie = responseCookie;
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.userId = userId;
    }
}
