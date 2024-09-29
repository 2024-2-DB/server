package org.devoceanyoung.feedflow.domain.feedback.repository;

import org.devoceanyoung.feedflow.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<User, Long> {
}
