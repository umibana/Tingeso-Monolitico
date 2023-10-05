package com.hans.tingeso.Services;

import com.hans.tingeso.Entities.InstallmentEntity;
import com.hans.tingeso.Repositories.InstallmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class InstallmentServiceTest {

    @Mock
    private InstallmentRepository installmentRepository;

    @InjectMocks
    private InstallmentService installmentService;

    @Test
    public void testFindById() {
        // Arrange
        InstallmentEntity installment = new InstallmentEntity();
        installment.setId(1);
        when(installmentRepository.findById(1)).thenReturn(Optional.of(installment));

        // Act
        InstallmentEntity result = installmentService.findById(1);

        // Assert
        assertEquals(installment, result);
        verify(installmentRepository, times(1)).findById(1);
    }

    @Test
    public void testSaveInstallment() {
        // Arrange
        InstallmentEntity installment = new InstallmentEntity();
        installment.setId(1);
        when(installmentRepository.save(installment)).thenReturn(installment);

        // Act
        InstallmentEntity result = installmentService.saveInstallment(installment);

        // Assert
        assertEquals(installment, result);
        verify(installmentRepository, times(1)).save(installment);
    }
}
