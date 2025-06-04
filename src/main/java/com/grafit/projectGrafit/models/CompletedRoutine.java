package com.grafit.projectGrafit.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * Clase que representa una rutina completada por un usuario.
 * Almacena información sobre los ejercicios realizados, incluyendo detalles
 * como fecha de finalización y día de la semana.
 */
public class CompletedRoutine {
    private String name;
    private LocalDateTime completionDate;
    private String dayOfWeek;
    private List<CompletedTraining> trainings;

    /**
     * Constructor por defecto que inicializa la lista de entrenamientos.
     */
    public CompletedRoutine() {
        this.trainings = new ArrayList<>();
    }

    /**
     * Obtiene el nombre de la rutina completada.
     * @return El nombre de la rutina
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre de la rutina completada.
     * @param name El nombre a establecer
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtiene la fecha y hora de finalización de la rutina.
     * @return La fecha y hora de finalización
     */
    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    /**
     * Establece la fecha y hora de finalización de la rutina.
     * @param completionDate La fecha y hora a establecer
     */
    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    /**
     * Obtiene el día de la semana en que se completó la rutina.
     * @return El día de la semana
     */
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * Establece el día de la semana en que se completó la rutina.
     * @param dayOfWeek El día de la semana a establecer
     */
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * Obtiene la lista de entrenamientos completados en esta rutina.
     * @return Lista de entrenamientos completados
     */
    public List<CompletedTraining> getTrainings() {
        return trainings;
    }

    /**
     * Establece la lista de entrenamientos completados en esta rutina.
     * @param trainings Lista de entrenamientos a establecer
     */
    public void setTrainings(List<CompletedTraining> trainings) {
        this.trainings = trainings;
    }

    /**
     * Clase interna que representa un entrenamiento completado dentro de una rutina.
     * Contiene información sobre el ejercicio realizado y sus series completadas.
     */
    public static class CompletedTraining {
        private Exercise exercise;
        private List<CompletedSet> completedSets;

        /**
         * Constructor por defecto que inicializa la lista de series completadas.
         */
        public CompletedTraining() {
            this.completedSets = new ArrayList<>();
        }

        /**
         * Obtiene el ejercicio realizado en este entrenamiento.
         * @return El ejercicio completado
         */
        public Exercise getExercise() {
            return exercise;
        }

        /**
         * Establece el ejercicio realizado en este entrenamiento.
         * @param exercise El ejercicio a establecer
         */
        public void setExercise(Exercise exercise) {
            this.exercise = exercise;
        }

        /**
         * Obtiene la lista de series completadas en este entrenamiento.
         * @return Lista de series completadas
         */
        public List<CompletedSet> getCompletedSets() {
            return completedSets;
        }

        /**
         * Establece la lista de series completadas en este entrenamiento.
         * @param completedSets Lista de series a establecer
         */
        public void setCompletedSets(List<CompletedSet> completedSets) {
            this.completedSets = completedSets;
        }
    }

    /**
     * Clase interna que representa una serie completada de un ejercicio.
     * Almacena información detallada sobre las repeticiones, peso y notas de la serie.
     */
    public static class CompletedSet {
        private Integer setNumber;
        private Integer repetitions;
        private Double weight;
        private String timerValue;
        private String note;

        /**
         * Obtiene el número de la serie completada.
         * @return El número de serie
         */
        public Integer getSetNumber() {
            return setNumber;
        }

        /**
         * Establece el número de la serie completada.
         * @param setNumber El número de serie a establecer
         */
        public void setSetNumber(Integer setNumber) {
            this.setNumber = setNumber;
        }

        /**
         * Obtiene el número de repeticiones realizadas.
         * @return El número de repeticiones
         */
        public Integer getRepetitions() {
            return repetitions;
        }

        /**
         * Establece el número de repeticiones realizadas.
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
         * Obtiene la nota asociada a la serie completada.
         * @return La nota o comentario de la serie
         */
        public String getNote() {
            return note;
        }

        /**
         * Establece la nota asociada a la serie completada.
         * @param note La nota o comentario a establecer
         */
        public void setNote(String note) {
            this.note = note;
        }
    }
} 