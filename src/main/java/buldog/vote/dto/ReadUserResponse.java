package buldog.vote.dto;

import buldog.vote.domain.Part;
import buldog.vote.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReadUserResponse {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Part part;

    public static ReadUserResponse from(User user) {
        ReadUserResponse response = new ReadUserResponse();
        response.name = user.getName();
        response.part = user.getPart();

        return response;
    }
}
