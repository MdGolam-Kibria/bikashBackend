package com.bikash.bikashBackend.repository;


import com.bikash.bikashBackend.Model.User;
import com.bikash.bikashBackend.annotation.NativeQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {


    User findByPhoneAndIsActiveTrue(String phone);

    User findByIdAndIsActiveTrue(Long id);

    User findByEmailAndIsActiveTrue(String email);

    int countAllByIsActiveTrue();

    int countByUsernameAndIsActiveTrue(String name);

    List<User> findAllByIsActiveTrue();

    @Query(value = "SELECT r.id FROM User r WHERE r.phone= :phone", nativeQuery = true)
    Long findUserIdByPhone(@Param("phone") String phone);

    @Query(value = "SELECT r.phone FROM User r WHERE r.phone= :phone", nativeQuery = true)
    String findUserPhoneByPhone(@Param("phone") String phone);

}
