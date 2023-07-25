package ru.itmentor.spring.boot_security.demo.repository;


import ru.itmentor.spring.boot_security.demo.model.Role;

import java.util.List;
import java.util.NoSuchElementException;

public interface RoleRepository {
    List<Role> findAll();

    Role findRoleByAuthority(String authority) throws NoSuchElementException;
}
