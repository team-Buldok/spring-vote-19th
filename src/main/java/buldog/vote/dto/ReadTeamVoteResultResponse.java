package buldog.vote.dto;

import buldog.vote.domain.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReadTeamVoteResultResponse {
    private Long teamId;
    private String name;
    private String introduction;
    private int voteCount;

    public static ReadTeamVoteResultResponse of(Team team, int voteCount) {
        ReadTeamVoteResultResponse response = new ReadTeamVoteResultResponse();
        response.teamId = team.getId();
        response.name = team.getName();
        response.introduction = team.getIntroduction();
        response.voteCount = voteCount;

        return response;
    }
}
