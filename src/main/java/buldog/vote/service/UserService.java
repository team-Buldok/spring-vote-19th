package buldog.vote.service;

import buldog.vote.domain.Part;
import buldog.vote.domain.Role;
import buldog.vote.domain.Team;
import buldog.vote.domain.User;
import buldog.vote.dto.*;
import buldog.vote.exception.AppException;
import buldog.vote.exception.ErrorCode;
import buldog.vote.repository.TeamRepository;
import buldog.vote.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * PK로 유저 정보 조회
     *
     * @param userId
     * @return
     */
    public ReadUserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NO_DATA_EXISTED, "User does not exist"));

        return ReadUserInfoResponse.from(user);
    }

    /**
     * 이메일로 유저 정보 조회
     *
     * @param email
     * @return
     */
    public ReadUserInfoResponse getUserInfo(String email) {
        User user = userRepository.findByEmail(email)
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
        userRepository.findByUsername(request.getUsername()).ifPresent(user -> {
            throw new AppException(ErrorCode.ID_DUPLICATED);
        });
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTED);
        });
        Team team = teamRepository.findByName(request.getTeam()).orElseThrow(() -> new AppException(ErrorCode.NO_DATA_EXISTED, "Team does not exists"));


        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername()).password(passwordEncoder.encode(request.getPassword())).email(request.getEmail())
                .part(request.getPart()).role(Role.GENERAL).team(team).build();

        userRepository.save(user);

        return user;
    }

    /**
     * 파트장 등록
     *
     * @param request
     * @return 유저 엔티티
     */
    @Transactional
    public User joinLeader(JoinLeaderRequest request) {
        userRepository.findLeaderByTeamAndName(request.getTeam(), request.getName()).ifPresent(e->{
            throw new AppException(ErrorCode.DATA_ALREADY_EXISTED,"leader already exists");
        });
        Team team = teamRepository.findByName(request.getTeam()).orElseThrow(() -> new AppException(ErrorCode.NO_DATA_EXISTED, "Team does not exists"));

        String randomString1 = ((Integer) ThreadLocalRandom.current().nextInt()).toString();
        String randomString2 = ((Integer) ThreadLocalRandom.current().nextInt()).toString();

        String randUserName = passwordEncoder.encode(randomString1);
        String randPassword = passwordEncoder.encode(randomString2);
        String randEmail = randUserName + "@vote.com";

        User user = User.builder()
                .name(request.getName())
                .username(randUserName).password(passwordEncoder.encode(randPassword)).email(randEmail)
                .part(request.getPart()).role(Role.LEADER).team(team).build();

        userRepository.save(user);

        return user;
    }

    /**
     * 프론트 파트장 후보를 반환
     *
     * @return 파트장의 이름, 파트
     */
    public List<ReadLeaderResponse> getPartLeaders(Part part) {
        List<ReadLeaderResponse> leaders = new ArrayList<>();
        userRepository.findByRoleAndPart(Role.LEADER, part)
                .forEach(leader -> leaders.add(ReadLeaderResponse.from(leader)));

        return leaders;
    }

    /**
     * 파트장 투표 결과 반환
     *
     * @return 파트장의 이름, 파트, 득표수를 득표수에 대해 내림차순으로 반환
     */
    public List<ReadLeaderVoteResultResponse> getLeadersVoteResult(Part part) {
        List<User> partLeaders = userRepository.findByRoleAndPart(Role.LEADER, part);

        Map<Long, Integer> votes = new HashMap<>(); // (pk, voteCount)
        for (User partLeader : partLeaders) {
            int voteCount = userRepository.countByPartLeader(partLeader);
            votes.put(partLeader.getId(), voteCount);
        }

        // 파트장 투표 내림차순 정렬
        List<Long> idSet = new ArrayList<>(votes.keySet());
        idSet.sort((o1, o2) -> votes.get(o2).compareTo(votes.get(o1)));

        List<ReadLeaderVoteResultResponse> leaders = new ArrayList<>();
        for (Long id : idSet) {
            User partLeader = userRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.NO_DATA_EXISTED, "User does not exist"));

            leaders.add(ReadLeaderVoteResultResponse.of(partLeader, votes.get(id)));
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

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }


}
