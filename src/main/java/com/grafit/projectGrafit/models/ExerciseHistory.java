package com.grafit.projectGrafit.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa el historial de ejercicios realizados.
 * Esta clase almacena información sobre las series y repeticiones de ejercicios
 * completados durante un entrenamiento.
 */
@Entity
@Table(name = "exercise_history")
public class ExerciseHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "training_id")
    private Training training;

    private LocalDateTime date;

    @ElementCollection
    @CollectionTable(name = "exercise_sets", 
        joinColumns = @JoinColumn(name = "exercise_history_id"))
    private List<ExerciseSet> sets = new ArrayList<>();

    /**
     * Constructor por defecto requerido por JPA.
     */
    public ExerciseHistory() {
    }

    /**
     * Obtiene el identificador único del historial de ejercicio.
     * @return El ID del historial
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único del historial de ejercicio.
     * @param id El ID a establecer
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el entrenamiento asociado a este historial.
     * @return El entrenamiento relacionado
     */
    public Training getTraining() {
        return training;
    }

    /**
     * Establece el entrenamiento asociado a este historial.
     * @param training El entrenamiento a asociar
     */
    public void setTraining(Training training) {
        this.training = training;
    }

    /**
     * Obtiene la fecha y hora del registro del historial.
     * @return La fecha y hora del registro
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Establece la fecha y hora del registro del historial.
     * @param date La fecha y hora a establecer
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * Obtiene la lista de series de ejercicios realizados.
     * @return Lista de series de ejercicios
     */
    public List<ExerciseSet> getSets() {
        return sets;
    }

    /**
     * Establece la lista de series de ejercicios realizados.
     * @param sets Lista de series a establecer
     */
    public void setSets(List<ExerciseSet> sets) {
        this.sets = sets;
    }

    /**
     * Clase embebida que representa una serie individual de un ejercicio.
     * Contiene información detallada sobre repeticiones, peso y notas de cada serie.
     */
    @Embeddable
    public static class ExerciseSet {
        private Long setId;
        private Integer setNumber;
        private Integer repetitions;
        private Double weight;
        private String timerValue;
        private String note;

        /**
         * Constructor por defecto requerido por JPA.
         */
        public ExerciseSet() {
        }

        /**
         * Constructor para crear una nueva serie de ejercicio.
         * 
         * @param setNumber Número de la serie
         * @param repetitions Número de repeticiones
         * @param weight Peso utilizado
         * @param timerValue Valor del temporizador
         */
        public ExerciseSet(Integer setNumber, Integer repetitions, Double weight, String timerValue) {
            this.setNumber = setNumber;
            this.repetitions = repetitions;
            this.weight = weight;
            this.timerValue = timerValue;
        }

        /**
         * Obtiene el ID de la serie.
         * @return El ID único de la serie
         */
        public Long getSetId() {
            return setId;
        }

        /**
         * Establece el ID de la serie.
         * @param setId El ID a establecer
         */
        public void setSetId(Long setId) {
            this.setId = setId;
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
         * Obtiene el número de repeticiones de la serie.
         * @return El número de repeticiones
         */
        public Integer getRepetitions() {
            return repetitions;
        }

        /**
         * Establece el número de repeticiones de la serie.
         * @param repetitions El número de repeticiones a establecer
         */
        public void setRepetitions(Integer repetitions) {
            this.repetitions = repetitions;
        }

        /**
         * Obtiene el peso utilizado en la serie.
         * @return El peso en la unidad correspondiente
         */
        public Double getWeight() {
            return weight;
        }

        /**
         * Establece el peso utilizado en la serie.
         * @param weight El peso a establecer
         */
        public void setWeight(Double weight) {
            this.weight = weight;
        }

        /**
         * Obtiene el valor del temporizador de la serie.
         * @return El valor del temporizador
         */
        public String getTimerValue() {
            return timerValue;
        }

        /**
         * Establece el valor del temporizador de la serie.
         * @param timerValue El valor del temporizador a establecer
         */
        public void setTimerValue(String timerValue) {
            this.timerValue = timerValue;
        }

        /**
         * Obtiene la nota asociada a la serie.
         * @return La nota o comentario de la serie
         */
        public String getNote() {
            return note;
        }

        /**
         * Establece la nota asociada a la serie.
         * @param note La nota o comentario a establecer
         */
        public void setNote(String note) {
            this.note = note;
        }
    }
}