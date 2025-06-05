package com.grafit.projectGrafit.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un entrenamiento específico dentro de una rutina.
 * Esta clase relaciona un ejercicio con una rutina y mantiene la configuración
 * de series y repeticiones, así como el historial de ejecución.
 */
@Entity
@Table(name = "trainings")
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTraining;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @OneToMany(mappedBy = "training")
    private List<ExerciseHistory> history = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "training_sets", 
        joinColumns = @JoinColumn(name = "training_id"))
    @OrderColumn(name = "set_order")
    private List<SetConfiguration> setConfigurations = new ArrayList<>();

    private Integer sets;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public Training() {
    }

    /**
     * Constructor para crear un nuevo entrenamiento.
     * 
     * @param routine La rutina a la que pertenece el entrenamiento
     * @param exercise El ejercicio asociado al entrenamiento
     * @param sets Número total de series a realizar
     */
    public Training(Routine routine, Exercise exercise, Integer sets) {
        this.routine = routine;
        this.exercise = exercise;
        this.sets = sets;
    }

    /**
     * Obtiene el identificador único del entrenamiento.
     * @return El ID del entrenamiento
     */
    public Long getIdTraining() {
        return idTraining;
    }

    /**
     * Establece el identificador único del entrenamiento.
     * @param idTraining El ID a establecer
     */
    public void setIdTraining(Long idTraining) {
        this.idTraining = idTraining;
    }

    /**
     * Obtiene la rutina asociada al entrenamiento.
     * @return La rutina a la que pertenece el entrenamiento
     */
    public Routine getRoutine() {
        return routine;
    }

    /**
     * Establece la rutina asociada al entrenamiento.
     * @param routine La rutina a establecer
     */
    public void setRoutine(Routine routine) {
        this.routine = routine;
    }

    /**
     * Obtiene el ejercicio asociado al entrenamiento.
     * @return El ejercicio que se realiza en el entrenamiento
     */
    public Exercise getExercise() {
        return exercise;
    }

    /**
     * Establece el ejercicio asociado al entrenamiento.
     * @param exercise El ejercicio a establecer
     */
    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    /**
     * Obtiene el historial de ejecuciones del entrenamiento.
     * @return Lista de registros históricos del entrenamiento
     */
    public List<ExerciseHistory> getHistory() {
        return history;
    }

    /**
     * Establece el historial de ejecuciones del entrenamiento.
     * @param history Lista de registros históricos a establecer
     */
    public void setHistory(List<ExerciseHistory> history) {
        this.history = history;
    }

    /**
     * Obtiene el número total de series del entrenamiento.
     * @return Número de series
     */
    public Integer getSets() {
        return sets;
    }

    /**
     * Establece el número total de series del entrenamiento.
     * @param sets Número de series a establecer
     */
    public void setSets(Integer sets) {
        this.sets = sets;
    }

    /**
     * Obtiene la lista de configuraciones de series del entrenamiento.
     * @return Lista de configuraciones de series
     */
    public List<SetConfiguration> getSetConfigurations() {
        return setConfigurations;
    }

    /**
     * Establece la lista de configuraciones de series del entrenamiento.
     * @param setConfigurations Lista de configuraciones a establecer
     */
    public void setSetConfigurations(List<SetConfiguration> setConfigurations) {
        this.setConfigurations = setConfigurations;
    }

    /**
     * Clase embebida que representa la configuración de una serie específica en el entrenamiento.
     * Contiene información sobre el número de repeticiones y peso objetivo para cada serie.
     */
    @Embeddable
    public static class SetConfiguration {
        private Integer setNumber;
        private Integer repetitions;
        private Double weight;

        /**
         * Constructor por defecto requerido por JPA.
         */
        public SetConfiguration() {
        }

        /**
         * Constructor para crear una nueva configuración de serie.
         * 
         * @param setNumber Número de la serie
         * @param repetitions Número de repeticiones objetivo
         * @param weight Peso objetivo para la serie
         */
        public SetConfiguration(Integer setNumber, Integer repetitions, Double weight) {
            this.setNumber = setNumber;
            this.repetitions = repetitions;
            this.weight = weight;
        }

        /**
         * Obtiene el número de la serie.
         * @return El número de serie
         */
        public Integer getSetNumber() {
            return setNumber;
        }

        /**
         * Establece el número de la serie.
         * @param setNumber El número de serie a establecer
         */
        public void setSetNumber(Integer setNumber) {
            this.setNumber = setNumber;
        }

        /**
         * Obtiene el número de repeticiones objetivo.
         * @return El número de repeticiones
         */
        public Integer getRepetitions() {
            return repetitions;
        }

        /**
         * Establece el número de repeticiones objetivo.
         * @param repetitions El número de repeticiones a establecer
         */
        public void setRepetitions(Integer repetitions) {
            this.repetitions = repetitions;
        }

        /**
         * Obtiene el peso objetivo para la serie.
         * @return El peso en la unidad correspondiente
         */
        public Double getWeight() {
            return weight;
        }

        /**
         * Establece el peso objetivo para la serie.
         * @param weight El peso a establecer
         */
        public void setWeight(Double weight) {
            this.weight = weight;
        }
    }
}
