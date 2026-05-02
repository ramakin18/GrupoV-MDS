package backend.features.repositories.specs;

import backend.features.models.Producto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductoSpecifications {

    public static Specification<Producto> filtrarProductos(String nombre, BigDecimal precio, Integer stock) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

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
            if (stock != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("stockDisponible"), stock));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

