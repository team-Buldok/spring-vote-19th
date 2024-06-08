package buldog.vote.service;

import buldog.vote.domain.Role;
import buldog.vote.domain.Team;
import buldog.vote.domain.User;
import buldog.vote.dto.JoinUserRequest;
import buldog.vote.dto.ReadLeaderResponse;
import buldog.vote.dto.ReadUserInfoResponse;
import buldog.vote.exception.AppException;
import buldog.vote.exception.ErrorCode;
import buldog.vote.repository.TeamRepository;
import buldog.vote.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    public ReadUserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NO_DATA_EXISTED, "User does not exist"));

        return ReadUserInfoResponse.from(user);
    }

    /**
     * 회원가입
     *
     * @param request
     * @return 유저 엔티티
     */
    @Transactional
    public User join(JoinUserRequest request) {
        userRepository.findByLoginId(request.getLoginId()).ifPresent(user -> {
            throw new AppException(ErrorCode.ID_DUPLICATED);
        });
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTED);
        });
        Team team = teamRepository.findByName(request.getTeam()).orElseThrow(() -> new AppException(ErrorCode.NO_DATA_EXISTED, "Team does not exists"));


        User user = User.builder()
                .name(request.getName())
                .loginId(request.getLoginId()).password(request.getPassword()).email(request.getEmail())
                .part(request.getPart()).role(request.getRole()).team(team).build();

        userRepository.save(user);

        return user;
    }

    /**
     * 부서 상관없이 모든 파트장을 반환
     *
     * @return 파트장의 이름, 파트
     */
    public List<ReadLeaderResponse> getLeaders() {
        List<ReadLeaderResponse> leaders = new ArrayList<>();
        userRepository.findByRole(Role.LEADER)
                .forEach(leader -> leaders.add(ReadLeaderResponse.from(leader)));

        return leaders;
    }

    /**
     * voter가 속한 파트의 파트장들을 반환
     *
     * @param voterId
     * @return 파트장의 이름, 파트
     */
    public List<ReadLeaderResponse> getPartLeaders(Long voterId) {
        List<ReadLeaderResponse> leaders = new ArrayList<>();

        User voter = userRepository.findById(voterId)
                .orElseThrow(() -> new AppException(ErrorCode.NO_DATA_EXISTED, "User does not exist"));

        userRepository.findByRoleAndPart(Role.LEADER, voter.getPart())
                .forEach(leader -> leaders.add(ReadLeaderResponse.from(leader)));

        return leaders;
    }

    /**
     * 파트장 투표
     *
     * @param leaderId
     * @param voterId
     */
    @Transactional
    public void voteToPartLeader(Long voterId, Long leaderId) {
        User voter = userRepository.findById(voterId)
                .orElseThrow(() -> new AppException(ErrorCode.NO_DATA_EXISTED, "User does not exists"));

        User partLeader = userRepository.findByIdAndRole(leaderId, Role.LEADER)
                .orElseThrow(() -> new AppException(ErrorCode.NO_DATA_EXISTED, "Invalid userId"));

        // 이미 투표한 경우
        if (voter.getPartLeader() != null) {
            throw new AppException(ErrorCode.ALREADY_VOTED);
        }

        // 투표하려는 파트장의 파트가 투표자의 파트와 다른 경우
        if (!partLeader.getPart().equals(voter.getPart())) {
            throw new AppException(ErrorCode.VOTE_TO_ANOTHER_PART);
        }

        voter.updatePartLeader(partLeader);
    }
}
