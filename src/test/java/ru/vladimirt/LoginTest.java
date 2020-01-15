package ru.vladimirt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.vladimirt.controllers.MainController;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;





@RunWith(SpringRunner.class)
@SpringBootTest //Указывает, что мы запускаем тесты в окружении спринг бут приложения. Будут подхвачены все конфигурации
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
public class LoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MainController controller;

    @Test
    public void contextLoads()throws Exception{

        this.mockMvc.perform(get("/")) // Выполняем гет запрос на главную страницу
                .andDo(print()) //Вывод сообщения в консоль. Как лог
                .andExpect(status().isOk()) //Обертка над ассертом. Сравниваем результат с содержанием. Должны получить код 200
                //Проверяем, что у нас возвращается какой-то контент и в этом контенте должна быть строка Hello guest
                .andExpect(content().string(containsString("Hello guest")))
                .andExpect(content().string(containsString("Please, login")));
    }


    @Test
    public void accessDeniedTest() throws Exception{

        this.mockMvc.perform(get("/main"))
                .andDo(print())
                // Здесь идет проверка на статус, который означает перенаправление на другую страниицу.
                .andExpect(status().is3xxRedirection())
                // Нас должно перекинуть на страницу логин
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @Sql(value = {"/create-user-before.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void correctLoginTest() throws Exception{
        this.mockMvc.perform(formLogin().user("TestUser").password("123"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void badCredentials() throws Exception{
        //Здесь также можно было проверить через formLogin
        this.mockMvc.perform(post("/login").param("user", "WrongUser"))
                .andDo(print())
                .andExpect(status().isForbidden()); // Ожидаем ошибку
    }
}
