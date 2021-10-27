package com.signicat.interview.repository;

import com.signicat.interview.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<Subject, Long> {
     Optional<Subject> findByUserName(String userName);
     Optional<Subject> findByUserNameOrEmail(String userName, String email);
}
