package com.example.test_task.repository;

import com.example.test_task.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT a FROM Account a WHERE a.user.id = :userId")
  Optional<Account> findAccountByUserIdForUpdate(Long userId);

  Optional<Account> findByUserId(Long userId);

  @Query("SELECT a.id FROM Account a")
  Page<Long> findAllAccountIds(Pageable pageable);

  @Modifying
  @Query(nativeQuery = true, value = """
    WITH updated_accounts AS (
        UPDATE account
        SET balance = LEAST(
            (balance * (1 + :increaseRate))::numeric(19,2),
            (init_balance * (1 + :maxIncreaseRate))::numeric(19,2)
        )
        WHERE id IN (:accountIds)
        AND balance < (init_balance * (1 + :maxIncreaseRate))::numeric(19,2)
        AND (balance * (1 + :increaseRate))::numeric(19,2) > balance
        RETURNING id, user_id
    )
    SELECT * FROM updated_accounts
    """)
  List<Object[]> batchIncreaseBalanceWithLimit(
      @Param("accountIds") List<Long> accountIds,
      @Param("increaseRate") double increaseRate,
      @Param("maxIncreaseRate") double maxIncreaseRate
  );
}
