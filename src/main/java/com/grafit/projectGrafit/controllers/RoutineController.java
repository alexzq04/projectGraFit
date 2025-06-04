package com.grafit.projectGrafit.controllers;

import com.grafit.projectGrafit.models.Routine;
import com.grafit.projectGrafit.models.Training;
import com.grafit.projectGrafit.services.ExerciseService;
import com.grafit.projectGrafit.services.RoutineService;
import com.grafit.projectGrafit.services.TrainingService;
import com.grafit.projectGrafit.services.ExerciseHistoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Controller
@RequestMapping("/routines")
public class RoutineController {

    private final RoutineService routineService;
    private final ExerciseService exerciseService;
    private final TrainingService trainingService;
    private final ExerciseHistoryService exerciseHistoryService;

    public RoutineController(RoutineService routineService,
            ExerciseService exerciseService,
            TrainingService trainingService,
            ExerciseHistoryService exerciseHistoryService) {
        this.routineService = routineService;
        this.exerciseService = exerciseService;
        this.trainingService = trainingService;
        this.exerciseHistoryService = exerciseHistoryService;
    }

    @GetMapping
    public String showFormAndList(
            Model model,
            @RequestParam(required = false) Long edit,
            @RequestParam(required = false) String successMessage,
            @RequestParam(required = false) String errorMessage,
            @RequestParam(required = false) Long lastExerciseId) {
        try {
            Routine toEdit = null;
            if (edit != null) {
                toEdit = routineService.getRoutineById(edit)
                        .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));
                // Forzar la carga de las relaciones
                toEdit.getTrainings().forEach(training -> {
                    if (training.getExercise() != null) {
                        training.getExercise().getName();
                    }
                });
            } else {
                toEdit = new Routine();
            }

            model.addAttribute("routine", toEdit);
            model.addAttribute("routines", routineService.getRoutinesForCurrentUser());
            model.addAttribute("exercises", exerciseService.getExercisesForCurrentUser());
            model.addAttribute("lastExerciseId", lastExerciseId);

            if (successMessage != null)
                model.addAttribute("successMessage", successMessage);
            if (errorMessage != null)
                model.addAttribute("errorMessage", errorMessage);

            return "routine/routinesView";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al cargar la rutina: " + e.getMessage());
            return "routine/routinesView";
        }
    }

    @PostMapping
    public String saveOrUpdate(@ModelAttribute Routine routine) {
        boolean isNew = (routine.getIdRoutine() == null);

        if (isNew) {
            routineService.createRoutine(
                    routine.getName(),
                    routine.getDescription(),
                    routine.getDayOfWeek());
            String msg = URLEncoder.encode("Rutina creada correctamente.", StandardCharsets.UTF_8);
            return "redirect:/routines?successMessage=" + msg;
        } else {
            routineService.updateRoutine(
                    routine.getIdRoutine(),
                    routine.getName(),
                    routine.getDescription(),
                    routine.getDayOfWeek());
            String msg = URLEncoder.encode("Rutina actualizada correctamente.", StandardCharsets.UTF_8);
            return "redirect:/routines?edit=" + routine.getIdRoutine() + "&successMessage=" + msg;
        }
    }

    @PostMapping("/{routineId}/exercises/add")
    public String addExerciseToRoutine(
            @PathVariable Long routineId,
            @RequestParam Long exerciseId,
            @RequestParam Integer numberOfSets,
            @RequestParam("repetitions[]") Integer[] repetitions,
            @RequestParam("weights[]") Double[] weights,
            RedirectAttributes redirectAttributes) {

        try {

            Optional<Routine> routineOpt = routineService.getRoutineById(routineId);
            if (!routineOpt.isPresent()) {
                throw new IllegalArgumentException("Rutina no encontrada");
            }

            Routine routine = routineOpt.get();

            boolean exerciseExists = routine.getTrainings().stream()
                    .anyMatch(t -> t.getExercise().getIdExercise().equals(exerciseId));

            if (exerciseExists) {

                redirectAttributes.addAttribute("successMessage",
                        "Ejercicio añadido. Nota: Este ejercicio ya existía en la rutina.");
            }

            if (repetitions.length != numberOfSets || weights.length != numberOfSets) {
                throw new IllegalArgumentException(
                        "El número de repeticiones y pesos debe coincidir con el número de series");
            }

            trainingService.addExerciseToRoutine(
                    routineId,
                    exerciseId,
                    numberOfSets,
                    repetitions,
                    weights);

            if (!exerciseExists) {
                redirectAttributes.addAttribute("successMessage", "Ejercicio añadido correctamente.");
            }
            redirectAttributes.addAttribute("lastExerciseId", exerciseId);
            return "redirect:/routines?edit=" + routineId;

        } catch (IllegalArgumentException e) {
            String msg = URLEncoder.encode("Error: " + e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/routines?edit=" + routineId + "&errorMessage=" + msg;
        } catch (Exception e) {
            String msg = URLEncoder.encode("Error inesperado al añadir el ejercicio: " + e.getMessage(),
                    StandardCharsets.UTF_8);
            return "redirect:/routines?edit=" + routineId + "&errorMessage=" + msg;
        }
    }

    @PostMapping("/{routineId}/exercises/{trainingId}/delete")
    public String removeExerciseFromRoutine(
            @PathVariable Long routineId,
            @PathVariable Long trainingId,
            RedirectAttributes redirectAttributes) {
        trainingService.deleteTraining(routineId, trainingId);
        redirectAttributes.addAttribute("successMessage", "Ejercicio eliminado de la rutina.");
        return "redirect:/routines?edit=" + routineId;
    }

    @GetMapping("/{routineId}/exercises/{trainingId}/edit")
    public String showEditExerciseForm(
            @PathVariable Long routineId,
            @PathVariable Long trainingId,
            Model model) {

        Optional<Training> trainingOpt = trainingService.findById(trainingId);
        if (trainingOpt.isEmpty() || !trainingOpt.get().getRoutine().getIdRoutine().equals(routineId)) {
            return "redirect:/routines?errorMessage=Ejercicio+no+encontrado+en+la+rutina";
        }

        model.addAttribute("training", trainingOpt.get());
        model.addAttribute("exercises", exerciseService.getExercisesForCurrentUser());
        model.addAttribute("routineId", routineId);
        return "routine/editTraining";
    }

    @PostMapping("/{routineId}/exercises/{trainingId}/edit")
    public String updateTraining(
            @PathVariable Long routineId,
            @PathVariable Long trainingId,
            @RequestParam Integer sets,
            @RequestParam("setConfigurations[].repetitions") Integer[] repetitions,
            @RequestParam("setConfigurations[].weight") Double[] weights,
            RedirectAttributes redirectAttributes) {
        try {
            trainingService.updateTraining(trainingId, sets, repetitions, weights);
            redirectAttributes.addFlashAttribute("successMessage", "Ejercicio actualizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el ejercicio: " + e.getMessage());
        }
        return "redirect:/routines?edit=" + routineId;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        routineService.deleteById(id);
        String msg = URLEncoder.encode("Rutina eliminada correctamente.", StandardCharsets.UTF_8);
        return "redirect:/routines?successMessage=" + msg;
    }

    @PostMapping("/complete/{id}")
    public String completeRoutine(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
         
            routineService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada"));

            exerciseHistoryService.persistRoutineSets(id);

            routineService.completeRoutine(id);

            redirectAttributes.addFlashAttribute("successMessage", "Rutina completada con éxito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al completar la rutina: " + e.getMessage());
           
            exerciseHistoryService.clearTemporarySets(id);
        }
        return "redirect:/home";
    }
}
