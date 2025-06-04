package com.grafit.projectGrafit.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.grafit.projectGrafit.models.User;

/**
 * Repositorio para la entidad {@link User}.
 * Proporciona métodos para acceder y consultar usuarios en la base de datos.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su nombre de usuario.
     * 
     * @param username Nombre de usuario a buscar.
     * @return Un {@link Optional} con el usuario si existe, vacío si no.
     */
    Optional<User> findByUsername(String username);

    /**
     * Verifica si existe un usuario con el nombre de usuario dado.
     * 
     * @param username Nombre de usuario a verificar.
     * @return true si existe, false en caso contrario.
     */
    boolean existsByUsername(String username);

    /**
     * Busca un usuario por su email.
     * 
     * @param email Email a buscar.
     * @return Un {@link Optional} con el usuario si existe, vacío si no.
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el email dado.
     * 
     * @param email Email a verificar.
     * @return true si existe, false en caso contrario.
     */
    boolean existsByEmail(String email);
}