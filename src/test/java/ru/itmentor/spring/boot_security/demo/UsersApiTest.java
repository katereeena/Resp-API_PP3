package ru.itmentor.spring.boot_security.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.itmentor.spring.boot_security.demo.controller.AppRestController;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.repository.RoleRepository;
import ru.itmentor.spring.boot_security.demo.service.UserService;


import java.util.*;

import static org.junit.matchers.JUnitMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WithUserDetails("admin@its.com")
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/remove-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UsersApiTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AppRestController appRestController;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    private String jsonAdmin = "{\"id\":1,\"firstName\":\"Василий\",\"lastName\":\"Уткин\",\"email\":\"admin@its.com\",\"password\":\"$2y$10$kbBc2/YyhalAHuK.SRiFPeu/iENCtVjUS9sK4A3/4b5EXdgqcj0cy\",\"enabled\":true,\"roles\":[{\"id\":1,\"name\":\"ROLE_ADMIN\",\"authority\":\"ROLE_ADMIN\"}],\"fullName\":\"Василий Уткин\",\"authorities\":[{\"id\":1,\"name\":\"ROLE_ADMIN\",\"authority\":\"ROLE_ADMIN\"}],\"username\":\"admin@its.com\",\"credentialsNonExpired\":true,\"accountNonExpired\":true,\"accountNonLocked\":true}";
    private String jsonUser = "{\"id\":2,\"firstName\":\"Петя\",\"lastName\":\"Пупкин\",\"email\":\"notadmin@its.com\",\"password\":\"$2a$10$ZwkWnsaM3h.QglCyIe85GuWFn.5dXois20CrRG76G8MBzr.LzDbqa\",\"enabled\":true,\"roles\":[{\"id\":1,\"name\":\"ROLE_ADMIN\",\"authority\":\"ROLE_ADMIN\"},{\"id\":2,\"name\":\"ROLE_USER\",\"authority\":\"ROLE_USER\"}],\"fullName\":\"Петя Пупкин\",\"authorities\":[{\"id\":1,\"name\":\"ROLE_ADMIN\",\"authority\":\"ROLE_ADMIN\"},{\"id\":2,\"name\":\"ROLE_USER\",\"authority\":\"ROLE_USER\"}],\"username\":\"notadmin@its.com\",\"accountNonLocked\":true,\"accountNonExpired\":true,\"credentialsNonExpired\":true}";

    // get /users
    @Test
    public void testUsersGet() throws Exception {
        this.mockMvc.perform(formLogin().user("admin@its.com").password("admin"));
        this.mockMvc.perform(get("/api/users")
                        .contentType("application/json"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().json("[" + jsonAdmin + "]"));
    }

    @Test
    public void testServiceUsersGet() throws Exception {
        List<User> userList = userService.findAllUsers();
        User admin = new User(1L, "Василий", "Уткин", "admin@its.com", "$2y$10$kbBc2/YyhalAHuK.SRiFPeu/iENCtVjUS9sK4A3/4b5EXdgqcj0cy", true);
        Role adminRole = new Role(1L, "ROLE_ADMIN");
        admin.setRoles(Set.of(adminRole));
        List<User> userListTest = new ArrayList<>(List.of(admin));
        Assert.assertEquals(userList.toString(), userListTest.toString());
    }

    // post /users
    @Test
    public void testUserCreate() throws Exception {
        this.mockMvc.perform(formLogin().user("admin@its.com").password("admin"));

        User admin = new User(2L, "Петя", "Пупкин", "notadmin@its.com", "notadmin", true);
        Role adminRole = new Role(1L, "ROLE_ADMIN");
        Role userRole = new Role(2L, "ROLE_USER");

        admin.setRoles(Set.of(adminRole, userRole));

        this.mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(asJsonString(admin))
                )
                .andDo(print())
                .andExpect(status().isOk());

        // берем пароль отсюда, т.к. BCrypt каждый раз разный хэш выдает
        User createdUser = userService.findUser(2L);

        jsonUser = "{\"id\":2,\"firstName\":\"Петя\",\"lastName\":\"Пупкин\",\"email\":\"notadmin@its.com\",\"password\":\"" + createdUser.getPassword() + "\",\"enabled\":true,\"roles\":[{\"id\":1,\"name\":\"ROLE_ADMIN\",\"authority\":\"ROLE_ADMIN\"},{\"id\":2,\"name\":\"ROLE_USER\",\"authority\":\"ROLE_USER\"}],\"fullName\":\"Петя Пупкин\",\"authorities\":[{\"id\":1,\"name\":\"ROLE_ADMIN\",\"authority\":\"ROLE_ADMIN\"},{\"id\":2,\"name\":\"ROLE_USER\",\"authority\":\"ROLE_USER\"}],\"username\":\"notadmin@its.com\",\"accountNonLocked\":true,\"accountNonExpired\":true,\"credentialsNonExpired\":true}";
        this.mockMvc.perform(get("/api/users")
                        .contentType("application/json")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[" + jsonAdmin + "," + jsonUser + "]"));
    }

    @Test
    public void testServiceUserCreate() throws Exception {
        List<User> userListTest = userService.findAllUsers();

        Role userRole = new Role(2L, "ROLE_USER");

        User newUser = new User(2L, "Петя", "Пупкин", "notadmin@its.com", "notadmin", true);
        newUser.setRoles(Set.of(userRole));

        userService.saveUser(newUser);
        userListTest.add(newUser);

        Assert.assertEquals(userService.findAllUsers().toString(), userListTest.toString());
    }

    // put /users
    @Test
    public void testUserUpdate() throws Exception {
        this.mockMvc.perform(formLogin().user("admin@its.com").password("admin"));

        User admin = new User(1L, "Вася", "Уткин", "admin@its.com", "", true);
        Role adminRole = new Role(1L, "ROLE_ADMIN");
        admin.setRoles(Set.of(adminRole));

        this.mockMvc.perform(put("/api/users")
                        .contentType("application/json")
                        .content(asJsonString(admin))
                )
                .andDo(print())
                .andExpect(status().isOk());

        // берем пароль отсюда, т.к. BCrypt каждый раз разный хэш выдает
        User updatedUser = userService.findUser(1L);

        String json = "{\"id\":1,\"firstName\":\"Вася\",\"lastName\":\"Уткин\",\"email\":\"admin@its.com\",\"password\":\"" + updatedUser.getPassword() + "\",\"enabled\":true,\"roles\":[{\"id\":1,\"name\":\"ROLE_ADMIN\",\"authority\":\"ROLE_ADMIN\"}],\"fullName\":\"Вася Уткин\",\"authorities\":[{\"id\":1,\"name\":\"ROLE_ADMIN\",\"authority\":\"ROLE_ADMIN\"}],\"username\":\"admin@its.com\",\"credentialsNonExpired\":true,\"accountNonExpired\":true,\"accountNonLocked\":true}";
        this.mockMvc.perform(get("/api/users/1")
                        .contentType("application/json")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    public void testServiceUserUpdate() throws Exception {
        User admin = new User(1L, "Вася", "Уткин", "admin@its.com", "", true);
        Role adminRole = new Role(1L, "ROLE_ADMIN");
        admin.setRoles(Set.of(adminRole));

        userService.updateUser(admin);

        User user = userService.findUser(1L);

        Assert.assertEquals(admin.toString(), user.toString());
    }

    // delete /users/{id}
    @Test
    public void testUserDelete() throws Exception {
        this.mockMvc.perform(formLogin().user("admin@its.com").password("admin"));

        User admin = new User(2L, "Петя", "Пупкин", "notadmin@its.com", "notadmin", true);
        Role adminRole = new Role(1L, "ROLE_ADMIN");
        Role userRole = new Role(2L, "ROLE_USER");

        admin.setRoles(Set.of(adminRole, userRole));

        this.mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(asJsonString(admin))
                )
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(delete("/api/users/2")
                        .contentType("application/json")
                )
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/api/users")
                        .contentType("application/json")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[" + jsonAdmin + "]"));
    }

    @Test
    public void testServiceUserDelete() throws Exception {
        Role adminRole = new Role(1L, "ROLE_ADMIN");
        Role userRole = new Role(2L, "ROLE_USER");

        User newUser = new User(2L, "Петя", "Пупкин", "notadmin@its.com", "notadmin", true);
        newUser.setRoles(Set.of(adminRole, userRole));

        userService.saveUser(newUser);
        userService.deleteUser(2L);

        List<User> usersList =  new ArrayList<>(List.of(userService.findUser(1L)));
        Assert.assertEquals(userService.findAllUsers().toString(), usersList.toString());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
