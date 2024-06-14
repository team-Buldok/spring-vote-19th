package buldog.vote.dto;

import buldog.vote.domain.Part;
import buldog.vote.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReadLeaderResponse {
    private Long userId;
    private String name;
    private Part part;

    public static ReadLeaderResponse from(User user) {
        ReadLeaderResponse response = new ReadLeaderResponse();
        response.userId = user.getId();
        response.name = user.getName();
        response.part = user.getPart();

        return response;
    }
}
