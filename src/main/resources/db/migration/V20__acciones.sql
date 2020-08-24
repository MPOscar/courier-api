CREATE TABLE `productos_acciones` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `producto_id` INT NOT NULL,
  `empresa_id` INT NOT NULL,
  `proveedor_id` INT NOT NULL,
  `accion` VARCHAR(255) NULL,
  `fecha_recibido` DATETIME NULL,
  `fecha_creacion` DATETIME NULL,
  `fecha_edicion` DATETIME NULL,
  `fue_recibido` TINYINT NULL DEFAULT 0,
  `eliminado` TINYINT NULL DEFAULT 0,
  PRIMARY KEY (`id`));
