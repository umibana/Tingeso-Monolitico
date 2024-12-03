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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
    void getUsersTest() {
        UserEntity user1 = new UserEntity();
        UserEntity user2 = new UserEntity();
        List<UserEntity> expectedUsers = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(expectedUsers);
        List<UserEntity> actualUsers = userService.getUsers();

        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    void createUserTest() {
        UserEntity user = new UserEntity();
        when(paymentService.getUserDiscounts(user)).thenReturn(10);
        when(paymentService.getAvailableInstallments(user)).thenReturn(3);
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        userService.createUser(user);

        verify(paymentService, times(1)).getUserDiscounts(user);
        verify(paymentService, times(1)).getAvailableInstallments(user);
        verify(userRepository, times(1)).save(user);
        assertEquals(3, user.getInstallments().size());
    }

    @Test
    void findInstallmentsTest() {
        String rut = "testRut";
        InstallmentEntity installment1 = new InstallmentEntity();
        InstallmentEntity installment2 = new InstallmentEntity();
        List<InstallmentEntity> expectedInstallments = Arrays.asList(installment1, installment2);
        when(userRepository.findInstallmentsByRut(rut)).thenReturn(expectedInstallments);

        List<InstallmentEntity> actualInstallments = userService.findInstallments(rut);

        assertEquals(expectedInstallments, actualInstallments);
    }
    @Test
    void addGradesTest() throws IOException {
        UserEntity user = new UserEntity();
        user.setRut("20.960.400-0");
        when(userRepository.findByRut(anyString())).thenReturn(user);

        ScoreEntity scoreEntity = new ScoreEntity();
        when(scoreRepository.findByUserAndDate(any(UserEntity.class), any(LocalDate.class))).thenReturn(null);
        when(scoreRepository.save(any(ScoreEntity.class))).thenReturn(scoreEntity);

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

        // Act
        userService.addGrades(file);

        // Assert
        verify(userRepository, times(1)).findByRut("20.960.400-0");
        verify(scoreRepository, times(1)).findByUserAndDate(user, LocalDate.parse("12-12-2021", DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        verify(scoreRepository, times(1)).save(any(ScoreEntity.class));

    }

    @Test
    void getAverageScoreTest() {
        // Arrange
        UserEntity user = new UserEntity();
        ScoreEntity score1 = new ScoreEntity();
        score1.setScore(10);
        ScoreEntity score2 = new ScoreEntity();
        score2.setScore(20);
        List<ScoreEntity> scores = Arrays.asList(score1, score2);

        when(scoreRepository.findByUser(user)).thenReturn(scores);

        int averageScore = userService.getAverageScore(user);

        // Assert
        assertEquals(15, averageScore);
    }

    @Test
    void getDiscountScoreTest() {
        // Arrange
        UserEntity user = new UserEntity();
        ScoreEntity score1 = new ScoreEntity();
        score1.setScore(960);
        ScoreEntity score2 = new ScoreEntity();
        score2.setScore(930);
        ScoreEntity score3 = new ScoreEntity();
        score3.setScore(880);
        ScoreEntity score4 = new ScoreEntity();
        score4.setScore(840);
        List<ScoreEntity> scores1 = Arrays.asList(score1);
        List<ScoreEntity> scores2 = Arrays.asList(score2);
        List<ScoreEntity> scores3 = Arrays.asList(score3);
        List<ScoreEntity> scores4 = Arrays.asList(score4);

        when(scoreRepository.findByUser(user)).thenReturn(scores1).thenReturn(scores2).thenReturn(scores3).thenReturn(scores4);

        // Act
        int discountScore1 = userService.getDiscountScore(user);
        int discountScore2 = userService.getDiscountScore(user);
        int discountScore3 = userService.getDiscountScore(user);
        int discountScore4 = userService.getDiscountScore(user);

        // Assert
        assertEquals(10, discountScore1);
        assertEquals(5, discountScore2);
        assertEquals(2, discountScore3);
        assertEquals(0, discountScore4);

    }

    @Test
    void getExamsTakenTest() {
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
    void getAmountPaidTest() {
        // Arrange
        UserEntity user = new UserEntity();
        InstallmentEntity installment1 = new InstallmentEntity();
        installment1.setAmountPaid(100);
        InstallmentEntity installment2 = new InstallmentEntity();
        installment2.setAmountPaid(200);
        List<InstallmentEntity> installments = Arrays.asList(installment1, installment2);
        when(userRepository.findPaidInstallmentsByRut(user.getRut())).thenReturn(installments);

        // Act
        int amountPaid = userService.getAmountPaid(user);

        // Assert

        assertEquals(300, amountPaid);
    }

    @Test
    void getAmountPendingPaymentTest() {
        // Arrange
        UserEntity user = new UserEntity();
        InstallmentEntity installment1 = new InstallmentEntity();
        installment1.setAmount(100);
        InstallmentEntity installment2 = new InstallmentEntity();
        installment2.setAmount(200);
        List<InstallmentEntity> installments = Arrays.asList(installment1, installment2);
        when(userRepository.findUnpaidInstallmentsByRut(user.getRut())).thenReturn(installments);

        // Act
        int amountPendingPayment = userService.getAmountPendingPayment(user);

        // Assert
        assertEquals(300, amountPendingPayment);
    }
    @Test
    void getUserSummaryTest() {
        UserEntity user = new UserEntity();
        user.setName("John");
        user.setSurname("Doe");
        user.setRut("12345678-9");
        user.setEnrollStatus(true);
        user.setUsingCredit(false);
        user.setDiscount(10);
        ArrayList<InstallmentEntity> installments = new ArrayList<>(1);
        installments.add(new InstallmentEntity());
        user.setInstallments(installments);

        String summary= userService.getUserSummary(user);

        assertTrue(summary.contains("RUT: "));
        assertTrue(summary.contains("Nombre: "));
        assertTrue(summary.contains("Examenes rendidos: "));
        assertTrue(summary.contains("Promedio: "));
        assertTrue(summary.contains("Monto total arancel a pagar"));
        assertTrue(summary.contains("Tipo de pago: "));
        assertTrue(summary.contains("Cuotas pactadas: "));
        assertTrue(summary.contains("Cuotas pagadas: "));
        assertTrue(summary.contains("Monto total pagado: "));
        assertTrue(summary.contains("Fecha ultimo pago: "));
        assertTrue(summary.contains("Saldo por pagar: "));
        assertTrue(summary.contains("Nro. Cuotas con retraso"));

        user.setUsingCredit(true);
        String userSummary1 = userService.getUserSummary(user);
        assertTrue(userSummary1.contains("Tipo de pago: Crédito"));
    }


}