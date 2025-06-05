package com.grafit.projectGrafit.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.grafit.projectGrafit.repositories.ExerciseHistoryRepository;
import com.grafit.projectGrafit.repositories.ExerciseRepository;
import com.grafit.projectGrafit.models.Exercise;
import com.grafit.projectGrafit.models.ExerciseHistory;
import com.grafit.projectGrafit.models.User;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class PersonalRecordService {

    private final ExerciseHistoryRepository exerciseHistoryRepository;
    private final ExerciseRepository exerciseRepository;

    public PersonalRecordService(ExerciseHistoryRepository exerciseHistoryRepository,
                               ExerciseRepository exerciseRepository) {
        this.exerciseHistoryRepository = exerciseHistoryRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public record WeightRecord(
        double weight,
        int maxReps
    ) {}

    public record PersonalRecord(
        List<WeightRecord> prRecords,
        double oneRepMax
    ) {
        // Constructor para crear un PR vacío
        public static PersonalRecord empty() {
            return new PersonalRecord(new ArrayList<>(), 0.0);
        }

        // Método para obtener el PR más pesado
        public WeightRecord getHeaviestPR() {
            return prRecords.stream()
                .max(Comparator.comparingDouble(WeightRecord::weight))
                .orElse(new WeightRecord(0.0, 0));
        }

        // Método para obtener los 3 PRs más pesados
        public List<WeightRecord> getTopThreeWeights() {
            return prRecords.stream()
                .sorted(Comparator.comparingDouble(WeightRecord::weight).reversed())
                .limit(3)
                .toList();
        }
    }

    public Map<String, PersonalRecord> getPersonalRecords(User user) {
        Map<String, PersonalRecord> records = new HashMap<>();
        List<String> basicExercises = Arrays.asList(
            "PRESS DE BANCA",
            "SENTADILLA HIGH-BAR",
            "SENTADILLA LOW-BAR",
            "PESO MUERTO CONVENCIONAL",
            "PESO MUERTO SUMO"
        );

        for (String exerciseName : basicExercises) {
            Optional<Exercise> exercise = exerciseRepository.findByNameAndUser(exerciseName, user);
            if (exercise.isPresent()) {
                List<ExerciseHistory> history = exerciseHistoryRepository
                    .findByTraining_Exercise_IdExercise(exercise.get().getIdExercise());

                PersonalRecord record = calculateRecords(history);
                
                // Agrupar los diferentes tipos de sentadilla y peso muerto
                String key = switch (exerciseName) {
                    case "SENTADILLA HIGH-BAR", "SENTADILLA LOW-BAR" -> "SENTADILLA";
                    case "PESO MUERTO CONVENCIONAL", "PESO MUERTO SUMO" -> "PESO MUERTO";
                    default -> exerciseName;
                };

                // Actualizar el récord solo si es mayor que el existente
                records.merge(key, record, (existing, newRecord) -> {
                    // Combinar los PRs de ambos records
                    Map<Double, Integer> weightToMaxReps = new HashMap<>();
                    
                    // Procesar los PRs existentes
                    existing.prRecords().forEach(pr -> 
                        weightToMaxReps.merge(pr.weight(), pr.maxReps(), Math::max));
                    
                    // Procesar los nuevos PRs
                    newRecord.prRecords().forEach(pr -> 
                        weightToMaxReps.merge(pr.weight(), pr.maxReps(), Math::max));
                    
                    // Convertir el mapa a lista de WeightRecord
                    List<WeightRecord> combinedRecords = weightToMaxReps.entrySet().stream()
                        .map(entry -> new WeightRecord(entry.getKey(), entry.getValue()))
                        .sorted(Comparator.comparingDouble(WeightRecord::weight).reversed())
                        .collect(java.util.stream.Collectors.toList());
                    
                    // Mantener el 1RM más alto
                    double maxOneRepMax = Math.max(existing.oneRepMax(), newRecord.oneRepMax());
                    
                    return new PersonalRecord(combinedRecords, maxOneRepMax);
                });
            }
        }

        return records;
    }

    private PersonalRecord calculateRecords(List<ExerciseHistory> history) {
        if (history == null || history.isEmpty()) {
            return PersonalRecord.empty();
        }

        Map<Double, Integer> weightToMaxReps = new HashMap<>();
        double oneRepMax = 0;

        for (ExerciseHistory eh : history) {
            if (eh.getSets() == null || eh.getSets().isEmpty()) {
                continue;
            }

            for (ExerciseHistory.ExerciseSet set : eh.getSets()) {
                // Actualizar el máximo de repeticiones para cada peso
                weightToMaxReps.merge(set.getWeight(), set.getRepetitions(), Math::max);

                // Actualizar el 1RM (peso máximo con una repetición)
                if (set.getRepetitions() == 1 && set.getWeight() > oneRepMax) {
                    oneRepMax = set.getWeight();
                }
            }
        }

        // Convertir el mapa a lista de WeightRecord
        List<WeightRecord> prRecords = weightToMaxReps.entrySet().stream()
            .map(entry -> new WeightRecord(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparingDouble(WeightRecord::weight).reversed())
            .collect(java.util.stream.Collectors.toList());

        return new PersonalRecord(prRecords, oneRepMax);
    }
} 