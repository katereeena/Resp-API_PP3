package ru.itmentor.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.itmentor.spring.boot_security.demo.service.UserService;


@EnableWebSecurity(debug = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    // сервис, с помощью которого тащим пользователя
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public WebSecurityConfig(UserService userService,
                             PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // конфигурация для прохождения аутентификации
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() //выключаем кроссдоменную секьюрность
                .authorizeRequests(authorize -> authorize
                        .antMatchers("/api/users/*", "/api/roles").hasRole("ADMIN")
                        .antMatchers("/", "/css/*", "/js/*").permitAll()
                        .antMatchers("/admin/**").hasRole("ADMIN")
                        .antMatchers("/user/**").hasAnyRole("ADMIN", "USER")
                        .anyRequest().authenticated()
                );

        http.formLogin()
                .loginPage("/") // указываем страницу с формой логина
                .permitAll()  // даем доступ к форме логина всем
                .usernameParameter("email") // Указываем параметры логина и пароля с формы логина
                .passwordParameter("password");

        http.logout()
                .permitAll() // разрешаем делать логаут всем
                .logoutUrl("/logout")
                .clearAuthentication(true)
                .invalidateHttpSession(true) // сделать невалидной текущую сессию
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/?logout"); // указываем URL при удачном логауте
    }
}