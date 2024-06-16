package buldog.vote.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignInRequestDto {

    @NotBlank(message = "아이디를 입력해 주세요")
    @Schema(description = "사용자의 로그인 아이디",example = "blackbox0209")
    private String userID;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Schema(description = "사용자의 로그인 비밀번호",example = "fakdjflk")
    private String password;
    public UsernamePasswordAuthenticationToken getAuthenticationToken(){
        return new UsernamePasswordAuthenticationToken(userID,password);
    }
}
