package com.hans.tingeso.Controllers;

import com.hans.tingeso.Entities.UserEntity;
import com.hans.tingeso.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import java.util.ArrayList;

import java.util.Vector;


@Controller
@RequestMapping
public class UserController {
        @Autowired
        UserService userService;

        @GetMapping("users")
    public String users(Model model){
            ArrayList<UserEntity> users = userService.getUsers();
            System.out.println(users);
            model.addAttribute("users", users);
            return "index";


        }

    @GetMapping("createUser")
    public String createUserPage() {
        return "createUser";
    }
    @PostMapping("users")
    public String createUser(@ModelAttribute UserEntity user) {
            System.out.println(user);
            userService.createUser(user);
        return "createUser";
    }




}
