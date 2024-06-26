package buldog.vote.dto;

import buldog.vote.domain.Part;
import buldog.vote.domain.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class JoinLeaderRequest {
    @NotBlank(message="name must be entered")
    private String name;

    @NotBlank
    private String team;
    private Part part;
}
