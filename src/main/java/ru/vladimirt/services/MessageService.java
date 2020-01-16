package ru.vladimirt.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.vladimirt.domain.Message;
import ru.vladimirt.domain.User;
import ru.vladimirt.domain.dto.MessageDto;
import ru.vladimirt.repositories.IMessageRepository;

import javax.persistence.EntityManager;

@Service
public class MessageService {

    @Autowired
    private IMessageRepository messageRepository;

    public Page<MessageDto> messageList(Pageable pageable, String filter, User user){
        if(filter != null && !filter.isEmpty()){
            return messageRepository.findByTag(filter, pageable, user);
        }else {
            return messageRepository.findAll(pageable, user);
        }
    }

    public Page<MessageDto> messageListForUser(Pageable pageable, User currentUser, User author) {

        return messageRepository.findByUser(pageable, author, currentUser );

    }
}
