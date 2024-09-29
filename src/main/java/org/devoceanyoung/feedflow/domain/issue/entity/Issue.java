package org.devoceanyoung.feedflow.domain.issue.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.devoceanyoung.feedflow.domain.comment.entity.Comment;
import org.devoceanyoung.feedflow.domain.feedback.entity.Feedback;
import org.devoceanyoung.feedflow.domain.team.entity.Team;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Table(name = "issue")
@Entity
public class Issue{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long issueId;
    private String title;
    private String content;
    private String type;
    private LocalDateTime dueDate;

    @OneToMany(mappedBy = "issue")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "issue")
    private List<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "teamId")
    private Team team;
}