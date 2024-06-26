package buldog.vote.repository;

import buldog.vote.domain.Part;
import buldog.vote.domain.Role;
import buldog.vote.domain.Team;
import buldog.vote.domain.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
    Optional<User> findByName(String name);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByRoleAndPart(Role role, Part part);
    Optional<User> findByIdAndRole(Long id, Role role);

    @Query("select u from User u " +
            "where u.role = 'LEADER' and u.team.name = :team and u.name = :name")
    Optional<User> findLeaderByTeamAndName(@Param("teamId") String team, @Param("name") String name);

    void deleteById(Long id);
    void deleteAll();

    int countByPartLeader(User partLeader);
    int countByVoteTeam(Team voteTeam);
}
