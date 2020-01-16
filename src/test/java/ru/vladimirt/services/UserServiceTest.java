package ru.vladimirt.services;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import ru.vladimirt.domain.Role;
import ru.vladimirt.domain.User;
import ru.vladimirt.repositories.IUserRepository;

import java.util.Collections;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private IUserRepository userRepository;

    @MockBean
    private MailSender mailSender;

    @MockBean
    private PasswordEncoder passwordEncoder;


    @Test
    public void addUserTest() {

        User user = new User();
        user.setEmail("some@mail.com");

        boolean isUserCreated = userService.addUser(user);

        Assert.assertTrue(isUserCreated);
        Assert.assertNotNull(user.getActivationCode());
        Assert.assertTrue(CoreMatchers.is(user.getRoles()).matches(Collections.singleton(Role.USER)));

        //Через verify можно посмотреть сколько раз был вызва замоканный объект, каким методом и с какими параметрами
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verify(mailSender, Mockito.times(1)).
                send(ArgumentMatchers.eq(user.getEmail()),
                        ArgumentMatchers.eq("Activation code"),
                        ArgumentMatchers.contains("Welcome to Sweater")); // Можно заменить на anyString


    }

    @Test
    public void addUserFailTest() {
        User user = new User();
        user.setUsername("Ivan");

        /*
            Мокито позволяет эмулировать поведение замоканных объектов. К примеру нам нужно
            чтобы метод findByUserName при поиске Ивана нам сказал что он уже есть
         */

        Mockito.doReturn(new User()) //Возвращаем пользователя...не важно какого главное показать, что он сущ.
                .when(userRepository) // возвращаем его тогда, когда вызываем у userRepository
                .findByUsername("Ivan"); // данный метод с аргументо Ivan

        boolean isUserCreated = userService.addUser(user);

        Assert.assertFalse(isUserCreated);

        //Раз пользователь уже сущ. то нового создавать не надо. Проверим, что метод сохранения в бд и другие, не былив вызваны
        Mockito.verify(userRepository, Mockito.times(0)).save(ArgumentMatchers.any(User.class));
        Mockito.verify(mailSender, Mockito.times(0)).
                send(ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString());
    }

    @Test
    public void activateUserTest() {

        User testUser = new User();
        testUser.setActivationCode("bingo");

        Mockito.doReturn(testUser)
                .when(userRepository)
                .findByActivationCode("someActivateString");

        boolean isUserActivated = userService.activateUser("someActivateString");

        Assert.assertTrue(isUserActivated);
        Assert.assertNull(testUser.getActivationCode());
        Mockito.verify(userRepository, Mockito.times(1)).save(testUser);

    }

    @Test
    public void activateUserFailTest() {

        boolean isUserActivated = userService.activateUser("someActivateString");

        Assert.assertFalse(isUserActivated);
        Mockito.verify(userRepository, Mockito.times(0)).save(ArgumentMatchers.any(User.class));
    }
}