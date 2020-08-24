

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
    DELETE FROM `productos_acciones`  WHERE  producto_id = producto AND empresa_id = empresa AND proveedor_id = proveedor AND ( fue_recibido = false OR fecha_recibido IS NULL);
    INSERT INTO `productos_acciones`(`producto_id`, `empresa_id`, `proveedor_id`, `accion`, `fecha_creacion`, `fecha_edicion`, `fue_recibido`, `eliminado`)
        VALUES (producto, empresa, proveedor, accion, NOW(), NOW(), '0', '0');
END$$
 
DELIMITER ;




DELIMITER $$
DROP PROCEDURE IF EXISTS productoPublico $$

/**
*
* Devuelve 1 solo si el Producto pasado por parámetros está Público
*
**/
CREATE PROCEDURE productoPublico (
    IN  producto long,
    OUT visible tinyint (1)
)
BEGIN    
    select distinct COUNT(*) into visible from productos AS p where  p.es_publico = 1  AND p.eliminado = 0 AND p.id = producto;
END$$
 
DELIMITER ;


DELIMITER $$
DROP PROCEDURE IF EXISTS productoVisibleDirectoPor $$
/**
*
* Devuelve 1 solo si el Producto pasado por parámetros está visible por la Empresa también pasada por parámetros mediante Visibilidad Directa
*
**/
CREATE PROCEDURE productoVisibleDirectoPor (
    IN  producto long,
    IN  empresa long,
    OUT visible tinyint (1)
)
BEGIN    
    select distinct COUNT(*) into visible from productos AS p where  p.es_privado = 1   AND (EXISTS( SELECT * FROM visible_por AS vp WHERE vp.producto_id =  p.id AND vp.empresa_id = empresa LIMIT 1)) AND p.eliminado = 0 AND p.id = producto;
END$$
 
DELIMITER ;

DELIMITER $$
DROP PROCEDURE IF EXISTS productoVisibleGrupoPor $$
/**
*
* Devuelve 1 solo si el Producto pasado por parámetros está visible por la Empresa también pasada por parámetros mediante algún Grupo
*
**/
CREATE PROCEDURE productoVisibleGrupoPor (
    IN  producto long,
    IN  empresa long,
    OUT visible tinyint (1)
)
BEGIN    
    select distinct COUNT(*) into visible from productos AS p where  p.es_privado = 1  AND EXISTS( SELECT * FROM grupo_visible_por AS gvp1 inner join grupos AS g ON (g.id = gvp1.grupo_id) WHERE g.eliminado != 0 AND gvp1.producto_id = p.id AND EXISTS( SELECT * FROM grupos_empresas AS ge WHERE ge.empresa_id = empresa AND ge.grupo_id = gvp1.grupo_id  LIMIT 1) )  AND p.eliminado = 0 AND p.id = producto;
END$$
 
DELIMITER ;

/****** Visibilidad Directa *****/
DELIMITER $$
DROP TRIGGER IF EXISTS before_visible_por_insert $$ 
/**
*
* Trigger encargado de Gestionar las Acciones correspondientes a los siguientes cambios de estado de los Productos:
* - Al crear un Producto se debe Notificar a todos los Clientes ( Empresas con Visibilidad ) con una Accion POST 
*
**/
CREATE TRIGGER before_visible_por_insert
BEFORE INSERT
ON visible_por FOR EACH ROW
BEGIN
set @visible = 0;
call productoPublico(NEW.producto_id, @visible);
 IF @visible = 0 THEN
	call productoVisibleDirectoPor(NEW.producto_id, new.empresa_id, @visible);
 END IF;
 IF @visible = 0 THEN
	call productoVisibleGrupoPor(NEW.producto_id, new.empresa_id, @visible);
 END IF;
 IF @visible = 0 THEN
 set @proveedor_id = 0;
 select empresa_id INTO @proveedor_id FROM productos AS p WHERE p.id = NEW.producto_id;
     call  agregarAccionEmpresaProductoProveedor('POST', NEW.empresa_id,NEW.producto_id,@proveedor_id);
END IF;
END$$
 
DELIMITER ;

DELIMITER $$
DROP TRIGGER IF EXISTS after_visible_por_delete $$

/**
*
* Trigger encargado de Gestionar las Acciones correspondientes a los siguientes cambios de estado de los Productos:
* - Al eliminar un Producto se debe Notificar a todos los Clientes ( Empresas con Visibilidad ) con una Accion DELETE 
*
**/
CREATE TRIGGER after_visible_por_delete
AFTER DELETE
ON visible_por FOR EACH ROW
BEGIN
set @visible = 0;
call productoPublico(OLD.producto_id, @visible);
IF @visible = 0 THEN
call productoVisibleGrupoPor(OLD.producto_id, OLD.empresa_id, @visible);
END IF;
 IF @visible = 0 THEN
 set @proveedor_id = 0;
 select empresa_id INTO @proveedor_id FROM productos AS p WHERE p.id = OLD.producto_id;
     call  agregarAccionEmpresaProductoProveedor('DELETE', OLD.empresa_id,OLD.producto_id,@proveedor_id);
    END IF;
END$$
 
DELIMITER ;


/****GRUPOS***/

DELIMITER $$
DROP TRIGGER IF EXISTS before_grupo_visible_por_insert $$ 
/**
*
* Trigger encargado de Gestionar las Acciones correspondientes a los siguientes cambios de estado de los Productos:
* - Al insertar un Producto como Visible por un Grupo se debe Notificar a todas las Empresas que no tenían Visibilidad 
* sobre el Producto y que son parte del Grupo al que se le esta dando la Visibilidad sobre el Producto
*
**/
CREATE TRIGGER before_grupo_visible_por_insert
BEFORE INSERT
ON grupo_visible_por FOR EACH ROW
BEGIN
DECLARE finished INTEGER DEFAULT 0;
DECLARE empresa long;
    DEClARE cur_empresas_grupo 
        CURSOR FOR 
            SELECT empresa_id FROM grupos_empresas WHERE grupo_id = NEW.grupo_id;
    DECLARE CONTINUE HANDLER 
        FOR NOT FOUND SET finished = 1;
        
    OPEN cur_empresas_grupo;
 
    getEmpresa: LOOP
        FETCH cur_empresas_grupo INTO empresa;
        IF finished = 1 THEN 
            LEAVE getEmpresa;
        END IF;
        set @visible = 0;
        call productoPublico(NEW.producto_id, @visible);
        IF @visible = 0 THEN
		call productoVisibleDirectoPor(NEW.producto_id, empresa, @visible);
        END IF;
		IF @visible = 0 THEN
			call productoVisibleGrupoPor(NEW.producto_id, empresa, @visible);
         END IF;
		 IF @visible = 0 THEN
		 set @proveedor_id = 0;
		 select empresa_id INTO @proveedor_id FROM productos AS p WHERE p.id = NEW.producto_id;
			 call  agregarAccionEmpresaProductoProveedor('POST', empresa,NEW.producto_id,@proveedor_id);
		 END IF;
        
    END LOOP getEmpresa;
    CLOSE cur_empresas_grupo;
END$$
 
DELIMITER ;

DELIMITER $$
DROP TRIGGER IF EXISTS after_grupo_visible_por_delete $$

/**
*
* Trigger encargado de Gestionar las Acciones correspondientes a los siguientes cambios de estado de los Productos:
* - Al eliminar un Producto como Visible por un Grupo se debe Notificar a todas las Empresas que pierden Visibilidad ( solo ven el Producto por estar en el Grupo )
* sobre el Producto y que son parte del Grupo al que se le esta quitando la Visibilidad sobre el Producto
*
**/
CREATE TRIGGER after_grupo_visible_por_delete
AFTER DELETE
ON grupo_visible_por FOR EACH ROW
BEGIN
DECLARE finished INTEGER DEFAULT 0;
DECLARE empresa long;
    DEClARE cur_empresas_grupo 
        CURSOR FOR 
            SELECT empresa_id FROM grupos_empresas WHERE grupo_id = OLD.grupo_id;
    DECLARE CONTINUE HANDLER 
        FOR NOT FOUND SET finished = 1;
        
    OPEN cur_empresas_grupo;
 
    getEmpresa: LOOP
        FETCH cur_empresas_grupo INTO empresa;
        IF finished = 1 THEN 
            LEAVE getEmpresa;
        END IF;
        set @visible = 0;
        call productoPublico(OLD.producto_id, @visible);
        IF @visible = 0 THEN
		call productoVisibleDirectoPor(OLD.producto_id, empresa, @visible);
        END IF;
        IF @visible = 0 THEN
        call productoVisibleGrupoPor(OLD.producto_id, empresa, @visible);
        END IF;
		IF @visible = 0 THEN
		 set @proveedor_id = 0;
		 select empresa_id INTO @proveedor_id FROM productos AS p WHERE p.id = OLD.producto_id;
			 call  agregarAccionEmpresaProductoProveedor('DELETE', empresa,OLD.producto_id,@proveedor_id);
		 END IF;
        
    END LOOP getEmpresa;
    CLOSE cur_empresas_grupo;
END$$
 
DELIMITER ;


/****GRUPOS AGREGADO/ELIMINACION DE EMPRESAS AL GRUPO***/

DELIMITER $$
DROP TRIGGER IF EXISTS after_grupos_empresas_delete $$
/**
*
* Trigger encargado de Gestionar las Acciones correspondientes a los siguientes cambios de estado de los Productos:
* - Al eliminar un Grupo se debe Notificar a todas las Empresas que pierden Visibilidad ( solo ven el Producto por estar en el Grupo )
* sobre el Producto y que son parte del Grupo al que se le esta quitando la Visibilidad sobre el Producto
*
**/
CREATE TRIGGER after_grupos_empresas_delete
AFTER DELETE
ON grupos_empresas FOR EACH ROW
BEGIN
DECLARE finished INTEGER DEFAULT 0;
DECLARE producto_id long;
    DEClARE cur_productos_grupo 
        CURSOR FOR 
            SELECT DISTINCT producto_id FROM grupo_visible_por WHERE grupo_id = OLD.grupo_id;
    DECLARE CONTINUE HANDLER 
        FOR NOT FOUND SET finished = 1;
        
    OPEN cur_productos_grupo;
 
    getProducto: LOOP
        FETCH cur_productos_grupo INTO producto_id;
        IF finished = 1 THEN 
            LEAVE getProducto;
        END IF;
        set @visible = 0;
        call productoPublico(producto_id, @visible);
        IF @visible = 0 THEN
		call productoVisibleDirectoPor(producto_id, OLD.empresa_id, @visible);
        END IF;
        IF @visible = 0 THEN
        call productoVisibleGrupoPor(producto_id, OLD.empresa_id, @visible);
        END IF;
		IF @visible = 0 THEN
		 set @proveedor_id = 0;
		 select empresa_id INTO @proveedor_id FROM productos AS p WHERE p.id = producto_id;
			 call  agregarAccionEmpresaProductoProveedor('DELETE', OLD.empresa_id,producto_id,@proveedor_id);
		 END IF;
        
    END LOOP getProducto;
    CLOSE cur_productos_grupo;
END$$
 
DELIMITER ;

DELIMITER $$
DROP TRIGGER IF EXISTS before_grupos_empresas_insert $$
/**
*
* Trigger encargado de Gestionar las Acciones correspondientes a los siguientes cambios de estado de los Productos:
* - Al agregar un Producto a un  Grupo se debe Notificar un POST solo a las Empresas que pasan a tener Visibilidad sobre el Producto por pertenecer al Grupo en cuestión.
*
**/
CREATE TRIGGER before_grupos_empresas_insert
BEFORE INSERT
ON grupos_empresas FOR EACH ROW
BEGIN
DECLARE finished INTEGER DEFAULT 0;
DECLARE producto_id long;
    DEClARE cur_productos_grupo 
        CURSOR FOR 
            SELECT DISTINCT producto_id FROM grupo_visible_por WHERE grupo_id = NEW.grupo_id;
    DECLARE CONTINUE HANDLER 
        FOR NOT FOUND SET finished = 1;
        
    OPEN cur_productos_grupo;
 
    getProducto: LOOP
        FETCH cur_productos_grupo INTO producto_id;
        IF finished = 1 THEN 
            LEAVE getProducto;
        END IF;
        set @visible = 0;
        call productoPublico(producto_id, @visible);
        IF @visible = 0 THEN
		call productoVisibleDirectoPor(producto_id, NEW.empresa_id, @visible);
        END IF;
        IF @visible = 0 THEN
        call productoVisibleGrupoPor(producto_id, NEW.empresa_id, @visible);
        END IF;
		IF @visible = 0 THEN
		 set @proveedor_id = 0;
		 select empresa_id INTO @proveedor_id FROM productos AS p WHERE p.id = producto_id;
			 call  agregarAccionEmpresaProductoProveedor('POST', NEW.empresa_id,producto_id,@proveedor_id);
		 END IF;
        
    END LOOP getProducto;
    CLOSE cur_productos_grupo;
END$$
 
DELIMITER ;


/***************** INICIO GESTION DE PRODUCTOS **********************/

DELIMITER $$
DROP TRIGGER IF EXISTS after_productos_insert $$
/**
*
* Trigger encargado de Gestionar las Acciones correspondientes a los siguientes cambios de estado de los Productos:
* - Al crear un Producto si es Público se debe Notificar un POST a todas las Empresas del Sistema
*
**/
CREATE TRIGGER after_productos_insert
AFTER INSERT
ON productos FOR EACH ROW
BEGIN
DECLARE finished INTEGER DEFAULT 0;
DECLARE empresa_id long;
    DEClARE cur_empresas
        CURSOR FOR 
            SELECT DISTINCT id FROM empresas WHERE id != NEW.empresa_id AND eliminado = 0;
	DECLARE CONTINUE HANDLER 
        FOR NOT FOUND SET finished = 1;
	SET @enabled = FALSE;
	call log(@enabled, CONCAT('AFTER-INSERT-PRODUCTO'),CONCAT('ID-',NEW.ID,'GTIN-CPP: ',NEW.gtin,'-',NEW.cpp,',DESCRIPCION-',NEW.descripcion));
 IF NEW.es_publico = 1 THEN
    OPEN cur_empresas;
    getEmpresa: LOOP
        FETCH cur_empresas INTO empresa_id;
        IF finished = 1 THEN 
            LEAVE getEmpresa;
        END IF;
		call  agregarAccionEmpresaProductoProveedor('POST', empresa_id,NEW.id,NEW.empresa_id);
    END LOOP getEmpresa;
    CLOSE cur_empresas;
 END IF;

END$$
 
DELIMITER ;

DELIMITER $$
DROP TRIGGER IF EXISTS after_productos_update_visibilidad $$ 
/**
*
* Trigger encargado de Gestionar las Acciones correspondientes a los siguientes cambios de estado de los Productos siguiendo la lógica siguiente en cuanto a los cambios de estado:
*  Oculto - Publico : Notificar POST a Todos
*  Oculto - Privado : Notificar POST a los que tienen visibilidad
*  Publico - Oculto : Notificar DELETE a todos
*  Publico - Privado : Notificar DELETE a los que no tienen visibilidad
*
**/
CREATE TRIGGER after_productos_update_visibilidad
AFTER UPDATE
ON productos FOR EACH ROW
method:BEGIN
DECLARE finished INTEGER DEFAULT 0;
DECLARE cliente_id long;
    DEClARE cur_empresas
        CURSOR FOR 
            SELECT DISTINCT id FROM empresas WHERE id != NEW.empresa_id AND eliminado = 0;
	DECLARE CONTINUE HANDLER 
        FOR NOT FOUND SET finished = 1;
	SET @enabled = FALSE;
	call log(@enabled, CONCAT('AFTER-UPDATE-PRODUCTO'),CONCAT('ID-',NEW.ID,'GTIN-CPP: ',NEW.gtin,'-',NEW.cpp,',DESCRIPCION-',NEW.descripcion));

	IF OLD.eliminado = 0 AND NEW.eliminado = 1 THEN
		LEAVE method;
	END IF;
	IF OLD.eliminado = 1 AND NEW.eliminado = 0 THEN
		OPEN cur_empresas;
			getEmpresa: LOOP
				FETCH cur_empresas INTO cliente_id;
				IF finished = 1 THEN 
					LEAVE getEmpresa;
				END IF;
				set @visible = 0;
				call productoPublico(NEW.id, @visible);
				IF @visible = 0 THEN
				call productoVisibleDirectoPor(NEW.id, cliente_id, @visible);
				END IF;
				IF @visible = 0 THEN
					call productoVisibleGrupoPor(NEW.id, cliente_id, @visible);
				 END IF;
				IF @visible = 1 THEN
					call  agregarAccionEmpresaProductoProveedor('POST', cliente_id,NEW.id,NEW.empresa_id);
                END IF;
			END LOOP getEmpresa;
		CLOSE cur_empresas;
        LEAVE method;
	LEAVE method;
	END IF;
	IF OLD.eliminado = NEW.eliminado AND OLD.es_publico = NEW.es_publico AND OLD.es_privado = NEW.es_privado THEN
		OPEN cur_empresas;
			getEmpresa: LOOP
				FETCH cur_empresas INTO cliente_id;
				IF finished = 1 THEN 
					LEAVE getEmpresa;
				END IF;
				set @visible = 0;
				call productoPublico(NEW.id, @visible);
				IF @visible = 0 THEN
				call productoVisibleDirectoPor(NEW.id, cliente_id, @visible);
				END IF;
				IF @visible = 0 THEN
					call productoVisibleGrupoPor(NEW.id, cliente_id, @visible);
				 END IF;
				IF @visible = 1 THEN
					call  agregarAccionEmpresaProductoProveedor('PUT', cliente_id,NEW.id,NEW.empresa_id);
                END IF;
			END LOOP getEmpresa;
			CLOSE cur_empresas;
            LEAVE method;
    END IF;

	IF OLD.es_publico = 0 AND OLD.es_privado = 0 THEN 		
		/** Producto probablemente saliendo de Estado Oculto **/
        IF NEw.es_publico = 1 THEN
			/** Producto pasa a estado Publico **/
			OPEN cur_empresas;
			getEmpresa: LOOP
				FETCH cur_empresas INTO cliente_id;
				IF finished = 1 THEN
					LEAVE getEmpresa;
				END IF;
				call  agregarAccionEmpresaProductoProveedor('POST', cliente_id,NEW.id,NEW.empresa_id);
			END LOOP getEmpresa;
			CLOSE cur_empresas;
            LEAVE method;
        END IF;
        IF NEw.es_privado = 1 THEN 
			/** Producto pasa a estado Privado **/
			OPEN cur_empresas;
			getEmpresa: LOOP
				FETCH cur_empresas INTO cliente_id;
				IF finished = 1 THEN 
					LEAVE getEmpresa;
				END IF;
                set @visible = 0;
				call productoPublico(NEW.id, @visible);
				IF @visible = 0 THEN
				call productoVisibleDirectoPor(NEW.id, cliente_id, @visible);
				END IF;
				IF @visible = 0 THEN
					call productoVisibleGrupoPor(NEW.id, cliente_id, @visible);
				 END IF;
				 IF @visible = 1 THEN
					call  agregarAccionEmpresaProductoProveedor('POST', cliente_id,NEW.id,NEW.empresa_id);
                END IF;
			END LOOP getEmpresa;
			CLOSE cur_empresas;
			LEAVE method;
        END IF;
        LEAVE method;
	END IF;
    
    IF OLD.es_publico = 1 THEN 
		/** Si no hubo cambio de estado Público no hago nada **/
		IF NEW.es_publico = 1	THEN
			LEAVE method;
		END IF;
		
        IF NEw.es_privado = 0 THEN 
			/** Producto pasa a estado Privado **/
            OPEN cur_empresas;
			getEmpresa: LOOP
				FETCH cur_empresas INTO cliente_id;
				IF finished = 1 THEN 
					LEAVE getEmpresa;
				END IF;
				call  agregarAccionEmpresaProductoProveedor('DELETE', cliente_id,NEW.id,NEW.empresa_id);
			END LOOP getEmpresa;
			CLOSE cur_empresas;
            LEAVE method;
        END IF;
        
        /** Producto paso a estado Privado **/
			OPEN cur_empresas;
			getEmpresa: LOOP
				FETCH cur_empresas INTO cliente_id;
				IF finished = 1 THEN 
					LEAVE getEmpresa;
				END IF;
                set @visible = 0;
				call productoPublico(NEW.id, @visible);
				IF @visible = 0 THEN
				call productoVisibleDirectoPor(NEW.id, cliente_id, @visible);
				END IF;
				IF @visible = 0 THEN
					call productoVisibleGrupoPor(NEW.id, cliente_id, @visible);
				END IF;
				IF @visible = 0 THEN
					call  agregarAccionEmpresaProductoProveedor('DELETE', cliente_id,NEW.id,NEW.empresa_id);
				END IF;
			END LOOP getEmpresa;
			CLOSE cur_empresas;
			LEAVE method;
        
    END IF;

END$$
 
DELIMITER ;

DELIMITER $$
DROP TRIGGER IF EXISTS before_productos_update_visibilidad $$ 
/**
*
* Trigger encargado de Gestionar las Acciones correspondientes a los siguientes cambios de estado de los Productos siguiendo la lógica siguiente en cuanto a los cambios de estado:
*  Privado - Publico : Notificar POST a los que no tienen visibilidad
*  Privado - Oculto : Notificar DELETE a los que tienen visibilidad
*
**/
CREATE TRIGGER before_productos_update_visibilidad
BEFORE UPDATE
ON productos FOR EACH ROW
method:BEGIN
DECLARE finished INTEGER DEFAULT 0;
DECLARE cliente_id long;
    DEClARE cur_empresas
        CURSOR FOR 
            SELECT DISTINCT id FROM empresas WHERE id != NEW.empresa_id AND eliminado = 0;
	DECLARE CONTINUE HANDLER 
        FOR NOT FOUND SET finished = 1;
	SET @enabled = FALSE;
	call log(@enabled, CONCAT('BEFORE-UPDATE-PRODUCTO'),CONCAT('ID-',NEW.ID,'GTIN-CPP: ',NEW.gtin,'-',NEW.cpp,',DESCRIPCION-',NEW.descripcion));

	IF OLD.eliminado = 0 AND NEW.eliminado = 1 THEN
		OPEN cur_empresas;
			getEmpresa: LOOP
				FETCH cur_empresas INTO cliente_id;
				IF finished = 1 THEN 
					LEAVE getEmpresa;
				END IF;
				call log(@enabled, CONCAT('BEFORE-UPDATE-PRODUCTO-ELIMINAR'),CONCAT('ID-',NEW.ID,'GTIN-CPP: ',NEW.gtin,'-',NEW.cpp,',DESCRIPCION-',NEW.descripcion));
				set @visible = 0;
				call productoPublico(NEW.id, @visible);
				IF @visible = 0 THEN
				call productoVisibleDirectoPor(NEW.id, cliente_id, @visible);
				END IF;
				IF @visible = 0 THEN
					call productoVisibleGrupoPor(NEW.id, cliente_id, @visible);
				 END IF;
				IF @visible = 1 THEN
					call  agregarAccionEmpresaProductoProveedor('DELETE', cliente_id,NEW.id,NEW.empresa_id);
                END IF;
			END LOOP getEmpresa;
			CLOSE cur_empresas;
            LEAVE method;
    END IF;

	IF OLD.es_publico = 0 AND OLD.es_privado = 1 THEN
		IF NEW.es_publico = 1 THEN
			OPEN cur_empresas;
			getEmpresa: LOOP
				FETCH cur_empresas INTO cliente_id;
				IF finished = 1 THEN 
					LEAVE getEmpresa;
				END IF;
                set @visible = 0;
				call productoPublico(NEW.id, @visible);
				IF @visible = 0 THEN
				call productoVisibleDirectoPor(NEW.id, cliente_id, @visible);
				END IF;
				IF @visible = 0 THEN
					call productoVisibleGrupoPor(NEW.id, cliente_id, @visible);
				END IF;
				IF @visible = 0 THEN
					call  agregarAccionEmpresaProductoProveedor('POST', cliente_id,NEW.id,NEW.empresa_id);
				END IF;
			END LOOP getEmpresa;
			CLOSE cur_empresas;
			LEAVE method;
        END IF;
        IF NEW.es_publico = 0 THEN 
			OPEN cur_empresas;
			getEmpresa: LOOP
				FETCH cur_empresas INTO cliente_id;
				IF finished = 1 THEN
					LEAVE getEmpresa;
				END IF;
                set @visible = 0;
				call productoPublico(NEW.id, @visible);
				IF @visible = 0 THEN
				call productoVisibleDirectoPor(NEW.id, cliente_id, @visible);
				END IF;
				IF @visible = 0 THEN
					call productoVisibleGrupoPor(NEW.id, cliente_id, @visible);
				END IF;
				IF @visible = 1 THEN
					call  agregarAccionEmpresaProductoProveedor('DELETE', cliente_id,NEW.id,NEW.empresa_id);
				END IF;
			END LOOP getEmpresa;
			CLOSE cur_empresas;
			LEAVE method;
        END IF;
    END IF;
    
END$$
 
DELIMITER ;