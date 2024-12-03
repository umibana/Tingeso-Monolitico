package com.hans.tingeso.Services;

import com.hans.tingeso.Entities.UserEntity;
import com.hans.tingeso.Models.SchoolType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Year;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @Test
    void testGetUserDiscounts() {
        // Arrange
        PaymentService paymentService = new PaymentService();
        UserEntity user1 = new UserEntity();
        user1.setSchoolType(SchoolType.MUNICIPAL);
        user1.setGraduationYear(Year.now().getValue() );
        UserEntity user2 = new UserEntity();
        user2.setSchoolType(SchoolType.SUBVENCIONADO);
        user2.setGraduationYear(Year.now().getValue() - 2);
        UserEntity user3 = new UserEntity();
        user3.setSchoolType(SchoolType.PRIVADO);
        user3.setGraduationYear(Year.now().getValue() - 5);

        // Act
        int discount1 = paymentService.getUserDiscounts(user1);
        int discount2 = paymentService.getUserDiscounts(user2);
        int discount3 = paymentService.getUserDiscounts(user3);
        int installments1 = paymentService.getAvailableInstallments(user1);
        int installments2 = paymentService.getAvailableInstallments(user2);
        int installments3 = paymentService.getAvailableInstallments(user3);

        // Assert
        assertEquals(35, discount1);
        assertEquals(18, discount2);
        assertEquals(0, discount3);
        assertEquals(10, installments1);
        assertEquals(7, installments2);
        assertEquals(4, installments3);
    }

    @Test
    void testGetAvailableInstallments() {
        // Arrange
        PaymentService paymentService = new PaymentService();
        UserEntity user = new UserEntity();
        user.setSchoolType(SchoolType.MUNICIPAL);

        int installments = paymentService.getAvailableInstallments(user);
        // Assert
        assertEquals(10, installments);


        UserEntity user1 = new UserEntity();
        user1.setSchoolType(SchoolType.SUBVENCIONADO);

        int installments1 = paymentService.getAvailableInstallments(user1);
        assertEquals(7, installments1);

        UserEntity user2 = new UserEntity();
        user2.setSchoolType(SchoolType.PRIVADO);

        int installments2 = paymentService.getAvailableInstallments(user2);
        assertEquals(4, installments2);


    }
}
