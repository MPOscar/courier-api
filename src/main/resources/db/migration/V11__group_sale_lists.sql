DROP TABLE IF EXISTS grupos_listas_de_venta;
CREATE TABLE grupos_listas_de_venta (
  id INT NOT NULL AUTO_INCREMENT,
  lista_de_venta_id INT NOT NULL,
  grupo_id INT NOT NULL,
  fecha_creacion DATETIME NULL,
  fecha_edicion DATETIME NULL,
  eliminado TINYINT NULL DEFAULT 0,
  PRIMARY KEY (id)
  )ENGINE=InnoDB DEFAULT CHARSET=utf8;