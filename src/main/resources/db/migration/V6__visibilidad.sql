ALTER TABLE productos 
ADD COLUMN visibilidad_asignada TINYINT(4) NULL DEFAULT 0 AFTER peso_bruto;

DROP TABLE IF EXISTS grupo_visible_por;
CREATE TABLE grupo_visible_por (
  id INT NOT NULL AUTO_INCREMENT,
  producto_id INT NULL,
  grupo_id INT NULL,
  fecha_creacion DATETIME NULL,
  fecha_edicion DATETIME NULL,
  eliminado TINYINT NULL DEFAULT 0,
  PRIMARY KEY (id)
  )ENGINE=InnoDB DEFAULT CHARSET=utf8;