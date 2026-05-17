package backend.features.repositories.specs;

import backend.features.models.Producto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductoSpecifications {

    public static Specification<Producto> hasNombre(String nombre) {
        return (root, query, cb) -> (nombre == null || nombre.isEmpty())
                ? null : cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%");
    }

    public static Specification<Producto> hasPrecioMax(Double precio) {
        return (root, query, cb) -> precio == null
                ? null : cb.lessThanOrEqualTo(root.get("precio"), precio);
    }

    public static Specification<Producto> hasStockMin(Integer stock) {
        return (root, query, cb) -> stock == null
                ? null : cb.greaterThanOrEqualTo(root.get("stock"), stock);
    }
    public static Specification<Producto> filtrarProductos(
            String nombre,
            BigDecimal precio,
            Integer stock,
            boolean incluirBorrados
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Filtro por estado (borrado)
            if (!incluirBorrados) {
                predicates.add(cb.isFalse(root.get("borrado")));
            }

            // 2. Filtro por nombre (like)
            if (nombre != null && !nombre.isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("nombre")),
                        "%" + nombre.toLowerCase() + "%"
                ));
            }

            // 3. Filtro por precio (Menor o igual al presupuesto enviado)
            if (precio != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("precio"), precio));
            }

            // 4. Filtro por stock (Mayor o igual al stock mínimo requerido)
            if (stock != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("stockDisponible"), stock));
            }

            // Unimos todos los predicados con un AND
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}

