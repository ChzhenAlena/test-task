package com.example.test_task.repository;

import com.example.test_task.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
  @Query("""
      SELECT u FROM User u
      JOIN u.phones p
      JOIN u.emails e
      WHERE e.email = :email
      OR p.phone = :phone
      """)
  Optional<User> findByEmailOrPhone(@Param("email") String email, @Param("phone") String phone);

}
