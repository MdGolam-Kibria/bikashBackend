package com.bikash.bikashBackend.repository;

import com.bikash.bikashBackend.Model.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface TransactionsRepository extends JpaRepository<Transactions, Long> {
    Transactions findByIdAndIsActiveTrue(Long id);

    @Query(value = "SELECT r.transactionDate FROM Transactions r WHERE r.userId= :id", nativeQuery = true)
    Date findTransactionsDateByUserId(@Param("id") Long userId);
}
