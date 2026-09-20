-- =====================================================================
-- Cafe Don Bosco - Esquema de base de datos (MySQL 8+)
-- =====================================================================

CREATE DATABASE IF NOT EXISTS cafe_don_bosco
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE cafe_don_bosco;

-- ---------------------------------------------------------------------
-- Usuario: administradores y consumidores registrados
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(80)  NOT NULL,
    apellido    VARCHAR(80)  NOT NULL,
    correo      VARCHAR(120) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    rol         ENUM('ADMINISTRADOR', 'CONSUMIDOR') NOT NULL,
    activo      BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Categoria: agrupacion de productos del catalogo
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categoria (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(60)  NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    activo      BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Producto: cafe, bebidas, postres y comida del catalogo
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS producto (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    categoria_id        INT NOT NULL,
    nombre              VARCHAR(120) NOT NULL,
    descripcion         VARCHAR(500),
    precio              DECIMAL(10,2) NOT NULL,
    imagen              VARCHAR(255),
    tiempo_preparacion  VARCHAR(60),
    activo              BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (categoria_id) REFERENCES categoria(id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Inventario: existencias por producto (1 a 1 con producto)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS inventario (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    producto_id     INT NOT NULL UNIQUE,
    cantidad        INT NOT NULL DEFAULT 0,
    stock_minimo    INT NOT NULL DEFAULT 5,
    CONSTRAINT fk_inventario_producto
        FOREIGN KEY (producto_id) REFERENCES producto(id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Venta: modelo unificado para ventas presenciales (POS) y pedidos web.
-- TipoVenta distingue el origen; el consumidor invitado no requiere
-- usuario_id, por lo que se guardan los datos de contacto en la venta.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS venta (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id          INT NULL,
    tipo_venta          ENUM('PRESENCIAL', 'WEB') NOT NULL,
    estado              ENUM('PENDIENTE', 'PAGADA', 'COMPLETADA', 'CANCELADA') NOT NULL DEFAULT 'PENDIENTE',
    subtotal            DECIMAL(10,2) NOT NULL,
    envio               DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total               DECIMAL(10,2) NOT NULL,
    metodo_pago         VARCHAR(30),
    estado_pago         VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE',
    tipo_entrega        VARCHAR(20),
    nombre_cliente      VARCHAR(150),
    correo_cliente      VARCHAR(120),
    telefono_cliente    VARCHAR(30),
    direccion_cliente   VARCHAR(255),
    notas               VARCHAR(500),
    token_ticket        VARCHAR(64) NOT NULL UNIQUE,
    fecha               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_venta_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- DetalleVenta: productos incluidos en cada venta (precio historico)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS detalle_venta (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    venta_id            INT NOT NULL,
    producto_id         INT NOT NULL,
    nombre_producto     VARCHAR(120) NOT NULL,
    cantidad            INT NOT NULL,
    precio_unitario     DECIMAL(10,2) NOT NULL,
    subtotal            DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_detalleventa_venta
        FOREIGN KEY (venta_id) REFERENCES venta(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalleventa_producto
        FOREIGN KEY (producto_id) REFERENCES producto(id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Compra: ingreso de mercaderia registrado por el administrador
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS compra (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    proveedor   VARCHAR(150) NOT NULL,
    usuario_id  INT NOT NULL,
    total       DECIMAL(10,2) NOT NULL,
    fecha       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_compra_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- DetalleCompra: productos ingresados en cada compra
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS detalle_compra (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    compra_id           INT NOT NULL,
    producto_id         INT NOT NULL,
    cantidad            INT NOT NULL,
    costo_unitario      DECIMAL(10,2) NOT NULL,
    subtotal            DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_detallecompra_compra
        FOREIGN KEY (compra_id) REFERENCES compra(id) ON DELETE CASCADE,
    CONSTRAINT fk_detallecompra_producto
        FOREIGN KEY (producto_id) REFERENCES producto(id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Datos iniciales
-- ---------------------------------------------------------------------

-- Administrador por defecto (password: Admin123! -- cambiar en produccion)
-- Hash BCrypt generado con PasswordUtil.hashear("Admin123!")
INSERT INTO usuario (nombre, apellido, correo, password, rol, activo)
VALUES ('Scarlett', 'Torres', 'admin@cafedonbosco.com',
        '$2a$12$B4q7wzKyFepDwVIgCCjfCesC3unEGtvFQIN3HwrlliAe4e88f6lGq',
        'ADMINISTRADOR', TRUE)
ON DUPLICATE KEY UPDATE correo = correo;

INSERT INTO categoria (nombre, descripcion, activo) VALUES
    ('Cafe', 'Bebidas a base de espresso', TRUE),
    ('Bebidas', 'Bebidas frias y calientes', TRUE),
    ('Postres', 'Reposteria y dulces', TRUE),
    ('Comida', 'Sandwiches y snacks', TRUE)
ON DUPLICATE KEY UPDATE nombre = nombre;

-- Productos de ejemplo para poder probar el catalogo sin cargar datos a mano
INSERT INTO producto (categoria_id, nombre, descripcion, precio, imagen, tiempo_preparacion, activo)
SELECT id, 'Cafe Latte', 'Cafe espresso con leche vaporizada.', 2.50, NULL, '2 - 3 minutos', TRUE
FROM categoria WHERE nombre = 'Cafe'
UNION ALL
SELECT id, 'Cafe Americano', 'Cafe puro, de sabor intenso.', 2.00, NULL, '1 - 2 minutos', TRUE
FROM categoria WHERE nombre = 'Cafe'
UNION ALL
SELECT id, 'Frappe de vainilla', 'Refrescante y cremoso.', 3.00, NULL, '3 - 4 minutos', TRUE
FROM categoria WHERE nombre = 'Bebidas'
UNION ALL
SELECT id, 'Pastel de chocolate', 'Suave, intenso y delicioso.', 3.50, NULL, NULL, TRUE
FROM categoria WHERE nombre = 'Postres'
UNION ALL
SELECT id, 'Croissant', 'Hojaldre artesanal.', 2.00, NULL, NULL, TRUE
FROM categoria WHERE nombre = 'Comida';

INSERT INTO inventario (producto_id, cantidad, stock_minimo)
SELECT id, 30, 5 FROM producto WHERE NOT EXISTS (
    SELECT 1 FROM inventario WHERE inventario.producto_id = producto.id
);
