package ru.itmentor.spring.boot_security.demo.service;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.SessionAttribute;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;

import ru.itmentor.spring.boot_security.demo.configs.exception.LoginException;
import javax.servlet.http.HttpSession;
import java.util.List;

public interface UserService extends UserDetailsService {
    List<User> findAllUsers();

    User findUser(Long userId) throws NullPointerException;

    void deleteUser(Long userId);

    List<Role> findAllRoles();

    User updateUser(User user);

    void tryIndex(Model model, HttpSession session, LoginException authenticationException, String authenticationName);

    boolean saveOrUpdateUser(User user, BindingResult bindingResult);

    boolean saveUser(User user);
}
