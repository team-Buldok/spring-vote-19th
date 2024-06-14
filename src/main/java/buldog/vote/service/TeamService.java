package buldog.vote.service;

import buldog.vote.domain.Team;
import buldog.vote.domain.User;
import buldog.vote.dto.ReadLeaderVoteResultResponse;
import buldog.vote.dto.ReadTeamResponse;
import buldog.vote.dto.ReadTeamVoteResultResponse;
import buldog.vote.exception.AppException;
import buldog.vote.exception.ErrorCode;
import buldog.vote.repository.TeamRepository;
import buldog.vote.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    /**
     *  모든 팀 정보 반환
     * @return 팀의 PK, 이름, 소개글
     */
    public List<ReadTeamResponse> getAllTeam() {
        List<ReadTeamResponse> responses = new ArrayList<>();

        teamRepository.findAll().stream().forEach(team -> {
            responses.add(ReadTeamResponse.from(team));
        });

        return responses;
    }

    /**
     * 데모데이 팀 투표 결과 반환
     *
     * @return 팀의 이름, 소개글, 득표수를 득표수에 대해 내림차순으로 반환
     */
    public List<ReadTeamVoteResultResponse> getTeamVoteResult() {
        List<Team> teams = teamRepository.findAll();

        Map<Long, Integer> votes = new HashMap<>(); // (pk, voteCount)
        for (Team team : teams) {
            int voteCount = userRepository.countByVoteTeam(team);
            votes.put(team.getId(), voteCount);
        }

        // 파트장 투표 내림차순 정렬
        List<Long> idSet = new ArrayList<>(votes.keySet());
        idSet.sort((o1, o2) -> votes.get(o2).compareTo(votes.get(o1)));

        List<ReadTeamVoteResultResponse> responses = new ArrayList<>();
        for (Long id : idSet) {
            Team team = teamRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.NO_DATA_EXISTED, "User does not exist"));

            responses.add(ReadTeamVoteResultResponse.of(team, votes.get(id)));
        }


        return responses;
    }

    /**
     * 데모데이 팀 투표
     * @param voterId
     * @param teamId
     */
    @Transactional
    public void voteToTeam(Long voterId, Long teamId) {
        User voter = userRepository.findById(voterId)
                .orElseThrow(() -> new AppException(ErrorCode.NO_DATA_EXISTED, "User does not exists"));

        Team voteTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new AppException(ErrorCode.NO_DATA_EXISTED, "Team does not exists"));

        // 이미 투표한 경우
        if (voter.getVoteTeam() != null) {
            throw new AppException(ErrorCode.ALREADY_VOTED);
        }

        // 본인의 팀에 투표 불가
        if(voter.getTeam().equals(voteTeam)) {
            throw new AppException(ErrorCode.VOTE_FOR_YOUR_TEAM);
        }

        voter.updateVoteTeam(voteTeam);
    }
}
