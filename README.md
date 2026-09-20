# Cafe Don Bosco - Backend

Backend Java (Jakarta EE 10 / Servlets + JDBC + MySQL) para Cafe Don Bosco.
Expone una API REST compartida que atiende tanto el panel del
administrador como la tienda del consumidor, con una unica base de datos
e inventario.

## Arquitectura

```
controller (Servlets) -> service (reglas de negocio) -> dao (JDBC) -> MySQL
```

- **model**: entidades del dominio (`Usuario`, `Categoria`, `Producto`,
  `Inventario`, `Venta`, `DetalleVenta`, `Compra`, `DetalleCompra`,
  `Carrito`).
- **dto**: objetos de entrada (`request`) y salida (`response`) que no
  exponen directamente las entidades (por ejemplo, la contrasena nunca
  viaja en una respuesta).
- **dao / dao.impl**: acceso a datos con JDBC puro y `PreparedStatement`.
- **service / service.impl**: validaciones y logica de negocio, incluida
  la transaccion de venta (registro + descuento de stock atomico).
- **controller**: Servlets anotados con `@WebServlet` que exponen la API
  bajo `/api/...`.
- **filter**: `CorsFilter` (global) y `RolAdminFilter` (protege
  `/api/admin/*`).
- **util**: `ConexionBD`, `PasswordUtil` (BCrypt), `JsonUtil` (Gson),
  `ValidacionUtil`, `SessionUtil`, `Constantes`.
- **exception**: `AppException` y subclases especificas, todas mapeadas a
  un codigo HTTP y un mensaje seguro para el cliente.

### Modelo de venta unificado

En vez de duplicar `Venta` y `Pedido`, se usa una sola entidad `Venta`
con `TipoVenta` (`PRESENCIAL` o `WEB`). Esto evita mantener dos historiales
y dos formas de descontar inventario: la venta del mostrador (POS del
administrador) y la compra del consumidor comparten exactamente la misma
transaccion (`VentaServiceImpl.registrarConTransaccion`).

## Requisitos

- Java 17+
- Maven 3.9+
- MySQL 8+
- Apache Tomcat 10.1+ (Jakarta EE 10 / Servlet 6.0)

## Configuracion de la base de datos

1. Ejecuta el script `db/schema.sql` en tu servidor MySQL:

   ```bash
   mysql -u root -p < db/schema.sql
   ```

   Esto crea la base `cafe_don_bosco`, todas las tablas y datos iniciales
   (categorias y un usuario administrador).

2. Por defecto, `ConexionBD` se conecta a
   `jdbc:mysql://localhost:3306/cafe_don_bosco` con el usuario `root` y
   contrasena vacia. Para otro entorno, define las variables de entorno
   antes de desplegar (no se necesita recompilar):

   ```bash
   export DB_URL="jdbc:mysql://localhost:3306/cafe_don_bosco?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
   export DB_USUARIO="root"
   export DB_PASSWORD="tu_password"
   ```

## Compilar y desplegar

```bash
mvn clean package
```

Esto genera `target/CafeDonBosco.war`. Copialo a la carpeta `webapps` de
Tomcat (o despliegalo con el manager de Tomcat) y la aplicacion quedara
disponible en `http://localhost:8080/CafeDonBosco/`.

## Endpoints principales

| Metodo | Ruta | Acceso | Descripcion |
| --- | --- | --- | --- |
| POST | `/api/auth/login` | Publico | Login compartido admin/consumidor |
| POST | `/api/auth/registro` | Publico | Registro de consumidor |
| GET | `/api/auth/sesion` | Publico | Usuario de la sesion actual |
| POST | `/api/auth/logout` | Publico | Cierra la sesion |
| GET | `/api/categorias` | Publico | Categorias activas |
| GET | `/api/productos` | Publico | Catalogo (filtros: `categoria`, `buscar`, `orden`) |
| GET | `/api/productos/{id}` | Publico | Detalle de producto |
| GET | `/api/productos/{id}/relacionados` | Publico | Productos de la misma categoria |
| GET/POST/PUT/DELETE | `/api/carrito`, `/api/carrito/items[/{id}]` | Publico (sesion) | Carrito de compras |
| POST | `/api/checkout` | Publico (sesion) | Registra la venta WEB y descuenta stock |
| GET | `/api/tickets/{token}` | Publico | Ticket de una venta por token aleatorio |
| GET/POST | `/api/admin/categorias` | Admin | Listar/crear categorias |
| PUT | `/api/admin/categorias/{id}` | Admin | Editar categoria |
| GET/POST | `/api/admin/productos` | Admin | Listar/crear productos (con stock) |
| PUT | `/api/admin/productos/{id}` | Admin | Editar producto |
| PUT | `/api/admin/productos/{id}/estado` | Admin | Activar/desactivar producto |
| GET | `/api/admin/inventario` | Admin | Inventario con stock exacto |
| PUT | `/api/admin/inventario` | Admin | Ajustar stock/stock minimo |
| GET/POST | `/api/admin/ventas` | Admin | Historial / registrar venta presencial (POS) |
| GET | `/api/admin/tickets/{id}` | Admin | Ticket de cualquier venta por id |
| GET/POST | `/api/admin/compras` | Admin | Historial / registrar compra a proveedor |
| GET | `/api/admin/dashboard` | Admin | KPIs, ventas recientes, stock bajo |

Todas las respuestas usan el sobre `ApiResponse`:

```json
{ "exitoso": true, "mensaje": "...", "datos": { } }
```

## Seguridad implementada

- Contrasenas con BCrypt (`jbcrypt`), nunca se devuelven en las respuestas.
- `PreparedStatement` en todo el acceso a datos.
- El precio y el stock se revalidan en el servidor durante el checkout;
  nunca se confia en lo que envia el navegador.
- El descuento de stock usa `UPDATE ... WHERE cantidad >= ?` dentro de una
  transaccion JDBC, para evitar sobreventa con solicitudes concurrentes.
- El ticket del consumidor se consulta por un token aleatorio
  (`UUID`), no por el id incremental de la venta.
- `RolAdminFilter` protege toda la seccion `/api/admin/*`.

## Usuario administrador de prueba

El script `db/schema.sql` crea `admin@cafedonbosco.com`. Cambia esa
contrasena (o genera un nuevo hash con `PasswordUtil.hashear(...)`) antes
de usar el sistema en un entorno real.

## Pendiente para siguientes fases

- Frontend (JSP/HTML + JS) consumiendo esta API.
- "Mis pedidos" para el consumidor autenticado.
- Generacion de PDF real del ticket (hoy se sirve como JSON para que el
  frontend lo renderice).
