package com.hans.tingeso.Controllers;

import com.hans.tingeso.Entities.InstallmentEntity;
import com.hans.tingeso.Entities.UserEntity;
import com.hans.tingeso.Services.UserService;
import com.hans.tingeso.Services.InstallmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
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
    public String users(Model model){
            ArrayList<UserEntity> users = userService.getUsers();
            List<InstallmentEntity> installments = userService.findInstallments("12345678-9");
            model.addAttribute("users", users);
            return "index";
        }

    @PostMapping("users")
    public String createUser(@ModelAttribute UserEntity user) {
        userService.createUser(user);
        System.out.println(user);
        return "index";
    }

    @GetMapping("createUser")
    public String createUserPage() {
        return "createUser";
    }

    @GetMapping("installments")
    public String installments(@RequestParam String search, Model model) {
        List<InstallmentEntity> installments = userService.findInstallments(search);
        model.addAttribute("installments", installments);
        model.addAttribute("user",installments.get(1).getUser());
        return "installments";
    }
    @PostMapping("togglePaidStatus")
    public String togglePaidStatus(@RequestParam Integer id) {
        InstallmentEntity installment = installmentService.findById(id);
        installment.setPaid(!installment.isPaid());
        installmentService.saveInstallment(installment);
        return "redirect:/installments?search=" + installment.getUser().getRut();
    }



}
