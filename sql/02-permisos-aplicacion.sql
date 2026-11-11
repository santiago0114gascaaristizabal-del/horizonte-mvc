-- Ejecutar DESPUES de crear el login SQL y mapearlo a HorizonteMVC en SSMS.
-- El nombre horizonte_app es un ejemplo: sustituir por el usuario elegido.
-- No crea ni modifica contrasenas o cuentas existentes.
USE HorizonteMVC;
GO
GRANT SELECT, INSERT, UPDATE, DELETE ON OBJECT::dbo.proyectos TO [horizonte_app];
GRANT SELECT, INSERT, UPDATE, DELETE ON OBJECT::dbo.entregables TO [horizonte_app];
GO
