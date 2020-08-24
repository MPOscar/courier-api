DROP TABLE IF EXISTS `usuario_empresa_roles`;
CREATE TABLE `usuario_empresa_roles` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `usuario_empresa_id` INT(11) NULL,
  `rol_id` INT(11) NULL,
  `fecha_creacion` DATETIME NULL,
  `fecha_edicion` DATETIME NULL,
  `eliminado` TINYINT NULL,
  PRIMARY KEY (`id`));

  DROP TABLE IF EXISTS `roles`;

  CREATE TABLE `roles` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `rol` VARCHAR(255) NULL,
  `fecha_creacion` DATETIME NULL,
  `fecha_edicion` DATETIME NULL,
  `eliminado` TINYINT NULL,
  PRIMARY KEY (`id`));
