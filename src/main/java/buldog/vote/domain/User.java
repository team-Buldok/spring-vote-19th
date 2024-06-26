package buldog.vote.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Part part;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team")
    private Team team;

    // vote
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_leader")
    private User partLeader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_team")
    private Team voteTeam;

    public void updatePartLeader(User partLeader) {
        this.partLeader = partLeader;
    }

    public void updateVoteTeam(Team voteTeam) {
        this.voteTeam = voteTeam;
    }

    @Builder
    public User(String name, String username, String password, String email, Role role, Part part, Team team) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.part = part;
        this.team = team;
    }

    public static User createTempUser(String username, String password, String role) {
        return User.builder()
                .username(username).password(password).role(Role.valueOf(role))
                .build();
    }
}
