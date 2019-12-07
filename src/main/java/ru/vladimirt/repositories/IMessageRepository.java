package ru.vladimirt.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.vladimirt.domain.Message;

import java.util.List;

//Спринг автоматически создает бин
public interface IMessageRepository extends CrudRepository<Message, Long> {

    List<Message> findByTag(String tag);

    List<Message> findByTextContaining(String filter);


}
