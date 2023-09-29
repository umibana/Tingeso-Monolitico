package com.hans.tingeso.Repositories;
import com.hans.tingeso.Entities.ScoreEntity;
import com.hans.tingeso.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ScoreRepository extends JpaRepository<ScoreEntity, Integer> {
    ScoreEntity findByUserAndDate(UserEntity user, LocalDate date);

}