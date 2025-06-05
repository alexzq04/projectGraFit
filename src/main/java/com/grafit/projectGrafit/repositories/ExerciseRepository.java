package com.grafit.projectGrafit.repositories;

import com.grafit.projectGrafit.models.Exercise;
import com.grafit.projectGrafit.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad {@link Exercise}.
 * Proporciona métodos CRUD para acceder y gestionar ejercicios en la base de datos.
 */
@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByUserUsername(String username);
    boolean existsByNameIgnoreCaseAndUser(String name, User user);
    Optional<Exercise> findByNameAndUser(String name, User user);
}