package ru.vladimirt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vladimirt.domain.Role;
import ru.vladimirt.domain.User;
import ru.vladimirt.repositories.IUserRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @GetMapping
    public String userList(Model model){

        model.addAttribute("userList", userRepository.findAll());
        return "userList";
    }

    /*Здесь в гетмэпинге мы не пишем первую часть т.к. она стоит над классом
        Мы должны только обработать параметр id и переать его в метод с помощью @PathVariable
        Поидее мы получаем id,  но спринг очень умный и мы можем получить сразу юзера
     */
    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model){
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());

        return "userEdit";
    }

    @PostMapping
    public String userSave(@RequestParam("userId") User user,
                           @RequestParam("userName") String userName,
                           //Такая запись получает все параметры с формы
                           @RequestParam Map<String, String> form){

        user.setUsername(userName);

        //Перевод ролей из Enum в Set
//        Set<String> roles = Arrays.stream(Role.values())
//                .map(Role::name)
//                .collect(Collectors.toSet());
        Set<String> roles = new HashSet<>();
        for (Role role : Role.values()) {
            roles.add(role.name());
        }

        user.getRoles().clear();

        //Проходим по всем параметрам с формы
        for (String key : form.keySet()) {
            //Если ключ из всех параметром формы совпадает с названием роли(содержится в сете), то мы добавляем эту роль юзеру
            // Таким образом все параметры, которые не роли - сюда точно не попадут
            //Единственный вопрос...если пользователь не выбрал какую-то роль...она ведь все равно должна придти, но со значение
            //uncheked или Null...т.е. она тоже должна попасть...но опять же все равботает..
            if(roles.contains(key)){
                user.getRoles().add(Role.valueOf(key));
            }
        }
        userRepository.save(user);
        //Здесь происходит редирект, но для перехода на спсок юзеров нам нужна модель со всеми юзерами
        //Мы здесь не используем никакую модель, но оно все равно работает...
        return "redirect:/user";
    }
}
