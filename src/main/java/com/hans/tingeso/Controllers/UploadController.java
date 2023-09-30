package com.hans.tingeso.Controllers;

import com.hans.tingeso.Models.UploadModel;
import com.hans.tingeso.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadController {
    @Autowired
    UserService userService;

    @PostMapping("/upload")
    public String handleFileUpload(@ModelAttribute UploadModel fileUploaded) {
        MultipartFile file = fileUploaded.getFile();
        System.out.println(file.getOriginalFilename());
        userService.addGrades(file);
        return "index";

    }
}
