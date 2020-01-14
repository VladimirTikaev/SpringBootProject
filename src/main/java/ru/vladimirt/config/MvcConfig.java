package ru.vladimirt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${upload.path}") //Это спел. Здесь он ищет свойство upload.path и вставляет в переменную
    private String uploadPath;

    @Bean
    public RestTemplate getRestTemplate(){
        return  new RestTemplate();
    }

    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/login").setViewName("login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //Любое обращение к серверу по пути img + что-то еще будет перенаправлять все запросы по указанному пути
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:///" + uploadPath + "/"); //Ищет в файловой системе
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");//Ищет в корне проекта
    }
}
