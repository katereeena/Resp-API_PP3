package ru.itmentor.spring.boot_security.demo.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.itmentor.spring.boot_security.demo.util.CustomAuthorityDeserializer;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Table(name = "users", indexes = {@Index(columnList = "name, last_name ASC")})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    @NotEmpty(message = "Name should not be empty")
    @Size(min = 2, max = 30, message = "Name should be between 2 to 30")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(unique = true)
    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotEmpty(message = "Password should not be empty")
    private String password;


    private boolean enabled;

    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles")
    private Set<Role> roles = new HashSet<>();

    public User() {

    }

    public User(String name, String lastName, String email, String password, boolean enabled) {
        this.firstName = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
    }

    public User(Long id, String name, String lastName, String email, String password, boolean enabled) {
        this.id = id;
        this.firstName = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public boolean persisted() {
        return id != null;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean hasRole(int roleId) {
        if (null == roles|| 0 == roles.size()) {
            return false;
        }
        Optional<Role> findRole = roles.stream().filter(role -> roleId == role.getId()).findFirst();
        return findRole.isPresent();
    }

    public boolean hasRole(String roleName) {
        if (null == roles|| 0 == roles.size()) {
            return false;
        }
        Optional<Role> findRole = roles.stream().filter(role -> roleName.equals(role.getName())).findFirst();
        return findRole.isPresent();
    }

    @JsonDeserialize(using = CustomAuthorityDeserializer.class)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return String.format("User [id = %d; firstName = %s; lastName = %s; email = %s; password = %s; enabled = %s; roles = (%s)]",
                id, firstName, lastName, email, password, isEnabled(), Collections.singletonList(roles));
    }
}
