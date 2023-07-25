package ru.itmentor.spring.boot_security.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.itmentor.spring.boot_security.demo.controller.AppRestController;
import ru.itmentor.spring.boot_security.demo.service.UserService;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class LoginTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AppRestController appRestController;

    @Test
    public void testLogin() throws Exception {
        this.mockMvc.perform(formLogin().user("admin@its.com").password("admin"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }
}
