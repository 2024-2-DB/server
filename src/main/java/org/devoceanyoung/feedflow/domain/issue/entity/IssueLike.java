package org.devoceanyoung.feedflow.domain.issue.entity;

import jakarta.persistence.*;
import lombok.*;


@NoArgsConstructor
@Table(name = "issue_like")
@Entity
public class IssueLike{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long issueLikeId;
    private Long userId;
    private Long issueId;


}
