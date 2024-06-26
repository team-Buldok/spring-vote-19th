package buldog.vote.dto;

import buldog.vote.domain.Part;
import buldog.vote.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class ReadLeaderVoteResultResponse {
    private Long userId;
    private String name;
    private Part part;
    private int voteCount;

    public static ReadLeaderVoteResultResponse of(User user, int voteCount) {
        ReadLeaderVoteResultResponse response = new ReadLeaderVoteResultResponse();
        response.userId = user.getId();
        response.name = user.getName();
        response.part = user.getPart();
        response.voteCount = voteCount;

        return response;
    }
}
