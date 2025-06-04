-- Eliminar las columnas obsoletas de la tabla trainings
ALTER TABLE trainings
DROP COLUMN repetitions,
DROP COLUMN weight; 