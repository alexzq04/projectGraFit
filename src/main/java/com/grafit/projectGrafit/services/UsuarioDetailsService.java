package com.grafit.projectGrafit.services;

import com.grafit.projectGrafit.models.User;
import com.grafit.projectGrafit.repositories.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Implementación de UserDetailsService para integración con Spring Security.
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public UsuarioDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carga un usuario por nombre de usuario para autenticación.
     * 
     * @param username nombre de usuario
     * @return UserDetails con información para autenticación y autorización
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.emptyList()) // Cambiar a roles si existen
                .build();
    }
}
