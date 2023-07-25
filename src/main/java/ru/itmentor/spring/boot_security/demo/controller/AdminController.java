package ru.itmentor.spring.boot_security.demo.controller;

import org.hibernate.AssertionFailure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String showUserList(Model model) {
        model.addAttribute("users", userService.findAllUsers());

        return "list";
    }

    @GetMapping(value = "/new")
    public String addUserForm(@ModelAttribute("user") User user, Model model) {
        model.addAttribute("allRoles", userService.findAllRoles());

        return "form";
    }

    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable(value = "id", required = true) Long userId, Model model) {
        try {
            model.addAttribute("user", userService.findUser(userId));
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();

            return "redirect:/admin";
        }
        model.addAttribute("allRoles", userService.findAllRoles());

        return "form";
    }

    @PostMapping()
    public String saveOrUpdateUser(@Valid @ModelAttribute("user") User user,
                                   BindingResult bindingResult, Model model) {
        model.addAttribute("allRoles", userService.findAllRoles());
        try {
            return userService.saveOrUpdateUser(user, bindingResult) ? "redirect:/admin" : "form";
        }
        catch (AssertionFailure | UnexpectedRollbackException e) {
            return "form";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") Long userId) {
        userService.deleteUser(userId);

        return "redirect:/admin";
    }
}
