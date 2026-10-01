package com.nexturn.hms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nexturn.hms.entity.UserLogin;


public interface UserLoginRepository extends JpaRepository<UserLogin,Integer>{

}
