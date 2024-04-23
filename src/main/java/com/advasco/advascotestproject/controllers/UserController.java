package com.advasco.advascotestproject.controllers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.advasco.advascotestproject.model.User;
import com.advasco.advascotestproject.model.UserCreate;
import com.advasco.advascotestproject.services.UserRepository;
import org.springframework.validation.FieldError;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;

    @RequestMapping({"", "/"})
    public String showUserList(Model model){
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("users", users);
        return "users/index";
    }

    @RequestMapping("/create")
    public String showCreatePage(Model model){
        UserCreate userCreate = new UserCreate();
        model.addAttribute("userCreate", userCreate);
        return "users/create";
    }

    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute UserCreate userCreate, BindingResult bindingResult){
        if(userCreate.getImage().isEmpty()){
            bindingResult.addError(new FieldError("userCreate", "image", "Image is required"));
        }

        if(bindingResult.hasErrors()){
            return "users/create";
        }

        // Save image in server
        MultipartFile image = userCreate.getImage();
        Date createdAt = new Date();
        String fileName = createdAt.getTime() + "_" + image.getOriginalFilename();

        try{
            String uploadDirection = "public/images/";
            Path uploadPath = Paths.get(uploadDirection);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);                
            }

            try(InputStream inputStream = image.getInputStream()){
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch(Exception e){
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        User user = new User();
        user.setName(userCreate.getName());
        user.setImage(fileName);

        userRepository.save(user);

        return "redirect:/users";
    }

    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id){
        try{
            User user = userRepository.findById(id).get();
            model.addAttribute("user", user);

            UserCreate userCreate = new UserCreate();
            userCreate.setName(userCreate.getName());

            model.addAttribute("userCreate", userCreate);
        }catch(Exception e){
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/users";
        }
        return "users/edit";
    }

    @PostMapping("/edit")
    public String editUser(Model model, 
            @RequestParam int id,
            @Valid @ModelAttribute UserCreate userCreate, 
            BindingResult bindingResult){

        try{
            User user = userRepository.findById(id).get();
            model.addAttribute("user", user);

            if (bindingResult.hasErrors()) {
                return "users/edit";
            }

            if(!userCreate.getImage().isEmpty()){
                // Delete old image
                String uploadDirection = "public/images/";
                Path oldImagePath = Paths.get(uploadDirection + user.getImage());

                try{
                    Files.delete(oldImagePath);
                }catch(Exception e){
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }

                // Save new image
                MultipartFile image = userCreate.getImage();
                Date createdAt = new Date();
                String fileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()){
                    Files.copy(inputStream, Paths.get(uploadDirection + fileName), StandardCopyOption.REPLACE_EXISTING);
                }catch(Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }

                user.setImage(fileName);
            }

            user.setName(userCreate.getName());
            userRepository.save(user);
        }catch(Exception e){
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/users";
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam int id){
        try{
            User user = userRepository.findById(id).get();
            Path imagePath = Paths.get("public/images/" + user.getImage());

            try{
                Files.delete(imagePath);
            }catch(Exception e){
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }

            userRepository.delete(user);
        }catch(Exception e){
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/users";
    }
}
