DROP TABLE IF EXISTS usuarios_codigos;
CREATE TABLE usuarios_codigos (
  id INT(11) NOT NULL AUTO_INCREMENT,
  codigo VARCHAR(8) NULL,
  usuario_id INT(11) NULL,
  usado tinyint(4) NOT NULL DEFAULT '0',
  expirado tinyint(4) NOT NULL DEFAULT '0',
  used_on DATETIME NULL,
  created_on DATETIME NULL,
  PRIMARY KEY (id)
  )ENGINE=InnoDB DEFAULT CHARSET=utf8;

