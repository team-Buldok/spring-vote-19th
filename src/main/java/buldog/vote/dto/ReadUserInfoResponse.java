package buldog.vote.dto;

import buldog.vote.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReadUserInfoResponse {
    private Long userId;
    private String name;
    private String loginId;
    private String email;

    public static ReadUserInfoResponse from(User user) {
        ReadUserInfoResponse response = new ReadUserInfoResponse();
        response.userId = user.getId();
        response.name = user.getName();
        response.loginId = user.getLoginId();
        response.email = user.getEmail();

        return response;
    }
}
