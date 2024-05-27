package buldog.vote.repository;

import buldog.vote.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findById(Long id);
    Optional<Team> findByName(String name);
}
