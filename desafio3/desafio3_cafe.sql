USE master;
GO

IF DB_ID(N'SistemaPedidosCafe') IS NOT NULL
BEGIN
    ALTER DATABASE SistemaPedidosCafe SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE SistemaPedidosCafe;
END
GO

CREATE DATABASE SistemaPedidosCafe;
GO

USE SistemaPedidosCafe;
GO

CREATE TABLE Cliente (
    id_cliente     INT IDENTITY(1,1) NOT NULL,
    nombre         NVARCHAR(100)     NOT NULL,
    telefono       VARCHAR(20)       NOT NULL,
    fecha_registro DATETIME2(0)      NOT NULL CONSTRAINT DF_Cliente_fecha_registro DEFAULT (SYSDATETIME()),
    CONSTRAINT PK_Cliente          PRIMARY KEY (id_cliente),
    CONSTRAINT UQ_Cliente_telefono UNIQUE (telefono)
);
GO

CREATE TABLE Categoria (
    id_categoria INT IDENTITY(1,1) NOT NULL,
    nombre       NVARCHAR(50)      NOT NULL,
    CONSTRAINT PK_Categoria        PRIMARY KEY (id_categoria),
    CONSTRAINT UQ_Categoria_nombre UNIQUE (nombre)
);
GO

CREATE TABLE Estado_Pedido (
    id_estado TINYINT     NOT NULL,
    nombre    VARCHAR(20) NOT NULL,
    CONSTRAINT PK_Estado_Pedido        PRIMARY KEY (id_estado),
    CONSTRAINT UQ_Estado_Pedido_nombre UNIQUE (nombre)
);
GO

CREATE TABLE Producto (
    id_producto  INT IDENTITY(1,1) NOT NULL,
    nombre       NVARCHAR(100)     NOT NULL,
    descripcion  NVARCHAR(255)     NULL,
    precio       DECIMAL(10,2)     NOT NULL,
    id_categoria INT               NOT NULL,
    activo       BIT               NOT NULL CONSTRAINT DF_Producto_activo DEFAULT (1),
    CONSTRAINT PK_Producto           PRIMARY KEY (id_producto),
    CONSTRAINT UQ_Producto_nombre    UNIQUE (nombre),
    CONSTRAINT CK_Producto_precio    CHECK (precio > 0),
    CONSTRAINT FK_Producto_Categoria FOREIGN KEY (id_categoria) REFERENCES Categoria (id_categoria)
);
GO

CREATE TABLE Pedido (
    id_pedido  INT IDENTITY(1,1) NOT NULL,
    id_cliente INT               NOT NULL,
    id_estado  TINYINT           NOT NULL CONSTRAINT DF_Pedido_estado DEFAULT (1),
    fecha      DATETIME2(0)      NOT NULL CONSTRAINT DF_Pedido_fecha DEFAULT (SYSDATETIME()),
    CONSTRAINT PK_Pedido         PRIMARY KEY (id_pedido),
    CONSTRAINT FK_Pedido_Cliente FOREIGN KEY (id_cliente) REFERENCES Cliente (id_cliente),
    CONSTRAINT FK_Pedido_Estado  FOREIGN KEY (id_estado)  REFERENCES Estado_Pedido (id_estado)
);
GO

CREATE TABLE Detalle_Pedido (
    id_pedido       INT          NOT NULL,
    id_producto     INT          NOT NULL,
    cantidad        INT          NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    CONSTRAINT PK_Detalle_Pedido          PRIMARY KEY (id_pedido, id_producto),
    CONSTRAINT CK_Detalle_cantidad        CHECK (cantidad > 0),
    CONSTRAINT CK_Detalle_precio_unitario CHECK (precio_unitario > 0),
    CONSTRAINT FK_Detalle_Pedido          FOREIGN KEY (id_pedido)   REFERENCES Pedido (id_pedido),
    CONSTRAINT FK_Detalle_Producto        FOREIGN KEY (id_producto) REFERENCES Producto (id_producto)
);
GO

CREATE INDEX IX_Producto_id_categoria ON Producto (id_categoria);
CREATE INDEX IX_Pedido_id_cliente     ON Pedido (id_cliente);
CREATE INDEX IX_Pedido_estado_fecha   ON Pedido (id_estado, fecha) INCLUDE (id_cliente);
CREATE INDEX IX_Detalle_id_producto   ON Detalle_Pedido (id_producto);
GO

INSERT INTO Estado_Pedido (id_estado, nombre) VALUES
    (1, 'Pendiente'),
    (2, 'Preparado'),
    (3, 'Entregado');

INSERT INTO Cliente (nombre, telefono) VALUES
    (N'María López',  '7777-1111'),
    (N'Carlos Pérez', '7777-2222');

INSERT INTO Categoria (nombre) VALUES
    (N'Cafés Calientes'),
    (N'Pastelería');

INSERT INTO Producto (nombre, descripcion, precio, id_categoria) VALUES
    (N'Espresso',            N'Café corto e intenso de 30 ml',                1.50, 1),
    (N'Cappuccino',          N'Espresso con leche vaporizada y espuma',       2.75, 1),
    (N'Croissant',           N'Croissant de mantequilla horneado al día',     1.80, 2),
    (N'Pastel de chocolate', N'Porción de pastel de chocolate con ganache',   3.25, 2);

INSERT INTO Pedido (id_cliente, id_estado, fecha) VALUES
    (1, 1, '2026-05-30T09:15:00'),
    (2, 2, '2026-05-30T09:40:00'),
    (1, 3, '2026-05-29T16:05:00');

INSERT INTO Detalle_Pedido (id_pedido, id_producto, cantidad, precio_unitario) VALUES
    (1, 2, 2, 2.75),
    (1, 3, 1, 1.80),
    (2, 1, 1, 1.50),
    (2, 4, 1, 3.25),
    (3, 2, 1, 2.75);
GO

CREATE OR ALTER VIEW Vista_Inventario_Precios
AS
SELECT
    p.nombre AS producto,
    p.precio AS precio,
    c.nombre AS categoria
FROM Producto AS p
INNER JOIN Categoria AS c ON c.id_categoria = p.id_categoria;
GO

CREATE OR ALTER VIEW Vista_Pedidos_Pendientes
AS
SELECT
    pe.id_pedido AS id_pedido,
    pe.fecha     AS fecha_pedido,
    cl.nombre    AS cliente
FROM Pedido AS pe
INNER JOIN Cliente       AS cl ON cl.id_cliente = pe.id_cliente
INNER JOIN Estado_Pedido AS es ON es.id_estado  = pe.id_estado
WHERE es.nombre IN ('Pendiente', 'Preparado');
GO

SELECT * FROM Vista_Inventario_Precios;
SELECT * FROM Vista_Pedidos_Pendientes;
GO
