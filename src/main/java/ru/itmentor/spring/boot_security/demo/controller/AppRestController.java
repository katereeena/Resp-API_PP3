package ru.itmentor.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AppRestController {
    private final UserService userService;

    @Autowired
    public AppRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/users")
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUser(id));
    }

    @PostMapping("/users")
    public ResponseEntity insert(@Valid @RequestBody User user) {
        if (userService.saveUser(user)) {
            return ResponseEntity.ok(200);
        }
        return ResponseEntity.internalServerError().body("Insert error!");
    }

    @PutMapping("/users")
    public ResponseEntity update(@RequestBody User user) {
        if (userService.updateUser(user) != null) {
            return ResponseEntity.ok(200);
        }
        return ResponseEntity.internalServerError().body("Update error!");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/roles")
    public ResponseEntity<List<Role>> findAllRoles() {
        return ResponseEntity.ok(userService.findAllRoles());
    }
}