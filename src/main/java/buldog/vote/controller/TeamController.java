package buldog.vote.controller;

import buldog.vote.dto.BaseResponse;
import buldog.vote.dto.ReadTeamResponse;
import buldog.vote.dto.ReadTeamVoteResultResponse;
import buldog.vote.service.TeamService;
import buldog.vote.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TeamController {

    private final TeamService teamService;

    @Operation(summary = "데모데이 팀 조회하기",description = "모든 데모데이 팀들의 정보를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "조회 성공"),
    })
    @GetMapping("/teams")
    public ResponseEntity<BaseResponse<List<ReadTeamResponse>>> readAllTeams() {
        List<ReadTeamResponse> response = teamService.getAllTeam();

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "모든 데모데이 팀 조회", response));
    }

    @Operation(summary = "데모데이 팀 투표 결과 반환",description = "데모데이 팀의 정보를 득표순으로 내림차순 정렬하여 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "조회 성공"),
    })
    @GetMapping("/teams/votes")
    public ResponseEntity<BaseResponse<List<ReadTeamVoteResultResponse>>> readTeamVoteResult() {
        List<ReadTeamVoteResultResponse> response = teamService.getTeamVoteResult();

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "데모데이 팀 투표 결과 조회", response));
    }
}
