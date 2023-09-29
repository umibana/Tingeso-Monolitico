package com.hans.tingeso.Repositories;

import com.hans.tingeso.Entities.InstallmentEntity;
import com.hans.tingeso.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstallmentRepository extends JpaRepository<InstallmentEntity, Integer> {


}