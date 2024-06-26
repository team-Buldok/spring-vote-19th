package buldog.vote.dto.auth;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseCookie;

@Getter
public class TokenReIssueResponseDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpirationTime;

    @Builder
    public TokenReIssueResponseDto(String grantType,String accessToken,String refreshToken, Long accessTokenExpirationTime){
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpirationTime = accessTokenExpirationTime;
    }
}
