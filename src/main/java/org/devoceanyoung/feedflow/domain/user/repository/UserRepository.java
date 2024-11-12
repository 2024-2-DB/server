package org.devoceanyoung.feedflow.domain.user.repository;

import org.devoceanyoung.feedflow.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
