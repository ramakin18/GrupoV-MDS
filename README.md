# GrupoV-MDS

Sistema de gestión de productos y clientes desarrollado con **Spring Boot** (backend) y **Angular** (frontend).

## Tabla de Contenidos

- [Tecnologías](#tecnologías)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Prerrequisitos](#prerrequisitos)
- [Configuración del Backend](#configuración-del-backend)
- [Configuración del Frontend](#configuración-del-frontend)
- [Ejecución del Proyecto](#ejecución-del-proyecto)
- [Documentación de API](#documentación-de-api)
- [Arquitectura](#arquitectura)
- [Buenas Prácticas](#buenas-prácticas)

---

## Tecnologías

### Backend
- **Java 21**
- **Spring Boot 3.5.13**
- **Spring Data JPA** con Hibernate
- **PostgreSQL** (Neon - Cloud Database)
- **Maven** como gestor de dependencias
- **Lombok** para reducción de boilerplate
- **Bean Validation** (Jakarta Validation)

### Frontend
- **Angular 21**
- **TypeScript 5.9+**
- **RxJS 7.8+**
- **Vitest 4.0+** para testing
- **Angular Router** para navegación

---

## Estructura del Proyecto

```
GrupoV-MDS/
├── backend/
│   ├── src/main/java/backend/
│   │   ├── features/
│   │   │   ├── controllers/     # REST Controllers
│   │   │   ├── services/        # Lógica de negocio
│   │   │   │   ├── impl/domain/ # Implementaciones
│   │   │   │   └── interfaces/  # Contratos de servicio
│   │   │   ├── dtos/            # Data Transfer Objects
│   │   │   │   ├── request/     # DTOs de entrada
│   │   │   │   └── response/    # DTOs de salida
│   │   │   ├── models/          # Entidades JPA
│   │   │   ├── mappers/         # Conversión Entity ↔ DTO
│   │   │   └── repositories/    # Acceso a datos
│   │   └── GrupoVMdsApplication.java
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
│
└── front/
    ├── src/app/
    │   ├── core/                # Servicios core, modelos, interfaces
    │   ├── features/            # Componentes por feature
    │   │   ├── home/           # Página principal
    │   │   ├── products/       # Gestión de productos
    │   │   └── clients/        # Gestión de clientes
    │   └── app.routes.ts       # Configuración de rutas
    ├── package.json
    └── angular.json
```

---

## Prerrequisitos

- **JDK 21** instalado
- **Node.js 18+** y npm
- **Maven 3.8+**
- Conexión a internet (base de datos en la nube Neon)

Verificar instalaciones:
```bash
java -version
mvn -version
node -version
npm -version
```

---

## Configuración del Backend

El backend utiliza una base de datos PostgreSQL en **Neon**. La configuración se encuentra en:

`backend/src/main/resources/application.properties`

```properties
# Base de datos PostgreSQL en Neon
spring.datasource.url=jdbc:postgresql://ep-lively-flower-acknkoey-pooler.sa-east-1.aws.neon.tech/neondb?sslmode=require
spring.datasource.username=neondb_owner
spring.datasource.password=npg_cb6qOpKwG3Ui

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Puerto
server.port=8081

# Deshabilitar Spring Security (para desarrollo)
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
```

> **Nota:** En producción, las credenciales deben estar en variables de entorno y no en el repositorio.

---

## Configuración del Frontend

Instalar dependencias:

```bash
cd front
npm install
```

El frontend se comunica con el backend en `http://localhost:8081`.

---

## Ejecución del Proyecto

### 1. Iniciar el Backend

```bash
cd backend
mvn spring-boot:run
```

El backend estará disponible en: `http://localhost:8081`

### 2. Iniciar el Frontend

```bash
cd front
npm start
```

El frontend estará disponible en: `http://localhost:4200`

---

## Documentación de API

### Base URL
```
http://localhost:8081/api
```

### Endpoints de Productos

| Método | Endpoint | Descripción | Parámetros |
|--------|----------|-------------|------------|
| `GET` | `/api/productos` | Listar productos | `nombre` (opcional), `precio` (opcional), `stock` (opcional), `rol` (opcional) |
| `GET` | `/api/productos/{id}` | Obtener producto por ID | `id` (path) |
| `POST` | `/api/productos` | Crear producto | Body: ProductoCreateReqDto |
| `PUT` | `/api/productos/{id}` | Actualizar producto | `id` (path), Body: ProductoCreateReqDto |
| `DELETE` | `/api/productos/{id}` | Eliminar producto | `id` (path) |

### Ejemplos de uso

**Listar productos:**
```bash
curl http://localhost:8081/api/productos
```

**Listar con filtros:**
```bash
curl "http://localhost:8081/api/productos?nombre=Test&precio=100&rol=usuario"
```

**Crear producto:**
```bash
curl -X POST http://localhost:8081/api/productos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Producto A","precio":150.00,"stock":10,"descripcion":"Descripción"}'
```

**Actualizar producto:**
```bash
curl -X PUT http://localhost:8081/api/productos/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Producto A Actualizado","precio":200.00,"stock":15}'
```

**Eliminar producto:**
```bash
curl -X DELETE http://localhost:8081/api/productos/1
```

### Roles de Vista (ProductoViewRole)

- `usuario` - Vista estándar para usuarios
- `admin` - Vista administrativa con información adicional
- Si no se especifica, usa el rol por defecto

---

## Arquitectura

### Backend (Hexagonal/Onion Architecture)

```
Controller → Service Interface → Service Implementation → Repository → Entity
                ↓
            DTOs / Mappers
```

- **Controllers:** Exponen endpoints REST y validan requests
- **Services:** Lógica de negocio, definidos mediante interfaces
- **Repositories:** Acceso a datos con Spring Data JPA
- **DTOs:** Separan la API de las entidades internas
- **Mappers:** Conversión entre entidades y DTOs

### Frontend (Feature-Based Architecture)

```
core/           # Servicios compartidos, modelos, interfaces
features/       # Módulos por funcionalidad
  ├── home/     # Componentes de inicio
  ├── products/ # Gestión de productos
  └── clients/  # Gestión de clientes
```

- **Inyección de dependencias** mediante interfaces (`IProductService`)
- **Lazy loading** de rutas
- **Observable patterns** con RxJS

---

## Buenas Prácticas

### Backend
- ✅ Validación de entrada con `@Valid` y Jakarta Validation
- ✅ Uso de DTOs para separar API de entidades
- ✅ Interfaces de servicio para facilitar testing y desacoplamiento
- ✅ Mappers dedicados para conversión de objetos
- ✅ JPA Specifications para consultas dinámicas con filtros
- ✅ Respuestas HTTP adecuadas (`ResponseEntity`)

### Frontend
- ✅ TypeScript estricto con interfaces definidas
- ✅ Servicios con tokens de inyección (`InjectionToken`)
- ✅ Separación de modelos en `core/models`
- ✅ Componentes organizados por feature
- ✅ Rutas lazy-loaded para mejor performance
- ✅ Prettier configurado para consistencia de código

### General
- ✅ `.gitignore` configurado para ambos proyectos
- ✅ Commits descriptivos (conventional commits recomendado)
- ✅ Estructura de carpetas limpia y predecible
- ⚠️ **Pendiente:** Mover credenciales a variables de entorno
- ⚠️ **Pendiente:** Agregar tests unitarios en backend
- ⚠️ **Pendiente:** Configurar CORS apropiadamente para producción

---

## Scripts Disponibles

### Backend
```bash
mvn clean          # Limpiar build
mvn compile        # Compilar código
mvn test          # Ejecutar tests
mvn spring-boot:run  # Iniciar aplicación
```

### Frontend
```bash
npm start         # Iniciar servidor de desarrollo
npm run build     # Build de producción
npm test          # Ejecutar tests con Vitest
npm run watch     # Build en modo watch
```

---

## Autores

**Grupo 5 - Metodologías de Desarrollo de Software**

- Backend: Spring Boot con Java 21
- Frontend: Angular 21
- Base de Datos: PostgreSQL (Neon)

---

## Licencia

Este proyecto es parte del curso de Metodologías de Desarrollo de Software - TUP.
