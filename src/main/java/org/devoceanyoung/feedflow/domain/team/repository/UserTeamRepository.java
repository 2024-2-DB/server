package org.devoceanyoung.feedflow.domain.team.repository;

import org.devoceanyoung.feedflow.domain.team.entity.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTeamRepository extends JpaRepository<UserTeam, Long> {
    Optional<UserTeam> findUserTeamByUser_UserIdAndTeam_TeamId(Long userId, Long teamId);

}
