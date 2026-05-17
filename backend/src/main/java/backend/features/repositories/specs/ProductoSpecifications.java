package backend.features.repositories.specs;

import backend.features.models.ProductoEstadoFiltro;
import backend.features.models.Producto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductoSpecifications {

    public static Specification<Producto> hasNombre(String nombre) {
        return (root, query, cb) -> (nombre == null || nombre.isEmpty())
                ? null : cb.like(cb.lower(root.get("nombreProducto")), "%" + nombre.toLowerCase() + "%");
    }

    public static Specification<Producto> hasPrecioMax(BigDecimal precio) {
        return (root, query, cb) -> precio == null
                ? null : cb.lessThanOrEqualTo(root.get("precio"), precio);
    }

    public static Specification<Producto> hasStockMin(Integer stock) {
        return (root, query, cb) -> stock == null
                ? null : cb.greaterThanOrEqualTo(root.get("stockDisponible"), stock);
    }
    public static Specification<Producto> filtrarProductos(
            String nombre,
            BigDecimal precio,
            Integer stockMin,
            Integer stockMax,
            ProductoEstadoFiltro estado
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (estado == ProductoEstadoFiltro.ACTIVO) {
                predicates.add(cb.isFalse(root.get("borrado")));
            }
            if (estado == ProductoEstadoFiltro.INACTIVO) {
                predicates.add(cb.isTrue(root.get("borrado")));
            }
            if (nombre != null && !nombre.isBlank()) {
                predicates.add(
                    cb.like(
                        cb.lower(root.get("nombreProducto")),
                        "%" + nombre.toLowerCase() + "%"
                    )
                );
            }
            if (precio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("precio"), precio));
            }
            if (stockMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("stockDisponible"), stockMin));
            }
            if (stockMax != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("stockDisponible"), stockMax));
            }

            query.orderBy(
                    cb.asc(root.get("stockDisponible")),
                    cb.asc(root.get("nombreProducto"))
            );

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}

