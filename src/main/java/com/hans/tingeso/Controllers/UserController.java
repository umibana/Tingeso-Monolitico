package com.hans.tingeso.Controllers;

import com.hans.tingeso.Entities.InstallmentEntity;
import com.hans.tingeso.Entities.UserEntity;
import com.hans.tingeso.Services.UserService;
import com.hans.tingeso.Services.InstallmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;

@Controller
@RequestMapping
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    InstallmentService installmentService;

    @GetMapping("users")
    public String users(Model model) {
        List<UserEntity> users = userService.getUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @PostMapping("users")
    public String createUser(@ModelAttribute UserEntity user) {
        userService.createUser(user);
        return "index";
    }

    @GetMapping("createUser")
    public String createUserPage() {
        return "createUser";
    }

    @GetMapping("installments")
    public String installments(@RequestParam String search, Model model) {
        List<InstallmentEntity> installments = userService.findInstallments(search);
        UserEntity user = installments.get(1).getUser();
        String summary = userService.getUserSummary(user);
        model.addAttribute("summary", summary);
        model.addAttribute("installments", installments);
        model.addAttribute("user", user);
        model.addAttribute("discountScore", userService.getDiscountScore(user));
        return "installments";
    }

    @PostMapping("togglePaidStatus")
    public String togglePaidStatus(@RequestParam Integer id) {
        InstallmentEntity installment = installmentService.findById(id);
        installment.setPaid(!installment.isPaid());
        installment.setPaidDate(LocalDate.now());
        int amount = installment.getAmount();
        int discount = installment.getUser().getDiscount();
        int scoreDiscount = userService.getDiscountScore(installment.getUser());
        installment.setAmountPaid(amount * ((100.0 - discount - scoreDiscount)/100));
        installmentService.saveInstallment(installment);
        return "redirect:/installments?search=" + installment.getUser().getRut();
    }


}
