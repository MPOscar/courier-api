
DROP TABLE IF EXISTS listas_de_venta;
CREATE TABLE listas_de_venta (
  id INT NOT NULL AUTO_INCREMENT,
  empresa_id INT NULL,
  nombre VARCHAR(256) NOT NULL,
  descripcion VARCHAR(512) NOT NULL,
  fecha_creacion DATETIME NULL,
  fecha_edicion DATETIME NULL,
  eliminado TINYINT NULL DEFAULT 0,
  PRIMARY KEY (id)
  )ENGINE=InnoDB DEFAULT CHARSET=utf8;
  
  DROP TABLE IF EXISTS productos_listas_de_venta;
CREATE TABLE productos_listas_de_venta (
  id INT NOT NULL AUTO_INCREMENT,
  lista_de_venta_id INT NOT NULL,
  producto_id INT NOT NULL,
  fecha_creacion DATETIME NULL,
  fecha_edicion DATETIME NULL,
  eliminado TINYINT NULL DEFAULT 0,
  PRIMARY KEY (id)
  )ENGINE=InnoDB DEFAULT CHARSET=utf8;
  
    
  DROP TABLE IF EXISTS empresas_listas_de_venta;
CREATE TABLE empresas_listas_de_venta (
  id INT NOT NULL AUTO_INCREMENT,
  lista_de_venta_id INT NOT NULL,
  empresa_id INT NOT NULL,
  fecha_creacion DATETIME NULL,
  fecha_edicion DATETIME NULL,
  eliminado TINYINT NULL DEFAULT 0,
  PRIMARY KEY (id)
  )ENGINE=InnoDB DEFAULT CHARSET=utf8;