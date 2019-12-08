package ru.vladimirt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource; // Создан спрингом и Необходим для авторизации т.к. там мы обращаемся к бд

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/","/registration").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .formLogin()
                    .loginPage("/login")
                    .permitAll()
                .and()
                    .logout()
                   .permitAll();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .jdbcAuthentication()
                .dataSource(dataSource)//Наш датасорс
                .passwordEncoder(NoOpPasswordEncoder.getInstance())//Кодирование пароля
                //Запрос на пользователя - порядок полей должен быть именно таким, он задан системой
                .usersByUsernameQuery("select username, password, active from usr where username=?")
                //Получаем список пользвователей с их ролями
                .authoritiesByUsernameQuery("Select u.username, ur.roles from" +
                        " usr u inner join user_role ur on u.id=ur.user_id where u.username=?");
    }
}