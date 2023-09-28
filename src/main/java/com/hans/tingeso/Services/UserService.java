package com.hans.tingeso.Services;

import com.hans.tingeso.Entities.InstallmentEntity;
import com.hans.tingeso.Entities.UserEntity;
import com.hans.tingeso.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PaymentService paymentService;

    public ArrayList<UserEntity> getUsers(){
        return (ArrayList<UserEntity>) userRepository.findAll();
    }
    public void createUser(@ModelAttribute UserEntity user){
        user.setDiscount(paymentService.getUserDiscounts(user));
        int availableInstallments = paymentService.getAvailableInstallments(user);
        // We start the installment on the 5th from the next month
        LocalDate nextMonth = LocalDate.now().plus(1, ChronoUnit.MONTHS).withDayOfMonth(5);
        // We populate the array according to how many installments the user is allowed
        ArrayList<InstallmentEntity> installments = new ArrayList<>(availableInstallments);
        // We add the installments to the user iterating over the months
        for(int i = 0; i < availableInstallments; i++) {
            LocalDate installmentDate = nextMonth.plus(i, ChronoUnit.MONTHS);
            InstallmentEntity installment = new InstallmentEntity();
            installment.setDate(installmentDate);
            installment.setAmount(paymentService.tuition / availableInstallments);
            installments.add(installment);
        }
        user.setInstallments(installments);
        userRepository.save(user);
    }

    public List<InstallmentEntity> findInstallments(String rut){
        return userRepository.findInstallmentsByRut(rut);
    }

}
