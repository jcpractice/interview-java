package com.signicat.interview.repository;

import com.signicat.interview.domain.UserGroupDTO;
import com.signicat.interview.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
}
