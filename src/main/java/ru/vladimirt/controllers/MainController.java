package ru.vladimirt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.vladimirt.domain.Message;
import ru.vladimirt.domain.User;
import ru.vladimirt.repositories.IMessageRepository;

import java.util.Map;

@Controller
public class MainController {
    @Autowired
    private IMessageRepository messageRepository;

    //Этот метод обрабатывает гет запросы по адресу /greeting
    //В параметрах метода мы передаем параметр name, который соответсвует name
    // required = false  Означает, что параметр не обязателен, и если его не будет
    // то подставится значение по умаолчнию World
//    Значение параметра name добавляется в объект Model,
//    что в конечном итоге делает его доступным для шаблона представления.
    @GetMapping("/")
    public String greeting(Map<String, Object> model){
        return "greeting";
    }

    @GetMapping("/main")
    public String main(Map<String, Object> model){
        Iterable<Message> messages = messageRepository.findAll();
        model.put("messages",messages);
        return "main";
    }

    @PostMapping("/main")
    public String addMessage(
            @AuthenticationPrincipal User user,
            @RequestParam String text,
            @RequestParam String tag,
            Map<String, Object> model){
        Message message = new Message(text, tag, user);
        messageRepository.save(message);

        Iterable<Message> messages = messageRepository.findAll();
        model.put("messages",messages);
        return "main";
    }

    @PostMapping("filter")
    public String showByFilter(@RequestParam String filter,
                               Map<String, Object> model){

        Iterable<Message> messages;
        if(filter != null && !filter.isEmpty()){
            messages = messageRepository.findByTextContaining(filter);
        }else {
            messages = messageRepository.findAll();
        }

        model.put("messages",messages);
        return "main";
    }
}
