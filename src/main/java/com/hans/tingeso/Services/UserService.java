package com.hans.tingeso.Services;

import com.hans.tingeso.Entities.UserEntity;
import com.hans.tingeso.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public ArrayList<UserEntity> getUsers(){
        return (ArrayList<UserEntity>) userRepository.findAll();
    }

    public void createUser(@ModelAttribute UserEntity user){
        userRepository.save(user);
    }




}
