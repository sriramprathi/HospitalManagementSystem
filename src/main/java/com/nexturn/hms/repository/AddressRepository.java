package com.nexturn.hms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nexturn.hms.entity.Address;

public interface AddressRepository extends JpaRepository<Address,Integer> {

}
