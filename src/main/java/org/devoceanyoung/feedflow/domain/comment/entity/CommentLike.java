package org.devoceanyoung.feedflow.domain.comment.entity;

import jakarta.persistence.*;
import lombok.*;


@NoArgsConstructor
@Table(name = "comment_like")
@Entity
public class CommentLike{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentlikeId;
    private Long userId;
    private Long commentId;


}