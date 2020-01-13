package ru.vladimirt.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.vladimirt.domain.Role;
import ru.vladimirt.domain.User;
import ru.vladimirt.repositories.IUserRepository;

import javax.swing.*;
import java.util.Collections;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private MailSender mailSender;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return userRepository.findByUsername(userName);
    }

    public boolean addUser(User user){
        User userFromDb = userRepository.findByUsername(user.getUsername());

        if(userFromDb != null){
            return false;
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        //Создаем рандомный код. Далее это будет ссылка, по которой пользователь должен будет
        // перейти. Если перешел, то аккаунт подтвержден
        user.setActivationCode(UUID.randomUUID().toString());

        //Если есть мэйд, то отправляем туда активационное письмо с сгенерированной ссылкой
        if(!StringUtils.isEmpty(user.getEmail())){
            String message = String.format(
                    "Hello, %s \n" +
                    "Welcome to Sweater. Please visit next link: http://localhost:8080/activate/%s",
                    user.getUsername(),
                    user.getActivationCode()
            );
            mailSender.send(user.getEmail(), "Activation code",message);
        }
        userRepository.save(user);

        return true;
    }

    public boolean activateUser(String code) {
        //Проверяем есть ли юзер с таким активационным кодом
        User user = userRepository.findByActivationCode(code);
        if(user == null){
            return false;
        }

        //Если есть, то сбрасываем код. Это означает, что пользователь подтвердил свою почту.
        user.setActivationCode(null);
        userRepository.save(user);
        return true;
    }
}
