package com.grafit.projectGrafit.services;

import com.grafit.projectGrafit.models.Routine;
import com.grafit.projectGrafit.models.User;
import com.grafit.projectGrafit.models.Exercise;
import com.grafit.projectGrafit.models.Training;
import com.grafit.projectGrafit.repositories.RoutineRepository;
import com.grafit.projectGrafit.repositories.TrainingRepository;
import com.grafit.projectGrafit.repositories.ExerciseRepository;
import com.grafit.projectGrafit.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Arrays;
import java.util.ArrayList;

@Service
@Transactional
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;
    private final ExerciseHistoryService exerciseHistoryService;

    public RoutineService(RoutineRepository routineRepository,
                          TrainingRepository trainingRepository,
                          ExerciseRepository exerciseRepository,
                          UserRepository userRepository,
                          ExerciseHistoryService exerciseHistoryService) {
        this.routineRepository = routineRepository;
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
        this.exerciseHistoryService = exerciseHistoryService;
    }

    public Routine createRoutine(String name, String description, String dayOfWeek) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username).orElseThrow();

        System.out.println("Creando rutina con día de la semana: " + dayOfWeek);
        String normalizedDayOfWeek = normalize(dayOfWeek);
        System.out.println("Día de la semana normalizado: " + normalizedDayOfWeek);

        Routine routine = new Routine();
        routine.setName(normalize(name));
        routine.setDescription(normalize(description));
        routine.setDayOfWeek(normalizedDayOfWeek);
        routine.setCreationDate(LocalDate.now());
        routine.setUser(user);
        routine.setCompleted(false);
        routine.setLastCompletedDate(null);

        Routine saved = routineRepository.save(routine);
        System.out.println("Rutina guardada con día de la semana: " + saved.getDayOfWeek());
        return saved;
    }

    public List<Routine> getAllRoutines() {
        return routineRepository.findAll();
    }

    public List<Routine> getRoutinesByDayOfWeek(String dayOfWeek) {
        String username = getCurrentUsername();
        return routineRepository.findByUserUsernameAndDayOfWeekIgnoreCase(username, dayOfWeek);
    }

    public Optional<Routine> getRoutineById(Long id) {
        try {
            return routineRepository.findWithTrainingsByIdRoutine(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar la rutina: " + e.getMessage(), e);
        }
    }

    public void updateRoutine(Long id, String name, String description, String dayOfWeek) {
        Routine routine = routineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));

        verifyOwnership(routine);

        routine.setName(normalize(name));
        routine.setDescription(normalize(description));
        routine.setDayOfWeek(normalize(dayOfWeek));

        routineRepository.save(routine);
    }

    public List<Routine> getRoutinesForCurrentUser() {
        String username = getCurrentUsername();
        System.out.println("Obteniendo rutinas para el usuario: " + username);
        List<Routine> routines = routineRepository.findByUserUsername(username);
        System.out.println("Rutinas encontradas: " + routines.size());
        return routines;
    }

    public List<Routine> getTodaysRoutines() {
        String username = getCurrentUsername();
        String today = java.time.LocalDate.now()
            .getDayOfWeek()
            .getDisplayName(TextStyle.FULL, new Locale("es", "ES"))
            .toUpperCase();
        
        System.out.println("Buscando rutinas para el día: " + today);
        System.out.println("Formato del día actual: " + today.getBytes());
        
        List<Routine> routines = routineRepository.findByUserUsernameAndDayOfWeek(username, today);
        System.out.println("Rutinas encontradas: " + routines.size());
        
        // Imprimir todas las rutinas del usuario para depuración
        List<Routine> allRoutines = routineRepository.findByUserUsername(username);
        System.out.println("Todas las rutinas del usuario:");
        allRoutines.forEach(r -> System.out.println("Rutina: " + r.getName() + 
                                                   ", día: '" + r.getDayOfWeek() + "'" +
                                                   ", bytes del día: " + r.getDayOfWeek().getBytes()));
        
        routines.forEach(r -> System.out.println("Rutina encontrada para hoy: " + r.getName() + ", día: " + r.getDayOfWeek()));
        
        LocalDate now = LocalDate.now();
        
        // Resetear el estado 'completed' si la última fecha de completación no es hoy
        for (Routine routine : routines) {
            if (routine.isCompleted() && 
                (routine.getLastCompletedDate() == null || 
                !now.equals(routine.getLastCompletedDate()))) {
                routine.setCompleted(false);
                routine.setLastCompletedDate(null);
                routineRepository.save(routine);
            }
        }
        
        return routines;
    }

    public Optional<Routine> findById(Long id) {
        return routineRepository.findById(id);
    }

    public void deleteById(Long id) {
        Routine routine = routineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + id));
        verifyOwnership(routine);

        // Lista de nombres de ejercicios básicos de powerlifting
        List<String> basicPowerliftingExercises = Arrays.asList(
            "PRESS DE BANCA",
            "SENTADILLA HIGH-BAR",
            "SENTADILLA LOW-BAR",
            "PESO MUERTO CONVENCIONAL",
            "PESO MUERTO SUMO"
        );

        // Separar los trainings en dos grupos: básicos y no básicos
        List<Training> basicTrainings = new ArrayList<>();
        List<Training> nonBasicTrainings = new ArrayList<>();

        for (Training training : routine.getTrainings()) {
            String exerciseName = training.getExercise().getName();
            if (basicPowerliftingExercises.contains(exerciseName)) {
                basicTrainings.add(training);
            } else {
                nonBasicTrainings.add(training);
            }
        }

        // Limpiar cualquier set temporal antes de eliminar
        exerciseHistoryService.clearTemporarySets(id);

        // Para los trainings no básicos, eliminar tanto el training como su historial
        nonBasicTrainings.forEach(training -> {
            exerciseHistoryService.deleteHistoryForTraining(training);
            routine.removeTraining(training);
        });

        // Para los trainings básicos de powerlifting, solo desasociarlos de la rutina
        // pero mantener tanto el training como su historial
        basicTrainings.forEach(training -> {
            training.setRoutine(null);
            routine.getTrainings().remove(training);
        });

        routineRepository.delete(routine);
    }

    // === Helpers ===

    private String normalize(String input) {
        return input != null ? input.toUpperCase().trim() : null;
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public void verifyOwnership(Routine routine) {
        String username = getCurrentUsername();
        if (!routine.getUser().getUsername().equals(username)) {
            throw new SecurityException("No tienes permiso para modificar esta rutina");
        }
    }

    public List<Exercise> getAllExercisesForCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return exerciseRepository.findByUserUsername(username);
    }

    public void completeRoutine(Long routineId) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));
        verifyOwnership(routine);

        // Si la rutina ya fue completada hoy, no hacer nada
        if (routine.isCompletedToday()) {
            return;
        }

        // Persistir los sets temporales antes de marcar la rutina como completada
        exerciseHistoryService.persistRoutineSets(routineId);

        // Marcar la rutina como completada y actualizar la fecha
        LocalDate now = LocalDate.now();
        routine.setCompleted(true);
        routine.setLastCompletedDate(now);

        // Guardar los cambios
        routineRepository.save(routine);

        // Log para verificar el estado después de guardar
        System.out.println("Rutina " + routineId + " completada: " +
                         "completed=" + routine.isCompleted() + ", " +
                         "lastCompletedDate=" + routine.getLastCompletedDate());
    }

}
