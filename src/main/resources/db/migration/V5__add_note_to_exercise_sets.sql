-- Eliminar la columna notes de exercise_history
ALTER TABLE exercise_history
DROP COLUMN IF EXISTS notes;

-- Añadir la columna note a exercise_sets
ALTER TABLE exercise_sets
ADD COLUMN note VARCHAR(255); 