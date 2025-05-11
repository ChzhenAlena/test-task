package com.example.test_task.repository;

import com.example.test_task.entity.EmailData;
import com.example.test_task.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailDataRepository extends JpaRepository<EmailData, Long> {

  boolean existsByEmail(String email);

  @Query("SELECT e.user FROM EmailData e WHERE e.id = :id")
  User getUserById(@Param("id") Long id);

  long countByUserId(Long id);
}
