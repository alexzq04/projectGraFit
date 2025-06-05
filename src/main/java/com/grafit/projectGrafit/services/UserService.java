package com.grafit.projectGrafit.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grafit.projectGrafit.models.UserDTO;
import com.grafit.projectGrafit.models.User;
import com.grafit.projectGrafit.repositories.UserRepository;
/**
 * Servicio que gestiona las operaciones relacionadas con los usuarios.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ExerciseService exerciseService;

    /**
     * Constructor del servicio de usuarios.
     *
     * @param userRepository  repositorio para operaciones con usuarios
     * @param passwordEncoder codificador de contraseñas
     * @param exerciseService servicio para operaciones con ejercicios
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ExerciseService exerciseService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.exerciseService = exerciseService;
    }

    /**
     * Crea un nuevo usuario a partir de un objeto DTO validado.
     *
     * @param dto objeto con los datos del usuario
     * @return el usuario creado y guardado
     * @throws IllegalArgumentException si el nombre de usuario o correo ya existen
     */
    public User createUserFromDto(UserDTO dto) {
        // Verificar si el nombre de usuario ya existe
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario '" + dto.getUsername() + "' ya está en uso");
        }

        // Verificar si el email ya existe
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El correo electrónico '" + dto.getEmail() + "' ya está registrado");
        }

        // Crear y guardar el usuario
        User user = dto.toUserEntity();
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user = userRepository.save(user);

        // Crear ejercicios básicos para el usuario
        exerciseService.createBasicExercisesForUser(user);

        return user;
    }

    /**
     * Crea un nuevo usuario directamente desde la entidad.
     *
     * @param user usuario a crear
     * @return usuario creado y guardado
     * @throws IllegalArgumentException si el nombre de usuario o correo ya existen
     */
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Nombre de usuario ya en uso: " + user.getUsername());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Correo electrónico ya en uso: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id identificador del usuario
     * @return el usuario si existe, envuelto en Optional
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Obtiene todos los usuarios registrados.
     *
     * @return lista de todos los usuarios
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param user usuario con los datos actualizados
     * @return el usuario actualizado
     * @throws IllegalArgumentException si el usuario no existe
     */
    public User updateUser(User user) {
        User existing = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + user.getId()));

        // Actualizar campos básicos
        existing.setName(user.getName());
        existing.setSurname1(user.getSurname1());
        existing.setSurname2(user.getSurname2());
        existing.setUsername(user.getUsername());
        existing.setEmail(user.getEmail());
        existing.setPhone(user.getPhone());
        existing.setGender(user.getGender());
        
        // Actualizar peso y altura directamente usando los setters específicos
        if (user.getWeightKg() > 0) {
            existing.setWeightKg(user.getWeightKg());
        }
        if (user.getHeightMeters() > 0) {
            existing.setHeightMeters(user.getHeightMeters());
        }

        // Actualizar características anatómicas
        existing.setArmLength(user.getArmLength());
        existing.setTorsoLength(user.getTorsoLength());
        existing.setFemurLength(user.getFemurLength());
        existing.setTibiaLength(user.getTibiaLength());

        // Guardar y retornar el usuario actualizado
        return userRepository.save(existing);
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id identificador del usuario a eliminar
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username nombre de usuario
     * @return el usuario si existe, envuelto en Optional
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Verifica si un nombre de usuario ya existe.
     *
     * @param username nombre de usuario
     * @return true si existe, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email correo electrónico
     * @return el usuario si existe, envuelto en Optional
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Verifica si un correo electrónico ya está registrado.
     *
     * @param email correo electrónico
     * @return true si existe, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
