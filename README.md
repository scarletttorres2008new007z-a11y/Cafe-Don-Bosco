# Cafe Don Bosco

Sistema web completo para una cafeteria: tienda para el consumidor (catalogo,
personalizacion de productos, carrito, checkout, ticket) y panel para el
administrador (dashboard, productos, inventario, ventas, compras a
proveedores), sobre una unica base de datos MySQL. Backend en Java puro
(Jakarta EE 10: Servlets + JSP + JDBC), sin frameworks tipo Spring.

## Stack y requisitos

- **Java 17+**
- **Maven 3.9+**
- **MySQL 8+** (o MariaDB, con la salvedad de la seccion de XAMPP mas abajo)
- **Apache Tomcat 10.1+** (Jakarta EE 10 / Servlet 6.0 — Tomcat 9 o menor
  **no** sirve este proyecto, usan la API `javax.servlet` en vez de
  `jakarta.servlet`)

---

## Como ejecutar el proyecto desde cero

Esta seccion asume que nunca has corrido el proyecto en tu maquina. Cubre
las dos piezas que hacen falta ademas del codigo: la base de datos (via
XAMPP) y el servidor de aplicaciones (via el plugin Smart Tomcat de
IntelliJ IDEA).

### 1. Base de datos con XAMPP

XAMPP es un paquete que trae Apache, MySQL (o MariaDB), PHP y phpMyAdmin
ya instalados y listos para usar. De todo eso, este proyecto **solo
necesita el servicio de MySQL** (Apache/PHP de XAMPP no se usan; la
aplicacion Java la sirve Tomcat, no XAMPP).

1. Instala XAMPP (https://www.apachefriends.org) si no lo tienes.
2. Abre el **Panel de control de XAMPP** e inicia el modulo **MySQL**
   (el boton "Start" a la par de MySQL). No hace falta iniciar Apache
   para este proyecto.
3. Abre **phpMyAdmin** (boton "Admin" junto a MySQL, o
   `http://localhost/phpmyadmin`).
4. Importa el esquema:
   - Pestana **Import** → selecciona el archivo `db/schema.sql` de este
     repositorio → **Go**.
   - O, si prefieres la pestana **SQL**, abre `db/schema.sql` con un
     editor de texto, copia todo su contenido, pegalo ahi y dale **Go**.
5. Verifica que se haya creado la base `cafe_don_bosco` con sus tablas
   (panel izquierdo de phpMyAdmin) y datos iniciales: categorias,
   5 productos de ejemplo, un usuario administrador y los grupos de
   personalizacion (Tipo de leche, Nivel de azucar, Tamano, Extras,
   Acompanamientos).

El script es seguro de volver a correr si algo sale mal a medias (usa
`CREATE TABLE IF NOT EXISTS` y `ON DUPLICATE KEY UPDATE`): no duplica
datos ni borra lo que ya tengas.

**Conexion por defecto:** el backend se conecta a
`jdbc:mysql://localhost:3306/cafe_don_bosco` con usuario `root` y
contrasena vacia — que es exactamente la configuracion por defecto de
MySQL en XAMPP, asi que normalmente **no hace falta configurar nada
mas**. Si tu MySQL tiene otro usuario/contrasena, define las variables
de entorno (ver `.env.example`) antes de desplegar:

```bash
export DB_URL="jdbc:mysql://localhost:3306/cafe_don_bosco?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
export DB_USUARIO="root"
export DB_PASSWORD="tu_password"
```

### 2. Servidor de aplicaciones con Smart Tomcat (IntelliJ IDEA)

**Smart Tomcat** es un plugin de IntelliJ que evita el paso manual de
"compilar → empaquetar en WAR → copiar a la carpeta de Tomcat →
reiniciar" cada vez que cambias algo: despliega el proyecto directo
desde el IDE contra una instalacion de Tomcat que tu ya tienes en disco.

1. **Descarga Tomcat 10.1+** (no lo trae XAMPP ni Smart Tomcat): baja el
   ZIP desde https://tomcat.apache.org/download-10.cgi y descomprimelo
   en cualquier carpeta, por ejemplo `C:\tomcat10` o `~/tomcat10`.
2. **Instala el plugin**: en IntelliJ, `File > Settings > Plugins >
   Marketplace`, busca **Smart Tomcat**, instalalo y reinicia el IDE.
3. **Abre este proyecto** en IntelliJ (`File > Open`, selecciona la
   carpeta `Cafe-Don-Bosco`) y deja que termine de importar las
   dependencias de Maven (barra de progreso abajo a la derecha).
4. **Crea la configuracion de ejecucion**:
   - `Run > Edit Configurations… > + > Smart Tomcat`.
   - **Tomcat Server**: `Configure…` y selecciona la carpeta donde
     descomprimiste Tomcat en el paso 1.
   - **Deployment directory**: selecciona el modulo web del proyecto
     (IntelliJ suele detectarlo solo como `Cafe-Don-Bosco:war exploded`
     o similar, gracias al empaquetado `war` del `pom.xml`).
   - **Context path**: escribe `/` si quieres que el sitio quede en
     `http://localhost:8080/` directo; dejalo con el nombre por
     defecto (`/CafeDonBosco`) si prefieres esa ruta.
   - Puerto: el que ya trae Tomcat por defecto, `8080` (cambialo aqui
     si ya tienes algo mas corriendo en ese puerto).
5. **Ejecuta** con el boton ▶ (Run) junto a esa configuracion. La
   consola de IntelliJ mostrara el arranque de Tomcat; cuando diga
   `Server startup in [...] ms`, ya esta listo.

### 3. Abrir en el navegador (localhost)

Con MySQL corriendo (XAMPP) y Tomcat corriendo (Smart Tomcat), abre:

- **`http://localhost:8080/`** (o `http://localhost:8080/CafeDonBosco/`
  si dejaste el context path por defecto) — portal inicial con dos
  botones: **Soy Administrador** (pide iniciar sesion) y **Soy
  Consumidor** (entra directo al catalogo, sin cuenta).

**Usuario administrador de prueba** (creado por `db/schema.sql`):

```
Correo:      admin@cafedonbosco.com
Contrasena:  Admin123!
```

Cambia esa contrasena (o genera un nuevo hash con
`PasswordUtil.hashear(...)`) antes de usar el sistema fuera de un
entorno de pruebas — nunca la dejes tal cual en produccion.

**Si algo no carga:** confirma que el modulo MySQL de XAMPP sigue
"verde"/corriendo (se detiene solo si la maquina se reinicia) y que la
consola de Smart Tomcat no muestre un error de conexion a la base de
datos (`No suitable driver found` o `Communications link failure` casi
siempre significa que MySQL esta apagado o los datos de `DB_URL` no
coinciden con tu XAMPP).

---

## Arquitectura

```
controller (Servlets JSON, /api/...)        -> service -> dao -> MySQL
controller.vista.* (Servlets + forward JSP) -> service -> dao -> MySQL
```

- **model**: entidades del dominio (`Usuario`, `Categoria`, `Producto`,
  `Inventario`, `Venta`, `DetalleVenta`, `Compra`, `DetalleCompra`,
  `Proveedor`, `GrupoOpcion`, `Opcion`, `Carrito`, `CarritoItem`, etc.).
- **dto.request / dto.response**: objetos de entrada/salida que nunca
  exponen las entidades directamente (la contrasena, por ejemplo, jamas
  viaja en una respuesta).
- **dao / dao.impl**: acceso a datos con JDBC puro y `PreparedStatement`
  (sin ORM).
- **service / service.impl**: todas las reglas de negocio y las
  validaciones — precio, stock, permisos, transiciones de estado —
  viven aqui, nunca en el Servlet ni en la JSP.
- **controller**: Servlets anotados con `@WebServlet` que exponen la API
  JSON bajo `/api/...`.
- **controller.vista.admin** / **controller.vista.tienda**: Servlets que
  hacen `forward()` a las JSP del panel de administrador y de la tienda,
  respectivamente.
- **filter**: `CorsFilter`, `RolAdminFilter` (protege `/api/admin/*`),
  `SesionVistaFilter` (protege `/admin/*`), `GlobalExceptionFilter`.
- **scheduler**: `VentaSchedulerListener`, un hilo en segundo plano que
  cada 30 segundos revisa los pedidos `EN_PREPARACION` cuyo tiempo
  estimado ya se cumplio y los pasa automaticamente a `LISTO`.
- **util**: `ConexionBD`, `PasswordUtil` (BCrypt), `JsonUtil` (Gson),
  `ValidacionUtil`, `SessionUtil`, `LoginRateLimiter`, `WhatsAppUtil`,
  `TicketPdfGenerator` (Apache PDFBox), `Constantes`.
- **exception**: `AppException` y subclases especificas, cada una
  mapeada a un codigo HTTP y un mensaje seguro para el cliente.

### Modelo de venta unificado

En vez de duplicar `Venta` y `Pedido`, se usa una sola entidad `Venta`
con `TipoVenta` (`PRESENCIAL` o `WEB`). La venta de mostrador (POS del
administrador) y la compra del consumidor comparten exactamente la
misma transaccion (`VentaServiceImpl.registrarConTransaccion`), el mismo
descuento de inventario y el mismo ciclo de estados
(`RECIBIDO → EN_PREPARACION → LISTO → ENTREGADO`, o `CANCELADO` en
cualquier punto antes de `ENTREGADO`).

### Personalizacion de productos

Un producto puede tener uno o mas **grupos de opciones** (`grupo_opcion`
/ `opcion` / `producto_grupo_opcion`), cada uno de seleccion unica u
obligatorio o no (por ejemplo "Tamano" es unico y obligatorio; "Extras"
es multiple y opcional). El precio se recalcula en vivo en el navegador
mientras el cliente elige, pero **el servidor vuelve a resolver y
recalcular todo contra la base de datos** al agregar al carrito y de
nuevo al confirmar el checkout: nunca se confia en el precio que manda
el navegador. Dos configuraciones distintas del mismo producto (ej.
Cafe Latte pequeno vs. Cafe Latte grande) quedan como lineas separadas
en el carrito, no se fusionan.

### Carrito por AJAX

Agregar un producto al carrito usa `fetch` contra `/api/carrito` en vez
de recargar la pagina: el contador del carrito en el encabezado se
actualiza al instante y se muestra un mensaje de confirmacion o de
error segun lo que responda el servidor. Si la red falla, cae de vuelta
a un envio de formulario normal para no dejar al cliente sin poder
comprar.

---

## Rutas de la tienda (`/tienda/*`, publico — sin cuenta)

| Ruta | Descripcion |
| --- | --- |
| `/tienda` | Home: destacados y categorias |
| `/tienda/menu` | Catalogo completo (busqueda en vivo, filtro por categoria, orden) |
| `/tienda/producto?id={id}` | Detalle con personalizacion, precio en vivo y productos relacionados |
| `/tienda/carrito` | Carrito de sesion (agregar/quitar/vaciar, por linea) |
| `/tienda/checkout` | Datos de envio + metodo de pago; registra la venta WEB |
| `/tienda/confirmacion?token={token}` | Confirmacion inmediata tras la compra |
| `/tienda/ticket?token={token}` | Comprobante: imprimir, descargar PDF, compartir por WhatsApp |
| `/tienda/nosotros` | Pagina institucional |

El consumidor **nunca inicia sesion** para comprar: el carrito y el
checkout funcionan enteramente sobre la sesion HTTP como invitado. Si
decide registrarse (`POST /api/auth/registro`) puede despues consultar
`/api/mis-pedidos` y `/api/mi-cuenta`, pero eso es opcional.

## Panel del administrador (`/admin/*`, protegido)

| Ruta | Descripcion |
| --- | --- |
| `/login`, `/logout` | Inicio/cierre de sesion, exclusivo para rol `ADMINISTRADOR` |
| `/admin/dashboard` | KPIs del dia/mes, ventas recientes, stock bajo, accesos rapidos |
| `/admin/productos` | Catalogo administrativo con stock exacto (solo lectura; crear/editar se hace por la API, ver mas abajo) |
| `/admin/venta-nueva` | POS: arma una venta presencial y la registra |
| `/admin/historial-ventas` | Historial combinado de ventas presenciales y web |
| `/admin/ticket?id={id}` | Comprobante de cualquier venta, por id |

Sin sesion con rol `ADMINISTRADOR`, `SesionVistaFilter` redirige
cualquier ruta bajo `/admin/*` a `/login`.

**Gestionable solo por API, sin pantalla propia todavia:** crear/editar
productos y categorias, ajustar inventario, proveedores, compras y los
grupos de opciones de personalizacion. La logica y la seguridad ya
estan completas (ver la tabla de endpoints); falta construir la vista
JSP para cada una.

---

## Endpoints de la API

Todas las respuestas usan el mismo sobre:

```json
{ "exitoso": true, "mensaje": "...", "datos": { } }
```

### Publicos

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| POST | `/api/auth/login` | Login compartido admin/consumidor (bloquea 15 min tras 5 fallos seguidos) |
| POST | `/api/auth/registro` | Registro de consumidor (opcional) |
| GET | `/api/auth/sesion` | Usuario de la sesion actual |
| POST | `/api/auth/logout` | Cierra la sesion |
| GET | `/api/categorias` | Categorias activas |
| GET | `/api/productos` | Catalogo (filtros `categoria`, `buscar`, `orden`) |
| GET | `/api/productos/{id}` | Detalle de un producto |
| GET | `/api/productos/{id}/relacionados` | Productos de la misma categoria |
| GET | `/api/productos/{id}/opciones` | Grupos de personalizacion de ese producto |
| GET/POST/PUT/DELETE | `/api/carrito`, `/api/carrito/items[/{claveLinea}]` | Carrito de compras (por sesion) |
| POST | `/api/checkout` | Registra la venta WEB, descuenta stock, revalida todo en el servidor |
| GET | `/api/horario/estado` | Si el local esta abierto ahora mismo |
| GET | `/api/tickets/{token}` | Datos del ticket por token aleatorio |
| GET | `/api/tickets/{token}/pdf` | PDF real del ticket (Apache PDFBox) |
| GET | `/api/tickets/{token}/whatsapp-link` | Enlace `wa.me` con el pedido ya redactado (no envia el mensaje solo) |
| POST | `/api/tickets/{token}/enviar-email` | Envia el PDF del ticket por correo (requiere `SMTP_HOST` configurado; sin boton en la interfaz todavia) |

### Con sesion propia (consumidor registrado)

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| GET | `/api/mi-cuenta` | Perfil del usuario autenticado |
| PUT | `/api/mi-cuenta` | Editar nombre/apellido/correo |
| GET | `/api/mis-pedidos`, `/api/mis-pedidos/{id}` | Historial de pedidos propio (ownership verificado en el servidor) |

### Administrador (`RolAdminFilter`, requiere rol `ADMINISTRADOR`)

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| GET/POST | `/api/admin/categorias` | Listar/crear categorias |
| PUT | `/api/admin/categorias/{id}` | Editar categoria |
| GET/POST | `/api/admin/productos` | Listar/crear productos (con stock exacto) |
| PUT | `/api/admin/productos/{id}` | Editar producto |
| PUT | `/api/admin/productos/{id}/estado` | Activar/desactivar producto |
| GET | `/api/admin/inventario` | Inventario con stock exacto |
| PUT | `/api/admin/inventario` | Ajustar stock/stock minimo (exige un motivo real) |
| GET/POST | `/api/admin/ventas` | Historial / registrar venta presencial (POS) |
| PATCH | `/api/admin/ventas/{id}` | Cambiar estado de una venta |
| GET | `/api/admin/tickets/{id}` | Ticket de cualquier venta por id |
| GET/POST | `/api/admin/compras` | Historial / registrar compra a proveedor |
| GET/POST/PUT | `/api/admin/proveedores[/{id}]` | Gestion de proveedores |
| GET/POST/PUT/DELETE | `/api/admin/grupos-opcion[/{id}]` | Gestion de grupos y opciones de personalizacion |
| GET/PUT | `/api/admin/horario[/{dia}]` | Horario de atencion por dia |
| GET | `/api/admin/dashboard` | KPIs, ventas recientes, stock bajo |

---

## Seguridad implementada

- Contrasenas con BCrypt (`jbcrypt`, costo 12), nunca se devuelven en
  ninguna respuesta; limitadas a 6-72 caracteres (BCrypt trunca mas
  alla de 72 bytes).
- `LoginRateLimiter` bloquea un correo 15 minutos tras 5 intentos de
  login fallidos seguidos (en memoria, por instancia de servidor).
- `PreparedStatement` en todo el acceso a datos.
- El precio, el stock y las opciones de personalizacion se revalidan
  en el servidor en cada paso (agregar al carrito, checkout); nunca se
  confia en lo que envia el navegador.
- El descuento de stock usa `UPDATE ... WHERE cantidad >= ?` dentro de
  una transaccion JDBC, para evitar sobreventa con solicitudes
  concurrentes.
- Rutas administrativas que tambien existen en version publica
  (`/api/productos` vs `/api/admin/productos`) verifican el prefijo de
  la URL **y** la sesion, no solo el filtro — para que una escritura
  nunca dependa de un unico punto de proteccion.
- `/api/mis-pedidos` y `/api/mi-cuenta` verifican que el recurso
  pedido pertenezca al usuario autenticado, no solo que haya sesion.
- El ticket del consumidor se consulta por un token aleatorio, nunca
  por el id incremental de la venta.
- Toda entrada de usuario que se vuelve a mostrar en una JSP pasa por
  `fn:escapeXml` (proteccion contra XSS).
- `RolAdminFilter` protege `/api/admin/*`; `SesionVistaFilter` protege
  las pantallas `/admin/*`.

## Nota tecnica: registro del driver JDBC en Tomcat

`ConexionBD` carga explicitamente `com.mysql.cj.jdbc.Driver` con
`Class.forName(...)` en un bloque estatico. En un classpath plano el
driver se auto-registra via `ServiceLoader`, pero dentro de un servlet
container el JAR vive en `WEB-INF/lib` bajo el classloader propio de la
aplicacion, y ese registro automatico no siempre se dispara: sin este
`Class.forName`, `DriverManager.getConnection()` falla con
`No suitable driver found` al desplegar en un Tomcat real (el error no
aparece en `mvn compile`/`package`, que no ejecutan el codigo).

## Pruebas

```bash
mvn test
```

83 pruebas unitarias (JUnit 5 + Mockito) sobre la capa de servicio y
utilidades: validaciones, maquina de estados de venta, carrito
(incluyendo lineas por personalizacion y limites de cantidad), login y
el limitador de intentos fallidos, horario de atencion.

## Variables de entorno

Ver `.env.example` para la lista completa (base de datos, SMTP para el
envio de tickets por correo, nivel de logging). Ninguna es obligatoria
para correr en local con XAMPP: todas tienen un valor por defecto
pensado para desarrollo.
