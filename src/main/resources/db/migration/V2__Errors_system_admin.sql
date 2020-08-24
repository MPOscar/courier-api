
DROP TABLE IF EXISTS `errors`;
CREATE TABLE `errors` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `message` VARCHAR(255) NULL,
  `created_on` DATETIME NULL,
  `user_id` INT(11) NULL,
  PRIMARY KEY (`id`)
  )ENGINE=InnoDB DEFAULT CHARSET=utf8;
  
ALTER TABLE `usuarios` 
ADD COLUMN `administrador_sistema` TINYINT(4) NOT NULL DEFAULT '0' AFTER `contrasena`;

ALTER TABLE `empresas` 
ADD COLUMN `proveedor` TINYINT(4) NOT NULL DEFAULT '0' AFTER `rut`;


