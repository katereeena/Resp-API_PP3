package ru.itmentor.spring.boot_security.demo.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.itmentor.spring.boot_security.demo.model.User;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<User> find(Long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    @Override
    public User find(String email) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> itemRoot = criteriaQuery.from(User.class);
        criteriaQuery.where(criteriaBuilder.equal(itemRoot.get("email"), email));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    public List<User> findAll() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> itemRoot = criteriaQuery.from(User.class);
        criteriaQuery.orderBy(criteriaBuilder.asc(itemRoot.get("firstName")),
                criteriaBuilder.asc(itemRoot.get("lastName")));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public void save(User entity) {
        try {
            entityManager.merge(entity);
        } catch (Exception err) {
            entityManager.persist(entity);
        }
    }

    @Override
    public void delete(User entity) {
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }
}