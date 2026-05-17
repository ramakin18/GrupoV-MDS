package backend.features.repositories.specs;

import backend.features.models.ProductoEstadoFiltro;
import backend.features.models.Producto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductoSpecifications {

    public static Specification<Producto> filtrarProductos(
            String nombre,
            BigDecimal precio,
            Integer stockMin,
            Integer stockMax,
            ProductoEstadoFiltro estado
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (estado == ProductoEstadoFiltro.ACTIVO) {
                predicates.add(criteriaBuilder.isFalse(root.get("borrado")));
            }
            if (estado == ProductoEstadoFiltro.INACTIVO) {
                predicates.add(criteriaBuilder.isTrue(root.get("borrado")));
            }
            if (nombre != null && !nombre.isBlank()) {
                predicates.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nombreProducto")),
                        "%" + nombre.toLowerCase() + "%"
                    )
                );
            }
            if (precio != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("precio"), precio));
            }
            if (stockMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("stockDisponible"), stockMin));
            }
            if (stockMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("stockDisponible"), stockMax));
            }

            query.orderBy(
                    criteriaBuilder.asc(root.get("stockDisponible")),
                    criteriaBuilder.asc(root.get("nombreProducto"))
            );

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

