-- Modificaciones para agregar la entidad Ubicacion al Catálogo
CREATE TABLE `ubicacion` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `fecha_creacion` DATETIME NULL,
  `fecha_edicion` DATETIME NULL,
  `eliminado` TINYINT NULL DEFAULT 0,
  `codigo` VARCHAR(13) NOT NULL,
  `descripcion` VARCHAR(255) NULL,
  `empresa_id` INT NOT NULL,
  `tipo` VARCHAR(1) NOT NULL,
  `fecha_alta` DATETIME NULL,
  `fecha_baja` DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY(codigo),
  KEY `codigo empresa_idx` (`codigo`,`empresa_id`)
  );

  ALTER TABLE `ubicacion` 
  ADD CONSTRAINT `codigo empresas`
  FOREIGN KEY (`empresa_id`)
  REFERENCES `empresas` (`id`)
  ON DELETE NO ACTION
  ON UPDATE CASCADE;

-- Modificaciones para agregar a los Productos información sobre las fechas de Suspensión del mismo
ALTER TABLE `productos` 
ADD COLUMN `suspendido_desde` DATETIME NULL AFTER `nivel_minimo_venta`;
ALTER TABLE `productos` 
ADD COLUMN `suspendido_hasta` DATETIME NULL AFTER `suspendido_desde`;

-- Modificaciones para agregar una Ubicacion a la Lista de Ventas
ALTER TABLE `listas_de_venta` 
ADD COLUMN `ubicacion_id` INT NULL AFTER `descripcion`;
ALTER TABLE `listas_de_venta` 
ADD CONSTRAINT `listas_de_venta ubicacion`
FOREIGN KEY (`ubicacion_id`)
REFERENCES `ubicacion` (`id`)
ON DELETE NO ACTION
ON UPDATE CASCADE;



