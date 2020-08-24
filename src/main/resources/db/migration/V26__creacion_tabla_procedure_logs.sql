/**
*
* Tabla para guardar los logs generados en la ejecución de los distintos Triggers y Procedimientos
*
**/
CREATE TABLE IF NOT EXISTS `logs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `acccion` varchar(500) DEFAULT NULL,
  `mensaje` varchar(500) DEFAULT NULL,
  `creado` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
);

DELIMITER $$
DROP PROCEDURE IF EXISTS `log`$$

/**
*
* Procedimiento para guardar los logs
*
**/
CREATE PROCEDURE log(enabled INTEGER, accion VARCHAR(80), mensaje TEXT)
BEGIN
  IF enabled THEN
    INSERT INTO `logs` (`acccion`, `mensaje`,`creado`) VALUES (accion, mensaje,NOW());
  END IF;
END $$

DELIMITER ;
