package buldog.vote.dto;

import buldog.vote.domain.Part;
import buldog.vote.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class JoinUserRequest {
    @NotBlank(message="name must be entered")
    private String name;

    @NotBlank(message="id must be entered")
    private String loginId;

    @NotBlank(message="password must be entered")
    private String password;

    @NotBlank(message="email must be entered")
    @Pattern(regexp="^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$", message="invalid email type")
    private String email;

    @NotBlank
    private String team;

    @NotBlank
    private Role role;

    @NotBlank
    private Part part;
}
