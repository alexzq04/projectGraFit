package com.grafit.projectGrafit.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Entidad que representa una rutina de entrenamiento.
 * Una rutina es una colección de ejercicios programados para un día específico de la semana,
 * que puede ser completada por un usuario y mantiene un registro de su última ejecución.
 */
@Entity
@Table(name = "routines")
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRoutine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String dayOfWeek;

    @Column(nullable = false)
    private LocalDate creationDate;

    @Column(name = "last_completed_date")
    private LocalDate lastCompletedDate;

    @Column(nullable = false)
    private boolean completed = false;

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Training> trainings = new ArrayList<>();

    /**
     * Constructor por defecto que inicializa la fecha de creación y el estado de completado.
     */
    public Routine() {
        this.creationDate = LocalDate.now();
        this.completed = false;
    }

    /**
     * Constructor que crea una nueva rutina con nombre, descripción y día de la semana.
     * Los valores de texto se convierten automáticamente a mayúsculas.
     *
     * @param name Nombre de la rutina
     * @param description Descripción detallada de la rutina
     * @param dayOfWeek Día de la semana en que se realiza la rutina
     */
    public Routine(String name, String description, String dayOfWeek) {
        this();
        this.name = name != null ? name.toUpperCase() : "";
        this.description = description != null ? description.toUpperCase() : "";
        this.dayOfWeek = dayOfWeek != null ? dayOfWeek.toUpperCase() : "LUNES";
    }

    /**
     * Obtiene el identificador único de la rutina.
     * @return El ID de la rutina
     */
    public Long getIdRoutine() {
        return idRoutine;
    }

    /**
     * Establece el identificador único de la rutina.
     * @param idRoutine El ID a establecer
     */
    public void setIdRoutine(Long idRoutine) {
        this.idRoutine = idRoutine;
    }

    /**
     * Obtiene el usuario propietario de la rutina.
     * @return El usuario asociado
     */
    public User getUser() {
        return user;
    }

    /**
     * Establece el usuario propietario de la rutina.
     * @param user El usuario a asociar
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Obtiene el nombre de la rutina.
     * @return El nombre de la rutina
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre de la rutina, convirtiéndolo a mayúsculas.
     * @param name El nombre a establecer
     */
    public void setName(String name) {
        this.name = name != null ? name.toUpperCase() : "";
    }

    /**
     * Obtiene la descripción de la rutina.
     * @return La descripción de la rutina
     */
    public String getDescription() {
        return description;
    }

    /**
     * Establece la descripción de la rutina, convirtiéndola a mayúsculas.
     * @param description La descripción a establecer
     */
    public void setDescription(String description) {
        this.description = description != null ? description.toUpperCase() : "";
    }

    /**
     * Obtiene el día de la semana asignado a la rutina.
     * @return El día de la semana
     */
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * Establece el día de la semana para la rutina, convirtiéndolo a mayúsculas.
     * Si es null, establece "LUNES" por defecto.
     * @param dayOfWeek El día de la semana a establecer
     */
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek != null ? dayOfWeek.toUpperCase() : "LUNES";
    }

    /**
     * Obtiene la fecha de creación de la rutina.
     * @return La fecha de creación
     */
    public LocalDate getCreationDate() {
        return creationDate;
    }

    /**
     * Establece la fecha de creación de la rutina.
     * Si es null, establece la fecha actual.
     * @param creationDate La fecha de creación a establecer
     */
    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate != null ? creationDate : LocalDate.now();
    }

    /**
     * Obtiene la fecha de la última vez que se completó la rutina.
     * @return La fecha de última completación
     */
    public LocalDate getLastCompletedDate() {
        return lastCompletedDate;
    }

    /**
     * Establece la fecha de la última vez que se completó la rutina.
     * @param lastCompletedDate La fecha de última completación a establecer
     */
    public void setLastCompletedDate(LocalDate lastCompletedDate) {
        this.lastCompletedDate = lastCompletedDate;
    }

    /**
     * Verifica si la rutina está marcada como completada.
     * @return true si la rutina está completada, false en caso contrario
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Establece el estado de completado de la rutina.
     * @param completed El estado de completado a establecer
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Verifica si la rutina ha sido completada hoy.
     * @return true si la rutina fue completada en la fecha actual, false en caso contrario
     */
    public boolean isCompletedToday() {
        return completed && lastCompletedDate != null && lastCompletedDate.equals(LocalDate.now());
    }

    /**
     * Obtiene la lista de entrenamientos asociados a la rutina.
     * @return Lista de entrenamientos
     */
    public List<Training> getTrainings() {
        return trainings;
    }

    /**
     * Establece la lista de entrenamientos de la rutina.
     * Si la lista es null, crea una nueva lista vacía.
     * @param trainings Lista de entrenamientos a establecer
     */
    public void setTrainings(List<Training> trainings) {
        this.trainings = trainings != null ? trainings : new ArrayList<>();
    }

    /**
     * Añade un nuevo entrenamiento a la rutina y establece la relación bidireccional.
     * @param training El entrenamiento a añadir
     */
    public void addTraining(Training training) {
        trainings.add(training);
        training.setRoutine(this);
    }

    /**
     * Elimina un entrenamiento de la rutina y rompe la relación bidireccional.
     * @param training El entrenamiento a eliminar
     */
    public void removeTraining(Training training) {
        trainings.remove(training);
        training.setRoutine(null);
    }
}