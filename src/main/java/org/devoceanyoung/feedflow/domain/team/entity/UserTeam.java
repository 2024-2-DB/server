package org.devoceanyoung.feedflow.domain.team.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.devoceanyoung.feedflow.domain.message.entity.Message;
import org.devoceanyoung.feedflow.domain.user.entity.User;

import java.util.List;

@NoArgsConstructor
@Table(name = "user_team")
@Entity
public class UserTeam{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userTeamId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "teamId")
    private Team team;

    @OneToMany(mappedBy = "userTeam")
    private List<Message> messages;

}
