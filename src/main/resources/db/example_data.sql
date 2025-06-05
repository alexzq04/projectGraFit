-- Insertar historiales de ejercicios para los últimos 30 días
-- Asumimos que training_id 62 corresponde al primer ejercicio y 63 al segundo

-- Primer ejercicio (ID: 62) - Progresión gradual en peso y repeticiones
INSERT INTO exercise_history (id, training_id, date) VALUES 
(101, 62, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(102, 62, DATE_SUB(NOW(), INTERVAL 25 DAY)),
(103, 62, DATE_SUB(NOW(), INTERVAL 20 DAY)),
(104, 62, DATE_SUB(NOW(), INTERVAL 15 DAY)),
(105, 62, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(106, 62, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(107, 62, NOW());

-- Sets para el primer ejercicio
INSERT INTO exercise_sets (exercise_history_id, set_number, repetitions, weight, timer_value, set_id, note) VALUES
-- Día 1 (30 días atrás)
(101, 1, 8, 60, '02:00', 1, 'Primera sesión'),
(101, 2, 8, 60, '02:00', 2, 'Buena forma'),
(101, 3, 7, 60, '02:00', 3, 'Última serie costó'),

-- Día 2 (25 días atrás)
(102, 1, 8, 62.5, '02:00', 4, 'Aumentando peso'),
(102, 2, 8, 62.5, '02:00', 5, 'Manteniendo forma'),
(102, 3, 8, 62.5, '02:00', 6, 'Mejor que la semana pasada'),

-- Día 3 (20 días atrás)
(103, 1, 8, 65, '02:00', 7, 'Subiendo peso'),
(103, 2, 8, 65, '02:00', 8, 'Buena técnica'),
(103, 3, 7, 65, '02:00', 9, 'Última serie dura'),

-- Día 4 (15 días atrás)
(104, 1, 9, 65, '02:00', 10, 'Aumentando reps'),
(104, 2, 9, 65, '02:00', 11, 'Manteniendo ritmo'),
(104, 3, 8, 65, '02:00', 12, 'Buen volumen'),

-- Día 5 (10 días atrás)
(105, 1, 9, 67.5, '02:00', 13, 'Nuevo PR'),
(105, 2, 9, 67.5, '02:00', 14, 'Manteniendo fuerza'),
(105, 3, 8, 67.5, '02:00', 15, 'Progresando bien'),

-- Día 6 (5 días atrás)
(106, 1, 10, 67.5, '02:00', 16, 'Más repeticiones'),
(106, 2, 9, 67.5, '02:00', 17, 'Buena sesión'),
(106, 3, 9, 67.5, '02:00', 18, 'Mejorando resistencia'),

-- Día 7 (hoy)
(107, 1, 10, 70, '02:00', 19, 'Nuevo máximo'),
(107, 2, 10, 70, '02:00', 20, 'Excelente forma'),
(107, 3, 9, 70, '02:00', 21, 'Gran progreso');

-- Segundo ejercicio (ID: 63) - Progresión en repeticiones manteniendo el peso
INSERT INTO exercise_history (id, training_id, date) VALUES 
(201, 63, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(202, 63, DATE_SUB(NOW(), INTERVAL 25 DAY)),
(203, 63, DATE_SUB(NOW(), INTERVAL 20 DAY)),
(204, 63, DATE_SUB(NOW(), INTERVAL 15 DAY)),
(205, 63, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(206, 63, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(207, 63, NOW());

-- Sets para el segundo ejercicio
INSERT INTO exercise_sets (exercise_history_id, set_number, repetitions, weight, timer_value, set_id, note) VALUES
-- Día 1 (30 días atrás)
(201, 1, 12, 40, '01:30', 22, 'Inicio'),
(201, 2, 12, 40, '01:30', 23, 'Manteniendo'),
(201, 3, 10, 40, '01:30', 24, 'Última serie'),

-- Día 2 (25 días atrás)
(202, 1, 13, 40, '01:30', 25, 'Aumentando reps'),
(202, 2, 12, 40, '01:30', 26, 'Buen ritmo'),
(202, 3, 12, 40, '01:30', 27, 'Mejorando'),

-- Día 3 (20 días atrás)
(203, 1, 14, 40, '01:30', 28, 'Más reps'),
(203, 2, 13, 40, '01:30', 29, 'Consistente'),
(203, 3, 12, 40, '01:30', 30, 'Progresando'),

-- Día 4 (15 días atrás)
(204, 1, 15, 40, '01:30', 31, 'Nueva marca'),
(204, 2, 14, 40, '01:30', 32, 'Buena técnica'),
(204, 3, 13, 40, '01:30', 33, 'Mejorando resistencia'),

-- Día 5 (10 días atrás)
(205, 1, 15, 40, '01:30', 34, 'Manteniendo'),
(205, 2, 15, 40, '01:30', 35, 'Excelente'),
(205, 3, 14, 40, '01:30', 36, 'Más volumen'),

-- Día 6 (5 días atrás)
(206, 1, 16, 40, '01:30', 37, 'Nuevo récord'),
(206, 2, 15, 40, '01:30', 38, 'Fuerte'),
(206, 3, 15, 40, '01:30', 39, 'Gran progreso'),

-- Día 7 (hoy)
(207, 1, 17, 40, '01:30', 40, 'Máximo personal'),
(207, 2, 16, 40, '01:30', 41, 'Excelente forma'),
(207, 3, 15, 40, '01:30', 42, 'Gran sesión'); 