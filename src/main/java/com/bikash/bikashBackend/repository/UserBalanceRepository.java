package com.bikash.bikashBackend.repository;

import com.bikash.bikashBackend.Model.UserBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBalanceRepository extends JpaRepository<UserBalance,Long> {
}
