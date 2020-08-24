ALTER TABLE `grupos` 
ADD COLUMN `fecha_creacion` DATETIME NULL AFTER `descripcion`,
ADD COLUMN `fecha_edicion` DATETIME NULL AFTER `fecha_creacion`,
ADD COLUMN `eliminado` TINYINT(4) NOT NULL DEFAULT 0 AFTER `fecha_edicion`;

ALTER TABLE `grupos` 
DROP FOREIGN KEY `grupos empresas `;
ALTER TABLE `grupos` 
CHANGE COLUMN `id_publicador` `empresa_id` INT(11) NOT NULL ;
ALTER TABLE `grupos` 
ADD CONSTRAINT `grupos empresas `
  FOREIGN KEY (`empresa_id`)
  REFERENCES `empresas` (`id`)
  ON DELETE NO ACTION
  ON UPDATE CASCADE;


ALTER TABLE `grupos_empresas` 
ADD COLUMN `fecha_creacion` DATETIME NULL AFTER `empresa_id`,
ADD COLUMN `fecha_edicion` DATETIME NULL AFTER `fecha_creacion`,
ADD COLUMN `eliminado` TINYINT(4) NOT NULL DEFAULT 0 AFTER `fecha_edicion`;

ALTER TABLE `categorias` 
DROP FOREIGN KEY `categorias empresas`;
ALTER TABLE `categorias` 
CHANGE COLUMN `publicador` `empresa_id` INT(11) NOT NULL ,
ADD COLUMN `fecha_edicion` DATETIME NULL AFTER `padre`,
ADD COLUMN `fecha_creacion` DATETIME NULL AFTER `fecha_edicion`,
ADD COLUMN `eliminado` TINYINT(4) NOT NULL DEFAULT 0 AFTER `fecha_creacion`;
ALTER TABLE `categorias` 
ADD CONSTRAINT `categorias empresas`
  FOREIGN KEY (`empresa_id`)
  REFERENCES `empresas` (`id`)
  ON DELETE NO ACTION
  ON UPDATE CASCADE;

ALTER TABLE `empaques` 
DROP FOREIGN KEY `empaques empresas`;
ALTER TABLE `empaques` 
CHANGE COLUMN `publicador` `empresa_id` INT(11) NOT NULL ,
ADD COLUMN `fecha_edicion` DATETIME NULL AFTER `cantidad_minima`,
ADD COLUMN `fecha_creacion` DATETIME NULL AFTER `fecha_edicion`,
ADD COLUMN `eliminado` TINYINT(4) NOT NULL DEFAULT 0 AFTER `fecha_creacion`;
ALTER TABLE `empaques` 
ADD CONSTRAINT `empaques empresas`
  FOREIGN KEY (`empresa_id`)
  REFERENCES `empresas` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;
  
  ALTER TABLE `params` 
ADD COLUMN `fecha_edicion` DATETIME NULL AFTER `valor`,
ADD COLUMN `fecha_creacion` DATETIME NULL AFTER `fecha_edicion`,
ADD COLUMN `eliminado` TINYINT(4) NOT NULL DEFAULT 0 AFTER `fecha_creacion`;

  ALTER TABLE `presentaciones` 
ADD COLUMN `fecha_edicion` DATETIME NULL AFTER `descripcion`,
ADD COLUMN `fecha_creacion` DATETIME NULL AFTER `fecha_edicion`,
ADD COLUMN `eliminado` TINYINT(4) NOT NULL DEFAULT 0 AFTER `fecha_creacion`;

ALTER TABLE `productos` 
DROP FOREIGN KEY `productos empresas`,
DROP FOREIGN KEY `productos publicador`;
ALTER TABLE `productos` 
CHANGE COLUMN `publicador` `empresa_id` INT(11) NOT NULL ;
ALTER TABLE `productos` 
ADD CONSTRAINT `productos empresas`
  FOREIGN KEY (`empresa_id`)
  REFERENCES `empresas` (`id`)
  ON DELETE NO ACTION
  ON UPDATE CASCADE,
ADD CONSTRAINT `productos publicador`
  FOREIGN KEY (`empresa_id`)
  REFERENCES `empresas` (`id`)
  ON DELETE NO ACTION
  ON UPDATE CASCADE;
  
  ALTER TABLE `productos_empaques` 
DROP FOREIGN KEY `productos_empaques empaques`,
DROP FOREIGN KEY `productos_empaques productos`;
ALTER TABLE `productos_empaques` 
CHANGE COLUMN `producto` `producto_id` INT(11) NOT NULL ,
CHANGE COLUMN `empaque` `empaque_id` INT(11) NOT NULL ;
ALTER TABLE `productos_empaques` 
ADD CONSTRAINT `productos_empaques empaques`
  FOREIGN KEY (`empaque_id`)
  REFERENCES `empaques` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
ADD CONSTRAINT `productos_empaques productos`
  FOREIGN KEY (`producto_id`)
  REFERENCES `productos` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;
  
    ALTER TABLE `productos_empaques` 
ADD COLUMN `fecha_edicion` DATETIME NULL AFTER `empaque_id`,
ADD COLUMN `fecha_creacion` DATETIME NULL AFTER `fecha_edicion`,
ADD COLUMN `eliminado` TINYINT(4) NOT NULL DEFAULT 0 AFTER `fecha_creacion`;

ALTER TABLE `productos_excel` 
CHANGE COLUMN `empresa` `empresa_id` INT(11) NULL ,
ADD COLUMN `id` INT(11) NOT NULL FIRST,
ADD COLUMN `fecha_edicion` DATETIME NULL AFTER `peso_bruto`,
ADD COLUMN `fecha_creacion` DATETIME NULL AFTER `fecha_edicion`,
ADD COLUMN `eliminado` TINYINT(4) NOT NULL DEFAULT 0 AFTER `fecha_creacion`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`id`);

ALTER TABLE `rbac_roles` 
CHANGE COLUMN `eliminado` `eliminado` TINYINT(4) NOT NULL DEFAULT '0' ;

ALTER TABLE `usuarios` 
CHANGE COLUMN `eliminado` `eliminado` TINYINT(4) NOT NULL DEFAULT '0' ;

ALTER TABLE `usuarios_codigos` 
CHANGE COLUMN `used_on` `fecha_uso` DATETIME NULL DEFAULT NULL ,
CHANGE COLUMN `created_on` `fecha_creacion` DATETIME NULL DEFAULT NULL ,
ADD COLUMN `fecha_edicion` DATETIME NULL AFTER `fecha_creacion`,
ADD COLUMN `eliminado` TINYINT(4) NOT NULL DEFAULT 0 AFTER `fecha_edicion`;

ALTER TABLE `usuarios_empresas` 
DROP FOREIGN KEY `usuarios_empresas empresas`,
DROP FOREIGN KEY `usuarios_empresas usuarios`;
ALTER TABLE `usuarios_empresas` 
CHANGE COLUMN `usuario` `usuario_id` INT(11) NOT NULL ,
CHANGE COLUMN `empresa` `empresa_id` INT(11) NOT NULL ;
ALTER TABLE `usuarios_empresas` 
ADD CONSTRAINT `usuarios_empresas empresas`
  FOREIGN KEY (`empresa_id`)
  REFERENCES `empresas` (`id`)
  ON DELETE NO ACTION
  ON UPDATE CASCADE,
ADD CONSTRAINT `usuarios_empresas usuarios`
  FOREIGN KEY (`usuario_id`)
  REFERENCES `usuarios` (`id`)
  ON DELETE NO ACTION
  ON UPDATE CASCADE;

ALTER TABLE `visible_por` 
DROP FOREIGN KEY `visible_por empresas`,
DROP FOREIGN KEY `visible_por productos`;
ALTER TABLE `visible_por` 
CHANGE COLUMN `empresa` `empresa_id` INT(11) NOT NULL ,
CHANGE COLUMN `grupo` `grupo_id` INT(11) NOT NULL ,
CHANGE COLUMN `producto` `producto_id` INT(11) NOT NULL ,
CHANGE COLUMN `categoria` `categoria_id` INT(11) NOT NULL ;
ALTER TABLE `visible_por` 
ADD CONSTRAINT `visible_por empresas`
  FOREIGN KEY (`empresa_id`)
  REFERENCES `empresas` (`id`)
  ON DELETE NO ACTION
  ON UPDATE CASCADE,
ADD CONSTRAINT `visible_por productos`
  FOREIGN KEY (`producto_id`)
  REFERENCES `productos` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `empresas` 
ADD COLUMN `validado` TINYINT(4) NOT NULL DEFAULT '0' AFTER `activo`;


UPDATE `usuarios` SET `administrador_sistema`='1' WHERE `id`='1';

UPDATE `usuarios_empresas` SET `rol`='systemAdmin' WHERE `id`='1';
UPDATE `usuarios_empresas` SET `rol`='providerAdmin' WHERE `id`='3';




