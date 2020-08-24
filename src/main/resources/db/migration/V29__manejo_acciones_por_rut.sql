
/** Se agregan campos para contener los RUT de las empresas Clientes y Proveedoras que intervienen en cada Acción **/
ALTER TABLE `productos_acciones` 
ADD COLUMN `empresa_rut` VARCHAR(20) NOT NULL AFTER `empresa_id`,
ADD COLUMN `proveedor_rut` VARCHAR(20) NOT NULL AFTER `proveedor_id`;

DELIMITER $$
DROP PROCEDURE IF EXISTS agregarAccionEmpresaProductoProveedor $$
/**
*
* Permite agregar una acción a una Empresa Cliente de parte de un Proveedor
*
*
**/
CREATE PROCEDURE agregarAccionEmpresaProductoProveedor (
	IN accion varchar(255),
    IN  empresa long,
    IN  producto long,
    IN proveedor long
)
BEGIN
	SET @proveedorRUT = 0;
	SELECT rut INTO @proveedorRUT FROM empresas AS e WHERE e.id = proveedor;
	SET @clienteRUT = 0;
	SELECT rut INTO @clienteRUT FROM empresas AS e WHERE e.id = empresa;
	/** TODO Evaluar el uso de UPSERT aqui **/
    DELETE FROM `productos_acciones` WHERE  producto_id = producto AND empresa_id = empresa AND proveedor_id = proveedor AND ( fue_recibido = false OR fecha_recibido IS NULL);
    INSERT INTO `productos_acciones`(`producto_id`, `empresa_id`, `proveedor_id`,`empresa_rut`, `proveedor_rut`, `accion`, `fecha_creacion`, `fecha_edicion`, `fue_recibido`, `eliminado`)
        VALUES (producto, empresa, proveedor,@clienteRUT, @proveedorRUT, accion, NOW(), NOW(), '0', '0');
END$$
 
DELIMITER ;