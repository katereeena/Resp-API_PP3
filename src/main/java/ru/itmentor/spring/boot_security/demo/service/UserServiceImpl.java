package ru.itmentor.spring.boot_security.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.repository.RoleRepository;
import ru.itmentor.spring.boot_security.demo.repository.UserRepository;

import javax.persistence.PersistenceException;
import ru.itmentor.spring.boot_security.demo.configs.exception.LoginException;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUser(Long userId) {
        return userRepository.find(userId)
                .orElseThrow(() -> new EmptyResultDataAccessException(String.format("User with ID = %d not found", userId), 1));
    }

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.find(email);
        if (null == user) {
            throw new UsernameNotFoundException(String.format("User email %s not found", email));
        }
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        Optional<User> user = userRepository.find(userId);
        if (user.isPresent()) {
            try {
                userRepository.delete(user.get());
            } catch (PersistenceException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null || userRepository.find(user.getId()).isEmpty()) {
            throw new NullPointerException("No such user");
        }

        user.setPassword(user.getPassword().isEmpty() ?
                findUser(user.getId()).getPassword() :
                passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return userRepository.find(user.getId()).get();
    }

    @Override
    public void tryIndex(Model model, HttpSession session, LoginException authenticationException, String authenticationName) {
        if (authenticationException != null) { // Восстанавливаем неверно введенные данные
            try {
                model.addAttribute("authenticationException", authenticationException);
                session.removeAttribute("Authentication-Exception");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            model.addAttribute("authenticationException", new LoginException(null));
        }

        if (authenticationName != null) { // Выводим прощальное сообщение
            try {
                model.addAttribute("authenticationName", authenticationName);
                session.removeAttribute("Authentication-Name");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean saveOrUpdateUser(User user, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return false;
        }

        for (Role role : user.getRoles()) {
            try {
                role.setId(roleRepository.findRoleByAuthority(role.getAuthority()).getId());
            } catch (NoSuchElementException e) {
                e.printStackTrace();
            }
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            userRepository.save(user);
        } catch (PersistenceException e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean saveUser(User user) {
        for (Role role : user.getRoles()) {
            try {
                role.setId(roleRepository.findRoleByAuthority(role.getAuthority()).getId());
            } catch (NoSuchElementException e) {
                e.printStackTrace();
            }
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return true;
    }
}