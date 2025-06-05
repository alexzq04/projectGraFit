package com.grafit.projectGrafit.controllers;

import com.grafit.projectGrafit.services.ExerciseHistoryService;
import com.grafit.projectGrafit.services.ExerciseService;
import com.grafit.projectGrafit.models.Exercise;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import com.grafit.projectGrafit.models.CompletedRoutine;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/statistics")
public class StatisticsController {

    private final ExerciseHistoryService exerciseHistoryService;
    private final ExerciseService exerciseService;

    public StatisticsController(ExerciseHistoryService exerciseHistoryService,
                              ExerciseService exerciseService) {
        this.exerciseHistoryService = exerciseHistoryService;
        this.exerciseService = exerciseService;
    }

    @GetMapping
    public String showStatistics(Model model) {
        List<Exercise> exercises = exerciseService.getExercisesForCurrentUser();
        model.addAttribute("exercises", exercises);
        return "statistics/statistics";
    }

    @GetMapping("/exercise/{exerciseId}/progress")
    @ResponseBody
    public Map<String, Object> getExerciseProgress(@PathVariable Long exerciseId) {
        return exerciseHistoryService.getExerciseProgress(exerciseId);
    }

    @GetMapping("/summary")
    public String showSummary(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String dayOfWeek,
            Model model) {
        
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
        
        List<CompletedRoutine> completedRoutines = exerciseHistoryService.getCompletedRoutinesBetweenDates(start, end);
        
        // Filtrar por día de la semana si se especifica
        if (dayOfWeek != null && !dayOfWeek.isEmpty()) {
            completedRoutines = completedRoutines.stream()
                .filter(routine -> routine.getDayOfWeek().equalsIgnoreCase(dayOfWeek))
                .collect(Collectors.toList());
        }
        
        model.addAttribute("startDate", start.toString());
        model.addAttribute("endDate", end.toString());
        model.addAttribute("dayOfWeek", dayOfWeek);
        model.addAttribute("completedRoutines", completedRoutines);
        
        return "statistics/summary";
    }
} 