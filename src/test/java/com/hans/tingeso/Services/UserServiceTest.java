package com.hans.tingeso.Services;

import com.hans.tingeso.Entities.InstallmentEntity;
import com.hans.tingeso.Entities.ScoreEntity;
import com.hans.tingeso.Entities.UserEntity;
import com.hans.tingeso.Repositories.ScoreRepository;
import com.hans.tingeso.Repositories.UserRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PaymentService paymentService;
    @Mock
    private ScoreRepository scoreRepository;

    @Test
    public void getUsersTest() {
        UserEntity user1 = new UserEntity();
        UserEntity user2 = new UserEntity();
        List<UserEntity> expectedUsers = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(expectedUsers);


        List<UserEntity> actualUsers = userService.getUsers();

        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    public void createUserTest() {
        UserEntity user = new UserEntity();
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        userService.createUser(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void findInstallmentsTest() {
        String rut = "testRut";
        InstallmentEntity installment1 = new InstallmentEntity();
        InstallmentEntity installment2 = new InstallmentEntity();
        List<InstallmentEntity> expectedInstallments = Arrays.asList(installment1, installment2);
        when(userRepository.findInstallmentsByRut(rut)).thenReturn(expectedInstallments);

        List<InstallmentEntity> actualInstallments = userService.findInstallments(rut);

        assertEquals(expectedInstallments, actualInstallments);
    }
    @Test
    public void addGradesTest() throws IOException {

        // We create a test file ".xlsx"
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("20.960.400-0");
        row.createCell(1).setCellValue("12-12-2021");
        row.createCell(2).setCellValue(750);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        byte[] data = outputStream.toByteArray();
        MultipartFile file = new MockMultipartFile("data.xlsx", data);
        userService.addGrades(file);
        String rut = "20.960.400-0";
        LocalDate date = LocalDate.parse("12-12-2021", DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        UserEntity user = userRepository.findByRut(rut);
        if (user != null) {
            ScoreEntity scoreEntity = scoreRepository.findByUserAndDate(user, date);
            assertNotNull(scoreEntity);
            assertEquals(750, scoreEntity.getScore());
        }

    }

    @Test
    public void getAverageScoreTest() {
        // Arrange
        UserEntity user = new UserEntity();
        ScoreEntity score1 = new ScoreEntity();
        score1.setScore(900);
        ScoreEntity score2 = new ScoreEntity();
        score2.setScore(850);
        List<ScoreEntity> scores = Arrays.asList(score1, score2);
        when(scoreRepository.findByUser(user)).thenReturn(scores);

        // Act
        int averageScore = userService.getAverageScore(user);

        // Assert
        assertEquals(875, averageScore);
    }

    @Test
    public void getDiscountScoreTest() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setDiscount(0);

        // Act
        int discountScore = userService.getDiscountScore(user);

        // Assert
        assertEquals(0, discountScore);
    }

    @Test
    public void getExamsTakenTest() {
        // Arrange
        UserEntity user = new UserEntity();
        ScoreEntity score1 = new ScoreEntity();
        ScoreEntity score2 = new ScoreEntity();
        List<ScoreEntity> scores = Arrays.asList(score1, score2);
        when(scoreRepository.findByUser(user)).thenReturn(scores);

        // Act
        int examsTaken = userService.getExamsTaken(user);

        // Assert
        assertEquals(2, examsTaken);
    }

    @Test
    public void getAmountPaidTest() {
        // Arrange
        UserEntity user = new UserEntity();
        InstallmentEntity installment1 = new InstallmentEntity();
        installment1.setAmount(100);
        InstallmentEntity installment2 = new InstallmentEntity();
        installment2.setAmount(200);
        List<InstallmentEntity> installments = Arrays.asList(installment1, installment2);
        when(userRepository.findUnpaidInstallmentsByRut(user.getRut())).thenReturn(installments);

        // Act
        int amountPaid = userService.getAmountPaid(user);

        // Assert
        assertEquals(300, amountPaid);
    }

    @Test
    public void getAmountPendingPaymentTest() {
        // Arrange
        UserEntity user = new UserEntity();
        InstallmentEntity installment1 = new InstallmentEntity();
        installment1.setAmountPaid(100);
        InstallmentEntity installment2 = new InstallmentEntity();
        installment2.setAmountPaid(200);
        List<InstallmentEntity> installments = Arrays.asList(installment1, installment2);
        when(userRepository.findPaidInstallmentsByRut(user.getRut())).thenReturn(installments);

        // Act
        int amountPendingPayment = userService.getAmountPendingPayment(user);

        // Assert
        assertEquals(300, amountPendingPayment);
    }
    @Test
    public void getUserSummaryTest() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setName("John");
        user.setSurname("Doe");
        user.setRut("12345678-9");
        user.setEnrollStatus(true);
        user.setUsingCredit(false);
        user.setDiscount(10);

        // Act
        String userSummary = userService.getUserSummary(user);

        // Assert
        assertTrue(userSummary.contains("Nombre: John Doe"));
        assertTrue(userSummary.contains("RUT: 12345678-9"));
        assertTrue(userSummary.contains("Tipo de pago Contado"));
        assertTrue(userSummary.contains("Examenes rendidos 0"));
        assertTrue(userSummary.contains("Promedio: 0"));
        assertTrue(userSummary.contains("Cuotas con retraso 0"));
        assertTrue(userSummary.contains("Monto total pagado 0"));
        assertTrue(userSummary.contains("Saldo por pagar 0"));
    }


}