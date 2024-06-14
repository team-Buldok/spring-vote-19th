package buldog.vote.controller;

import buldog.vote.dto.BaseResponse;
import buldog.vote.dto.ReadTeamResponse;
import buldog.vote.dto.ReadTeamVoteResultResponse;
import buldog.vote.service.TeamService;
import buldog.vote.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    /**
     * 모든 데모데이 팀 보기
     *
     * @return 팀의 이름, 소개글
     */
    @GetMapping("/teams")
    public ResponseEntity<BaseResponse<List<ReadTeamResponse>>> readAllTeams() {
        List<ReadTeamResponse> response = teamService.getAllTeam();

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "모든 데모데이 팀 조회", response));
    }

    /**
     * 팀 투표 결과 보기
     *
     * @return 팀의 이름, 소개글, 득표수
     */
    @GetMapping("/teams/votes")
    public ResponseEntity<BaseResponse<List<ReadTeamVoteResultResponse>>> readTeamVoteResult() {
        List<ReadTeamVoteResultResponse> response = teamService.getTeamVoteResult();

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "데모데이 팀 투표 결과 조회", response));
    }
}
