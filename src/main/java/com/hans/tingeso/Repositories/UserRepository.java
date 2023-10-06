package com.hans.tingeso.Repositories;

import com.hans.tingeso.Entities.InstallmentEntity;
import com.hans.tingeso.Entities.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    UserEntity findByRut(String rut);

    @Query("SELECT u.installments FROM UserEntity u WHERE u.rut = :userRut")
    List<InstallmentEntity> findInstallmentsByRut(@Param("userRut") String rut);
    @Query("SELECT i FROM InstallmentEntity i WHERE i.isPaid = true AND i.user.rut = :userRut")
    List<InstallmentEntity> findPaidInstallmentsByRut(@Param("userRut") String rut);
    @Query("SELECT i FROM InstallmentEntity i WHERE i.isPaid = false AND i.user.rut = :userRut")
    List<InstallmentEntity> findUnpaidInstallmentsByRut(@Param("userRut") String rut);
    @Query("SELECT MAX(i.paidDate) FROM InstallmentEntity i WHERE i.isPaid = true AND i.user.rut = :userRut")
    LocalDate findLastPaidDateByRut(@Param("userRut") String rut);

}