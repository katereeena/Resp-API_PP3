package ru.itmentor.spring.boot_security.demo.repository;

import org.springframework.stereotype.Repository;
import ru.itmentor.spring.boot_security.demo.model.Role;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Role> findAll() {
        return entityManager.createQuery("from Role", Role.class).getResultList();
    }

    public Role findRoleByAuthority(String authority) throws NoSuchElementException {
        return findAll().stream()
                .filter(r -> authority.equals(r.getAuthority()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("Role %s not found", authority)));
    }
}
