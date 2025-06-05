package com.grafit.projectGrafit.repositories;
import com.grafit.projectGrafit.models.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {
    
    List<Routine> findByUserUsername(String username);
    List<Routine> findByUserUsernameAndDayOfWeek(String username, String dayOfWeek);
    List<Routine> findByUserUsernameAndDayOfWeekIgnoreCase(String username, String dayOfWeek);
    
    @Query("SELECT r FROM Routine r LEFT JOIN FETCH r.trainings t LEFT JOIN FETCH t.exercise WHERE r.idRoutine = :idRoutine")
    Optional<Routine> findWithTrainingsByIdRoutine(Long idRoutine);
}
