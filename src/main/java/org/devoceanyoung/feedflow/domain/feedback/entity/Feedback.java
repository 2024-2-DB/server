package org.devoceanyoung.feedflow.domain.feedback.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.devoceanyoung.feedflow.domain.issue.entity.Issue;
import org.devoceanyoung.feedflow.domain.team.entity.Team;
import org.devoceanyoung.feedflow.domain.user.entity.User;

@NoArgsConstructor
@Table(name = "feedback")
@Entity
public class Feedback{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;
    private String title;
    private String content;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "issueId")
    private Issue issue;

    @ManyToOne
    @JoinColumn(name = "teamId")
    private Team team;

}