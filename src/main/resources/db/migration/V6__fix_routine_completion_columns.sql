-- Eliminar la columna redundante last_completed
ALTER TABLE routines DROP COLUMN last_completed;

-- Asegurarnos que completed sea un booleano NOT NULL con valor por defecto
ALTER TABLE routines MODIFY completed BOOLEAN NOT NULL DEFAULT FALSE;

-- Actualizar completed basado en last_completed_date
UPDATE routines 
SET completed = TRUE 
WHERE last_completed_date IS NOT NULL; 