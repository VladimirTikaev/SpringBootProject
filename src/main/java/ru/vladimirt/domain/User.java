package ru.vladimirt.domain;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "usr")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private String username;

    private String password;

    private boolean active;

    //Не совсем понятно для чего эта аннотация.... Для работы с коллекциями
    //feth - способ подгрузки EAGER - подгружает сразу все при запросе пользователя
    // LAZY- подгружает только при обращении к полю
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    // Указывается что роли будут хранится в отдельной таблице, которую мы не описывали ранее
    // а также поле, по которому таблицы связаны
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    // Говорим, что храним перечисление в виде строк
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
