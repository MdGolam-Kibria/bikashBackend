package com.bikash.bikashBackend.repository;

import com.bikash.bikashBackend.Model.Commission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommissionRepository extends JpaRepository<Commission,Long> {
}
