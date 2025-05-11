package com.example.test_task.repository;

import com.example.test_task.entity.PhoneData;
import com.example.test_task.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {

  boolean existsByPhone(String phone);

  @Query("SELECT p.user FROM PhoneData p WHERE p.id = :id")
  User getUserById(@Param("id") Long id);

  long countByUserId(Long id);
}
