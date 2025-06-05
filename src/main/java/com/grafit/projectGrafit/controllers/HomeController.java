package com.grafit.projectGrafit.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import com.grafit.projectGrafit.models.UserDTO;
import com.grafit.projectGrafit.models.Routine;
import com.grafit.projectGrafit.services.RoutineService;
import com.grafit.projectGrafit.services.ExerciseHistoryService;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import com.grafit.projectGrafit.services.UserService;
import com.grafit.projectGrafit.models.ExerciseHistory;
import com.grafit.projectGrafit.services.PersonalRecordService;
import java.util.Map;
import com.grafit.projectGrafit.models.User;

/**
 * Controlador principal que maneja las operaciones relacionadas con la página
 * de inicio
 * y la autenticación de usuarios en la aplicación GraFit.
 * 
 * Este controlador gestiona:
 * - La página de inicio
 * - El proceso de login
 * - El registro de nuevos usuarios
 * - La visualización del dashboard principal
 */
@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoutineService routineService;

    @Autowired
    private ExerciseHistoryService exerciseHistoryService;

    @Autowired
    private PersonalRecordService personalRecordService;

    /**
     * Maneja la solicitud a la página raíz de la aplicación.
     * 
     * @param authentication El objeto de autenticación actual
     * @param model          El modelo para la vista
     * @return Redirección a /home si el usuario está autenticado, o la vista index
     *         si no lo está
     */
    @GetMapping("/")
    public String index(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home";
        }
        return "index";
    }

    /**
     * Maneja las solicitudes a la página de login.
     * 
     * @param authentication El objeto de autenticación actual
     * @return Redirección a /home si el usuario está autenticado, o la vista login
     *         si no lo está
     */
    @GetMapping("/login")
    public String loginRedirectIfAuthenticated(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home";
        }
        return "login";
    }

    /**
     * Maneja las solicitudes a la página de registro.
     * 
     * @param authentication El objeto de autenticación actual
     * @param model          El modelo para la vista
     * @return Redirección a /home si el usuario está autenticado, o la vista
     *         register si no lo está
     */
    @GetMapping("/register")
    public String register(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home";
        }
        model.addAttribute("userDTO", new UserDTO());
        return "register";
    }

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Procesa el formulario de registro de nuevos usuarios.
     * 
     * @param userDTO       El objeto DTO con los datos del usuario
     * @param bindingResult El resultado de la validación del formulario
     * @param model         El modelo para la vista
     * @return Redirección a /home si el registro es exitoso, o la vista register si
     *         hay errores
     */
    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("userDTO") UserDTO userDTO,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.createUserFromDto(userDTO);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDTO.getUsername(),
                    userDTO.getPassword());
            Authentication auth = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }

        return "redirect:/home";
    }

    /**
     * Maneja las solicitudes al dashboard principal de la aplicación.
     * Este método:
     * - Obtiene las rutinas del día actual
     * - Procesa el estado de cada set de ejercicios
     * - Prepara la información necesaria para mostrar el progreso del usuario
     * 
     * @param model     El modelo para la vista
     * @param principal El objeto Principal que contiene la información del usuario
     *                  autenticado
     * @return La vista del dashboard principal
     */
    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        LocalDate today = LocalDate.now();
        String currentDay = today.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        currentDay = currentDay.substring(0, 1).toUpperCase() + currentDay.substring(1);
        List<Routine> routines = routineService.getRoutinesByDayOfWeek(currentDay);

        routines.forEach(routine -> {
            routine.getTrainings().forEach(training -> {
                List<ExerciseHistory> histories = exerciseHistoryService.getTodaysSets(training);

                for (int setNumber = 1; setNumber <= training.getSets(); setNumber++) {
                    final int currentSetNumber = setNumber;

                    boolean isCompleted = histories.stream()
                            .flatMap(h -> h.getSets().stream())
                            .anyMatch(s -> s.getSetNumber() == currentSetNumber);

                    boolean previousCompleted = true;
                    if (setNumber > 1) {
                        previousCompleted = histories.stream()
                                .flatMap(h -> h.getSets().stream())
                                .anyMatch(s -> s.getSetNumber() == (currentSetNumber - 1));
                    }

                    model.addAttribute(
                            String.format("history_%d_set_%d", training.getIdTraining(), setNumber),
                            isCompleted);
                    model.addAttribute(
                            String.format("history_%d_set_%d_previous", training.getIdTraining(), setNumber),
                            previousCompleted);
                }

                model.addAttribute("history_" + training.getIdTraining(), histories);
            });
        });

        Map<String, PersonalRecordService.PersonalRecord> records = personalRecordService.getPersonalRecords(user);

        model.addAttribute("currentDate", today);
        model.addAttribute("currentDay", currentDay);
        model.addAttribute("routines", routines);
        model.addAttribute("personalRecords", records);

        return "home/home";
    }

    /**
     * Maneja las solicitudes a la página de powerlifting.
     * 
     * @return La vista de la página de powerlifting
     */
    @GetMapping("/powerlifting")
    public String powerlifting() {
        return "powerlifting";
    }
}
