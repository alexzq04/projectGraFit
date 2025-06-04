package com.grafit.projectGrafit.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.grafit.projectGrafit.services.UserService;
import com.grafit.projectGrafit.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/tutorials")
public class TutorialController {

    private static final Logger logger = LoggerFactory.getLogger(TutorialController.class);
    private final UserService userService;

    public TutorialController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/press-banca")
    public String pressBanca() {
        return "tutorials/press-banca";
    }

    @GetMapping("/peso-muerto")
    public String pesoMuerto() {
        return "tutorials/peso-muerto";
    }

    @GetMapping("/sentadilla")
    public String sentadilla() {
        return "tutorials/sentadilla";
    }

    @GetMapping("/custom")
    public String customAnthropometry(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                logger.error("Usuario no autenticado intentando acceder a la página de antropometría personalizada");
                return "redirect:/login";
            }

            String username = auth.getName();
            logger.info("Usuario {} accediendo a la página de antropometría personalizada", username);

            User user = userService.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("No se encontró el usuario {} en la base de datos", username);
                    return new RuntimeException("Usuario no encontrado");
                });

            model.addAttribute("user", user);
            return "tutorials/custom";
            
        } catch (Exception e) {
            logger.error("Error al cargar la página de antropometría personalizada: {}", e.getMessage(), e);
            model.addAttribute("error", "Ha ocurrido un error al cargar tus datos. Por favor, inténtalo de nuevo.");
            return "error";
        }
    }
} 