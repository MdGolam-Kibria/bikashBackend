package com.bikash.bikashBackend.repository;

import com.bikash.bikashBackend.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
    int countByNameAndIsActiveTrue(String name);
    Role findByNameAndIsActiveTrue(String name);
}
