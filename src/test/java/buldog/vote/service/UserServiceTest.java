package buldog.vote.service;

import buldog.vote.domain.Part;
import buldog.vote.domain.Role;
import buldog.vote.domain.Team;
import buldog.vote.domain.User;
import buldog.vote.dto.JoinUserRequest;
import buldog.vote.dto.ReadLeaderVoteResultResponse;
import buldog.vote.dto.ReadLeaderResponse;
import buldog.vote.exception.AppException;
import buldog.vote.repository.TeamRepository;
import buldog.vote.repository.UserRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamRepository teamRepository;

    private Long userId;

    @BeforeEach
    public void setUp() {
        Team team = new Team("buldog", "buldog");
        teamRepository.save(team);

        JoinUserRequest joinUserRequest = new JoinUserRequest("leader1", "leader1id", "pass", "leader1@naver.com", "buldog", Role.LEADER, Part.BACK);
        JoinUserRequest joinUserRequest1 = new JoinUserRequest("leader2", "leader2id", "pass", "leader2@naver.com", "buldog", Role.LEADER, Part.BACK);
        JoinUserRequest joinUserRequest2 = new JoinUserRequest("leader3", "leader3id", "pass", "leader3@naver.com", "buldog", Role.LEADER, Part.FRONT);
        JoinUserRequest joinUserRequest3 = new JoinUserRequest("user1", "user1id", "pass", "user1@naver.com", "buldog", Role.GENERAL, Part.BACK);

        User leader1 = userService.join(joinUserRequest);
        User leader2 = userService.join(joinUserRequest1);
        User leader3 = userService.join(joinUserRequest2);
        User user = userService.join(joinUserRequest3);
        userId = user.getId();

        userService.voteToPartLeader(userId, leader2.getId());
    }

    @Test
    @DisplayName(value = "front-end 리더 조회")
    void getFrontLeaders() {
        List<ReadLeaderResponse> allLeaders = userService.getPartLeaders(Part.FRONT);
        Assertions.assertEquals(allLeaders.size(), 1);
    }

    @Test
    @DisplayName(value = "back-end 리더 조회")
    void getBackLeaders() {
        List<ReadLeaderResponse> allLeaders = userService.getPartLeaders(Part.BACK);
        Assertions.assertEquals(allLeaders.size(), 2);
    }

    @Test
    @DisplayName(value = "투표수 내림차순으로 백엔드 파트장 조회")
    void getPartLeaders() {
        List<ReadLeaderVoteResultResponse> partLeaders = userService.getLeadersVoteResult(Part.BACK);
        for (ReadLeaderVoteResultResponse partLeader : partLeaders) {
            System.out.println("partLeader = " + partLeader);
        }

        Assertions.assertEquals(partLeaders.size(), 2);
        Assertions.assertEquals(partLeaders.getFirst().getName(), "leader2");
    }

    @Test
    @DisplayName(value = "중복 아이디로 가입시도")
    void joinWithDuplicatedLoginId() {
        JoinUserRequest joinUserRequest = new JoinUserRequest("user2", "user1id", "pass", "user2@naver.com", "buldog", Role.GENERAL, Part.FRONT);
        AppException appException = Assert.assertThrows(AppException.class, () -> userService.join(joinUserRequest));
        Assert.assertEquals(appException.getMessage(), "id already exists");
    }

    @Test
    @DisplayName(value = "중복 이메일로 가입시도")
    void joinWithDuplicatedEmail() {
        JoinUserRequest joinUserRequest = new JoinUserRequest("user2", "user2id", "pass", "user1@naver.com", "buldog", Role.GENERAL, Part.FRONT);
        AppException appException = Assert.assertThrows(AppException.class, () -> userService.join(joinUserRequest));
        Assert.assertEquals(appException.getMessage(), "same email already exists");
    }

    @Test
    @DisplayName(value = "중복 투표 시도")
    void voteToWrongPart() {
        User leader2 = userRepository.findByName("leader2").get();
        AppException appException = Assert.assertThrows(AppException.class, () -> userService.voteToPartLeader(userId, leader2.getId()));
        Assert.assertEquals(appException.getMessage(), "already voted");
    }
}