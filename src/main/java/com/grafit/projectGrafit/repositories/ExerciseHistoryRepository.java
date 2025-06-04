package com.grafit.projectGrafit.repositories;

import com.grafit.projectGrafit.models.ExerciseHistory;
import com.grafit.projectGrafit.models.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExerciseHistoryRepository extends JpaRepository<ExerciseHistory, Long> {
    List<ExerciseHistory> findByTrainingAndDateBetweenOrderByDateDesc(
        Training training, 
        LocalDateTime startOfDay, 
        LocalDateTime endOfDay
    );

    List<ExerciseHistory> findByTraining_Exercise_IdExerciseAndDateBetweenOrderByDateAsc(
        Long exerciseId,
        LocalDateTime startDate,
        LocalDateTime endDate
    );

    List<ExerciseHistory> findByTraining_Exercise_IdExercise(Long exerciseId);

    List<ExerciseHistory> findByTraining(Training training);
} 