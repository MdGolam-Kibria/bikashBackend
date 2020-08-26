package com.bikash.bikashBackend.repository;

import com.bikash.bikashBackend.Model.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionsRepository extends JpaRepository<Transactions,Long> {
    Transactions findByIdAndIsActiveTrue(Long id);
}
