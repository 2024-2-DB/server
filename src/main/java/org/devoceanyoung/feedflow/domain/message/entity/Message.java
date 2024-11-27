package org.devoceanyoung.feedflow.domain.message.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import org.devoceanyoung.feedflow.domain.team.entity.Team;
import org.devoceanyoung.feedflow.domain.team.entity.UserTeam;
import org.devoceanyoung.feedflow.domain.user.entity.User;

import java.time.LocalDateTime;
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"id", "userTeam", "createdAt"})
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(nullable = false)
    private String role;

    @ManyToOne
    @JoinColumn(name = "user_team_id",nullable = false)
    private UserTeam userTeam;;


    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Message(String role, String content, LocalDateTime createdAt) {
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
    }

}
