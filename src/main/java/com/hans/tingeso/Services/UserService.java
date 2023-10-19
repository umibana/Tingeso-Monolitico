package com.hans.tingeso.Services;

import com.hans.tingeso.Entities.InstallmentEntity;
import com.hans.tingeso.Entities.ScoreEntity;
import com.hans.tingeso.Entities.UserEntity;
import com.hans.tingeso.Repositories.ScoreRepository;
import com.hans.tingeso.Repositories.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PaymentService paymentService;
    @Autowired
    ScoreRepository scoreRepository;

    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }

    public void createUser(@ModelAttribute UserEntity user) {
        user.setDiscount(paymentService.getUserDiscounts(user));
        int availableInstallments = paymentService.getAvailableInstallments(user);
        // We start the installment on the 5th from the next month
        LocalDate nextMonth = LocalDate.now().plus(1, ChronoUnit.MONTHS).withDayOfMonth(5);
        // We populate the array according to how many installments the user is allowed
        ArrayList<InstallmentEntity> installments = new ArrayList<>(availableInstallments);
        // We add the installments to the user iterating over the months
        for (int i = 0; i < availableInstallments; i++) {
            LocalDate installmentDate = nextMonth.plus(i, ChronoUnit.MONTHS);
            InstallmentEntity installment = new InstallmentEntity();
            installment.setDate(installmentDate);
            installment.setAmount(paymentService.tuition / availableInstallments);
            installments.add(installment);
            installment.setUser(user);
        }
        user.setInstallments(installments);
        userRepository.save(user);
    }

    public List<InstallmentEntity> findInstallments(String rut) {
        return userRepository.findInstallmentsByRut(rut);
    }

    public void addGrades(MultipartFile file) {
        try {
            // We read the file
            InputStream inputStream = file.getInputStream();
            // We create a new XLSX object
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            // Formatter to read as string
            DataFormatter formatter = new DataFormatter();
            // We read the file row by row
            // First cell should be RUT, then date, then score
            for (Row row : sheet) {
                String rut = formatter.formatCellValue(row.getCell(0));
                String date = formatter.formatCellValue(row.getCell(1));
                int score = Integer.parseInt(formatter.formatCellValue(row.getCell(2)));
                // We convert the date string to a LocalDate
                LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                UserEntity user = userRepository.findByRut(rut);
                if (user != null) {
                    // Check if the date is already added
                    ScoreEntity scoreEntity = scoreRepository.findByUserAndDate(user, localDate);
                    if (scoreEntity == null) {
                        // If the date is not added, create a new ScoreEntity
                        ScoreEntity newScore = new ScoreEntity();
                        newScore.setUser(user);
                        newScore.setDate(localDate);
                        newScore.setScore(score);
                        scoreRepository.save(newScore); // Save the new ScoreEntity
                    }

                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getAverageScore(UserEntity user) {
        List<ScoreEntity> scores = scoreRepository.findByUser(user);
        int total = 0;
        for (ScoreEntity score : scores) {
            System.out.println(score);
            total += score.getScore();
        }
        if (scores.size() == 0 ){
            return 0;
        }
        return total / scores.size();
    }

    public int getDiscountScore(UserEntity user) {
        int score = getAverageScore(user);
        if (score >= 950) {
            return 10;
        } else if (score >= 900) {
            return 5;
        } else if (score >= 850) {
            return 2;
        }
        return 0;

    }
    public int getExamsTaken(UserEntity user) {
        List<ScoreEntity> scores = scoreRepository.findByUser(user);
        return scores.size();

    }
    public int getAmountPendingPayment(UserEntity user){
        List<InstallmentEntity> installments = userRepository.findUnpaidInstallmentsByRut(user.getRut());
        int discountScore= getDiscountScore(user);
        int totalAmount = 0;
        for (InstallmentEntity installment : installments) {
            totalAmount += installment.getAmount() *  ((100.0 - user.getDiscount() - discountScore)/100);
        }
        return totalAmount;

    }
    public int getAmountPaid(UserEntity user){
        List<InstallmentEntity> installments = userRepository.findPaidInstallmentsByRut(user.getRut());
        int totalAmount = 0;
        for (InstallmentEntity installment : installments) {
            totalAmount += installment.getAmountPaid();
        }
        return totalAmount;
    }
    public String getUserSummary(UserEntity user) {
        LocalDate lastPayment = userRepository.findLastPaidDateByRut(user.getRut());
        DateTimeFormatter formatter = null;
        if (lastPayment != null) {
            formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        }
        String summary = "";
        summary += "RUT: " + user.getRut() + "\n";
        summary += "Nombre: " + user.getName() + " " + user.getSurname() + "\n";
        summary += "Examenes rendidos: " + getExamsTaken(user) + "\n";
        summary += "Promedio: " + getAverageScore(user) + "\n";
        summary += "Monto total arancel a pagar" + (getAmountPaid(user) + getAmountPendingPayment(user)) + "\n";
        summary += "Tipo de pago: " + (user.isUsingCredit() ? "Crédito" : "Contado") + "\n";
        summary += "Cuotas pactadas: " + user.getInstallments().size() + "\n";
        summary += "Cuotas pagadas: " + userRepository.findPaidInstallmentsByRut(user.getRut()).size() + "\n";
        summary += "Monto total pagado: " + getAmountPaid(user) + "\n";
        summary += "Fecha ultimo pago: " + (lastPayment != null ? lastPayment.format(formatter) : "No pagada") + "\n";
        summary += "Saldo por pagar: " + getAmountPendingPayment(user) + "\n";
        summary += "Nro. Cuotas con retraso" + userRepository.findTaxedInstallments(user.getRut()).size() + "\n";
        return summary;


    }

}