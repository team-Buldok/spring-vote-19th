package buldog.vote.repository;

import buldog.vote.domain.Part;
import buldog.vote.domain.Role;
import buldog.vote.domain.Team;
import buldog.vote.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
    Optional<User> findByName(String name);
    Optional<User> findByLoginId(String loginId);
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByRoleAndPart(Role role, Part part);
    Optional<User> findByIdAndRole(Long id, Role role);

    int countByCandidate(User candidate);
    int countByVoteTeam(Team voteTeam);
}
