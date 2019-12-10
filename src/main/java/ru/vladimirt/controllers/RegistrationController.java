package ru.vladimirt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.vladimirt.domain.Role;
import ru.vladimirt.domain.User;
import ru.vladimirt.repositories.IUserRepository;

import java.util.Collections;


@Controller
public class RegistrationController {

    @Autowired
    private IUserRepository userRepository;

    @GetMapping("/registration")
    public String registration(){
//        model.addAttribute("message","");
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model){
        User userFromDb = userRepository.findByUsername(user.getUsername());
        if(userFromDb != null){
            model.addAttribute("message", "User exists!");
            return "registration";
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        userRepository.save(user);
        return "redirect:/login";
    }
}
