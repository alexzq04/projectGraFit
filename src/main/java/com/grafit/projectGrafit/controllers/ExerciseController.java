package com.grafit.projectGrafit.controllers;

import com.grafit.projectGrafit.models.Exercise;
import com.grafit.projectGrafit.services.ExerciseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Controller
@RequestMapping("/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping
    public String showFormAndList(
            Model model,
            @RequestParam(required = false) Long edit,
            @RequestParam(required = false) String successMessage,
            @RequestParam(required = false) String errorMessage
    ) {
        Exercise exerciseToEdit;
        if (edit != null) {
            Optional<Exercise> opt = exerciseService.findById(edit);
            exerciseToEdit = opt.orElse(new Exercise());
        } else {
            exerciseToEdit = new Exercise();
        }

        model.addAttribute("exercise", exerciseToEdit);
        model.addAttribute("exercises", exerciseService.getExercisesForCurrentUser());

        if (successMessage != null) model.addAttribute("successMessage", successMessage);
        if (errorMessage   != null) model.addAttribute("errorMessage", errorMessage);

        return "exercise/exercisesView";
    }

    @PostMapping
    public String saveOrUpdate(@ModelAttribute Exercise exercise) {
        try {
            boolean isNew = (exercise.getIdExercise() == null);

            if (isNew) {
                exerciseService.createExercise(
                    exercise.getName(),
                    exercise.getDescription(),
                    exercise.getMuscleGroup()
                );
                String msg = URLEncoder.encode("Ejercicio creado correctamente.", StandardCharsets.UTF_8);
                return "redirect:/exercises?successMessage=" + msg;
            } else {
                exerciseService.updateExercise(
                    exercise.getIdExercise(),
                    exercise.getName(),
                    exercise.getDescription(),
                    exercise.getMuscleGroup()
                );
                String msg = URLEncoder.encode("Ejercicio actualizado correctamente.", StandardCharsets.UTF_8);
                return "redirect:/exercises?successMessage=" + msg;
            }
        } catch (IllegalArgumentException e) {
            String msg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/exercises?errorMessage=" + msg;
        } catch (Exception e) {
            String msg = URLEncoder.encode("Error inesperado: " + e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/exercises?errorMessage=" + msg;
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteExercise(@PathVariable Long id) {
        try {
            exerciseService.deleteById(id);
            String msg = URLEncoder.encode("Ejercicio eliminado correctamente.", StandardCharsets.UTF_8);
            return "redirect:/exercises?successMessage=" + msg;
        } catch (IllegalStateException e) {
            String msg = URLEncoder.encode("No se puede eliminar este ejercicio: " + e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/exercises?errorMessage=" + msg;
        } catch (Exception e) {
            String msg = URLEncoder.encode("Error al eliminar el ejercicio: " + e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/exercises?errorMessage=" + msg;
        }
    }
}
