package ru.vladimirt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.vladimirt.controllers.MessageController;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
//Здесь передаем имя пользователя, под которым мы будем выполнять тесты. Т.е. этот пользователь как бы авторизирован
@WithUserDetails("TestUser")
//Позволяет указать файл со свойствами для тестирования. Таким образом мы можем тестировать все на отдельно бд.
@TestPropertySource("/application-test.properties")
//Позволяет перед тестами выполнить скрипт.
@Sql(value = {"/create-user-before.sql" ,"/messages-list-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //До выполнения тестов
@Sql(value = {"/messages-list-after.sql", "/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) // После выполнения тестов
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageController controller;

    @Test
    public void mainPageTest() throws Exception{
        this.mockMvc.perform(get("/main"))
                .andDo(print())
                //Тест пройдет только если для пользователя в спринг контексте установлена веб сессия.
                //Т.е. пользователь должен быть авторизован. Для нас это делает аннотация @WithUserDetails("TestUser")
                .andExpect(authenticated())
                //Проверка соответствия отобрежня имени пользователя в интерфейсе по xpath
                .andExpect(xpath("//div[@id='navbarSupportedContent']/div").string("TestUser"));
    }

    @Test
    public void messageListTest() throws Exception{
        this.mockMvc.perform(get("/main"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id='message-list']/div").nodeCount(4));
    }

    @Test
    public void filterMessageTest() throws Exception{

        this.mockMvc.perform(get("/main").param("filter", "my tag"))
                .andDo(print())
                .andExpect(authenticated())
                //Проверяем что у нас только 2 записил с таким тегом
                .andExpect(xpath("//div[@id='message-list']/div").nodeCount(2))
                //Проверяем совпадение айдишников сообщений с айдишниками в базе
                .andExpect(xpath("//div[@id='message-list']/div[@data-id=1]").exists())
                .andExpect(xpath("//div[@id='message-list']/div[@data-id=3]").exists());
    }

    @Test
    public void addMessageToListTest() throws Exception{
        MockHttpServletRequestBuilder multipart =  multipart("/main")
                .file("file_name", "123".getBytes())
                .param("text", "fifth")
                .param("tag", "new one")
                .with(csrf());


        this.mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id='message-list']/div").nodeCount(5))
                //Проверяем совпадение айдишников сообщений с айдишниками в базе
                .andExpect(xpath("//div[@id='message-list']/div[@data-id=10]").exists())
                .andExpect(xpath("//div[@id='message-list']/div[@data-id=10]/div/span").string("fifth"))
                .andExpect(xpath("//div[@id='message-list']/div[@data-id=10]/div/i").string("#new one"));
    }
}
