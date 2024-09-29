package org.devoceanyoung.feedflow.domain.issue.repository;

import org.devoceanyoung.feedflow.domain.issue.entity.Issue;
import org.devoceanyoung.feedflow.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, Long> {
}
