package org.devoceanyoung.feedflow.domain.message.repository;


import org.devoceanyoung.feedflow.domain.message.entity.Message;
import org.devoceanyoung.feedflow.domain.team.entity.Team;
import org.devoceanyoung.feedflow.domain.team.entity.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByUserTeamOrderByCreatedAtAsc(UserTeam userTeam);


    List<Message> findAllByUserTeamOrderByCreatedAtDesc(UserTeam userTeam);
}