ALTER TABLE `productos` 
CHANGE COLUMN  `presentacion` `presentacion` varchar(45)  DEFAULT NULL;

UPDATE `productos` SET `es_privado`=0 WHERE es_privado IS NULL;
UPDATE `productos` SET `es_publico`=0 WHERE eliminado IS NULL;