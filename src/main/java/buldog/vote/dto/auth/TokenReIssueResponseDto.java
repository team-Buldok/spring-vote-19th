package buldog.vote.dto.auth;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseCookie;

@Getter
public class TokenReIssueResponseDto {
    private String grantType;
    private String accessToken;
    private ResponseCookie responseCookie;
    private Long accessTokenExpirationTime;

    @Builder
    public TokenReIssueResponseDto(String grantType,String accessToken,ResponseCookie responseCookie, Long accessTokenExpirationTime){
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.responseCookie = responseCookie;
        this.accessTokenExpirationTime = accessTokenExpirationTime;
    }
}
