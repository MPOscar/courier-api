DROP TABLE IF EXISTS reseteos_contrasenas;
CREATE TABLE reseteos_contrasenas (
  id INT(11) NOT NULL AUTO_INCREMENT,
  codigo VARCHAR(8) NULL,
  usuario_id INT(11) NULL,
  usado tinyint(4) NOT NULL DEFAULT '0',
  expirado tinyint(4) NOT NULL DEFAULT '0',
  used_on DATETIME NULL,
  created_on DATETIME NULL,
  PRIMARY KEY (id)
  )ENGINE=InnoDB DEFAULT CHARSET=utf8;


  ALTER TABLE `reseteos_contrasenas` 
CHANGE COLUMN `used_on` `fecha_uso` DATETIME NULL DEFAULT NULL ,
CHANGE COLUMN `created_on` `fecha_creacion` DATETIME NULL DEFAULT NULL ,
ADD COLUMN `fecha_edicion` DATETIME NULL AFTER `fecha_creacion`,
ADD COLUMN `eliminado` TINYINT(4) NOT NULL DEFAULT 0 AFTER `fecha_edicion`;
