package ru.vladimirt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.vladimirt.domain.Message;
import ru.vladimirt.domain.User;
import ru.vladimirt.repositories.IMessageRepository;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class MainController {
    @Autowired
    private IMessageRepository messageRepository;

    @Value("${upload.path}") //Это спел. Здесь он ищет свойство upload.path и вставляет в переменную
    private String uploadPath;

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
    public String main(@RequestParam(required = false) String filter,
                       Model model,
                       //Т.к. сообщения могут выводиться каждый раз в разном порядке
                       // Задаем сортировку по id и по убыванию
                       @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
    ){
        Page<Message> page;

        if(filter != null && !filter.isEmpty()){
            page = messageRepository.findByTagContaining(filter, pageable);
        }else {
            page = messageRepository.findAll(pageable);
        }

        model.addAttribute("page",page);
        model.addAttribute("url","/main");
        model.addAttribute("filter", filter);
        return "main";
    }

    @PostMapping("/main")
    public String addMessage(
            @AuthenticationPrincipal User user,
            @Valid Message message, //Вместо 2-ух параметров используем 1 - просто класс. Также теперь можно добавить валидацию
            BindingResult bindingResult, // Список аргументов и сообщений ощибок валидаций. Всегда должен идти перед model
            Model model,
            @RequestParam("file_name") MultipartFile file)
             throws IOException {

        message.setAuthor(user);


        //Проверка ошибок валидации
        if(bindingResult.hasErrors()){
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);

            model.mergeAttributes(errorsMap);
            model.addAttribute("message", message);

        }else { // Если ошибок нет, то добавляем в бд

            saveFile(message, file);

            model.addAttribute("message", null);
            messageRepository.save(message);
        }

        Iterable<Message> messages = messageRepository.findAll();
        model.addAttribute("messages",messages);
        return "main";
    }

    private void saveFile(@Valid Message message, @RequestParam("file_name") MultipartFile file) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) { //Если такой переменной не существует, то мы ее создадим
                uploadDir.mkdir();
            }

            // Создаем уникальный идентификатор, чтобы небыло коллизий в именах файлов
            String uuidFile = UUID.randomUUID().toString();

            String resultFileName = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFileName)); // Создаем файл с уникальным именем
            message.setFileName(resultFileName);
        }
    }

    @GetMapping("/user-messages/{user}")
    public String userMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User user,
            Model model,
            @RequestParam(required = false) Message message //необязательный параметр
    ){

        Set<Message> messages = user.getMessages();

        model.addAttribute("userChannel", user);
        model.addAttribute("subscriptionsCount", user.getSubscriptions().size());
        model.addAttribute("subscribersCount", user.getSubscribers().size());
        model.addAttribute("isSubscriber", user.getSubscribers().contains(currentUser));
        model.addAttribute("messages", messages);
        model.addAttribute("message", message);
        model.addAttribute("isCurrentUser", currentUser.equals(user));

        return "userMessages";
    }

    @PostMapping("/user-messages/{user}")
    public String updateMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User user,
            @RequestParam("id") Message message,
            @RequestParam("text") String text,
            @RequestParam("tag") String tag,
            @RequestParam("file_name") MultipartFile file

    ) throws IOException {

        if(message.getAuthor().equals(currentUser)){
            if(!StringUtils.isEmpty(text)){
                message.setText(text);
            }

            if(!StringUtils.isEmpty(tag)){
                message.setTag(tag);
            }

            saveFile(message, file);
            messageRepository.save(message);
        }

        return "redirect:/user-messages/" + user.getId();
    }


}
