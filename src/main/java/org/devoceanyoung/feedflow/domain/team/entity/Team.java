package org.devoceanyoung.feedflow.domain.team.entity;

import jakarta.persistence.*;
import lombok.*;
import org.devoceanyoung.feedflow.domain.feedback.entity.Feedback;
import org.devoceanyoung.feedflow.domain.issue.entity.Issue;

import java.util.List;


@NoArgsConstructor
@Table(name = "team")
@Entity
public class Team{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;
    private String teamName;
    private String teamProfile;

    @OneToMany(mappedBy = "team")
    private List<UserTeam> userTeams;

    @OneToMany(mappedBy = "team")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "team")
    private List<Issue> issues;
}