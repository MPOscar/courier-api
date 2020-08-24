ALTER TABLE `presentaciones` 
ADD COLUMN `para_mostrar` TINYINT(4) NULL DEFAULT '0' AFTER `fecha_creacion`;

ALTER TABLE `presentaciones` 
DROP COLUMN `descripcion`;


INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Aerosol', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Aerosol',`para_mostrar`='1';
INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Bandeja', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Bandeja',`para_mostrar`='1';
INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Barra', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Barra',`para_mostrar`='1';
INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Barril', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Barril',`para_mostrar`='1';
INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Blister', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Blister',`para_mostrar`='1';
INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Bolsa', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Bolsa',`para_mostrar`='1';
INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Botella', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Botella',`para_mostrar`='1';
INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Caja', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Caja',`para_mostrar`='1';
INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Casillero', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Casillero',`para_mostrar`='1';
INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Dispensador', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Dispensador',`para_mostrar`='1';
INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Display', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Display',`para_mostrar`='1';
INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Envoltura Plástica', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Envoltura Plástica',`para_mostrar`='1';
INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Funda', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Funda',`para_mostrar`='1';
INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Lata', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Lata',`para_mostrar`='1';
INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Multipack', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Multipack',`para_mostrar`='1';
INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Pallet', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Pallet',`para_mostrar`='1';
INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Pote', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Pote',`para_mostrar`='1';
INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Sachet', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Sachet',`para_mostrar`='1';
INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Sin Empaque', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Sin Empaque',`para_mostrar`='1';
INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Stick', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Stick',`para_mostrar`='1';
INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Tambor', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Tambor',`para_mostrar`='1';
INSERT INTO `presentaciones` (`nombre`, `para_mostrar`) VALUES ('Tubo', '1') 
ON DUPLICATE KEY UPDATE `nombre`='Tubo',`para_mostrar`='1';

ALTER TABLE `productos` 
CHANGE COLUMN `visibilidad_asignada` `es_publico` TINYINT(4) NULL DEFAULT '0' AFTER `es_privado`,
CHANGE COLUMN `es_privado` `es_privado` TINYINT(4) NULL DEFAULT '1' ;