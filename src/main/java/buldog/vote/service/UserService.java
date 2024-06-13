package buldog.vote.service;

import buldog.vote.domain.Role;
import buldog.vote.domain.Team;
import buldog.vote.domain.User;
import buldog.vote.dto.JoinUserRequest;
import buldog.vote.dto.ReadLeaderResponse;
import buldog.vote.dto.ReadUserResponse;
import buldog.vote.dto.ReadUserInfoResponse;
import buldog.vote.exception.AppException;
import buldog.vote.exception.ErrorCode;
import buldog.vote.repository.TeamRepository;
import buldog.vote.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    public List<ReadUserResponse> getAllLeaders() {
        List<ReadUserResponse> leaders = new ArrayList<>();
        userRepository.findByRole(Role.LEADER)
                .forEach(leader -> leaders.add(ReadUserResponse.from(leader)));

        return leaders;
    }

    /**
     * voter가 속한 파트의 파트장들을 반환
     *
     * @param voterId
     * @return 파트장의 이름, 파트, 득표수를 득표수에 대해 내림차순으로 반환
     */
    public List<ReadLeaderResponse> getPartLeaders(Long voterId) {
        User voter = userRepository.findById(voterId)
                .orElseThrow(() -> new AppException(ErrorCode.NO_DATA_EXISTED, "User does not exist"));

        List<User> partLeaders = userRepository.findByRoleAndPart(Role.LEADER, voter.getPart());

        Map<Long, Integer> votes = new HashMap<>(); // (pk, voteCount)
        for (User partLeader : partLeaders) {
            int voteCount = userRepository.countByPartLeader(partLeader);
            votes.put(partLeader.getId(), voteCount);
        }

        // 파트장 투표 내림차순 정렬
        List<Long> idSet = new ArrayList<>(votes.keySet());
        idSet.sort((o1, o2) -> votes.get(o2).compareTo(votes.get(o1)));

        List<ReadLeaderResponse> leaders = new ArrayList<>();
        for (Long id : idSet) {
            User partLeader = userRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.NO_DATA_EXISTED, "User does not exist"));

            leaders.add(ReadLeaderResponse.from(partLeader, votes.get(id)));
        }


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