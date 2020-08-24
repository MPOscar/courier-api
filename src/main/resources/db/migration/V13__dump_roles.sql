ALTER TABLE `roles` 
ADD COLUMN `descripcion` VARCHAR(255) NULL DEFAULT NULL AFTER `rol`,
ADD COLUMN `visible` TINYINT NULL DEFAULT NULL AFTER `fecha_edicion`;


INSERT INTO `roles` (`id`, `rol`, `descripcion`, `fecha_creacion`, `fecha_edicion`,`visible`, `eliminado`) VALUES ('1', 'editarProductos', 'Editar Productos', '2018-07-02 00:00:00', '2018-07-02 00:00:00','1', '0');
INSERT INTO `roles` (`id`, `rol`, `descripcion`, `fecha_creacion`, `fecha_edicion`,`visible`, `eliminado`) VALUES ('2', 'crearListaDeVenta', 'Crear Lista de Venta', '2018-07-02 00:00:00', '2018-07-02 00:00:00','1', '0');
INSERT INTO `roles` (`id`, `rol`, `descripcion`, `fecha_creacion`, `fecha_edicion`,`visible`, `eliminado`) VALUES ('3', 'crearGruposDeEmpresa', 'Crear Grupos de Empresa', '2018-07-02 00:00:00', '2018-07-02 00:00:00','1', '0');
INSERT INTO `roles` (`id`, `rol`, `descripcion`, `fecha_creacion`, `fecha_edicion`,`visible`, `eliminado`) VALUES ('4', 'administradorEmpresa', 'Administrador', '2018-07-02 00:00:00', '2018-07-02 00:00:00','1', '0');
INSERT INTO `roles` (`id`, `rol`, `descripcion`, `fecha_creacion`, `fecha_edicion`,`visible`, `eliminado`) VALUES ('5', 'usuarioEmpresa', 'Usuario', '2018-07-02 00:00:00', '2018-07-02 00:00:00','0', '0');
INSERT INTO `roles` (`id`, `rol`, `descripcion`, `fecha_creacion`, `fecha_edicion`,`visible`, `eliminado`) VALUES ('6', 'administradorSupermercado', 'Super', '2018-07-02 00:00:00', '2018-07-02 00:00:00', '0', '0');


INSERT INTO `usuario_empresa_roles` (`id`, `usuario_empresa_id`, `rol_id`) VALUES ('3', '3', '4');
INSERT INTO `usuario_empresa_roles` (`id`, `usuario_empresa_id`, `rol_id`) VALUES ('4', '4', '4');
INSERT INTO `usuario_empresa_roles` (`id`, `usuario_empresa_id`, `rol_id`) VALUES ('5', '4', '6');
INSERT INTO `usuario_empresa_roles` (`id`, `usuario_empresa_id`, `rol_id`) VALUES ('6', '7', '6');





ALTER TABLE `empresas` 
DROP COLUMN `proveedor`;


ALTER TABLE `usuarios_empresas` 
ADD COLUMN `activo` TINYINT(4) NOT NULL DEFAULT '0' AFTER `fecha_edicion`;

ALTER TABLE `usuarios_codigos` 
ADD COLUMN `empresa_id` INT(11) NOT NULL DEFAULT '0' AFTER `usuario_id`;

ALTER TABLE `usuarios` 
CHANGE COLUMN `contrasena` `contrasena` VARCHAR(255) NULL ;

ALTER TABLE `usuarios` 
CHANGE COLUMN `nombre` `nombre` VARCHAR(255) NULL ;

ALTER TABLE `usuarios_empresas` 
ADD COLUMN `validado` TINYINT(4) NOT NULL DEFAULT '0' AFTER `activo`;




