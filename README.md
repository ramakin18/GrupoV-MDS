# GrupoV-MDS

Sistema de gestión de productos, clientes, pedidos y kits desarrollado con **Spring Boot 3** (backend) y **Angular 21** (frontend).

---

## Tabla de Contenidos

- [Tecnologías](#tecnologías)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Prerrequisitos](#prerrequisitos)
- [Configuración del Backend](#configuración-del-backend)
- [Configuración del Frontend](#configuración-del-frontend)
- [Ejecución del Proyecto](#ejecución-del-proyecto)
- [Ejecución de Pruebas](#ejecución-de-pruebas)
- [Documentación de API (Swagger)](#documentación-de-api-swagger)
- [Endpoints REST](#endpoints-rest)
- [Arquitectura](#arquitectura)
- [Features](#features)
- [Buenas Prácticas](#buenas-prácticas)
- [Scripts Disponibles](#scripts-disponibles)

---

## Tecnologías

### Backend
| Tecnología | Versión |
|------------|---------|
| Java | 21 |
| Spring Boot | 3.5.13 |
| Spring Data JPA (Hibernate) | 6.6.x |
| PostgreSQL (Neon) | 17 |
| Maven Wrapper | 3.x |
| Lombok | 1.18.x |
| MapStruct | 1.5.5 |
| Bean Validation (Jakarta) | latest |
| SpringDoc OpenAPI (Swagger) | 2.8.5 |
| Cloudinary | 1.36.0 |
| JUnit 5 + Mockito | latest |

### Frontend
| Tecnología | Versión |
|------------|---------|
| Angular | 21 |
| TypeScript | 5.9+ |
| RxJS | 7.8+ |
| Vitest | 4.0+ |
| Angular Router | standalone |

### Testing
| Componente | Framework | Cantidad de Tests |
|------------|-----------|------------------:|
| Backend (JUnit 5 + Mockito) | `@WebMvcTest` / `@ExtendWith(MockitoExtension.class)` | 81 |
| Frontend (Vitest) | `@angular/build:unit-test` | 105 |

---

## Estructura del Proyecto

```
GrupoV-MDS/
├── backend/
│   ├── src/main/java/backend/
│   │   ├── BackendApplication.java
│   │   ├── configs/
│   │   │   ├── CloudinaryConfig.java       # Configuración Cloudinary
│   │   │   ├── CorsFilter.java             # Filtro CORS global
│   │   │   ├── BaseResponse.java           # Wrapper genérico de respuesta
│   │   │   └── OpenApiConfig.java          # Configuración Swagger/OpenAPI
│   │   ├── features/
│   │   │   ├── controllers/                # REST Controllers
│   │   │   │   ├── ProductoController.java
│   │   │   │   ├── CarritoController.java
│   │   │   │   ├── PedidoController.java
│   │   │   │   ├── ClienteController.java
│   │   │   │   ├── KitController.java
│   │   │   │   ├── ResenaController.java
│   │   │   │   ├── ReporteController.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── dtos/
│   │   │   │   ├── DomicilioEnvioDto.java
│   │   │   │   ├── request/                # DTOs de entrada
│   │   │   │   │   ├── ProductoCreateReqDto.java
│   │   │   │   │   ├── CarritoValidateRequestDto.java
│   │   │   │   │   ├── CarritoItemRequestDto.java
│   │   │   │   │   ├── ClienteCreateRequestDto.java
│   │   │   │   │   ├── ClienteLoginRequestDto.java
│   │   │   │   │   ├── DomicilioUpdateRequest.java
│   │   │   │   │   ├── PedidoCreateRequest.java
│   │   │   │   │   ├── PedidoItemRequest.java
│   │   │   │   │   ├── PedidoCancelRequest.java
│   │   │   │   │   ├── PedidoSituacionUpdateRequestDto.java
│   │   │   │   │   ├── KitCreateRequest.java
│   │   │   │   │   └── ResenaCreateRequestDto.java
│   │   │   │   └── response/               # DTOs de salida
│   │   │   │       ├── ResenaResponseDto.java
│   │   │   │       ├── ProductoResponseDto.java
│   │   │   │       ├── CarritoResponseDto.java
│   │   │   │       ├── CarritoItemResponseDto.java
│   │   │   │       ├── ClienteResponseDto.java
│   │   │   │       ├── PedidoResponseDTO.java
│   │   │   │       ├── PedidoDetalleResponseDTO.java
│   │   │   │   └── KitResponseDto.java
│   │   │   │   └── ReporteResponse.java
│   │   │   ├── models/                     # Entidades JPA
│   │   │   │   ├── Producto.java
│   │   │   │   ├── Cliente.java
│   │   │   │   ├── Pedido.java
│   │   │   │   ├── PedidoDetalle.java
│   │   │   │   ├── Kit.java
│   │   │   │   ├── KitProducto.java
│   │   │   │   ├── Resena.java
│   │   │   │   ├── SituacionPedido.java (enum)
│   │   │   │   ├── ProductoEstadoFiltro.java (enum)
│   │   │   │   └── ProductoViewRole.java (enum)
│   │   │   ├── mappers/                    # Conversión Entity ↔ DTO
│   │   │   │   ├── ProductoMapper.java
│   │   │   │   ├── PedidoMapper.java
│   │   │   │   ├── ClienteMapper.java
│   │   │   │   ├── KitMapper.java
│   │   │   │   └── ResenaMapper.java
│   │   │   ├── services/
│   │   │   │   ├── interfaces/
│   │   │   │   │   └── domain/             # Contratos de servicio
│   │   │   │   │       ├── IProductoService.java
│   │   │   │   │       ├── IProductoCreateService.java
│   │   │   │   │       ├── IProductoListService.java
│   │   │   │   │       ├── ICarritoService.java
│   │   │   │   │       ├── IPedidoService.java
│   │   │   │   │       ├── IClienteService.java
│   │   │   │   │   ├── IKitService.java
│   │   │   │   │   ├── IResenaService.java
│   │   │   │   │   └── IReporteService.java
│   │   │   │   └── impl/domain/            # Implementaciones
│   │   │   │       ├── ResenaServiceImpl.java
│   │   │   │       ├── ProductoServiceImpl.java
│   │   │   │       ├── ProductoCreateService.java
│   │   │   │       ├── ProductoListService.java
│   │   │   │       ├── CarritoServiceImpl.java
│   │   │   │       ├── PedidoServiceImpl.java
│   │   │   │       ├── ClienteServiceImpl.java
│   │   │   │       ├── KitServiceImpl.java
│   │   │   │       ├── CloudinaryServiceImpl.java
│   │   │   │       └── ReporteServiceImpl.java
│   │   │   └── repositories/
│   │   │       ├── IProductoRepository.java
│   │   │       ├── IKitRepository.java
│   │   │       ├── PedidoRepository.java
│   │   │       ├── PedidoDetalleRepository.java
│   │   │       ├── ClienteRepository.java
│   │   │       └── specs/
│   │   │           ├── IResenaRepository.java
│   │   │           └── ProductoSpecifications.java
│   │   │           └── ProductoSpecifications.java
│   │   └── exceptions/
│   │       ├── DuplicateResourceException.java
│   │       ├── ResourceNotFoundException.java
│   │       ├── InvalidCredentialsException.java
│   │       └── ValidationException.java
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   ├── application-dev.properties
│   │   └── application-prod.properties
│   ├── src/test/java/backend/
│   │   ├── BackendApplicationTests.java
│   │   ├── features/controllers/
│   │   │   ├── ProductoControllerTest.java
│   │   │   ├── CarritoControllerTest.java
│   │   │   ├── PedidoControllerTest.java
│   │   │   ├── ClienteControllerTest.java
│   │   │   └── KitControllerTest.java
│   │   └── features/services/impl/domain/
│   │       ├── ProductoServiceImplTest.java
│   │       ├── CarritoServiceImplTest.java
│   │       ├── PedidoServiceImplTest.java
│   │       ├── ClienteServiceImplTest.java
│   │       └── KitServiceImplTest.java
│   ├── .env.example
│   ├── mvnw / mvnw.cmd
│   └── pom.xml
│
└── front/
    ├── src/app/
    │   ├── app.config.ts
    │   ├── app.routes.ts
    │   ├── app.ts
    │   ├── app.html
    │   ├── app.css
    │   ├── core/
    │   │   ├── models/          # Interfaces TypeScript
    │   │   ├── services/        # Servicios Angular (HttpClient)
    │   │   │   ├── auth.service.ts
    │   │   │   ├── auth.guard.ts
    │   │   │   ├── admin.guard.ts
    │   │   │   ├── cart.service.ts
    │   │   │   ├── product.service.ts
    │   │   │   ├── client.service.ts
    │   │   │   ├── order.service.ts
    │   │   │   └── kit.service.ts
    │   │   └── validators/      # Validadores personalizados
    │   ├── features/
    │   │   ├── home/            # Página principal / catálogo
    │   │   ├── products/        # CRUD de productos
    │   │   ├── clients/         # Registro, login, perfil
    │   │   ├── orders/          # Pedidos + pendientes entrega
    │   │   ├── kits/            # CRUD de kits
    │   │   └── modal-carrito/   # Modal de carrito de compras
    │   └── shared/              # Componentes compartidos
    ├── package.json
    └── angular.json
```

---

## Prerrequisitos

- **JDK 21** instalado (`java -version`)
- **Node.js 18+** y npm (`node -version`, `npm -version`)
- **Maven Wrapper** incluido (no requiere Maven global)
- Conexión a internet (base de datos Neon en la nube + Cloudinary)

Verificar instalaciones:
```bash
java -version
node -version
npm -version
```

---

## Configuración del Backend

### Base de Datos (Neon PostgreSQL)

El backend usa una base de datos PostgreSQL en la nube (Neon). Configuración en `application.properties`:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://...}
spring.datasource.username=${DB_USER:neondb_owner}
spring.datasource.password=${DB_PASS:...}
```

Las credenciales por defecto están en el archivo, pero se pueden sobrescribir con variables de entorno:
```bash
set DB_URL=jdbc:postgresql://tu-host/neondb?sslmode=require
set DB_USER=tu-usuario
set DB_PASS=tu-contraseña
```

### Cloudinary (Imágenes de Productos)

Para la subida de imágenes de productos se requiere Cloudinary. Configurar vía `.env` (copiar `.env.example`):

```
CLOUDINARY_CLOUD_NAME=tu-cloud
CLOUDINARY_API_KEY=tu-api-key
CLOUDINARY_API_SECRET=tu-api-secret
```

### Perfiles

- **`dev`** (default): `spring.jpa.show-sql=true`, `spring.sql.init.mode=never`
- **`prod`**: Variables de entorno requeridas, logs JSON

---

## Configuración del Frontend

```bash
cd front
npm install
```

Proxy configurado en `angular.json` para redirigir `/api/*` a `http://localhost:8081`.

---

## Ejecución del Proyecto

### 1. Iniciar el Backend

```bash
cd backend
mvnw spring-boot:run
# o: ./mvnw spring-boot:run (Linux/Mac)
```

El backend arranca en: `http://localhost:8081`

### 2. Iniciar el Frontend

```bash
cd front
npm start
```

El frontend arranca en: `http://localhost:4200`

---

## Ejecución de Pruebas

### Backend (81 tests - JUnit 5 + Mockito)

```bash
cd backend
mvnw test
```

Estructura de tests:

| Capa | Archivo | Tipo |
|------|---------|------|
| Controller | `ProductoControllerTest.java` | `@WebMvcTest` |
| Controller | `CarritoControllerTest.java` | `@WebMvcTest` |
| Controller | `PedidoControllerTest.java` | `@WebMvcTest` |
| Controller | `ClienteControllerTest.java` | `@WebMvcTest` |
| Controller | `KitControllerTest.java` | `@WebMvcTest` |
| Service | `ProductoServiceImplTest.java` | `@ExtendWith(MockitoExtension.class)` |
| Service | `CarritoServiceImplTest.java` | `@ExtendWith(MockitoExtension.class)` |
| Service | `PedidoServiceImplTest.java` | `@ExtendWith(MockitoExtension.class)` |
| Service | `ClienteServiceImplTest.java` | `@ExtendWith(MockitoExtension.class)` |
| Service | `KitServiceImplTest.java` | `@ExtendWith(MockitoExtension.class)` |

### Frontend (105 tests - Vitest)

```bash
cd front
npm test
```

Estructura de tests (archivos `.spec.ts`):

| Feature | Archivo |
|---------|---------|
| App | `app.spec.ts` |
| Auth | `auth.service.spec.ts`, `auth.guard.spec.ts`, `admin.guard.spec.ts` |
| Cart | `cart.service.spec.ts`, `modal-carrito.spec.ts` |
| Clients | `client-registration.component.spec.ts`, `client-login.component.spec.ts` |
| Products | `product-list.component.spec.ts` |
| Orders | `order-list.component.spec.ts`, `pending-delivery.component.spec.ts` |
| Kits | `kit-list.component.spec.ts` |

> **Nota:** Usar `ng test --watch=false` para ejecución única.

---

## Documentación de API (Swagger)

La API REST está documentada con **SpringDoc OpenAPI 3** (Swagger UI).

### Acceso

| Recurso | URL |
|---------|-----|
| Swagger UI | `http://localhost:8081/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8081/api-docs` |

### ¿Qué incluye la documentación?

- **Tags** por recurso: Productos, Carrito, Pedidos, Clientes, Kits, Reseñas
- **`@Operation`** con descripciones en español para cada endpoint
- **`@ApiResponses`** con códigos HTTP (200, 201, 400, 401, 404, 409, 500)
- **`@Parameter`** con descripciones para query params y path vars
- **`@Schema`** en todos los DTOs con ejemplos (`example`) y descripciones de campos
- **Request/Response schemas** generados automáticamente desde los DTOs y records de Java

---

## Endpoints REST

### Productos — `/api/productos`

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/productos` | Listar productos (con filtros) | No |
| `GET` | `/api/productos/{id}` | Obtener producto por ID | No |
| `POST` | `/api/productos` | Crear producto con imagen (multipart) | No |
| `PUT` | `/api/productos/{id}` | Actualizar producto | No |
| `DELETE` | `/api/productos/{id}` | Borrado lógico del producto | No |

**Filtros GET:** `nombre`, `precio`, `stock`, `stockMin`, `stockMax`, `estado` (TODOS/ACTIVO/INACTIVO), `rol` (ADMIN/USUARIO)

### Carrito — `/api/carrito`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/carrito/validar` | Validar stock y precios del carrito |

### Pedidos — `/api/pedidos`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/pedidos` | Listar pedidos (filtro `?estado=`) |
| `GET` | `/api/pedidos/pendientes` | Pedidos pendientes de entrega |
| `GET` | `/api/pedidos/{id}` | Obtener pedido por ID |
| `POST` | `/api/pedidos` | Crear pedido |
| `PUT` | `/api/pedidos/{id}/situacion` | Actualizar situación |
| `PUT` | `/api/pedidos/{id}/cancelar` | Cancelar pedido con motivo |

**Situaciones:** `RESERVADO` → `PENDIENTE` → `LISTO` → `RETIRADO` → `ENTREGADO` | `CANCELADO`

### Clientes — `/api/clientes`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/clientes/registrar` | Registrar nuevo cliente |
| `POST` | `/api/clientes/login` | Iniciar sesión |
| `GET` | `/api/clientes` | Listar todos los clientes |
| `GET` | `/api/clientes/{id}` | Obtener cliente por ID |
| `PUT` | `/api/clientes/{id}` | Actualizar cliente |
| `PUT` | `/api/clientes/{id}/domicilio` | Actualizar solo el domicilio |
| `DELETE` | `/api/clientes/{id}` | Eliminar cliente |

### Reseñas — `/api/resenas`

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/resenas` | Listar reseñas (`?productoId=&admin=true`) | No |
| `GET` | `/api/resenas/kit/{id}` | Listar reseñas de un kit | No |
| `POST` | `/api/resenas` | Crear reseña (producto o kit) | No |
| `PUT` | `/api/resenas/{id}` | Actualizar reseña existente | No |
| `DELETE` | `/api/resenas/{id}` | Soft-delete de reseña | No |
| `PUT` | `/api/resenas/{id}/restore` | Restaurar reseña eliminada (admin) | No |

### Kits — `/api/kits`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/kits` | Listar kits (`?activos=true`) |
| `GET` | `/api/kits/{id}` | Obtener kit por ID |
| `POST` | `/api/kits` | Crear kit con productos |
| `PUT` | `/api/kits/{id}` | Actualizar kit |
| `DELETE` | `/api/kits/{id}` | Eliminar kit |

### Estados HTTP

| Código | Significado |
|--------|-------------|
| `200` | OK |
| `201` | Creado (POST) |
| `400` | Bad Request (validación) |
| `401` | Unauthorized (credenciales inválidas) |
| `404` | Not Found |
| `409` | Conflict (email duplicado) |
| `500` | Internal Server Error |

---

## Arquitectura

### Backend (Hexagonal / Onion Architecture)

```
Controller → Service Interface → Service Implementation → Repository → Entity
                ↓
            DTOs / Mappers
```

- **Controllers:** Exponen endpoints REST, validan requests con `@Valid`
- **Services:** Lógica de negocio vía interfaces (IProductoService, IPedidoService...)
- **Repositories:** Spring Data JPA + Specifications para consultas dinámicas
- **DTOs:** Separan la API de las entidades JPA (records inmutables)
- **Mappers:** MapStruct para conversión Entity ↔ DTO
- **Exceptions:** Manejadas centralizadamente con `@RestControllerAdvice`

### Frontend (Feature-Based Architecture)

```
core/       → Servicios, modelos, guards compartidos
features/   → Módulos por funcionalidad (home, products, clients, orders, kits)
shared/     → Componentes reutilizables
```

- **Standalone Components** (Angular 21, sin NgModules)
- **Control Flow** (`@if` / `@for` en templates)
- **Inyección con `provideHttpClient(withFetch())`**
- **Route Guards** (`auth.guard`, `admin.guard`) para protección de rutas
- **RxJS** para comunicación asíncrona y estado del carrito

---

## Features

### Catálogo de Productos
- Listado con filtros por nombre, precio, stock y estado
- Vista ADMIN (productos borrados) / USUARIO (solo activos)
- Subida de imágenes a Cloudinary (multipart/form-data)
- Borrado lógico (soft delete)

### Registro y Login de Clientes
- Validación de email único
- Domicilio de envío completo (país, provincia, localidad, calle, número, piso, depto.)
- Roles: `USUARIO`, `ADMIN`

### Carrito de Compras
- Validación de stock y precios antes de crear pedido
- Cálculo de subtotales y total

### Pedidos
- Ciclo de vida: RESERVADO → PENDIENTE → LISTO → RETIRADO → ENTREGADO
- Cancelación con motivo
- Vista de pedidos pendientes de entrega
- Dirección de envío desnormalizada en el pedido

### Kits de Productos
- CRUD completo de kits con productos asociados
- Control de stock (no permitir desactivar kit con producto inactivo)
- Filtro de kits activos para el catálogo
- Precio y stock propios del kit
- Puntuación promedio y cantidad de reseñas por kit

### Sistema de Reseñas
- Reseñas para productos y kits con calificación de 1 a 5 estrellas
- Validación de compra: solo clientes que hayan comprado el producto/kit pueden reseñar
- Soft-delete de reseñas con restauración por administradores
- Vista admin: muestra reseñas eliminadas con opción de restaurar
- Vista normal: solo muestra reseñas activas, ordenadas de más reciente a más antigua
- Cálculo automático de puntuación promedio al crear/eliminar/restaurar reseñas
- Interfaz con selector visual de estrellas (hover + click)
- Prevención de reseñas duplicadas por cliente+producto o cliente+kit

### Reportes
- Vista de productos más vendidos y con stock mínimo
- Gestión de cupones de descuento

---

## Buenas Prácticas

### Backend
- ✅ Validación de entrada con `@Valid` y Jakarta Validation
- ✅ DTOs inmutables (records) para request/response
- ✅ Interfaces de servicio para desacoplamiento y testabilidad
- ✅ MapStruct para mappers (type-safe, sin reflection)
- ✅ JPA Specifications para consultas dinámicas
- ✅ ResponseEntity con códigos HTTP adecuados
- ✅ Manejador global de excepciones (`@RestControllerAdvice`)
- ✅ Documentación OpenAPI/Swagger 3
- ✅ Tests unitarios con JUnit 5 + Mockito (81 tests)
- ✅ Separación de perfiles (dev/prod)

### Frontend
- ✅ TypeScript estricto con interfaces definidas
- ✅ Servicios con HttpClient y RxJS
- ✅ Componentes standalone (Angular 21)
- ✅ Control Flow (`@if`/`@for`) en templates
- ✅ Route Guards para autenticación y roles
- ✅ Tests con Vitest (105 tests)

### General
- ✅ `.gitignore` configurado para ambos proyectos
- ✅ Estructura de carpetas limpia y predecible
- ✅ Variables de entorno para credenciales
- ✅ Maven Wrapper (no requiere Maven global)

---

## Scripts Disponibles

### Backend
```bash
cd backend
mvnw clean                  # Limpiar build
mvnw compile                # Compilar código
mvnw test                   # Ejecutar tests (81 tests)
mvnw spring-boot:run        # Iniciar aplicación
```

### Frontend
```bash
cd front
npm start                   # Iniciar servidor de desarrollo
npm run build               # Build de producción
npm test                    # Ejecutar tests (105 tests)
ng test --watch=false       # Tests una sola vez
```

---

## Autores

**Grupo 5 — Metodologías de Desarrollo de Software — TUP**

- Backend: Spring Boot 3 + Java 21 + PostgreSQL (Neon)
- Frontend: Angular 21 standalone
- Testing: JUnit 5 / Mockito (backend) + Vitest (frontend)
- Documentación API: Swagger UI (SpringDoc OpenAPI 3)
- Imágenes: Cloudinary
