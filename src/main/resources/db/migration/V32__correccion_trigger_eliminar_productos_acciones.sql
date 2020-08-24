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
                set @visible = 1;
				IF @visible = 1 THEN
					call  agregarAccionEmpresaProductoProveedor('DELETE', cliente_id,NEW.id,NEW.empresa_id);
                END IF;
			END LOOP getEmpresa;
			CLOSE cur_empresas;
            LEAVE method;
    END IF;

	IF OLD.es_publico = 0 AND OLD.es_privado = 1 AND NEW.eliminado = 0 THEN
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