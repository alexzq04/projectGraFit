package com.grafit.service;

import com.grafit.projectGrafit.models.User;
import com.grafit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new RuntimeException("El ID del usuario no puede ser nulo");
        }
        
        // Verificar que el usuario existe
        userRepository.findById(user.getId())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
        return userRepository.save(user);
    }
} 