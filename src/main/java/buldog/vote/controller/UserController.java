package buldog.vote.controller;

import buldog.vote.domain.User;
import buldog.vote.dto.BaseResponse;
import buldog.vote.dto.JoinUserRequest;
import buldog.vote.dto.ReadLeaderResponse;
import buldog.vote.dto.ReadUserInfoResponse;
import buldog.vote.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    /**
     * 회원 가입
     * @param request
     * @return
     */
    @PostMapping("/join")
    public ResponseEntity<BaseResponse<User>> join(@RequestBody JoinUserRequest request) {
        User user = userService.join(request);

        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK, "회원 가입 성공", user.getEmail(), 1));
    }

    /**
     * 유저 정보 조회
     * @param user_id
     * @return pk, 이름, 아이디, 이메일
     */
    @GetMapping("/users/{user_id}")
    public ResponseEntity<BaseResponse<ReadUserInfoResponse>> readUser(@PathVariable Long user_id) {
        ReadUserInfoResponse userInfo = userService.getUserInfo(user_id);

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "유저 정보 조회", userInfo, 1));
    }

    /**
     * 모든 파트장 후보 보기
     * @return 파트장의 이름, 파트
     */
    @GetMapping("/users/leaders")
    public ResponseEntity<BaseResponse<List<ReadLeaderResponse>>> readLeaders() {
        List<ReadLeaderResponse> leaders = userService.getLeaders();

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK,"파트장 후보 반환",leaders, leaders.size()));
    }

    /**
     * 내가 속한 파트의 파트장 후보 보기
     * @return 파트장의 이름, 파트
     */
    @GetMapping("/users/{voter_id}/leaders")
    public ResponseEntity<BaseResponse<List<ReadLeaderResponse>>> readLeaders(@PathVariable("voter_id")Long voterId) {
        List<ReadLeaderResponse> leaders = userService.getPartLeaders(voterId);

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK,"내가 속한 파트의 파트장 후보 반환",leaders, leaders.size()));
    }

    /**
     * 파트장 투표
     * @param voterId
     * @param leaderId
     * @return
     */
    @PostMapping("/users/{voter_id}/vote/leader")
    public ResponseEntity<BaseResponse> addLeader(@PathVariable("voter_id") Long voterId, @RequestParam Long leaderId) {
        userService.voteToPartLeader(voterId, leaderId);

        return ResponseEntity.ok(new BaseResponse<>(HttpStatus.OK, "파트장 투표 완료", null, 0));
    }

}
