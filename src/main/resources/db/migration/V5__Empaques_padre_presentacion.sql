ALTER TABLE `empaques` 
ADD COLUMN `padre` INT(11) NULL DEFAULT NULL AFTER `cantidad_minima`,
ADD INDEX `empaques empaques padre_idx` (`padre` ASC);

ALTER TABLE `empaques` 
ADD CONSTRAINT `empaques empaques padre`
  FOREIGN KEY (`padre`)
  REFERENCES `empaques` (`id`)
  ON DELETE NO ACTION
  ON UPDATE CASCADE;
  
ALTER TABLE `empaques` 
DROP FOREIGN KEY `empaques presentaciones`;

ALTER TABLE `empaques` 
CHANGE COLUMN `presentacion` `presentacion` VARCHAR(45) NULL DEFAULT NULL ;

ALTER TABLE `empaques` 
ADD CONSTRAINT `empaques presentaciones`
  FOREIGN KEY (`presentacion`)
  REFERENCES `presentaciones` (`nombre`)
  ON DELETE NO ACTION
  ON UPDATE CASCADE;

ALTER TABLE `productos` 
CHANGE COLUMN `foto` `foto` VARCHAR(255) NULL DEFAULT NULL ;

ALTER TABLE `productos` 
DROP INDEX `gtin_UNIQUE` ,
DROP INDEX `cpp_UNIQUE` ;

ALTER TABLE `productos`
  ADD CONSTRAINT `productos` UNIQUE(`gtin`, `cpp`);
  
  ALTER TABLE `empresas` 
ADD COLUMN `foto` VARCHAR(255) NULL AFTER `rut`;

INSERT INTO `usuarios` (`id`, `nombre`, `email`, `usuario`, `contrasena`, `administrador_sistema`, `reseteo_contrasena`, `validado`, `activo`, `eliminado`, `fecha_creacion`, `fecha_edicion`) VALUES ('3', 'Usuario Tienda Inglesa', 'tiendainglesa@tiendainglesa.com.uy', 'tiendainglesa@tiendainglesa.com.uy', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', '0', '0', '1', '1', '0', '2018-05-05 00:00:00', '2018-05-05 00:00:00');
INSERT INTO `usuarios` (`id`, `nombre`, `email`, `usuario`, `contrasena`, `administrador_sistema`, `reseteo_contrasena`, `validado`, `activo`, `eliminado`, `fecha_creacion`, `fecha_edicion`) VALUES ('4', 'Usuario Proveedor', 'proveedor@proveedor.com', 'proveedor@proveedor.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', '0', '0', '1', '1', '0', '2018-05-05 00:00:00', '2018-05-05 00:00:00');
INSERT INTO `usuarios` (`id`, `nombre`, `email`, `usuario`, `contrasena`, `administrador_sistema`, `reseteo_contrasena`, `validado`, `activo`, `eliminado`, `fecha_creacion`, `fecha_edicion`) VALUES ('5', 'Usuario Supermercado', 'supermercado@supermercado.com', 'supermercado@supermercado.com', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', '0', '0', '1', '1', '0', '2018-05-05 00:00:00', '2018-05-05 00:00:00');
INSERT INTO `usuarios` (`id`, `nombre`, `email`, `usuario`, `contrasena`, `administrador_sistema`, `reseteo_contrasena`, `validado`, `activo`, `eliminado`, `fecha_creacion`, `fecha_edicion`) VALUES ('6', 'Usuario Tata', 'tata@tata.com.uy', 'tata@tata.com.uy', 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', '0', '0', '1', '1', '0', '2018-05-05 00:00:00', '2018-05-05 00:00:00');


UPDATE `usuarios` SET `nombre`='Sebamar', `email`='sebamar@sebamar.com.uy', `usuario`='sebamar@sebamar.com.uy' WHERE `id`='2';

UPDATE `empresas` SET `razon_social`='Sebamar', `nombre`='Sebamar', `foto`='https://yt3.ggpht.com/a-/AN66SAwPdvdZQAzagchh4A7VIbyw0Vy7EXVPFJWJPA=s288-mo-c-c0xffffffff-rj-k-no', `proveedor`='1', `validado`='1' WHERE `id`='3';
UPDATE `empresas` SET `razon_social`='Proveedor 2', `nombre`='Proveedor 2',`foto`='https://files.informabtl.com/uploads/2015/08/proveedor.jpg', `proveedor`='1', `validado`='1' WHERE `id`='1';
UPDATE `empresas` SET `razon_social`='Proveedor 3', `nombre`='Proveedor 3', `foto`='http://pngimage.net/wp-content/uploads/2018/06/icono-proveedor-png-5.png', `proveedor`='1', `validado`='1' WHERE `id`='2';
INSERT INTO `empresas` (`id`, `gln`, `razon_social`, `nombre`, `rut`, `proveedor`, `activo`, `validado`, `fecha_creacion`, `fecha_edicion`, `eliminado`) VALUES ('4', '7737001071019', 'Tienda Inglesa', 'Tienda Inglesa', '210094030014', '0', '1', '1', '2018-05-02 00:00:00', '2018-05-02 00:00:00', '0');
INSERT INTO `empresas` (`id`, `gln`, `razon_social`, `nombre`, `rut`, `proveedor`, `activo`, `validado`, `fecha_creacion`, `fecha_edicion`, `eliminado`) VALUES ('5', '7737001071019', 'Supermercado 2', 'Supermercado 2', '210297450018', '0', '1', '1', '2018-05-02 00:00:00', '2018-05-02 00:00:00', '0');
INSERT INTO `empresas` (`id`, `gln`, `razon_social`, `nombre`, `rut`, `proveedor`, `activo`, `validado`, `fecha_creacion`, `fecha_edicion`, `eliminado`) VALUES ('6', '7737001071019', 'Almacen 1', 'Almacen 1', '210297450018', '0', '1', '1', '2018-05-02 00:00:00', '2018-05-02 00:00:00', '0');
INSERT INTO `empresas` (`id`, `gln`, `razon_social`, `nombre`, `rut`, `proveedor`, `activo`, `validado`, `fecha_creacion`, `fecha_edicion`, `eliminado`) VALUES ('7', '7737001071019', 'Almacen 2', 'Almacen 2', '210297450018', '0', '1', '1', '2018-05-02 00:00:00', '2018-05-02 00:00:00', '0');
INSERT INTO `empresas` (`id`, `gln`, `razon_social`, `nombre`, `rut`, `proveedor`, `activo`, `validado`, `fecha_creacion`, `fecha_edicion`, `eliminado`) VALUES ('8', '7737001071019', 'Kiosko', 'Kiosko', '210297450018', '0', '1', '1', '2018-05-02 00:00:00', '2018-05-02 00:00:00', '0');
INSERT INTO `empresas` (`id`, `gln`, `razon_social`, `nombre`, `rut`, `proveedor`, `activo`, `validado`, `fecha_creacion`, `fecha_edicion`, `eliminado`) VALUES ('9', '7737001071019', 'Tata', 'Tata', '210297450018', '0', '1', '1', '2018-05-02 00:00:00', '2018-05-02 00:00:00', '0');



INSERT INTO `usuarios_empresas` (`id`, `usuario_id`, `empresa_id`, `rol`, `fecha_creacion`, `fecha_edicion`, `eliminado`) VALUES ('4', '3', '4', 'supermarketAdmin', '2018-05-31 00:00:00', '2018-05-31 00:00:00', '0');
INSERT INTO `usuarios_empresas` (`id`, `usuario_id`, `empresa_id`, `rol`, `fecha_creacion`, `fecha_edicion`, `eliminado`) VALUES ('5', '4', '1', 'providerAdmin', '2018-05-31 00:00:00', '2018-05-31 00:00:00', '0');
INSERT INTO `usuarios_empresas` (`id`, `usuario_id`, `empresa_id`, `rol`, `fecha_creacion`, `fecha_edicion`, `eliminado`) VALUES ('6', '5', '5', 'supermarketAdmin', '2018-05-31 00:00:00', '2018-05-31 00:00:00', '0');
INSERT INTO `usuarios_empresas` (`id`, `usuario_id`, `empresa_id`, `rol`, `fecha_creacion`, `fecha_edicion`, `eliminado`) VALUES ('7', '6', '9', 'supermarketAdmin', '2018-05-31 00:00:00', '2018-05-31 00:00:00', '0');



ALTER TABLE `visible_por` 
ADD COLUMN `id` INT NOT NULL AUTO_INCREMENT FIRST,
ADD PRIMARY KEY (`id`);


ALTER TABLE `visible_por` 
CHANGE COLUMN `grupo_id` `grupo_id` INT(11) NOT NULL DEFAULT 0 ,
CHANGE COLUMN `categoria_id` `categoria_id` INT(11) NOT NULL DEFAULT 0 ;


CREATE TABLE `wishlist` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `producto_id` INT NULL,
  `empresa_id` INT NULL,
  `en_wishlist` TINYINT NULL,
  `fecha_edicion` DATETIME NULL,
  `fecha_creacion` DATETIME NULL,
  `eliminado` TINYINT NULL,
  PRIMARY KEY (`id`));

UPDATE `empresas` SET `rut` = '210094030014' WHERE (`id` = '4');



