USE master;
GO
IF DB_ID(N'HorizonteMVC') IS NULL
    CREATE DATABASE HorizonteMVC;
GO
USE HorizonteMVC;
GO
IF OBJECT_ID(N'dbo.proyectos', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.proyectos (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nombre NVARCHAR(120) NOT NULL,
        descripcion NVARCHAR(500) NOT NULL
    );
END;
GO
IF OBJECT_ID(N'dbo.entregables', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.entregables (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        titulo NVARCHAR(120) NOT NULL,
        fecha DATE NOT NULL,
        proyecto_id BIGINT NOT NULL,
        CONSTRAINT fk_entregables_proyecto FOREIGN KEY (proyecto_id)
            REFERENCES dbo.proyectos(id) ON DELETE NO ACTION
    );
    CREATE INDEX ix_entregables_proyecto ON dbo.entregables(proyecto_id);
END;
GO
