package ru.vladimirt.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import ru.vladimirt.domain.Message;
import ru.vladimirt.domain.User;

import java.util.List;

//Спринг автоматически создает бин
public interface IMessageRepository extends CrudRepository<Message, Long> {


    Page<Message> findByTagContaining(String filter, Pageable pageable);
    Page<Message> findByAuthor(User user, Pageable pageable);
    Page<Message> findAll(Pageable pageable);


}
