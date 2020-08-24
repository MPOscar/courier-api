ALTER TABLE productos 
ADD COLUMN mercado_objetivo VARCHAR(255) NULL DEFAULT NULL AFTER descripcion,
ADD COLUMN unidad_medida_peso_bruto VARCHAR(3) NULL AFTER peso_bruto,
ADD COLUMN nivel_minimo_venta INT(11) NULL DEFAULT 1 AFTER unidad_medida_peso_bruto,
ADD COLUMN gtin_presentacion VARCHAR(20) NULL DEFAULT NULL AFTER gtin;
ALTER TABLE productos 
DROP INDEX productos;


ALTER TABLE empaques
ADD COLUMN cpp VARCHAR(45) NULL DEFAULT NULL AFTER gtin;

DROP TABLE IF EXISTS pallets;
CREATE TABLE pallets (
  id INT(11) NOT NULL,
  fecha_edicion DATETIME NULL,
  eliminado TINYINT(4) NULL,
  alto VARCHAR(255) NULL,
  ancho VARCHAR(255)NULL,
  profundidad VARCHAR(255) NULL,
  unidades_venta VARCHAR(255) NULL,
  cajas VARCHAR(255) NULL,
  camadas VARCHAR(255) NULL,
  fecha_creacion DATETIME NULL,
  PRIMARY KEY (id)) ENGINE=InnoDB DEFAULT CHARSET=utf8;
  
  ALTER TABLE pallets 
CHANGE COLUMN id id INT(11) NOT NULL AUTO_INCREMENT ;



ALTER TABLE empaques 
CHANGE COLUMN cantidad_minima pallet_id INT(11) NULL DEFAULT NULL ;



