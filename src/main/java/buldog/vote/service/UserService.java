package buldog.vote.service;

import buldog.vote.domain.Part;
import buldog.vote.domain.Team;
import buldog.vote.domain.User;
import buldog.vote.dto.JoinUserRequest;
import buldog.vote.exception.AppException;
import buldog.vote.exception.ErrorCode;
import buldog.vote.repository.TeamRepository;
import buldog.vote.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public User join(JoinUserRequest request) {
        userRepository.findByLoginId(request.getLoginId()).ifPresent(user -> {
            throw new AppException(ErrorCode.ID_DUPLICATED);
        });
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTED);
        });
        Team team = teamRepository.findByName(request.getTeam()).orElseThrow(() -> new AppException(ErrorCode.NO_DATA_EXISTED, "Team does not exist"));


        User user = User.builder()
                .name(request.getName())
                .loginId(request.getLoginId()).password(request.getPassword()).email(request.getEmail())
                .part(request.getPart()).role(request.getRole()).team(team).build();

        userRepository.save(user);

        return user;
    }
}
