package com.bikash.bikashBackend.repository;

import com.bikash.bikashBackend.Model.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionDetailsRepository extends JpaRepository<TransactionDetails,Long> {
}
