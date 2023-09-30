package com.hans.tingeso.Services;

import com.hans.tingeso.Entities.InstallmentEntity;
import com.hans.tingeso.Entities.UserEntity;
import com.hans.tingeso.Repositories.InstallmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class InstallmentService {
    @Autowired
    InstallmentRepository installmentRepository;

    public InstallmentEntity findById(int id) {
        return installmentRepository.findById(id).get();
    }

    public InstallmentEntity saveInstallment(InstallmentEntity installment) {
        return installmentRepository.save(installment);
    }
}
