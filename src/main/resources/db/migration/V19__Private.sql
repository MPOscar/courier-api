ALTER TABLE `productos` 
ADD COLUMN `es_privado` TINYINT(4) NULL DEFAULT '0' AFTER `es_promo`;
