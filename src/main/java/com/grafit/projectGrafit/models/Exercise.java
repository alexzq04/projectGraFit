package com.grafit.projectGrafit.models;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidad que representa un ejercicio en el sistema.
 * Esta clase almacena la información relacionada con los ejercicios que los usuarios pueden crear y utilizar en sus rutinas.
 */
@Entity
@Table(name = "exercises")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idExercise;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String name;
    private String description;
    private String muscleGroup;
    private LocalDate creationDate;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public Exercise() {
    }

    /**
     * Constructor para crear un nuevo ejercicio.
     * 
     * @param name Nombre del ejercicio
     * @param description Descripción detallada del ejercicio
     * @param muscleGroup Grupo muscular principal que trabaja el ejercicio
     * @param creationDate Fecha de creación del ejercicio
     */
    public Exercise(String name, String description, String muscleGroup, LocalDate creationDate) {
        this.name = name;
        this.description = description;
        this.muscleGroup = muscleGroup;
        this.creationDate = creationDate;
    }

    /**
     * Constructor protegido para crear un ejercicio con ID específico.
     * 
     * @param idExercise Identificador único del ejercicio
     * @param name Nombre del ejercicio
     * @param description Descripción detallada del ejercicio
     * @param muscleGroup Grupo muscular principal que trabaja el ejercicio
     * @param creationDate Fecha de creación del ejercicio
     */
    protected Exercise(Long idExercise, String name, String description, String muscleGroup, LocalDate creationDate) {
        this.idExercise = idExercise;
        this.name = name;
        this.description = description;
        this.muscleGroup = muscleGroup;
        this.creationDate = creationDate;
    }

    /**
     * Obtiene el ID del ejercicio.
     * @return El identificador único del ejercicio
     */
    public Long getIdExercise() {
        return idExercise;
    }

    /**
     * Establece el ID del ejercicio.
     * @param idExercise El identificador único a establecer
     */
    public void setIdExercise(Long idExercise) {
        this.idExercise = idExercise;
    }

    /**
     * Obtiene el usuario asociado al ejercicio.
     * @return El usuario que creó el ejercicio
     */
    public User getUser() {
        return user;
    }

    /**
     * Establece el usuario asociado al ejercicio.
     * @param user El usuario a asociar con el ejercicio
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Obtiene el nombre del ejercicio.
     * @return El nombre del ejercicio
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre del ejercicio.
     * @param name El nombre a establecer
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtiene la descripción del ejercicio.
     * @return La descripción detallada del ejercicio
     */
    public String getDescription() {
        return description;
    }

    /**
     * Establece la descripción del ejercicio.
     * @param description La descripción a establecer
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Obtiene el grupo muscular del ejercicio.
     * @return El grupo muscular principal que trabaja el ejercicio
     */
    public String getMuscleGroup() {
        return muscleGroup;
    }

    /**
     * Establece el grupo muscular del ejercicio.
     * @param muscleGroup El grupo muscular a establecer
     */
    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    /**
     * Obtiene la fecha de creación del ejercicio.
     * @return La fecha en que se creó el ejercicio
     */
    public LocalDate getCreationDate() {
        return creationDate;
    }

    /**
     * Establece la fecha de creación del ejercicio.
     * @param creationDate La fecha de creación a establecer
     */
    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }
}
