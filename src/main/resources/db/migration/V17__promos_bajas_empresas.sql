ALTER TABLE `productos` 
ADD COLUMN `es_promo` TINYINT(4) NULL DEFAULT '0' AFTER `unidad_medida_peso_bruto`;

UPDATE `usuarios` SET `nombre`='Max', `apellido`='Alegre' WHERE `id`='1';

CREATE TABLE IF NOT EXISTS `bajas` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `admin_id` INT(11) NOT NULL,
  `empresa_id` INT(11) NOT NULL,
  `motivo` VARCHAR(512) NULL,
  `fecha_creacion` DATETIME NULL,
  `fecha_edicion` DATETIME NULL,
  `activo` TINYINT(4) NOT NULL DEFAULT '0',
  `eliminado` TINYINT(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`));
