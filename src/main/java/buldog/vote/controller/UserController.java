package buldog.vote.controller;

import buldog.vote.common.interfaces.CurrentMemberLoginId;
import buldog.vote.common.interfaces.CurrentMemberPK;
import buldog.vote.domain.Part;
import buldog.vote.domain.User;
import buldog.vote.dto.*;
import buldog.vote.service.TeamService;
import buldog.vote.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final TeamService teamService;

    @Operation(summary = "회원 가입",description = "이름, 아이디, 비밀번호, 이메일, 팀, 파트(FRONT, BACK)의 정보로 회원가입을 진행합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "회원가입 성공"),
            @ApiResponse(responseCode = "404",description = "가입하려는 팀이 존재하지 않을 때"),
            @ApiResponse(responseCode = "409",description = "아이디나 이메일 중복 시")
    })
    @PostMapping("/join")
    public ResponseEntity<BaseResponse<String>> join(@RequestBody @Valid JoinUserRequest request) {
        User user = userService.join(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse(HttpStatus.CREATED, "회원 가입 성공", user.getEmail()));
    }

    @Operation(summary = "파트장 등록",description = "이름, 팀, 파트(FRONT, BACK)의 정보로 회원가입을 진행합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "회원가입 성공"),
            @ApiResponse(responseCode = "404",description = "가입하려는 팀이 존재하지 않을 때"),
            @ApiResponse(responseCode = "409",description = "아이디나 이메일 중복 시")
    })
    @PostMapping("/join/leader")
    public ResponseEntity<BaseResponse<String>> joinLeader(@RequestBody @Valid JoinLeaderRequest request) {
        User user = userService.joinLeader(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse(HttpStatus.CREATED, "파트장 등록 성공", user.getEmail()));
    }

    @Operation(summary = "유저 정보 조회",description = "현재 로그인한 유저의 정보를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "조회 성공"),
            @ApiResponse(responseCode = "404",description = "유저의 PK로 저장된 데이터 조회가 불가능 시"),
    })
    @GetMapping("/users")
    public ResponseEntity<BaseResponse<ReadUserInfoResponse>> readUser(@CurrentMemberPK Long userId) {
        ReadUserInfoResponse userInfo = userService.getUserInfo(userId);

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "유저 정보 조회", userInfo));
    }

    @Operation(summary = "프론트엔드 파트장 후보 정보 조회",description = "모든 프론트엔드 파트장 후보 정보를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "조회 성공"),
    })
    @GetMapping("/users/leaders/front")
    public ResponseEntity<BaseResponse<List<ReadLeaderResponse>>> readFrontLeaders() {
        List<ReadLeaderResponse> leaders = userService.getPartLeaders(Part.FRONT);

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "프론트엔드 파트장 후보 반환", leaders));
    }

    @Operation(summary = "백엔드 파트장 후보 정보 조회",description = "모든 백엔드 파트장 후보 정보를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "조회 성공"),
    })
    @GetMapping("/users/leaders/back")
    public ResponseEntity<BaseResponse<List<ReadLeaderResponse>>> readBackLeaders() {
        List<ReadLeaderResponse> leaders = userService.getPartLeaders(Part.BACK);

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "백엔드 파트장 후보 반환", leaders));
    }

    @Operation(summary = "프론트엔드 파트장 투표 결과 반환",description = "프론트엔드 파트장의 정보를 득표순으로 내림차순 정렬하여 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "조회 성공"),
    })
    @GetMapping("/users/votes/leaders/front")
    public ResponseEntity<BaseResponse<List<ReadLeaderVoteResultResponse>>> readFrontLeadersVoteResult() {
        List<ReadLeaderVoteResultResponse> leaders = userService.getLeadersVoteResult(Part.FRONT);

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "프론트엔드 파트장 후보들 투표 결과 반환", leaders));
    }

    @Operation(summary = "백엔드 파트장 투표 결과 반환",description = "백엔드 파트장의 정보를 득표순으로 내림차순 정렬하여 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "조회 성공"),
    })
    @GetMapping("/users/votes/leaders/back")
    public ResponseEntity<BaseResponse<List<ReadLeaderVoteResultResponse>>> readBackLeadersVoteResult() {
        List<ReadLeaderVoteResultResponse> leaders = userService.getLeadersVoteResult(Part.BACK);

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "백엔드 파트장 후보들 투표 결과 반환", leaders));
    }

    @Operation(summary = "파트장 투표",description = "파트장에 투표를 진행합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "투표 성공"),
            @ApiResponse(responseCode = "404",description = "존재하지 않는 leader_id 기입 시"),
            @ApiResponse(responseCode = "409",description = "이미 투표한 경우 or 자신의 파트와 다른 파트에 투표한 경우"),
    })
    @PostMapping("/users/votes/leaders/{leader_id}")
    public ResponseEntity<BaseResponse> addLeader(@CurrentMemberPK Long voterId, @PathVariable(value="leader_id") Long leaderId) {
        userService.voteToPartLeader(voterId, leaderId);

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "파트장 투표 완료", null));
    }

    @Operation(summary = "데모데이 팀 투표",description = "데모데이 팀에 투표를 진행합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "투표 성공"),
            @ApiResponse(responseCode = "404",description = "존재하지 않는 team_id 기입 시"),
            @ApiResponse(responseCode = "409",description = "이미 투표한 경우 or 본인의 팀에 투표한 경우"),
    })
    @PostMapping("/users/votes/teams/{team_id}")
    public ResponseEntity<BaseResponse> voteTeam(@CurrentMemberLoginId String username, @PathVariable(value = "team_id") Long teamId) {
        teamService.voteToTeam(username,teamId);

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "데모데이 팀 투표", null));
    }

    @Operation(summary = "유저 제거",description = "유저를 제거합니다(임시로 만든 기능, 추후 삭제 필수)")
    @DeleteMapping("/users/{delete_user_id}")
    public ResponseEntity<BaseResponse> deleteUser(@PathVariable(value = "delete_user_id") Long deleteUserId) {
        userService.deleteUser(deleteUserId);
        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "유저 제거", null));
    }

    @Operation(summary = "유저 전체 제거",description = "유저를 전부 제거합니다(임시로 만든 기능, 추후 삭제 필수)")
    @DeleteMapping("/users/{delete_user_id}/all")
    public ResponseEntity<BaseResponse> deleteAllUser() {
        userService.deleteAllUsers();
        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "유저 제거", null));
    }
}
