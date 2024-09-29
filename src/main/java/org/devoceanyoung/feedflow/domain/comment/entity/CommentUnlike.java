package org.devoceanyoung.feedflow.domain.comment.entity;
import jakarta.persistence.*;
import lombok.*;


@NoArgsConstructor
@Table(name = "comment_unlike")
@Entity
public class CommentUnlike{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentUnlikeId;
    private Long userId;
    private Long commentId;


}