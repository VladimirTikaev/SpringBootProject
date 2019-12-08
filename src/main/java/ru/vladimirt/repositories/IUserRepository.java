package ru.vladimirt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladimirt.domain.User;

public interface IUserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
