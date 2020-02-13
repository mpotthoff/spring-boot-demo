package it.fb5.imgshare.imgshare;

import java.util.Locale;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@SpringBootApplication
public class ImgShareApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImgShareApplication.class, args);
    }

    @Bean
    @DependsOn("liquibase")
    public FakeInitializer fakeInitializer() {
        return new FakeInitializer();
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.US);
        return resolver;
    }
}
