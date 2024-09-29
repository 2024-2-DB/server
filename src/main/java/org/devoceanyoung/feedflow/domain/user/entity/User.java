package org.devoceanyoung.feedflow.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.devoceanyoung.feedflow.domain.comment.entity.Comment;
import org.devoceanyoung.feedflow.domain.feedback.entity.Feedback;
import org.devoceanyoung.feedflow.domain.team.entity.UserTeam;

import java.util.List;


@NoArgsConstructor
@Table(name = "user")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String userName;
    private String userProfile;

    @OneToMany(mappedBy = "user")
    private List<UserTeam> userTeams;

    @OneToMany(mappedBy = "user")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "user")
    private List<Comment> comments;
}