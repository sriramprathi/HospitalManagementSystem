package com.nexturn.hms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nexturn.hms.entity.Role;
import com.nexturn.hms.entity.UserLogin;


public interface UserLoginRepository extends JpaRepository<UserLogin,Integer>{
    boolean existsByUserName(String userName);
    Optional<UserLogin> findByUserName(String userName);
    List<UserLogin> findAllByRole(Role role);

}
