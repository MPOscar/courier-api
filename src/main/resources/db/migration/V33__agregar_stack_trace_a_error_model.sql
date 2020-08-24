ALTER TABLE `catalogo`.`errors` 
ADD COLUMN `stack_trace` varchar(255) NULL AFTER `user_id`;