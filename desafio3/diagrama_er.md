# Diagrama Entidad-Relación – Sistema de Pedidos de Café

```mermaid
erDiagram
    CLIENTE       ||--o{ PEDIDO         : realiza
    CATEGORIA     ||--o{ PRODUCTO       : agrupa
    ESTADO_PEDIDO ||--o{ PEDIDO         : clasifica
    PEDIDO        ||--|{ DETALLE_PEDIDO : contiene
    PRODUCTO      ||--o{ DETALLE_PEDIDO : aparece_en

    CLIENTE {
        int id_cliente PK
        nvarchar nombre
        varchar telefono UK
        datetime2 fecha_registro
    }
    CATEGORIA {
        int id_categoria PK
        nvarchar nombre UK
    }
    ESTADO_PEDIDO {
        tinyint id_estado PK
        varchar nombre UK
    }
    PRODUCTO {
        int id_producto PK
        nvarchar nombre UK
        nvarchar descripcion
        decimal precio
        int id_categoria FK
        bit activo
    }
    PEDIDO {
        int id_pedido PK
        int id_cliente FK
        tinyint id_estado FK
        datetime2 fecha
    }
    DETALLE_PEDIDO {
        int id_pedido PK, FK
        int id_producto PK, FK
        int cantidad
        decimal precio_unitario
    }
```

## Cardinalidades

| Relación | Cardinalidad |
|---|---|
| Cliente – Pedido | 1:N |
| Categoría – Producto | 1:N |
| Estado_Pedido – Pedido | 1:N |
| Pedido – Producto | N:M, resuelta con `Detalle_Pedido` (PK compuesta `id_pedido, id_producto`) |
