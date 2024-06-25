package buldog.vote.dto.auth;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseCookie;

@Getter
public class TokenResponseWithUserIdDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpirationTime;
    private String userId;

    @Builder
    public TokenResponseWithUserIdDto(String grantType,String accessToken,String refreshToken, Long accessTokenExpirationTime,
            String userId){
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.userId = userId;
    }
}
