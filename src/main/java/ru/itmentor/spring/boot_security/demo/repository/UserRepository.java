package ru.itmentor.spring.boot_security.demo.repository;


import ru.itmentor.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();

    Optional<User> find(Long id);

    User find(String email);

    void save(User entity);

    void delete(User entity);
}
