package com.grafit.projectGrafit.dto;

public class ExerciseSetDTO {
    private Long exerciseId;
    private Integer[] repetitions;
    private Double[] weights;
    private Integer numberOfSets;

    // Getters y Setters
    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Integer[] getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(Integer[] repetitions) {
        this.repetitions = repetitions;
    }

    public Double[] getWeights() {
        return weights;
    }

    public void setWeights(Double[] weights) {
        this.weights = weights;
    }

    public Integer getNumberOfSets() {
        return numberOfSets;
    }

    public void setNumberOfSets(Integer numberOfSets) {
        this.numberOfSets = numberOfSets;
    }
} 