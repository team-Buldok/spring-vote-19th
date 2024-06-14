package buldog.vote.dto;

import buldog.vote.domain.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReadTeamResponse {
    private String name;
    private String introduction;

    public static ReadTeamResponse from(final Team team) {
        final ReadTeamResponse response = new ReadTeamResponse();
        response.name = team.getName();
        response.introduction = team.getIntroduction();

        return response;
    }
}
