package org.devoceanyoung.feedflow.domain.comment.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.devoceanyoung.feedflow.domain.issue.entity.Issue;
import org.devoceanyoung.feedflow.domain.user.entity.User;

@NoArgsConstructor
@Table(name = "comment")
@Entity
public class Comment{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    private String content;
    private int likeCount;
    private int unlikeCount;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "issueId")
    private Issue issue;

}