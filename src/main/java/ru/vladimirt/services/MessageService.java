package ru.vladimirt.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.vladimirt.domain.Message;
import ru.vladimirt.domain.User;
import ru.vladimirt.repositories.IMessageRepository;

@Service
public class MessageService {

    @Autowired
    private IMessageRepository messageRepository;

    public Page<Message> messageList(Pageable pageable, String filter){
        if(filter != null && !filter.isEmpty()){
            return messageRepository.findByTagContaining(filter, pageable);
        }else {
            return messageRepository.findAll(pageable);
        }
    }

    public Page<Message> messageListForUser(Pageable pageable, User author) {

        return messageRepository.findByAuthor(author, pageable);

    }
}
