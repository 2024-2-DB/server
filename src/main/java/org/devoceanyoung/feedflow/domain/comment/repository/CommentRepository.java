package org.devoceanyoung.feedflow.domain.comment.repository;

import org.devoceanyoung.feedflow.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<User, Long> {
}
