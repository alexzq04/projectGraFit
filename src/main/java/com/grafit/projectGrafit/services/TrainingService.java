package com.grafit.projectGrafit.services;

import com.grafit.projectGrafit.models.Routine;
import com.grafit.projectGrafit.models.Training;
import com.grafit.projectGrafit.models.Exercise;
import com.grafit.projectGrafit.repositories.RoutineRepository;
import com.grafit.projectGrafit.repositories.TrainingRepository;
import com.grafit.projectGrafit.repositories.ExerciseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;

@Service
@Transactional
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;
    private final RoutineService routineService;

    public TrainingService(TrainingRepository trainingRepository,
                           RoutineRepository routineRepository,
                           ExerciseRepository exerciseRepository,
                           RoutineService routineService) {
        this.trainingRepository = trainingRepository;
        this.routineRepository = routineRepository;
        this.exerciseRepository = exerciseRepository;
        this.routineService = routineService;
    }

    public void addExerciseToRoutine(Long routineId, Long exerciseId, Integer totalSets,
                                   Integer[] repetitions, Double[] weights) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada con id: " + routineId));
        routineService.verifyOwnership(routine);

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Ejercicio no encontrado con id: " + exerciseId));

        // Verificar que los arrays tengan el tamaño correcto
        if (repetitions.length != totalSets || weights.length != totalSets) {
            throw new IllegalArgumentException("El número de repeticiones y pesos debe coincidir con el número de series");
        }

        Training training = new Training();
        training.setRoutine(routine);
        training.setExercise(exercise);
        training.setSets(totalSets);

        // Inicializar las configuraciones de los sets
        for (int i = 0; i < totalSets; i++) {
            Training.SetConfiguration setConfig = new Training.SetConfiguration(
                i + 1,  // setNumber es 1-based
                repetitions[i],
                weights[i]
            );
            training.getSetConfigurations().add(setConfig);
        }

        trainingRepository.save(training);
    }

    public void updateTraining(Long trainingId, Integer sets, Integer[] repetitions, Double[] weights) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new IllegalArgumentException("Entrenamiento no encontrado con id: " + trainingId));

        Routine routine = training.getRoutine();
        routineService.verifyOwnership(routine);

        // Validar que los arrays tengan el tamaño correcto
        if (repetitions.length != sets || weights.length != sets) {
            throw new IllegalArgumentException("El número de repeticiones y pesos debe coincidir con el número de series");
        }

        // Actualizar el número total de sets
        training.setSets(sets);

        // Limpiar las configuraciones existentes
        training.getSetConfigurations().clear();

        // Crear nuevas configuraciones de sets
        for (int i = 0; i < sets; i++) {
            Training.SetConfiguration setConfig = new Training.SetConfiguration(
                i + 1,  // setNumber es 1-based
                repetitions[i],
                weights[i]
            );
            training.getSetConfigurations().add(setConfig);
        }

        trainingRepository.save(training);
    }

    public void deleteTraining(Long routineId, Long trainingId) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada con id: " + routineId));
        routineService.verifyOwnership(routine);

        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new IllegalArgumentException("Entrenamiento no encontrado con id: " + trainingId));

        if (!training.getRoutine().equals(routine)) {
            throw new IllegalArgumentException("El entrenamiento no pertenece a la rutina especificada");
        }

        // Lista de nombres de ejercicios básicos
        List<String> basicExercises = Arrays.asList(
            "PRESS DE BANCA",
            "SENTADILLA HIGH-BAR",
            "SENTADILLA LOW-BAR",
            "PESO MUERTO CONVENCIONAL",
            "PESO MUERTO SUMO"
        );

        String exerciseName = training.getExercise().getName();
        if (basicExercises.contains(exerciseName)) {
            // Para ejercicios básicos, solo desasociamos el training de la rutina
            routine.removeTraining(training);
            training.setRoutine(null);
            trainingRepository.save(training);
        } else {
            // Para ejercicios no básicos, eliminamos completamente el training
            routine.removeTraining(training);
            trainingRepository.delete(training);
        }
    }

    public Optional<Training> findById(Long id) {
        return trainingRepository.findById(id);
    }

}
