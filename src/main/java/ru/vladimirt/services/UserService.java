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

import java.util.*;

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

        userRepository.save(user);
        sendMessage(user);

        return true;
    }

    private void sendMessage(User user) {
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

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void saveUser(User user, String userName, Map<String, String> form) {

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
    }

    public void updateProfile(User user, String password, String email) {
        String userEmai1 = user.getEmail();

        //Проверка на то, что мэйл был изменен
        boolean isEmailChanged = (email != null && !email.equals(userEmai1)
                || (userEmai1 != null && !userEmai1.equals(email)));

        if(isEmailChanged){
            user.setEmail(email);
            //Если мэйл изменен и он не пуст, то генерируем новый активационный код
            if(!StringUtils.isEmpty(email)){
                user.setActivationCode(UUID.randomUUID().toString());
            }
        }

        if(!StringUtils.isEmpty(password)){
            user.setPassword(password);
        }

        userRepository.save(user);

        if(isEmailChanged) {
            sendMessage(user);
        }

    }
}
