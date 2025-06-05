package com.grafit.projectGrafit.controllers;

import com.grafit.projectGrafit.models.User;
import com.grafit.projectGrafit.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Collections;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("user", user);
        
        return "user/profile";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("user") User user,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "user/profile";
        }

        try {
<<<<<<< HEAD
            // Obtener el usuario actual para mantener datos sensibles
=======
      
>>>>>>> origin/main
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

<<<<<<< HEAD
            // Mantener datos sensibles que no deben ser modificados desde el formulario
=======
>>>>>>> origin/main
            user.setId(currentUser.getId());
            user.setPassword(currentUser.getPassword());
            user.setRole(currentUser.getRole());
            user.setDateOfRegister(currentUser.getDateOfRegister());

<<<<<<< HEAD
            // Validar si el nuevo username o email ya existe
=======
        
>>>>>>> origin/main
            if (!currentUser.getUsername().equals(user.getUsername()) && 
                userService.existsByUsername(user.getUsername())) {
                model.addAttribute("errorMessage", "El nombre de usuario ya está en uso.");
                return "user/profile";
            }

            if (!currentUser.getEmail().equals(user.getEmail()) && 
                userService.existsByEmail(user.getEmail())) {
                model.addAttribute("errorMessage", "El email ya está registrado.");
                return "user/profile";
            }

<<<<<<< HEAD
            // Validar peso y altura
=======
>>>>>>> origin/main
            if (user.getWeightKg() <= 0) {
                model.addAttribute("errorMessage", "El peso debe ser mayor que 0 kg.");
                return "user/profile";
            }

            if (user.getHeightMeters() <= 0) {
                model.addAttribute("errorMessage", "La altura debe ser mayor que 0 metros.");
                return "user/profile";
            }

<<<<<<< HEAD
            // Actualizar el usuario con los nuevos datos
            User updatedUser = userService.updateUser(user);
            
            // Actualizar el contexto de seguridad si el nombre de usuario ha cambiado
=======
           
            User updatedUser = userService.updateUser(user);
            
>>>>>>> origin/main
            if (!currentUser.getUsername().equals(updatedUser.getUsername())) {
                Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    updatedUser.getUsername(),
                    auth.getCredentials(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + updatedUser.getRole().name()))
                );
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "Perfil actualizado correctamente.");
            return "redirect:/profile";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al actualizar el perfil: " + e.getMessage());
            return "user/profile";
        }
    }
} 