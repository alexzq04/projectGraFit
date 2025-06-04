package com.grafit.projectGrafit.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import org.springframework.transaction.annotation.Transactional;

import com.grafit.projectGrafit.models.Exercise;
import com.grafit.projectGrafit.models.User;
import com.grafit.projectGrafit.repositories.UserRepository;
import com.grafit.projectGrafit.repositories.ExerciseRepository;

@Service
public class ExerciseService {

    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;

    private final List<String> BASIC_POWERLIFTING_EXERCISES = Arrays.asList(
        "PRESS DE BANCA",
        "SENTADILLA HIGH-BAR",
        "SENTADILLA LOW-BAR",
        "PESO MUERTO CONVENCIONAL",
        "PESO MUERTO SUMO"
    );

    public ExerciseService(UserRepository userRepository, ExerciseRepository exerciseRepository) {
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public Exercise createExercise(String name, String description, String muscleGroup) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        // Verificar si ya existe un ejercicio con el mismo nombre para este usuario
        if (exerciseRepository.existsByNameIgnoreCaseAndUser(name, user)) {
            throw new IllegalArgumentException("Ya existe un ejercicio con ese nombre.");
        }

        Exercise exercise = new Exercise();
        exercise.setName(name != null ? name.toUpperCase() : null);
        exercise.setDescription(description != null ? description.toUpperCase() : null);
        exercise.setMuscleGroup(muscleGroup != null ? muscleGroup.toUpperCase() : null);
        exercise.setUser(user);
        exercise.setCreationDate(LocalDate.now()); // <-- aquí asignas la fecha actual

        return exerciseRepository.save(exercise);
    }

    public Exercise createExerciseForUser(String name, String description, String muscleGroup, User user) {
        // Verificar si ya existe un ejercicio con el mismo nombre para este usuario
        if (exerciseRepository.existsByNameIgnoreCaseAndUser(name, user)) {
            throw new IllegalArgumentException("Ya existe un ejercicio con ese nombre.");
        }

        Exercise exercise = new Exercise();
        exercise.setName(name != null ? name.toUpperCase() : null);
        exercise.setDescription(description != null ? description.toUpperCase() : null);
        exercise.setMuscleGroup(muscleGroup != null ? muscleGroup.toUpperCase() : null);
        exercise.setUser(user);
        exercise.setCreationDate(LocalDate.now());

        return exerciseRepository.save(exercise);
    }

    public List<Exercise> getExercisesForCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return exerciseRepository.findByUserUsername(username);
    }

    public Optional<Exercise> findById(Long id) {
        return exerciseRepository.findById(id);
    }

    public void deleteById(Long id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ejercicio no encontrado: " + id));
        
        if (BASIC_POWERLIFTING_EXERCISES.contains(exercise.getName())) {
            throw new IllegalStateException("No se puede eliminar un ejercicio básico de powerlifting");
        }
        
        exerciseRepository.delete(exercise);
    }

    @Transactional
    public void updateExercise(Long id, String name, String description, String muscleGroup) {
        Exercise ex = exerciseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ejercicio no encontrado: " + id));
        ex.setName(name.toUpperCase());
        ex.setDescription(description != null ? description.toUpperCase() : null);
        ex.setMuscleGroup(muscleGroup != null ? muscleGroup.toUpperCase() : null);
        exerciseRepository.save(ex);
    }

    /**
     * Crea los ejercicios básicos para un nuevo usuario.
     *
     * @param user el usuario para el que se crearán los ejercicios
     */
    public void createBasicExercisesForUser(User user) {
        // Lista de ejercicios básicos con sus descripciones y grupos musculares
        String[][] basicExercises = {
            {"PRESS DE BANCA", "", "PECHO"},
            {"SENTADILLA HIGH-BAR", "", "PIERNAS"},
            {"SENTADILLA LOW-BAR", "", "PIERNAS"},
            {"PESO MUERTO CONVENCIONAL", "", "ESPALDA"},
            {"PESO MUERTO SUMO", "", "PIERNAS"}
        };

        for (String[] exerciseData : basicExercises) {
            try {
                createExerciseForUser(exerciseData[0], exerciseData[1], exerciseData[2], user);
            } catch (Exception e) {
                // Log error but continue with other exercises
                System.err.println("Error creating exercise " + exerciseData[0] + ": " + e.getMessage());
            }
        }
    }
}
