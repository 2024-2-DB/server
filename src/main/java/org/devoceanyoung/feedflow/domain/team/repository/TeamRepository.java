package org.devoceanyoung.feedflow.domain.team.repository;

import org.devoceanyoung.feedflow.domain.team.entity.Team;
import org.devoceanyoung.feedflow.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
