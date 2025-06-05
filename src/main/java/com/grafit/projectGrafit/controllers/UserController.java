package com.grafit.projectGrafit.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import com.grafit.projectGrafit.services.UserService;

/**
 * Controlador MVC para la gestión de usuarios.
 */
@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Muestra la lista de usuarios existentes.
     */
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "user_list";
    }
}
