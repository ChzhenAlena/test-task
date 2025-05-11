package com.example.test_task.repository.specification;

import com.example.test_task.entity.EmailData;
import com.example.test_task.entity.PhoneData;
import com.example.test_task.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class UserSpecification {

  public static Specification<User> nameStartsWith(String name) {
    return (root, query, cb) -> cb.like(cb.lower(root.get("name")), name.toLowerCase() + "%");
  }

  public static Specification<User> phoneEquals(String phone) {
    return (root, query, cb) -> {
      if (query != null) query.distinct(true);
      Join<User, PhoneData> emails = root.join("phones", JoinType.LEFT);
      return cb.equal(emails.get("phone"), phone);
    };
  }

  public static Specification<User> emailEquals(String email) {
    return (root, query, cb) -> {
      if (query != null) query.distinct(true);
      Join<User, EmailData> emails = root.join("emails", JoinType.LEFT);
      return cb.equal(emails.get("email"), email);
    };
  }

  public static Specification<User> dateOfBirthAfter(LocalDate dob) {
    return (root, query, cb) -> cb.greaterThan(root.get("dateOfBirth"), dob);
  }
}
