package com.grafit.projectGrafit.repositories;

import com.grafit.projectGrafit.models.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link Training}.
 * Proporciona métodos CRUD para acceder y gestionar entrenamientos en la base de datos.
 */
@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
}