-- Añadir columnas para características anatómicas
ALTER TABLE users
ADD COLUMN wingspan FLOAT,
ADD COLUMN arm_length FLOAT,
ADD COLUMN torso_length FLOAT,
ADD COLUMN femur_length FLOAT,
ADD COLUMN tibia_length FLOAT;

-- Añadir comentarios a las columnas
COMMENT ON COLUMN users.wingspan IS 'Envergadura en centímetros';
COMMENT ON COLUMN users.arm_length IS 'Longitud de brazos en centímetros';
COMMENT ON COLUMN users.torso_length IS 'Longitud del torso en centímetros';
COMMENT ON COLUMN users.femur_length IS 'Longitud del fémur en centímetros';
COMMENT ON COLUMN users.tibia_length IS 'Longitud de la tibia en centímetros'; 