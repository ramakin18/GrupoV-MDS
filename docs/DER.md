# DER — Diagrama Entidad-Relación del Sistema

## Visión General del Dominio

El sistema gestiona la venta de **productos** (individuales o armados como **kits**) a **clientes** registrados, quienes realizan **pedidos** que pasan por un ciclo de vida de 6 estados. Los clientes pueden dejar **reseñas** con puntuación (1-5 estrellas) tanto en productos como en kits, pero solo si comprobaron ese producto/kit en un pedido entregado. El sistema también maneja **cupones de descuento** asignables a clientes y productos específicos.

---

## 1. `productos` — Productos individuales

| Columna | Tipo | Restricciones |
|---------|------|---------------|
| `id_producto` | BIGINT (PK, auto-increment) | Clave primaria |
| `nombre_producto` | VARCHAR(50) | NOT NULL, min 4 |
| `descripcion` | TEXT | NOT NULL |
| `precio` | DECIMAL(10,2) | NOT NULL, > 0 |
| `stock_disponible` | INTEGER | NOT NULL, >= 0 |
| `stock_minimo` | INTEGER | DEFAULT 0, >= 0 |
| `imagen_url` | VARCHAR | nullable (opcional) |
| `borrado` | BOOLEAN | DEFAULT FALSE, soft-delete |
| `promedio_puntuacion` | DOUBLE | DEFAULT 0.0, calculado |
| `cantidad_resenas` | INTEGER | DEFAULT 0, calculado |

**Relaciones:**
- 1 → N con `pedido_detalle` (un producto aparece en muchos detalles de pedido)
- 1 → N con `resenas` (un producto tiene muchas reseñas)
- N → M con `cupones` (vía tabla `cupon_productos`)
- N → M con `kits` (vía tabla `kit_productos`)

**Reglas de negocio:**
- `borrado = true` es soft-delete: el producto no se muestra a usuarios normales, pero los ADMIN lo ven oculto.
- `promedio_puntuacion` y `cantidad_resenas` se recalculan automáticamente al crear/eliminar/restaurar reseñas.
- `stock_disponible` se descuenta al crear un pedido con situación distinta de CANCELADO.

---

## 2. `clientes` — Usuarios registrados

| Columna | Tipo | Restricciones |
|---------|------|---------------|
| `id` | BIGINT (PK, auto-increment) | Clave primaria |
| `nombre` | VARCHAR(50) | NOT NULL, min 2 |
| `apellido` | VARCHAR(50) | NOT NULL, min 2 |
| `email` | VARCHAR | NOT NULL, formato email |
| `contrasena` | VARCHAR | NOT NULL, min 8 |
| `pais` | VARCHAR | NOT NULL |
| `provincia` | VARCHAR | NOT NULL |
| `localidad` | VARCHAR | NOT NULL |
| `calle` | VARCHAR | NOT NULL |
| `numero` | VARCHAR | NOT NULL |
| `piso` | VARCHAR | nullable |
| `departamento` | VARCHAR | nullable |
| `rol` | VARCHAR | NOT NULL (ADMIN o USUARIO) |

**Relaciones:**
- 1 → N con `pedidos` (un cliente tiene muchos pedidos)
- 1 → N con `resenas` (como `usuario_id`, un cliente escribe muchas reseñas)
- N → M con `cupones` (vía tabla `cupon_clientes`)

---

## 3. `pedidos` — Órdenes de compra

| Columna | Tipo | Restricciones |
|---------|------|---------------|
| `id_pedido` | BIGINT (PK, auto-increment) | Clave primaria |
| `cliente_id` | BIGINT (FK) | → `clientes(id)` |
| `fecha` | DATETIME | NOT NULL |
| `fecha_actualizacion` | DATETIME | nullable |
| `situacion` | VARCHAR (enum) | NOT NULL — ver SituacionPedido |
| `total` | DECIMAL | NOT NULL, >= 0 |
| `subtotal` | DECIMAL | DEFAULT 0, >= 0 |
| `descuento` | DECIMAL | DEFAULT 0, >= 0 |
| `cupon_id` | BIGINT (FK) | → `cupones(id_cupon)`, nullable |
| `forma_pago` | VARCHAR | DEFAULT 'EFECTIVO' |
| `motivo_cancelacion` | TEXT | nullable |
| `pais_envio` | VARCHAR | nullable |
| `provincia_envio` | VARCHAR | nullable |
| `localidad_envio` | VARCHAR | nullable |
| `calle_envio` | VARCHAR | nullable |
| `numero_envio` | VARCHAR | nullable |
| `piso_envio` | VARCHAR | nullable |
| `departamento_envio` | VARCHAR | nullable |

**Relaciones:**
- N → 1 con `clientes` (muchos pedidos pertenecen a un cliente)
- 1 → N con `pedido_detalle` (un pedido tiene muchos items)
- N → 1 con `cupones` (un pedido puede tener un cupón aplicado)
- 1 → 1 con `cupon_clientes` (vía `pedido_id`)

**Ciclo de vida de situación (enum `SituacionPedido`):**
```
RESERVADO → PENDIENTE → LISTO → RETIRADO → ENTREGADO
                                                         → CANCELADO (desde cualquier estado)
```

**Reglas de negocio:**
- Las direcciones de envío se desnormalizan (copian del cliente al momento del pedido) para preservar la dirección histórica aunque el cliente la cambie después.
- `subtotal` es la suma de los subtotales de los detalles.
- `descuento` se calcula según el cupón aplicado.
- `total = subtotal - descuento`.
- Solo pedidos con situación `ENTREGADO` habilitan al cliente para dejar reseñas de los productos comprados.

---

## 4. `pedido_detalle` — Items individuales de cada pedido

| Columna | Tipo | Restricciones |
|---------|------|---------------|
| `id` | BIGINT (PK, auto-increment) | Clave primaria |
| `pedido_id` | BIGINT (FK) | → `pedidos(id_pedido)` |
| `producto_id` | BIGINT (FK) | → `productos(id_producto)` |
| `cantidad` | INTEGER | NOT NULL, > 0 |
| `precio_unitario` | DECIMAL | NOT NULL, >= 0 |
| `subtotal` | DECIMAL | NOT NULL, >= 0 |

**Relaciones:**
- N → 1 con `pedidos` (muchos detalles pertenecen a un pedido)
- N → 1 con `productos` (muchos detalles referencian un producto)

**Reglas:**
- `subtotal = cantidad * precio_unitario` (precio congelado al momento de la compra).
- `precio_unitario` se copia del producto al crear el detalle, para preservar el precio histórico.

---

## 5. `kits` — Paquetes de productos

| Columna | Tipo | Restricciones |
|---------|------|---------------|
| `id_kit` | BIGINT (PK, auto-increment) | Clave primaria |
| `nombre` | VARCHAR(50) | UNIQUE, NOT NULL |
| `descripcion` | TEXT | NOT NULL |
| `precio` | DECIMAL(10,2) | NOT NULL, > 0 |
| `stock` | INTEGER | >= 0, nullable |
| `activo` | BOOLEAN | DEFAULT TRUE |
| `promedio_puntuacion` | DOUBLE | DEFAULT 0.0, calculado |
| `cantidad_resenas` | INTEGER | DEFAULT 0, calculado |

**Relaciones:**
- 1 → N con `kit_productos` (un kit contiene muchos productos)
- 1 → N con `resenas` (un kit tiene muchas reseñas)

**Reglas:**
- `activo = false` oculta el kit del catálogo.
- No se puede desactivar un kit si alguno de sus productos está inactivo/borrado.
- El kit no se vende directamente como item de pedido (solo productos individuales aparecen en `pedido_detalle`).

---

## 6. `kit_productos` — Productos que componen cada kit

| Columna | Tipo | Restricciones |
|---------|------|---------------|
| `id` | BIGINT (PK, auto-increment) | Clave primaria |
| `kit_id` | BIGINT (FK) | → `kits(id_kit)` |
| `producto_id` | BIGINT (FK) | → `productos(id_producto)` |
| `cantidad` | INTEGER | NOT NULL, > 0 |

**Relaciones:**
- N → 1 con `kits`
- N → 1 con `productos`

**Reglas:**
- Un producto aparece con su `cantidad` específica dentro de cada kit (ej: 2 unidades del producto A en el kit X).
- La `cantidad` del producto total que necesita un kit = cantidad aquí definida.

---

## 7. `resenas` — Reseñas con puntuación de clientes

| Columna | Tipo | Restricciones |
|---------|------|---------------|
| `id` | BIGINT (PK, auto-increment) | Clave primaria |
| `puntuacion` | INTEGER | NOT NULL (1-5) |
| `descripcion` | VARCHAR(500) | nullable, max 500 |
| `fecha_creacion` | DATETIME | NOT NULL |
| `usuario_id` | BIGINT (FK) | → `clientes(id)` |
| `producto_id` | BIGINT (FK) | → `productos(id_producto)`, nullable |
| `kit_id` | BIGINT (FK) | → `kits(id_kit)`, nullable |
| `eliminado` | BOOLEAN | DEFAULT FALSE |

**Relaciones:**
- N → 1 con `clientes` (como `usuario_id`)
- N → 1 con `productos` (nullable: null si la reseña es sobre un kit)
- N → 1 con `kits` (nullable: null si la reseña es sobre un producto)

**Reglas de negocio claves:**

1. **Polimorfismo**: una reseña apunta a **producto** O a **kit**, no a ambos. Si `producto_id` tiene valor, `kit_id` debe ser null, y viceversa.
2. **Validación de compra obligatoria**: para reseñar un producto, el cliente debe tener un pedido con ese producto en estado `ENTREGADO`. Para reseñar un kit, el cliente debe haber comprado (en uno o varios pedidos entregados) **todos** los productos que componen ese kit.
3. **Sin duplicados**: un cliente no puede reseñar dos veces el mismo producto ni el mismo kit (unique lógico vía service).
4. **Soft-delete**: `eliminado = true` oculta la reseña de la vista normal. Los administradores pueden ver las eliminadas y restaurarlas.
5. **Cálculo automático**: al crear, eliminar o restaurar una reseña, se recalcula `promedio_puntuacion` y `cantidad_resenas` del producto o kit correspondiente.
6. **Orden**: las reseñas activas se muestran de la más reciente a la más antigua por `fecha_creacion DESC`.

---

## 8. `cupones` — Cupones de descuento

| Columna | Tipo | Restricciones |
|---------|------|---------------|
| `id_cupon` | BIGINT (PK, auto-increment) | Clave primaria |
| `codigo` | VARCHAR(12) | UNIQUE, NOT NULL |
| `tipo_descuento` | VARCHAR (enum) | NOT NULL — PORCENTAJE o MONTO_FIJO |
| `valor` | DECIMAL(12,2) | NOT NULL, > 0 |
| `fecha_desde` | DATE | NOT NULL |
| `fecha_hasta` | DATE | NOT NULL |
| `fecha_creacion` | DATETIME | NOT NULL, DEFAULT now() |

**Relaciones:**
- 1 → N con `cupon_clientes` (un cupón se asigna a muchos clientes)
- N → M con `productos` (vía tabla `cupon_productos`)

**Reglas:**
- Si `tipo_descuento = PORCENTAJE`, el `valor` es el porcentaje (ej: 10.00 = 10%).
- Si `tipo_descuento = MONTO_FIJO`, el `valor` es el monto en efectivo (ej: 500.00 = $500 off).
- `fecha_hasta` define la expiración; el cupón no aplica después de esa fecha.
- Un cupón puede aplicarse solo a productos específicos (vía `cupon_productos`).

---

## 9. `cupon_productos` — Tabla intermedia (ManyToMany entre cupones y productos)

| Columna | Tipo | Restricciones |
|---------|------|---------------|
| `cupon_id` | BIGINT (FK) | → `cupones(id_cupon)` |
| `producto_id` | BIGINT (FK) | → `productos(id_producto)` |

**PK compuesta:** (`cupon_id`, `producto_id`)

---

## 10. `cupon_clientes` — Asignación de cupones a clientes específicos

| Columna | Tipo | Restricciones |
|---------|------|---------------|
| `id` | BIGINT (PK, auto-increment) | Clave primaria |
| `cupon_id` | BIGINT (FK) | → `cupones(id_cupon)`, NOT NULL |
| `cliente_id` | BIGINT (FK) | → `clientes(id)`, NOT NULL |
| `usado` | BOOLEAN | NOT NULL, DEFAULT FALSE |
| `fecha_uso` | DATETIME | nullable |
| `pedido_id` | BIGINT (FK) | → `pedidos(id_pedido)`, nullable |

**Restricción única:** (`cupon_id`, `cliente_id`) — un cliente recibe un cupón una sola vez.

**Relaciones:**
- N → 1 con `cupones`
- N → 1 con `clientes`
- 1 → 1 con `pedidos` (vía `pedido_id`)

---

## Enums Persistidos

| Enum | Valores | Dónde se usa |
|------|---------|-------------|
| `SituacionPedido` | `RESERVADO`, `PENDIENTE`, `LISTO`, `RETIRADO`, `ENTREGADO`, `CANCELADO` | `pedidos.situacion` |
| `TipoDescuento` | `PORCENTAJE`, `MONTO_FIJO` | `cupones.tipo_descuento` |

## Enums de Filtro (no persistidos)

| Enum | Valores | Uso |
|------|---------|-----|
| `ProductoEstadoFiltro` | `TODOS`, `ACTIVO`, `INACTIVO` | Filtro para listar productos |
| `ProductoViewRole` | `ADMIN`, `USUARIO` | Controla visibilidad de productos borrados |

---

## Diagrama de Relaciones

```
┌───────────┐       ┌─────────────────┐       ┌──────────────┐
│  clientes │1──N──>│    pedidos      │1──N──>│pedido_detalle│
│           │       │                 │       │              │
│           │1──N──>│    resenas      │       │   productos  │<──N──┐
│           │       └─────────────────┘       └──────────────┘      │
│           │                                                        │
│           │N──M──┐                        ┌───────────────────┐    │
└───────────┘      │                        │ cupon_productos   │N──M┘
                   ▼                        └───────────────────┘
           ┌──────────────┐                        │
           │cupon_clientes│                        │
           └──────────────┘                        │
                   │                               │
                   ▼                               ▼
           ┌───────────────────────────────────────────┐
           │                cupones                     │
           └───────────────────────────────────────────┘

┌───────────┐1──N──>┌──────────────┐
│    kits   │       │kit_productos │N──1──>┌───────────┐
│           │       │              │       │ productos │
│           │1──N──>│   resenas    │       └───────────┘
└───────────┘       └──────────────┘
```

## Resumen de Tablas

| # | Tabla | Tipo | FK hacia |
|---|-------|------|----------|
| 1 | `productos` | Entidad principal | — |
| 2 | `clientes` | Entidad principal | — |
| 3 | `pedidos` | Entidad transaccional | clientes, cupones |
| 4 | `pedido_detalle` | Detalle transaccional | pedidos, productos |
| 5 | `kits` | Entidad principal | — |
| 6 | `kit_productos` | Asociativa | kits, productos |
| 7 | `resenas` | Entidad transaccional | clientes, productos, kits |
| 8 | `cupones` | Entidad principal | — |
| 9 | `cupon_productos` | Asociativa | cupones, productos |
| 10 | `cupon_clientes` | Asociativa | cupones, clientes, pedidos |

## Convenciones

- **Nombres de tablas**: en plural y snake_case (`pedido_detalle`, no `pedidoDetalle`).
- **FKs**: se genera columna automática con el nombre del campo + `_id` (ej: `cliente_id`).
- **PKs**: siempre BIGINT auto-increment, excepto tablas ManyToMany con PK compuesta.
- **Enums**: persistidos como VARCHAR con `EnumType.STRING`.
- **Auditoría**: `Resena.fechaCreacion`; `Pedido.fecha` y `Pedido.fechaActualizacion`.
- **Borrado lógico**: `Producto.borrado` (BOOLEAN), `Resena.eliminado` (BOOLEAN).
- **Totales desnormalizados**: `promedioPuntuacion` y `cantidadResenas` se cachean en producto/kit y se recalculan.
