package com.nexturn.hms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nexturn.hms.entity.Bill;

public interface BillRepository extends JpaRepository<Bill,Integer> {

}
