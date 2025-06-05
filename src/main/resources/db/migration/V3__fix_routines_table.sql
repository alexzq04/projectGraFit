-- Eliminar la columna last_completed ya que es redundante con last_completed_date
ALTER TABLE routines
DROP COLUMN last_completed;

-- Asegurar que completed tenga un valor por defecto y no sea NULL
ALTER TABLE routines
MODIFY COLUMN completed BOOLEAN NOT NULL DEFAULT FALSE;

-- Actualizar los días de la semana a formato estándar
UPDATE routines 
SET day_of_week = 'LUNES' 
WHERE day_of_week IN ('MONDAY', 'Lunes', 'Monday', 'lunes');

UPDATE routines 
SET day_of_week = 'MARTES' 
WHERE day_of_week IN ('TUESDAY', 'Martes', 'Tuesday', 'martes');

UPDATE routines 
SET day_of_week = 'MIÉRCOLES' 
WHERE day_of_week IN ('WEDNESDAY', 'Miercoles', 'Wednesday', 'miercoles');

UPDATE routines 
SET day_of_week = 'JUEVES' 
WHERE day_of_week IN ('THURSDAY', 'Jueves', 'Thursday', 'jueves');

UPDATE routines 
SET day_of_week = 'VIERNES' 
WHERE day_of_week IN ('FRIDAY', 'Viernes', 'Friday', 'viernes');

UPDATE routines 
SET day_of_week = 'SÁBADO' 
WHERE day_of_week IN ('SATURDAY', 'Sabado', 'Saturday', 'sabado');

UPDATE routines 
SET day_of_week = 'DOMINGO' 
WHERE day_of_week IN ('SUNDAY', 'Domingo', 'Sunday', 'domingo');

-- Asegurar que day_of_week no sea NULL
ALTER TABLE routines
MODIFY COLUMN day_of_week VARCHAR(255) NOT NULL; 