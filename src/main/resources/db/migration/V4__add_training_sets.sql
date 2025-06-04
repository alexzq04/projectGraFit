-- Crear la tabla training_sets
CREATE TABLE training_sets (
    training_id BIGINT NOT NULL,
    set_number INTEGER NOT NULL,
    repetitions INTEGER,
    weight DOUBLE PRECISION,
    set_order INTEGER NOT NULL,
    PRIMARY KEY (training_id, set_order),
    FOREIGN KEY (training_id) REFERENCES trainings(id_training)
);

-- Migrar datos existentes
INSERT INTO training_sets (training_id, set_number, repetitions, weight, set_order)
SELECT id_training, 1, repetitions, weight, 0
FROM trainings
WHERE repetitions IS NOT NULL AND weight IS NOT NULL;

-- Eliminar columnas antiguas de trainings
ALTER TABLE trainings
DROP COLUMN repetitions,
DROP COLUMN weight; 