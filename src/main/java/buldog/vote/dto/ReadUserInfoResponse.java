package buldog.vote.dto;

import buldog.vote.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReadUserInfoResponse {
    private String name;
    private String username;
    private String email;
    private String role;
    private String part;
    private String team;

    public static ReadUserInfoResponse from(User user) {
        ReadUserInfoResponse response = new ReadUserInfoResponse();
        response.name = user.getName();
        response.username = user.getUsername();
        response.email = user.getEmail();
        response.role = user.getRole().toString();
        response.part = user.getPart().toString();
        response.team = user.getTeam().getName();

        return response;
    }
}
