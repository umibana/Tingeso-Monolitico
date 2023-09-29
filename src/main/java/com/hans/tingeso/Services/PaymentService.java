package com.hans.tingeso.Services;

import com.hans.tingeso.Models.SchoolType;
import com.hans.tingeso.Entities.UserEntity;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
public class PaymentService {
    int enrollment = 70000;
    int tuition = 1500000;
    // Returns discount according to schoolType and years
    // since graduation
    public Integer getUserDiscounts(UserEntity user){
        int currentDiscount = 0;
        int yearsSinceGraduation = Year.now().getValue() - user.getGraduationYear();
        SchoolType schoolType = user.getSchoolType();
        // If it isn't any of these then it's a private school
        // and no discount applies
        if (schoolType == SchoolType.MUNICIPAL){
            currentDiscount +=  20;
        }
        else if (schoolType == SchoolType.SUBVENCIONADO){
            currentDiscount += 10;
        }
        if (yearsSinceGraduation < 1){
            currentDiscount += 15;
        }
        else if (yearsSinceGraduation <= 2){
            currentDiscount += 8;
        }
        else if (yearsSinceGraduation <= 4){
            currentDiscount += 4;
        }
        return currentDiscount;
    }
    public Integer getAvailableInstallments(UserEntity user){
        int installments = 0;
        SchoolType schoolType = user.getSchoolType();
        if (schoolType == SchoolType.MUNICIPAL){
            installments =  10;
        }
        else if (schoolType == SchoolType.SUBVENCIONADO){
            installments = 7;
        }

        else if (schoolType == SchoolType.PRIVADO){
            installments =4;
        }
        return installments;
    }

}
