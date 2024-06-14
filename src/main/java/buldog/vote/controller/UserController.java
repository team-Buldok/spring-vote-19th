package buldog.vote.controller;

import buldog.vote.domain.Part;
import buldog.vote.domain.User;
import buldog.vote.dto.*;
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
public class UserController {
    private final UserService userService;
    private final TeamService teamService;

    /**
     * 회원 가입
     *
     * @param request
     * @return
     */
    @PostMapping("/join")
    public ResponseEntity<BaseResponse<String>> join(@RequestBody JoinUserRequest request) {
        User user = userService.join(request);

        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK, "회원 가입 성공", user.getEmail()));
    }

    /**
     * PK로 유저 정보 조회
     *
     * @param user_id
     * @return pk, 이름, 아이디, 이메일
     */
    @GetMapping("/users/{user_id}")
    public ResponseEntity<BaseResponse<ReadUserInfoResponse>> readUser(@PathVariable Long user_id) {
        ReadUserInfoResponse userInfo = userService.getUserInfo(user_id);

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "유저 정보 조회", userInfo));
    }

    /**
     * 이메일로 유저 정보 조회
     *
     * @param email
     * @return pk, 이름, 아이디, 이메일
     */
    @GetMapping("/users")
    public ResponseEntity<BaseResponse<ReadUserInfoResponse>> readUser(@RequestParam String email) {
        ReadUserInfoResponse userInfo = userService.getUserInfo(email);

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "유저 정보 조회", userInfo));
    }

    /**
     * 모든 파트장 후보 보기
     *
     * @return 파트장의 이름, 파트
     */
    @GetMapping("/users/leaders/front")
    public ResponseEntity<BaseResponse<List<ReadLeaderResponse>>> readFrontLeaders() {
        List<ReadLeaderResponse> leaders = userService.getPartLeaders(Part.FRONT);

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "프론트엔드 파트장 후보 반환", leaders));
    }

    /**
     * 모든 파트장 후보 보기
     *
     * @return 파트장의 이름, 파트
     */
    @GetMapping("/users/leaders/back")
    public ResponseEntity<BaseResponse<List<ReadLeaderResponse>>> readBackLeaders() {
        List<ReadLeaderResponse> leaders = userService.getPartLeaders(Part.BACK);

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "백엔드 파트장 후보 반환", leaders));
    }

    /**
     * 프론트 파트장 투표 결과 확인
     *
     * @return 파트장의 이름, 파트
     */
    @GetMapping("/users/votes/leaders/front")
    public ResponseEntity<BaseResponse<List<ReadLeaderVoteResultResponse>>> readFrontLeadersVoteResult() {
        List<ReadLeaderVoteResultResponse> leaders = userService.getLeadersVoteResult(Part.FRONT);

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "프론트엔드 파트장 후보들 투표 결과 반환", leaders));
    }

    /**
     * 백엔드 파트장 투표 결과 확인
     *
     * @return 파트장의 이름, 파트
     */
    @GetMapping("/users/votes/leaders/back")
    public ResponseEntity<BaseResponse<List<ReadLeaderVoteResultResponse>>> readBackLeadersVoteResult() {
        List<ReadLeaderVoteResultResponse> leaders = userService.getLeadersVoteResult(Part.BACK);

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "프론트엔드 파트장 후보들 투표 결과 반환", leaders));
    }

    /**
     * 파트장 투표
     *
     * @param voterId
     * @param leaderId
     * @return
     */
    @PostMapping("/users/{voter_id}/votes/leaders/{leader_id}")
    public ResponseEntity<BaseResponse> addLeader(@PathVariable("voter_id") Long voterId, @PathVariable(value="leader_id") Long leaderId) {
        userService.voteToPartLeader(voterId, leaderId);

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "파트장 투표 완료", null));
    }

    /**
     * 데모데이 팀에 투표하기
     * @param voterId
     * @param teamId
     * @return
     */
    @PostMapping("/users/{voter_id}/votes/teams/{team_id}")
    public ResponseEntity<BaseResponse> voteTeam(@PathVariable("voter_id") Long voterId, @PathVariable(value = "team_id") Long teamId) {
        teamService.voteToTeam(voterId, teamId);

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "데모데이 팀 투표", null));
    }
}
