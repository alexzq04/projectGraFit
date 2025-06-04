package com.grafit.projectGrafit.controllers;

import com.grafit.projectGrafit.models.ExerciseHistory;
import com.grafit.projectGrafit.services.ExerciseHistoryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/exercise-history")
public class ExerciseHistoryController {

    private final ExerciseHistoryService exerciseHistoryService;

    public ExerciseHistoryController(ExerciseHistoryService exerciseHistoryService) {
        this.exerciseHistoryService = exerciseHistoryService;
    }

    @PostMapping("/add")
    public String addSet(
            @RequestParam Long trainingId,
            @RequestParam Integer repetitions,
            @RequestParam Double weight,
            @RequestParam String timerValue,
            @RequestParam(required = false) String note,
            RedirectAttributes redirectAttributes) {

        try {
            ExerciseHistory history = exerciseHistoryService.addSet(trainingId, repetitions, weight, timerValue, note);
            int setNumber = history.getSets().get(0).getSetNumber();
            redirectAttributes.addFlashAttribute("successMessage", "Set " + setNumber + " registrado correctamente");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("errorMessage", "Error al registrar el set: " + e.getMessage());
        }

        return "redirect:/home";
    }

    @PostMapping("/edit")
    public String editSet(
            @RequestParam Long historyId,
            @RequestParam Long setId,
            @RequestParam Integer repetitions,
            @RequestParam Double weight,
            @RequestParam String timerValue,
            @RequestParam(required = false) String note,
            RedirectAttributes redirectAttributes) {

        try {
            exerciseHistoryService.editSet(historyId, setId, repetitions, weight, timerValue, note);
            redirectAttributes.addFlashAttribute("successMessage", "Set actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el set: " + e.getMessage());
        }
        return "redirect:/home";
    }

    @PostMapping("/delete/{historyId}/{setId}")
    @ResponseBody
    public ResponseEntity<?> deleteSet(
            @PathVariable Long historyId,
            @PathVariable Long setId) {

        try {
            exerciseHistoryService.deleteSet(historyId, setId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar el set: " + e.getMessage());
        }
    }
}