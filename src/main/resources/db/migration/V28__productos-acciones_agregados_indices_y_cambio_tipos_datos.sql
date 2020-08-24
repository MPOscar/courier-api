ALTER TABLE `productos_acciones` 
CHANGE COLUMN `id` `id` BIGINT(20) NOT NULL AUTO_INCREMENT ;

ALTER TABLE `productos_acciones` 
ADD INDEX `indice_proveedor` (`proveedor_id` ASC, `empresa_id` ASC);