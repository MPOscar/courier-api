ALTER TABLE `usuarios` 
ADD UNIQUE INDEX `usuario_unico` (`usuario` ASC);
ALTER TABLE `usuarios` 
ADD UNIQUE INDEX `mail_unico` (`email` ASC);