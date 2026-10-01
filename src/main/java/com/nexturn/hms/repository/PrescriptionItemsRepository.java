package com.nexturn.hms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nexturn.hms.entity.PrescriptionItems;

public interface PrescriptionItemsRepository extends JpaRepository<PrescriptionItems,Integer> {

}
