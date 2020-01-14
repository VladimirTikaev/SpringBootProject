package ru.vladimirt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import ru.vladimirt.domain.User;
import ru.vladimirt.domain.dto.CaptchaResponseDto;
import ru.vladimirt.services.UserService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;


@Controller
public class RegistrationController {

    private final static String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${recaptcha.secret}")
    private String secret;

    @GetMapping("/registration")
    public String registration(){
//        model.addAttribute("message","");
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(
            @RequestParam("password2") String passwordConfirm,
            @RequestParam("g-recaptcha-response") String captchaResponse,
            @Valid User user, BindingResult bindingResult, Model model)
    {

        //Получаем ответ на основе ссылки и ключа
        String url = String.format(CAPTCHA_URL, secret, captchaResponse);
        CaptchaResponseDto response = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);

        //Если проверка не прошла, то скажем об этом пользователю
        if(!response.isSuccess()){
            model.addAttribute("captchaError", "Fill captcha");
        }
        boolean isConfirmEmpty = StringUtils.isEmpty(passwordConfirm);
        if(isConfirmEmpty){
            model.addAttribute("password2Error", "Password confirmation cannot be empty");
        }

        if(user.getPassword() != null && !user.getPassword().equals(passwordConfirm))
            model.addAttribute("passwordError", "Password are different");

        if(isConfirmEmpty || bindingResult.hasErrors() || !response.isSuccess()){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            model.addAllAttributes(errors);
            return "registration";
        }

        if(!userService.addUser(user)){
            model.addAttribute("usernameError", "User exists!");
            return "registration";
        }

        return "redirect:/login";
    }

    //Проверяем есть ли пользователь с таким активационным кодом и высылаем соответствующий ответ
    @GetMapping("/activate/{code}")
    public String activate(@PathVariable String code,  Model model){

        boolean isActivated = userService.activateUser(code);

        if(isActivated){
            model.addAttribute("messageType", "success");
            model.addAttribute("message", "User successfully activated");
        }else {
            model.addAttribute("messageType", "danger");
            model.addAttribute("message", "Activation code is not found");
        }

        return "login";
    }
}
