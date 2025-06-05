package com.grafit.projectGrafit.services;

import com.grafit.projectGrafit.models.ExerciseHistory;
import com.grafit.projectGrafit.models.Training;
import com.grafit.projectGrafit.models.Routine;
import com.grafit.projectGrafit.models.CompletedRoutine;
import com.grafit.projectGrafit.repositories.ExerciseHistoryRepository;
import com.grafit.projectGrafit.repositories.TrainingRepository;
import com.grafit.projectGrafit.repositories.RoutineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class ExerciseHistoryService {

    private final ExerciseHistoryRepository exerciseHistoryRepository;
    private final TrainingRepository trainingRepository;
    private final RoutineRepository routineRepository;
    
    // Almacenamiento temporal de sets por training
    private final Map<Long, ExerciseHistory> temporarySetsByTraining = new ConcurrentHashMap<>();

    public ExerciseHistoryService(ExerciseHistoryRepository exerciseHistoryRepository,
                                 TrainingRepository trainingRepository,
                                 RoutineRepository routineRepository) {
        this.exerciseHistoryRepository = exerciseHistoryRepository;
        this.trainingRepository = trainingRepository;
        this.routineRepository = routineRepository;
    }

    public List<ExerciseHistory> getTodaysSets(Training training) {
        Long trainingId = training.getIdTraining();
        ExerciseHistory tempHistory = temporarySetsByTraining.get(trainingId);
        
        // Obtener los sets persistidos
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        List<ExerciseHistory> persistedHistories = exerciseHistoryRepository
            .findByTrainingAndDateBetweenOrderByDateDesc(training, startOfDay, endOfDay);
        
        // Si no hay sets temporales, devolver solo los persistidos
        if (tempHistory == null) {
            return persistedHistories;
        }
        
        // Si hay sets temporales, combinarlos con los persistidos
        // pero asegurándonos de que no haya duplicados y manteniendo el orden correcto
        ExerciseHistory combinedHistory = new ExerciseHistory();
        combinedHistory.setId(tempHistory.getId());
        combinedHistory.setTraining(training);
        combinedHistory.setDate(LocalDateTime.now());
        
        // Mapa para mantener un solo set por número
        Map<Integer, ExerciseHistory.ExerciseSet> setsByNumber = new HashMap<>();
        
        // Primero añadir los sets persistidos
        persistedHistories.forEach(history -> {
            history.getSets().forEach(set -> {
                if (!setsByNumber.containsKey(set.getSetNumber())) {
                    setsByNumber.put(set.getSetNumber(), set);
                }
            });
        });
        
        // Luego añadir o sobrescribir con los sets temporales
        tempHistory.getSets().forEach(set -> {
            setsByNumber.put(set.getSetNumber(), set);
        });
        
        // Convertir el mapa a lista ordenada por número de set
        List<ExerciseHistory.ExerciseSet> orderedSets = new ArrayList<>(setsByNumber.values());
        orderedSets.sort((s1, s2) -> Integer.compare(s1.getSetNumber(), s2.getSetNumber()));
        
        combinedHistory.setSets(orderedSets);
        
        return Collections.singletonList(combinedHistory);
    }

    public ExerciseHistory addSet(Long trainingId, Integer repetitions, Double weight, String timerValue, String note) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new IllegalArgumentException("Entrenamiento no encontrado"));
        
        // Si la rutina está marcada como completada, marcarla como no completada
        Routine routine = training.getRoutine();
        if (routine.isCompleted()) {
            routine.setCompleted(false);
            routine.setLastCompletedDate(null);
            routineRepository.save(routine);
        }

        // Obtener los sets persistidos del día actual
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        List<ExerciseHistory> persistedHistories = exerciseHistoryRepository
            .findByTrainingAndDateBetweenOrderByDateDesc(training, startOfDay, endOfDay);

        // Obtener o crear el historial temporal
        ExerciseHistory history = temporarySetsByTraining.computeIfAbsent(trainingId, k -> {
            ExerciseHistory newHistory = new ExerciseHistory();
            newHistory.setTraining(training);
            newHistory.setDate(LocalDateTime.now());
            newHistory.setId(-System.currentTimeMillis());
            return newHistory;
        });

        // Mapa para mantener un solo set por número
        Map<Integer, ExerciseHistory.ExerciseSet> setsByNumber = new HashMap<>();
        
        // Primero añadir los sets persistidos
        persistedHistories.forEach(h -> {
            h.getSets().forEach(set -> {
                if (!setsByNumber.containsKey(set.getSetNumber())) {
                    setsByNumber.put(set.getSetNumber(), set);
                }
            });
        });
        
        // Luego añadir los sets temporales existentes
        history.getSets().forEach(set -> {
            setsByNumber.put(set.getSetNumber(), set);
        });

        // Encontrar el primer número de set disponible
        int setNumberToAdd = 1;
        while (setNumberToAdd <= training.getSets()) {
            if (!setsByNumber.containsKey(setNumberToAdd)) {
                break;
            }
            setNumberToAdd++;
        }

        // Verificar si el número de set está dentro del rango permitido
        if (setNumberToAdd > training.getSets()) {
            throw new IllegalStateException("Ya has completado todos los sets programados para este ejercicio");
        }

        // Crear el nuevo set
        ExerciseHistory.ExerciseSet newSet = new ExerciseHistory.ExerciseSet(
            setNumberToAdd, repetitions, weight, timerValue);
        newSet.setNote(note);
        newSet.setSetId(-System.currentTimeMillis());
        
        // Añadir el nuevo set al mapa
        setsByNumber.put(setNumberToAdd, newSet);
        
        // Convertir el mapa a lista ordenada
        List<ExerciseHistory.ExerciseSet> orderedSets = new ArrayList<>(setsByNumber.values());
        orderedSets.sort((s1, s2) -> Integer.compare(s1.getSetNumber(), s2.getSetNumber()));
        
        // Actualizar los sets en el historial temporal
        history.getSets().clear();
        history.getSets().addAll(orderedSets);
        
        return history;
    }

    public void editSet(Long historyId, Long setId, Integer repetitions, Double weight, String timerValue, String note) {
        // Primero intentar editar en el almacenamiento temporal
        Optional<ExerciseHistory> tempHistoryOpt = temporarySetsByTraining.values().stream()
                .filter(h -> h.getSets().stream().anyMatch(s -> s.getSetId().equals(setId)))
                .findFirst();

        if (tempHistoryOpt.isPresent()) {
            // Editar set temporal
            ExerciseHistory history = tempHistoryOpt.get();
            updateSetValues(history, setId, repetitions, weight, timerValue, note);
        } else {
            // Buscar y editar en la base de datos
            ExerciseHistory history = exerciseHistoryRepository.findById(historyId)
                    .orElseThrow(() -> new IllegalArgumentException("Historial no encontrado"));
            updateSetValues(history, setId, repetitions, weight, timerValue, note);
            exerciseHistoryRepository.save(history);
            
            // Marcar la rutina como no completada
            Routine routine = history.getTraining().getRoutine();
            routine.setCompleted(false);
            routine.setLastCompletedDate(null);
            routineRepository.save(routine);
        }
    }

    private void updateSetValues(ExerciseHistory history, Long setId, Integer repetitions, Double weight, String timerValue, String note) {
        Optional<ExerciseHistory.ExerciseSet> setToEdit = history.getSets().stream()
                .filter(set -> set.getSetId().equals(setId))
                .findFirst();

        if (setToEdit.isPresent()) {
            ExerciseHistory.ExerciseSet set = setToEdit.get();
            set.setRepetitions(repetitions);
            set.setWeight(weight);
            set.setTimerValue(timerValue);
            set.setNote(note);
        } else {
            throw new IllegalArgumentException("Set no encontrado");
        }
    }

    public void deleteSet(Long historyId, Long setId) {
        // Si los IDs son negativos, estamos trabajando con datos temporales
        if (historyId < 0) {
            Optional<ExerciseHistory> tempHistoryOpt = temporarySetsByTraining.values().stream()
                    .filter(h -> h.getId().equals(historyId))
                    .findFirst();

            if (tempHistoryOpt.isPresent()) {
                ExerciseHistory history = tempHistoryOpt.get();
                
                // Obtener el set que se va a eliminar
                Optional<ExerciseHistory.ExerciseSet> setToDelete = history.getSets().stream()
                    .filter(set -> set.getSetId().equals(setId))
                    .findFirst();
                    
                if (!setToDelete.isPresent()) {
                    throw new IllegalArgumentException("Set temporal no encontrado");
                }
                
                // Eliminar el set
                history.getSets().remove(setToDelete.get());
                
                // Si no quedan sets temporales, eliminar el historial temporal
                if (history.getSets().isEmpty()) {
                    temporarySetsByTraining.remove(history.getTraining().getIdTraining());
                }
                
                return;
            }
        }

        // Si llegamos aquí, o el ID es positivo o no se encontró en temporales
        ExerciseHistory history = exerciseHistoryRepository.findById(Math.abs(historyId))
                .orElseThrow(() -> new IllegalArgumentException("Historial no encontrado"));
        
        // Obtener el set que se va a eliminar
        Optional<ExerciseHistory.ExerciseSet> setToDelete = history.getSets().stream()
            .filter(set -> set.getSetId().equals(setId))
            .findFirst();
            
        if (!setToDelete.isPresent()) {
            throw new IllegalArgumentException("Set no encontrado");
        }
        
        // Eliminar el set
        history.getSets().remove(setToDelete.get());
        
        // Si no quedan sets, eliminar el historial
        if (history.getSets().isEmpty()) {
            exerciseHistoryRepository.delete(history);
        } else {
            exerciseHistoryRepository.save(history);
        }

        // Obtener la rutina y marcarla como no completada
        Routine routine = history.getTraining().getRoutine();
        routine.setCompleted(false);
        routine.setLastCompletedDate(null);
        routineRepository.save(routine);
    }

    public void persistRoutineSets(Long routineId) {
        // Obtener todos los trainings de la rutina
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));
        
        // Persistir los sets temporales de cada training
        routine.getTrainings().forEach(training -> {
            ExerciseHistory tempHistory = temporarySetsByTraining.get(training.getIdTraining());
            if (tempHistory != null && !tempHistory.getSets().isEmpty()) {
                // Obtener los sets persistidos existentes del día actual
                LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
                LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
                List<ExerciseHistory> existingHistories = exerciseHistoryRepository
                    .findByTrainingAndDateBetweenOrderByDateDesc(training, startOfDay, endOfDay);

                // Mapa para mantener un solo set por número
                Map<Integer, ExerciseHistory.ExerciseSet> setsByNumber = new HashMap<>();
                
                // Primero añadir los sets persistidos existentes
                existingHistories.forEach(h -> {
                    h.getSets().forEach(set -> {
                        if (!setsByNumber.containsKey(set.getSetNumber())) {
                            setsByNumber.put(set.getSetNumber(), set);
                        }
                    });
                });
                
                // Luego sobrescribir con los sets temporales
                tempHistory.getSets().forEach(tempSet -> {
                    setsByNumber.put(tempSet.getSetNumber(), tempSet);
                });

                // Si hay historiales existentes, eliminarlos
                if (!existingHistories.isEmpty()) {
                    existingHistories.forEach(h -> exerciseHistoryRepository.delete(h));
                }

                // Crear un nuevo historial con todos los sets combinados
                ExerciseHistory newHistory = new ExerciseHistory();
                newHistory.setTraining(training);
                newHistory.setDate(LocalDateTime.now());
                
                // Convertir el mapa a lista ordenada
                List<ExerciseHistory.ExerciseSet> orderedSets = new ArrayList<>(setsByNumber.values());
                orderedSets.sort((s1, s2) -> Integer.compare(s1.getSetNumber(), s2.getSetNumber()));
                
                // Asignar nuevos IDs secuenciales a los sets
                for (int i = 0; i < orderedSets.size(); i++) {
                    ExerciseHistory.ExerciseSet originalSet = orderedSets.get(i);
                    ExerciseHistory.ExerciseSet newSet = new ExerciseHistory.ExerciseSet(
                        originalSet.getSetNumber(),
                        originalSet.getRepetitions(),
                        originalSet.getWeight(),
                        originalSet.getTimerValue()
                    );
                    newSet.setNote(originalSet.getNote());
                    newSet.setSetId((long)(i + 1));
                    orderedSets.set(i, newSet);
                }
                
                newHistory.setSets(orderedSets);
                
                // Guardar el nuevo historial
                exerciseHistoryRepository.save(newHistory);
                
                // Limpiar los sets temporales
                temporarySetsByTraining.remove(training.getIdTraining());
            }
        });
    }

    public void clearTemporarySets(Long routineId) {
        // Obtener todos los trainings de la rutina
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));
        
        // Limpiar los sets temporales de cada training
        routine.getTrainings().forEach(training -> {
            temporarySetsByTraining.remove(training.getIdTraining());
        });
    }

    public Map<String, Object> getExerciseProgress(Long exerciseId) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30); // Últimos 30 días
        LocalDateTime endDate = LocalDateTime.now();
        
        List<ExerciseHistory> history = exerciseHistoryRepository.findByTraining_Exercise_IdExerciseAndDateBetweenOrderByDateAsc(
            exerciseId, startDate, endDate);

        Map<String, List<Object>> progressBySet = new HashMap<>();
        progressBySet.put("dates", new ArrayList<>());
        progressBySet.put("weights", new ArrayList<>());
        progressBySet.put("repetitions", new ArrayList<>());
        progressBySet.put("setNumbers", new ArrayList<>());
        progressBySet.put("notes", new ArrayList<>());

        // Mostrar cada set individualmente
        history.forEach(h -> {
            String date = h.getDate().toLocalDate().toString();
            h.getSets().forEach(set -> {
                progressBySet.get("dates").add(date);
                progressBySet.get("weights").add(set.getWeight());
                progressBySet.get("repetitions").add(set.getRepetitions());
                progressBySet.get("setNumbers").add(set.getSetNumber());
                progressBySet.get("notes").add(set.getNote());
            });
        });

        Map<String, Object> result = new HashMap<>();
        result.put("progress", progressBySet);
        return result;
    }

    public Map<String, Object> getRoutineProgress(Long routineId) {
        System.out.println("Obteniendo progreso para la rutina: " + routineId);
        Routine routine = routineRepository.findWithTrainingsByIdRoutine(routineId)
            .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));

        System.out.println("Rutina encontrada: " + routine.getName());
        System.out.println("Número de entrenamientos: " + routine.getTrainings().size());

        Map<String, Object> progress = new HashMap<>();
        List<Map<String, Object>> exercisesProgress = new ArrayList<>();

        routine.getTrainings().forEach(training -> {
            try {
                System.out.println("Procesando entrenamiento: " + training.getIdTraining());
                Long exerciseId = training.getExercise().getIdExercise();
                System.out.println("ID del ejercicio: " + exerciseId);
                Map<String, Object> exerciseProgress = getExerciseProgress(exerciseId);
                exerciseProgress.put("exerciseName", training.getExercise().getName());
                exercisesProgress.add(exerciseProgress);
            } catch (Exception e) {
                System.err.println("Error al procesar entrenamiento: " + e.getMessage());
                e.printStackTrace();
            }
        });

        progress.put("routineName", routine.getName());
        progress.put("exercises", exercisesProgress);

        return progress;
    }

    public List<CompletedRoutine> getCompletedRoutinesBetweenDates(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        // Obtener el nombre de usuario autenticado
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Mapa para almacenar las rutinas completadas por fecha
        Map<String, CompletedRoutine> routinesByDateAndId = new HashMap<>();
        
        // Obtener solo las rutinas del usuario actual
        List<Routine> userRoutines = routineRepository.findByUserUsername(username);
        
        for (Routine routine : userRoutines) {
            for (Training training : routine.getTrainings()) {
                List<ExerciseHistory> histories = exerciseHistoryRepository
                    .findByTrainingAndDateBetweenOrderByDateDesc(training, startDateTime, endDateTime);
                
                if (!histories.isEmpty()) {
                    histories.forEach(history -> {
                        LocalDate historyDate = history.getDate().toLocalDate();
                        // Clave única para la rutina: ID_RUTINA + FECHA
                        String routineKey = routine.getIdRoutine() + "_" + historyDate;
                        
                        CompletedRoutine completedRoutine = routinesByDateAndId.computeIfAbsent(routineKey, k -> {
                            CompletedRoutine cr = new CompletedRoutine();
                            cr.setName(routine.getName());
                            cr.setCompletionDate(historyDate.atStartOfDay());
                            cr.setDayOfWeek(historyDate.getDayOfWeek()
                                .getDisplayName(TextStyle.FULL, new Locale("es", "ES")));
                            return cr;
                        });
                        
                        // Buscar o crear el training correspondiente
                        CompletedRoutine.CompletedTraining completedTraining = completedRoutine.getTrainings().stream()
                            .filter(t -> t.getExercise().getIdExercise().equals(training.getExercise().getIdExercise()))
                            .findFirst()
                            .orElseGet(() -> {
                                CompletedRoutine.CompletedTraining ct = new CompletedRoutine.CompletedTraining();
                                ct.setExercise(training.getExercise());
                                completedRoutine.getTrainings().add(ct);
                                return ct;
                            });
                        
                        // Añadir los sets al training
                        history.getSets().forEach(set -> {
                            CompletedRoutine.CompletedSet completedSet = new CompletedRoutine.CompletedSet();
                            completedSet.setSetNumber(set.getSetNumber());
                            completedSet.setRepetitions(set.getRepetitions());
                            completedSet.setWeight(set.getWeight());
                            completedSet.setTimerValue(set.getTimerValue());
                            completedSet.setNote(set.getNote());
                            completedTraining.getCompletedSets().add(completedSet);
                        });
                    });
                }
            }
        }
        
        // Convertir el mapa a lista y ordenar
        List<CompletedRoutine> completedRoutines = new ArrayList<>(routinesByDateAndId.values());
        completedRoutines.sort((r1, r2) -> r2.getCompletionDate().compareTo(r1.getCompletionDate()));
        
        return completedRoutines;
    }

    public void deleteHistoryForTraining(Training training) {
        // Lista de nombres de ejercicios básicos de powerlifting
        List<String> basicPowerliftingExercises = Arrays.asList(
            "PRESS DE BANCA",
            "SENTADILLA HIGH-BAR",
            "SENTADILLA LOW-BAR",
            "PESO MUERTO CONVENCIONAL",
            "PESO MUERTO SUMO"
        );

        // Si es un ejercicio básico, no eliminamos su historial
        if (basicPowerliftingExercises.contains(training.getExercise().getName())) {
            return;
        }

        // Si no es un ejercicio básico, eliminamos todo su historial
        List<ExerciseHistory> histories = exerciseHistoryRepository.findByTraining(training);
        exerciseHistoryRepository.deleteAll(histories);
    }
} 